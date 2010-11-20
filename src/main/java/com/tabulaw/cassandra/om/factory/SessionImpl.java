package com.tabulaw.cassandra.om.factory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import me.prettyprint.cassandra.dao.Command;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.HectorException;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.Mutator;
import me.prettyprint.cassandra.service.Keyspace;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;
import com.tabulaw.cassandra.om.factory.relations.ManyToOneRelation;


public class SessionImpl implements Session {

	private Map<String, Map<Object, Object>> sessionCache;
	private Map<Object, Object> initialStates;
	private Map<Object, Void> toRemove;
	private Map<ManyToOneRelation, Multimap<Object, Object>> entityRelationsLoading;
	
	private SessionFactory factory; 
	
	SessionImpl(SessionFactory factory) {
		this.factory = factory;
		sessionCache = Maps.newHashMap();
		initialStates = Maps.newIdentityHashMap();
		toRemove = Maps.newIdentityHashMap();
	}
	
	public <T> boolean isAttached(T object) {
		return initialStates.containsKey(object);
	}

	private void addToCache(String columnFamily, Object key, Object object) {
		if (object == null) {
			return;
		}
		Map<Object, Object> cfCache;
		if (! sessionCache.containsKey(columnFamily)) {
			cfCache = Maps.newHashMap();
			sessionCache.put(columnFamily, cfCache);
		} else {
			cfCache = sessionCache.get(columnFamily);
		}
		cfCache.put(key, object);
		
		Object initialState = factory.getCFDescriptor(object.getClass()).clone(object, null);
		initialStates.put(object, initialState);		
	}
	
	private Object findInCache(String columnFamily, Object key) {
		if (sessionCache.containsKey(columnFamily)) {
			return sessionCache.get(columnFamily).get(key);
		}
		return null;
	}
	
	private ColumnFamilyDescriptor safeFindCF(Class<?> klass) {
		ColumnFamilyDescriptor cfDescriptor = factory.getCFDescriptor(klass);
		if (cfDescriptor == null) {
			throw new HelenaException("Bad entity class: " + klass.getName());
		}
		return cfDescriptor;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T find(Class<T> klass, Object key) {
		ColumnFamilyDescriptor cfDescriptor = safeFindCF(klass);
		Object cachedObject = findInCache(cfDescriptor.getName(), key);
		if (cachedObject != null) {
			if (klass.isAssignableFrom(cachedObject.getClass())) {
				return (T) cachedObject;
			}
			return null;			
		}
		KeyspaceOperator operator = factory.createKeyspaceOperator();
		List<Object> result = cfDescriptor.find(this, operator, key);
		if (result.isEmpty()) {
			return null;
		}
		addToCache(cfDescriptor.getName(), key, result.get(0));
		loadDeferredRelations();
		return (T) result.get(0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> klass, Object... keys) {
		if (keys == null || keys.length == 0) {
			return Lists.newArrayList();
		}
		ColumnFamilyDescriptor cfDescriptor = safeFindCF(klass);
		List<Object> result = Lists.newArrayListWithCapacity(keys.length);
		List<Object> toLoad = Lists.newArrayList();
		for (Object key : keys) {
			Object fromCache = findInCache(cfDescriptor.getName(), key);
			if (fromCache != null) {
				if (klass.isAssignableFrom(fromCache.getClass())) {
					result.add(fromCache);
				}
			} else {
				toLoad.add(key);
			}
		}
		List<Object> dbResults = cfDescriptor.find(this, factory.createKeyspaceOperator(), 
				toLoad.toArray(new Object[toLoad.size()]));
		for (Object dbResult : dbResults) {			
			addToCache(cfDescriptor.getName(), cfDescriptor.getKey(dbResult), dbResult);
			result.add(dbResult);
		}
		loadDeferredRelations();
		return (List<T>) result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T merge(T object) {
		ColumnFamilyDescriptor cfDescriptor = safeFindCF(object.getClass());
		if (! isAttached(object)) {
			Object key = cfDescriptor.getKey(object);
			T existent = find((Class<T>) object.getClass(), key) ;
			if (existent == null) {
				Object attached = cfDescriptor.clone(object, null);
				addToCache(cfDescriptor.getName(), key, attached);
				initialStates.put(attached, null);
				return (T) attached;
			} else {
				cfDescriptor.clone(object, existent);
				return existent;
			}
		} 
		toRemove.remove(object);
		Object newAttached = cfDescriptor.clone(object, null);
		Object key = cfDescriptor.getKey(object);
		sessionCache.get(cfDescriptor.getName()).put(key, newAttached);
		initialStates.put(newAttached, initialStates.get(object));
		initialStates.remove(object);
		return (T) newAttached;
	}

	@Override
	public <T> void refresh(T object) {
		if (! isAttached(object)) {
			throw new HelenaException("Entity isn't attached: " + object);
		}
		ColumnFamilyDescriptor cfDescriptor = safeFindCF(object.getClass());
		Object key = cfDescriptor.getKey(object);
		List<Object> result = cfDescriptor.find(this, factory.createKeyspaceOperator(), key);
		if (result.isEmpty()) {
			throw new HelenaException("Entity doesn't exists: " + cfDescriptor.getColumnFamilyType().getName() 
					+ " with key: " + key);
		}
		cfDescriptor.clone(object, result.get(0));
		initialStates.put(object, result.get(0));
		toRemove.remove(object);
	}
	
	@Override
	public <T> void persist(T object) {
		if (isAttached(object)) {
			toRemove.remove(object);
			return;
		}
		ColumnFamilyDescriptor cfDescriptor = safeFindCF(object.getClass());
		Object key = cfDescriptor.getKey(object);
		if (find(object.getClass(), key) != null) {
			throw new HelenaException("Entity " + object.getClass() + " with id " + key + 
					" is already existent in db");
		}
		addToCache(cfDescriptor.getName(), key, object);
		initialStates.put(object, null);
	}
	
	@Override
	public <T> void remove(T object) {
		if (! isAttached(object)) {
			throw new HelenaException("Entity isn't attached: " + object);
		}
		toRemove.put(object, null);
	}

	@Override
	public void flush() {
		factory.executeCommand(new Command<Void>() {

			@Override
			public Void execute(Keyspace keyspace) throws HectorException {
				for (Object object : toRemove.keySet()) {
					ColumnFamilyDescriptor cf = safeFindCF(object.getClass());
					cf.remove(keyspace, object);
					initialStates.remove(object);
				}
				return null;
			}
		});
		toRemove.clear();
		KeyspaceOperator operator = factory.createKeyspaceOperator();
		Mutator mutator = HFactory.createMutator(operator);		
		for (Entry<Object, Object> entry : initialStates.entrySet()) {
			ColumnFamilyDescriptor cf = safeFindCF(entry.getKey().getClass());
			cf.mutate(this, mutator, entry.getKey(), entry.getValue());
			initialStates.put(entry.getKey(), cf.clone(entry.getKey(), null));
		}
		mutator.execute();		
	}

	@Override
	public void close() {
		flush();
		sessionCache = null;
		initialStates = null;
		toRemove = null;
	}

	@Override
	public boolean isClosed() {
		return sessionCache == null;
	}		
	
	public void loadRelation(ManyToOneRelation relation, String relationKey, Object entity) {
		ColumnFamilyDescriptor cf = relation.getRelationCF();
		Object obj = findInCache(cf.getName(), relationKey);
		if (obj != null) {
			relation.setValue(entity, obj);
			return;
		}
		if (entityRelationsLoading == null) {
			entityRelationsLoading = Maps.newIdentityHashMap();
		}
		Multimap<Object, Object> relationLoad;
		if (! entityRelationsLoading.containsKey(relation)) {
			relationLoad = HashMultimap.create();
			entityRelationsLoading.put(relation, relationLoad);
		} else {
			relationLoad = entityRelationsLoading.get(relation);
		}
		relationLoad.put(relationKey, entity);
	}
	
	private void loadDeferredRelations() {		
		if (entityRelationsLoading == null) {
			return;
		}
		Map<ManyToOneRelation, Multimap<Object, Object>> deffered = entityRelationsLoading;
		entityRelationsLoading = null;
		while (! deffered.isEmpty()) {
			ManyToOneRelation relation = deffered.keySet().iterator().next();
			ColumnFamilyDescriptor cf = relation.getRelationCF();
			
			Multimap<Object, Object> toLoad = deffered.get(relation);
			Object[] keys = toLoad.keySet().toArray();
			List<?> resultList = find(cf.getColumnFamilyType(), keys);
			
			for (Object result : resultList) {
				Object key = cf.getKey(result);
				for (Object entity : toLoad.get(key)) {
					relation.setValue(entity, result);
					relation.setValue(initialStates.get(entity), result);
				}
			}		
			deffered.remove(relation);
		}
	}

	@Override
	public SessionFactory getFactory() {
		return factory;
	}	
}
