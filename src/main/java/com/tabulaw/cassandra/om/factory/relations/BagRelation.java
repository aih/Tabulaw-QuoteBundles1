package com.tabulaw.cassandra.om.factory.relations;

import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.extractors.StringExtractor;
import me.prettyprint.cassandra.model.ColumnSlice;
import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.SliceQuery;

import com.google.common.collect.Maps;
import com.tabulaw.cassandra.om.TypeConverter;
import com.tabulaw.cassandra.om.factory.ColumnDescriptor;
import com.tabulaw.cassandra.om.factory.ColumnFamilyDescriptor;
import com.tabulaw.cassandra.om.factory.SessionFactory;
import com.tabulaw.cassandra.om.factory.SessionImpl;

public abstract class BagRelation implements Relation {

	protected final ColumnFamilyDescriptor cfDescriptor;
	protected final ColumnDescriptor descriptor;
	protected final ColumnFamilyDescriptor relationCf;
	
	protected BagRelation(ColumnFamilyDescriptor cfDescriptor, ColumnFamilyDescriptor relationCf, 
			ColumnDescriptor descriptor) {
		this.descriptor = descriptor;
		this.relationCf = relationCf;
		this.cfDescriptor = cfDescriptor;
	}
	
	public ColumnFamilyDescriptor getRelationCF() {
		return relationCf;
	}
	
	public Map<Object, String> loadList(SessionImpl session, Object key) {
		SessionFactory factory = session.getFactory();
		KeyspaceOperator operator = factory.createKeyspaceOperator();
		StringExtractor se = StringExtractor.get();
		String keyStr = TypeConverter.INSTANCE.asString(key);
		
		SliceQuery<String, String> query = HFactory.createSliceQuery(operator, se, se);
		query.setColumnFamily(getJoinColumnFamily());
		query.setKey(keyStr);
		query.setRange("", "", false, Integer.MAX_VALUE);
		ColumnSlice<String, String> result = query.execute().get();		
		Map<String, String> keys = Maps.newLinkedHashMap();
		for (HColumn<String, String> column : result.getColumns()) {
			keys.put(isByValue() ? column.getValue() : column.getName(), column.getName());			
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
	
	protected abstract String getJoinColumnFamily();
	
	protected abstract boolean isByValue();
	
	public abstract boolean isReadonly();			
}
