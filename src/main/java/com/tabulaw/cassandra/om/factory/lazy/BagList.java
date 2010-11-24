package com.tabulaw.cassandra.om.factory.lazy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tabulaw.cassandra.om.factory.SessionImpl;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;
import com.tabulaw.cassandra.om.factory.relations.BagRelation;
import com.tabulaw.cassandra.om.factory.relations.OneToManyRelation;


public class BagList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;
	
	private SessionImpl session;	
	private BagRelation relation;
	private Object key;	
	private boolean isInitialized;
	
	private Set<Object> addedKeys;
	private Set<Object> removedKeys;
	private Map<Object, String> initialKeys;
	
	public BagList(SessionImpl session, Object key, BagRelation relation) {
		this.key = key;
		this.relation = relation;
		this.session = session;
		isInitialized = false;
	}
	
	@SuppressWarnings("unchecked")
	private void initialize() {
		if (isInitialized) {
			return;
		}
		if (session.isClosed()) {
			throw new HelenaException("Loading lazy collection with closed session");
		}
		Map<Object, String> entities = relation.loadList(session, key);
		super.addAll((Set<T>) entities.keySet());
		if (! relation.isReadonly()) {
			addedKeys = Sets.newHashSet();
			removedKeys = Sets.newHashSet();
			initialKeys = Maps.newHashMap();
			for (Entry<Object, String> entity : entities.entrySet()) {
				initialKeys.put(relation.getRelationCF().getKey(entity.getKey()), entity.getValue());
			}
		}
		isInitialized = true;
	}
	
	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(T e) {
		initialize();
		if (relation.isReadonly()) {
			throw new UnsupportedOperationException("Read only relation");
		}
		if (! session.isAttached(e)) {
			throw new IllegalArgumentException("Attempt to add detached object to collection: " + e);
		}
		Object key = relation.getRelationCF().getKey(e);
		if (addedKeys.contains(key)) {
			return false;
		}
		boolean addToList = false;
		if (removedKeys.contains(key)) {
			removedKeys.remove(key);
			addToList = true;			
		}
		if (! initialKeys.containsKey(key)) {
			addedKeys.add(key);
			addToList = true;
		}
		if (addToList) {
			super.add(e);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (relation.isReadonly()) {
			throw new UnsupportedOperationException("Read only relation");
		}
		boolean result = false;
		for (T element : c) {
			result = result | add(element);
		}
		return result;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		initialize();
		if (relation.isReadonly()) {
			throw new UnsupportedOperationException("Read only relation");
		}
		removedKeys.addAll(initialKeys.keySet());
		super.clear();
	}

	@Override
	public boolean contains(Object o) {
		initialize();
		return super.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		initialize();
		return super.containsAll(c);
	}

	@Override
	public T get(int index) {
		initialize();
		return super.get(index);
	}

	@Override
	public int indexOf(Object o) {
		initialize();
		return super.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		initialize();
		return super.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		initialize();
		return super.lastIndexOf(o);
	}

	@Override
	public T remove(int index) {
		T obj = get(index);
		remove(obj);
		return obj;
	}

	@Override
	public boolean remove(Object o) {
		initialize();
		if (relation.isReadonly()) {
			throw new UnsupportedOperationException("Read only relation");
		}
		if (! relation.getRelationCF().getColumnFamilyType().isAssignableFrom(o.getClass())) {
			return false;
		}	
		if (! session.isAttached(o)) {
			throw new IllegalArgumentException("Attempt to remove detached object from collection: " + o);
		}
		Object key = relation.getRelationCF().getKey(o);
		if (removedKeys.contains(key)) {
			return false;
		}
		boolean removeFromList = false;
		if (addedKeys.contains(key)) {
			addedKeys.remove(key);
			removeFromList = true;
		}
		if (initialKeys.containsKey(key)) {
			removedKeys.add(key);
			removeFromList = true;
		}
		if (removeFromList) {
			super.remove(o);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (relation.isReadonly()) {
			throw new UnsupportedOperationException("Read only relation");
		}
		boolean result = false;
		for (Object obj : c) {
			result = result | remove(obj);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public T set(int index, T element) {		
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<T> iterator() {
		initialize();
		return super.iterator();
	}

	@Override
	public ListIterator<T> listIterator() {
		initialize();
		return super.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int paramInt) {
		initialize();
		return super.listIterator(paramInt);
	}

	@Override
	public int size() {
		initialize();
		return super.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		initialize();
		return super.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		initialize();
		return super.toArray();
	}

	@Override
	public <C> C[] toArray(C[] a) {
		initialize();
		return super.toArray(a);
	}

	public Set<Object> getAddedKeys() {
		return addedKeys;
	}

	public void setAddedKeys(Set<Object> addedKeys) {
		this.addedKeys = addedKeys;
	}

	public Set<Object> getRemovedKeys() {
		return removedKeys;
	}

	public void setRemovedKeys(Set<Object> removedKeys) {
		this.removedKeys = removedKeys;
	}

	public Map<Object, String> getInitialKeys() {
		return initialKeys;
	}

	public void setInitialKeys(Map<Object, String> initialKeys) {
		this.initialKeys = initialKeys;
	}	
}
