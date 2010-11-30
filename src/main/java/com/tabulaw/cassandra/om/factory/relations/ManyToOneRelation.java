package com.tabulaw.cassandra.om.factory.relations;

import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.Mutator;
import me.prettyprint.cassandra.model.Row;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.factory.HFactory;


import com.google.common.base.Objects;
import com.tabulaw.cassandra.om.TypeConverter;
import com.tabulaw.cassandra.om.annotations.ManyToOne;
import com.tabulaw.cassandra.om.factory.ColumnDescriptor;
import com.tabulaw.cassandra.om.factory.ColumnFamilyDescriptor;
import com.tabulaw.cassandra.om.factory.SessionImpl;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;


public class ManyToOneRelation implements Relation {

	final ManyToOne annotation;
	final ColumnFamilyDescriptor cfDescriptor;
	final ColumnDescriptor descriptor;
	final ColumnFamilyDescriptor relationCF;
		
	public ManyToOneRelation(ColumnFamilyDescriptor cfDescriptor, ColumnFamilyDescriptor relationCF, 
			ColumnDescriptor descriptor, ManyToOne annotation) {
		this.descriptor = descriptor;
		this.relationCF = relationCF;
		this.cfDescriptor = cfDescriptor;
		this.annotation = annotation;
	}
	
	@Override
	public void load(SessionImpl session, Object object, Row<String, byte[]> row) {
		HColumn<String, byte[]> column = row.getColumnSlice().getColumnByName(descriptor.getName());
		if (column != null && column.getValue() != null) {
			String relKey = (String) TypeConverter.INSTANCE.asObject(String.class, column.getValue());
			session.loadRelation(this, relKey, object);
		}
	}
	
	@Override
	public void mutate(SessionImpl session, Mutator mutator, Object object, Object existent) {
		if (annotation.readonly() || ! "".equals(annotation.mappedBy())) {
			return;
		}
		Object newRel = descriptor.getValue(object);		
		if (newRel != null && ! session.isAttached(newRel)) {
			throw new HelenaException("not attached object in entity relation");
		}
		Object newKey = relationCF.getKey(newRel);
		Object oldKey = relationCF.getKey(descriptor.getValue(existent));
		StringSerializer se = StringSerializer.get();
		if (! Objects.equal(newKey, oldKey)) {
			String key = TypeConverter.INSTANCE.asString(cfDescriptor.getKey(object));
			String keyRel = TypeConverter.INSTANCE.asString(newKey);
			if (oldKey != null) {
				String oldKeyRel = TypeConverter.INSTANCE.asString(oldKey);			
				mutator.addDeletion(oldKeyRel, annotation.inverseColumnFamily().columnFamily(), key, se);
			}
			if (newKey != null) {
				mutator.addInsertion(key, cfDescriptor.getName(), HFactory.createColumn(descriptor.getName(), keyRel, se, se));
				mutator.addInsertion(keyRel, annotation.inverseColumnFamily().columnFamily(), HFactory.createColumn(key, key, se, se));
			} else {
				mutator.addDeletion(key, cfDescriptor.getName(), descriptor.getName(), se);
			}
		}
	}
	
	@Override
	public void clone(Object object, Object toClone) {
		descriptor.setValue(toClone, descriptor.getValue(object));
	}

	public void setValue(Object entity, Object relation) {
		descriptor.setValue(entity, relation);
	}
	
	public ColumnFamilyDescriptor getRelationCF() {
		return relationCF;
	}	
}
