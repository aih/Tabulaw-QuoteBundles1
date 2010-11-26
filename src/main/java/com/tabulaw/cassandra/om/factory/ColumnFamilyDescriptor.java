package com.tabulaw.cassandra.om.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.MultigetSliceQuery;
import me.prettyprint.cassandra.model.Mutator;
import me.prettyprint.cassandra.model.Row;
import me.prettyprint.cassandra.model.Rows;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.apache.cassandra.thrift.ColumnPath;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tabulaw.cassandra.om.TypeConverter;
import com.tabulaw.cassandra.om.annotations.DiscriminatorColumn;
import com.tabulaw.cassandra.om.annotations.DiscriminatorValue;
import com.tabulaw.cassandra.om.annotations.HelenaBean;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;
import com.tabulaw.cassandra.om.factory.relations.Relation;

public class ColumnFamilyDescriptor {

	private Class<?> columnFamilyClass;
	
	private HelenaBean description;
	private Map<String, String> discriminators;
	
	private ColumnDescriptor keyColumn;
	
	private Set<String> columnNames;	
	private Map<String, ColumnDescriptor> columnDescriptors; 	
	private Map<String, Relation> relations;
	
	private ColumnFamilyDescriptor superCF;
	private List<ColumnFamilyDescriptor> childCF;
	
	private boolean isStabilized = false;
	
	public ColumnFamilyDescriptor(Class<?> columnFamilyClass, ColumnFamilyDescriptor superCF) {
		this.columnFamilyClass = columnFamilyClass;
		columnNames = Sets.newHashSet();
		columnDescriptors = Maps.newHashMap();
		relations = Maps.newHashMap();
		this.superCF = superCF;
	}
	
	private Map<String, String> getDiscriminators() {
		if (discriminators == null) {
			discriminators = Maps.newHashMap();
		}
		return discriminators;
	}
	
	private void extend(ColumnFamilyDescriptor superCF) {
		if (isStabilized) {
			throw new IllegalStateException("Stabilized CF");
		}
		if (superCF == null) {
			return;
		}
		if (superCF.childCF == null) {
			superCF.childCF = Lists.newArrayList();
		}
		superCF.childCF.add(this);
		keyColumn = superCF.keyColumn;
		columnNames.addAll(superCF.columnNames);
		columnDescriptors.putAll(superCF.columnDescriptors);
		relations.putAll(superCF.relations);
		if (superCF.discriminators != null) {
			getDiscriminators().putAll(superCF.getDiscriminators());
		}
	}	
	
	void startProcess() {
		if (columnFamilyClass.isAnnotationPresent(HelenaBean.class)) {
			description = columnFamilyClass.getAnnotation(HelenaBean.class);
		}
		extend(superCF);
		if (columnFamilyClass.isAnnotationPresent(DiscriminatorColumn.class)) {
			DiscriminatorColumn discriminator = columnFamilyClass.getAnnotation(DiscriminatorColumn.class); 
			getDiscriminators().put(discriminator.value(), null);
			columnNames.add(discriminator.value());
		}
		if (columnFamilyClass.isAnnotationPresent(DiscriminatorValue.class)) {
			DiscriminatorValue discValue = columnFamilyClass.getAnnotation(DiscriminatorValue.class);
			boolean ok = false;
			for (String key : getDiscriminators().keySet()) {
				if (discriminators.get(key) == null) {
					discriminators.put(key, discValue.value());
					ok = true;
				}
			}
			if (! ok) {
				throw new IllegalStateException("DiscriminatorValue is specified without DiscriminatorColumn in "
						 + columnFamilyClass.getName());
			}
		}
	}
	
	void addRelation(String column, Relation relation) {
		if (isStabilized) {
			throw new IllegalStateException("Stabilized CF");
		}
		columnNames.add(column);
		relations.put(column, relation);
		if (superCF != null) {
			superCF.addRelation(column, relation);
		}
	}
	
	void addSimpleColumn(String column, ColumnDescriptor descriptor) {
		if (isStabilized) {
			throw new IllegalStateException("Stabilized CF");
		}
		columnNames.add(column);
		columnDescriptors.put(column, descriptor);
		if (superCF != null) {
			superCF.addSimpleColumn(column, descriptor);
		}
	}
	
	void setKeyColumn(ColumnDescriptor keyDescriptor) {		
		if (isStabilized) {
			throw new IllegalStateException("Stabilized CF");
		}
		keyColumn = keyDescriptor;
		if (superCF != null) {
			superCF.setKeyColumn(keyDescriptor);
		}
	}
	
	void stabilize() {
		if (isStabilized) {
			throw new IllegalStateException("Stabilized CF");
		}
		if (discriminators != null) {
			for (String discriminator : discriminators.keySet()) {
				if (discriminators.get(discriminator) == null) {
					discriminators.remove(discriminator);
				}
			}
			discriminators = ImmutableMap.copyOf(discriminators);
		}
		columnNames = ImmutableSet.copyOf(columnNames);
		columnDescriptors = ImmutableMap.copyOf(columnDescriptors);
		relations = ImmutableMap.copyOf(relations);
		if (childCF != null) {
			childCF = ImmutableList.copyOf(childCF);
		}
		isStabilized = true;
	}
	
	public HelenaBean getDescription() {
		return description;
	}	
	
	public String getName() {
		return description.columnFamily();
	}
	
	public Object getKey(Object object) {
		return keyColumn.getValue(object);
	}
	
	public Class<?> getColumnFamilyType() {
		return columnFamilyClass;
	}
	
	public Object clone(Object object, Object toClone) {
		try {
			Object cloned = toClone == null ? columnFamilyClass.newInstance() : toClone;
			keyColumn.setValue(cloned, keyColumn.getValue(object));
			for (ColumnDescriptor column : columnDescriptors.values()) {
				column.setValue(cloned, column.getValue(object));
			}
			for (Relation relation : relations.values()) {
				relation.clone(object, cloned);
			}
			return cloned;
		} catch (InstantiationException ex) {
			throw new HelenaException(ex);
		} catch (IllegalAccessException ex) {
			throw new HelenaException(ex);
		}
	}
	
	public void mutate(SessionImpl session, Mutator mutator, Object object, Object existent) {
		String key = TypeConverter.INSTANCE.asString(keyColumn.getValue(object));
		String columnFamily = getDescription().columnFamily();
		StringSerializer se = StringSerializer.get();		
		if (existent == null && discriminators != null) {
			for (Entry<String, String> discriminator : discriminators.entrySet()) {
				if (discriminator.getValue() == null) {
					throw new IllegalStateException("You can't save object without all discriminators");
				}
				mutator.addInsertion(key, columnFamily, HFactory.createColumn(
						discriminator.getKey(), 
						discriminator.getValue(), 
						se,
						se
				));
			}
		}
		for (Entry<String, ColumnDescriptor> column : columnDescriptors.entrySet()) {
			Object oldValue = column.getValue().getValue(existent);
			Object newValue = column.getValue().getValue(object);
			if (! Objects.equal(newValue, oldValue)) {
				if (newValue != null) {
					String strValue = TypeConverter.INSTANCE.asString(newValue);
					mutator.addInsertion(key, columnFamily, 
							HFactory.createColumn(column.getKey(), strValue, se, se));
				} else {
					mutator.addDeletion(key, columnFamily, column.getKey(), se);
				}
			}			
		}
		for (Entry<String, Relation> relation : relations.entrySet()) {
			relation.getValue().mutate(session, mutator, object, existent);
		}
	}
	
	public void remove(Keyspace keyspace, Object existent) {
		Object key = keyColumn.getValue(existent);
		String keyStr = TypeConverter.INSTANCE.asString(key);		
		keyspace.remove(keyStr, new ColumnPath(description.columnFamily()));
	}
	
	public List<Object> find(SessionImpl session, KeyspaceOperator operator, Object... keys) {
		if (keys == null || keys.length == 0) {
			return Lists.newArrayList();
		}
		StringSerializer se = StringSerializer.get();	
		MultigetSliceQuery<String, String> query = HFactory.createMultigetSliceQuery(operator, se, se);
		
		String[] keysStr = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			if (! keyColumn.getType().isAssignableFrom(keys[i].getClass())) {
				throw new IllegalArgumentException("Bad key " + keys[i] + " for CF: " + description.columnFamily());
			}
			keysStr[i] = TypeConverter.INSTANCE.asString(keys[i]);
		}
		String[] columnNames = this.columnNames.toArray(new String[this.columnNames.size()]);
		query.setColumnFamily(description.columnFamily());
		query.setKeys(keysStr);
		query.setColumnNames(columnNames);
		
		Rows<String, String> rows = query.execute().get();
		List<Object> result = Lists.newArrayListWithCapacity(rows.getCount());
		for (Row<String, String> row : rows) {
			if (! row.getColumnSlice().getColumns().isEmpty() || columnNames.length == 0) {
				ColumnFamilyDescriptor resultCF = findResultCF(row);
				if (resultCF != null) {
					result.add(resultCF.createObject(session, row));
				}
			}
		}
		return result;
	}
	
	private Object createObject(SessionImpl session, Row<String, String> row) {
		Object object;
		try {
			object = columnFamilyClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} 
		Object key = TypeConverter.INSTANCE.asObject(keyColumn.getType(), row.getKey());
		keyColumn.setValue(object, key);
		for (Entry<String, ColumnDescriptor> column : columnDescriptors.entrySet()) {
			HColumn<String, String> hColumn = row.getColumnSlice().getColumnByName(column.getKey());
			if (hColumn != null) {
				Object value = TypeConverter.INSTANCE.asObject(column.getValue().getType(), hColumn.getValue());
				column.getValue().setValue(object, value);
			}
		}
		for (Relation relation : relations.values()) {
			relation.load(session, object, row);
		}
		return object;
	}
	
	private ColumnFamilyDescriptor findResultCF(Row<String, String> row) {
		if (discriminators == null) {
			return this;
		}
		if (childCF != null) {
			for (ColumnFamilyDescriptor cf : childCF) {
				ColumnFamilyDescriptor childResult = cf.findResultCF(row);
				if (childResult != null) {
					return childResult;
				}
			}
		}
		for (Entry<String, String> discriminator : discriminators.entrySet()) {
			String expected = discriminator.getValue();
			HColumn<String, String> column = row.getColumnSlice().getColumnByName(discriminator.getKey());
			String value = null;
			if (column != null) {
				value = column.getValue();
			}
			if (! Objects.equal(expected, value)) {
				return null;
			}
		}
		return this;
	}
}
