package com.tabulaw.cassandra.om.factory.relations;

import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.extractors.StringExtractor;
import me.prettyprint.cassandra.model.ColumnSlice;
import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.Mutator;
import me.prettyprint.cassandra.model.Row;
import me.prettyprint.cassandra.model.SliceQuery;


import com.google.common.collect.Maps;
import com.tabulaw.cassandra.om.TypeConverter;
import com.tabulaw.cassandra.om.annotations.OneToMany;
import com.tabulaw.cassandra.om.factory.ColumnDescriptor;
import com.tabulaw.cassandra.om.factory.ColumnFamilyDescriptor;
import com.tabulaw.cassandra.om.factory.SessionFactory;
import com.tabulaw.cassandra.om.factory.SessionImpl;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;
import com.tabulaw.cassandra.om.factory.lazy.BagList;


public class OneToManyRelation implements Relation {

	private OneToMany annotation;
	private ColumnDescriptor descriptor;
	private ColumnFamilyDescriptor cfDescriptor;
	private ColumnFamilyDescriptor relationCf;
	
	public OneToManyRelation(ColumnFamilyDescriptor cfDescriptor, ColumnFamilyDescriptor relationCf, 
			ColumnDescriptor descriptor, OneToMany annotation) {
		this.descriptor = descriptor;
		this.relationCf = relationCf;
		this.cfDescriptor = cfDescriptor;
		this.annotation = annotation;
	}
	
	public boolean isReadonly() {
		return annotation.readonly() || ! "".equals(annotation.mappedBy());
	}
	
	@Override
	public void clone(Object object, Object toClone) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(SessionImpl session, Object object, Row<String, String> row) {
		Object key = cfDescriptor.getKey(object);
		BagList list = new BagList(session, key, this);
		descriptor.setValue(object, list);
	}

	@Override
	public void mutate(SessionImpl session, Mutator mutator, Object object, Object existent) {
		if (isReadonly()) {
			return;
		}
		Object list = descriptor.getValue(object);
		if (! (list instanceof BagList<?>)) {
			throw new HelenaException("Try to store not attached bag");			
		}
		BagList<?> bagList = (BagList<?>) list;
		String key = TypeConverter.INSTANCE.asString(cfDescriptor.getKey(object));
		StringExtractor se = StringExtractor.get();
		for (Object obj : bagList.getRemovedKeys()) {
			String relationKey = TypeConverter.INSTANCE.asString(obj);
			mutator.addDeletion(key, annotation.columnFamily().columnFamily(), bagList.getInitialKeys().get(obj), se);
			mutator.addDeletion(relationKey, relationCf.getName(), annotation.inverseColumn(), se);
		}
		for (Object obj : bagList.getAddedKeys()) {
			String relationKey = TypeConverter.INSTANCE.asString(obj);
			HColumn<String, String> column = HFactory.createColumn(annotation.inverseColumn(), key, se, se);
			HColumn<String, String> joinColumn = HFactory.createColumn(
					annotation.columnFamily().byValue() ?  ("" + System.currentTimeMillis()) : relationKey, relationKey, se, se);
			
			mutator.addInsertion(relationKey, relationCf.getName(), column);
			mutator.addInsertion(key, annotation.columnFamily().columnFamily(), joinColumn);
		}
				
	}
	
	public Map<Object, String> loadList(SessionImpl session, Object key) {
		SessionFactory factory = session.getFactory();
		KeyspaceOperator operator = factory.createKeyspaceOperator();
		StringExtractor se = StringExtractor.get();
		String keyStr = TypeConverter.INSTANCE.asString(key);
		
		SliceQuery<String, String> query = HFactory.createSliceQuery(operator, se, se);
		query.setColumnFamily(annotation.columnFamily().columnFamily());
		query.setKey(keyStr);
		query.setRange("", "", false, Integer.MAX_VALUE);
		ColumnSlice<String, String> result = query.execute().get();		
		Map<String, String> keys = Maps.newLinkedHashMap();
		for (HColumn<String, String> column : result.getColumns()) {
			keys.put(annotation.columnFamily().byValue() ? column.getValue() : column.getName(), column.getName());			
		}
		Object[] keysArr = keys.keySet().toArray();
		List<?> entities = session.find(relationCf.getColumnFamilyType(), keysArr);
		Map<Object, String> entitiesMap = Maps.newLinkedHashMap();
		for (Object entity : entities) {
			String relationKey = keys.get(relationCf.getKey(entity));
			entitiesMap.put(entity, relationKey);
		}
		return entitiesMap;
	}	
	
	public ColumnFamilyDescriptor getRelationCF() {
		return relationCf;
	}
}
