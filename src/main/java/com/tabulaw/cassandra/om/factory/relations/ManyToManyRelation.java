package com.tabulaw.cassandra.om.factory.relations;

import me.prettyprint.cassandra.extractors.StringExtractor;
import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.Mutator;
import me.prettyprint.cassandra.model.Row;

import com.tabulaw.cassandra.om.TypeConverter;
import com.tabulaw.cassandra.om.annotations.ManyToMany;
import com.tabulaw.cassandra.om.factory.ColumnDescriptor;
import com.tabulaw.cassandra.om.factory.ColumnFamilyDescriptor;
import com.tabulaw.cassandra.om.factory.SessionImpl;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;
import com.tabulaw.cassandra.om.factory.lazy.BagList;

public class ManyToManyRelation extends BagRelation {

	private ManyToMany annotation;
	
	public ManyToManyRelation(ColumnFamilyDescriptor cfDescriptor, ColumnFamilyDescriptor relationCf, 
			ColumnDescriptor descriptor, ManyToMany annotation) {
		super(cfDescriptor, relationCf, descriptor);
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
			mutator.addDeletion(relationKey, annotation.inverseColumnFamily().columnFamily(), key, se);
		}
		for (Object obj : bagList.getAddedKeys()) {
			String relationKey = TypeConverter.INSTANCE.asString(obj);
			HColumn<String, String> column = HFactory.createColumn(
					annotation.inverseColumnFamily().byValue() ?  ("" + System.currentTimeMillis()) : key, key, se, se);
			HColumn<String, String> joinColumn = HFactory.createColumn(
					annotation.columnFamily().byValue() ?  ("" + System.currentTimeMillis()) : relationKey, relationKey, se, se);
			
			mutator.addInsertion(relationKey, annotation.inverseColumnFamily().columnFamily(), column);
			mutator.addInsertion(key, annotation.columnFamily().columnFamily(), joinColumn);
		}
				
	}
	
	public ColumnFamilyDescriptor getRelationCF() {
		return relationCf;
	}

	@Override
	protected String getJoinColumnFamily() {
		return annotation.columnFamily().columnFamily();
	}

	@Override
	protected boolean isByValue() {
		return annotation.columnFamily().byValue();
	}
}
