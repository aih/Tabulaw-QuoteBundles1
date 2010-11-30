package com.tabulaw.cassandra.om.factory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tabulaw.cassandra.om.annotations.HelenaBean;
import com.tabulaw.cassandra.om.annotations.HelenaSuperclass;
import com.tabulaw.cassandra.om.annotations.KeyProperty;
import com.tabulaw.cassandra.om.annotations.ManyToOne;
import com.tabulaw.cassandra.om.annotations.OneToMany;
import com.tabulaw.cassandra.om.annotations.Transient;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;
import com.tabulaw.cassandra.om.factory.relations.ManyToOneRelation;
import com.tabulaw.cassandra.om.factory.relations.OneToManyRelation;

public class ColumnFamiliesBuilder {
	
	Map<Class<?>, List<Class<?>>> inheritance = Maps.newHashMap();
	Set<Class<?>> classesSet;
	Map<Class<?>, ColumnFamilyDescriptor> cfDescriptors = Maps.newHashMap();
	
	public ColumnFamiliesBuilder(Class<?>... classes) throws IntrospectionException {
		classesSet = excludeBadClasses(classes);
		processInheritance(classesSet);
		
		List<Class<?>> firstLevel = Lists.newArrayList();
		for (Class<?> klass : classesSet) {
			Class<?> superClass = klass.getSuperclass();
			if (! classesSet.contains(superClass)) {
				createColumnFamilyDescriptor(klass, null).startProcess();
				firstLevel.add(klass);
			}
		}
		for (Class<?> klass : firstLevel) {
			processColumnsForBeans(klass);
		}
	}
	
	private Set<Class<?>> excludeBadClasses(Class<?>[] classes) {
		Set<Class<?>> beans = Sets.newHashSet();
		for (Class<?> klass : classes) {
			boolean superClass = klass.isAnnotationPresent(HelenaSuperclass.class);
			boolean bean = klass.isAnnotationPresent(HelenaBean.class);
			if (superClass && bean) {
				// TODO: throw exception, two annotations
			}
			if (superClass || bean) {
				beans.add(klass);
			}
		}
		return beans;
	}
	
	private void processInheritance(Set<Class<?>> classesSet) {
		for (Class<?> clazz : classesSet) {
			Class<?> superClass = clazz.getSuperclass();
			if (classesSet.contains(superClass)) {
				if (! inheritance.containsKey(superClass)) {
					inheritance.put(superClass, new ArrayList<Class<?>>());
				}
				inheritance.get(superClass).add(clazz);
			}
		}
	}
	
	private ColumnFamilyDescriptor createColumnFamilyDescriptor(Class<?> klass, ColumnFamilyDescriptor superCF) {
		ColumnFamilyDescriptor cf = new ColumnFamilyDescriptor(klass, superCF);
		cfDescriptors.put(klass, cf);
		if (inheritance.containsKey(klass)) {
			for (Class<?> child : inheritance.get(klass)) {
				createColumnFamilyDescriptor(child, cf);
			}
		}
		return cf;
	}	
	
	private void processColumnsForBeans(Class<?> klass) {
		ColumnFamilyDescriptor columnFamily = cfDescriptors.get(klass); 		
		Map<String, PropertyDescriptor> properties;
		try {
			properties = getClassProperties(klass);
		} catch (IntrospectionException ex) {
			throw new HelenaException(ex);
		}
		for (Entry<String, PropertyDescriptor> entry : properties.entrySet()) {
			AccessibleObject accessibleObject = getAnnotatedProperty(klass, entry);
			if (accessibleObject.isAnnotationPresent(Transient.class)) {
				continue;
			}
			ColumnDescriptor descriptor = new ColumnDescriptor(entry.getKey(), entry.getValue(), accessibleObject);
			if (accessibleObject.isAnnotationPresent(ManyToOne.class)) {
				ManyToOneRelation relation = processManyToOne(columnFamily, descriptor); 
				columnFamily.addRelation(descriptor.getName(), relation);				
				continue;
			}
			if (accessibleObject.isAnnotationPresent(OneToMany.class)) {
				OneToManyRelation relation = processOneToMany(columnFamily, descriptor); 
				columnFamily.addRelation(descriptor.getName(), relation);				
				continue;
			}
			if (accessibleObject.isAnnotationPresent(KeyProperty.class)) {
				columnFamily.setKeyColumn(descriptor);
				continue;
			}
			columnFamily.addSimpleColumn(descriptor.getName(), descriptor);
		}
		if (inheritance.containsKey(klass)) {
			for (Class<?> child : inheritance.get(klass)) {
				cfDescriptors.get(child).startProcess();
			}
			for (Class<?> child : inheritance.get(klass)) {
				processColumnsForBeans(child);
			}
		}
		columnFamily.stabilize();
		cfDescriptors.put(klass, columnFamily);
	}
	
	private OneToManyRelation processOneToMany(ColumnFamilyDescriptor cf, ColumnDescriptor descriptor) {
		Type returnType;
		AccessibleObject accessibleObject = descriptor.getAnnotatedObject();
		if (accessibleObject instanceof Field) {
			returnType = ((Field) accessibleObject).getGenericType();
		} else {
			returnType = ((Method) accessibleObject).getGenericReturnType();
		}
		Class<?> relationEntity;
		if (returnType instanceof ParameterizedType) {
			ParameterizedType parametrized = (ParameterizedType) returnType;
			if (! ((Class<?>) parametrized.getRawType()).isAssignableFrom(List.class)) {
				throw new HelenaException(cf.getColumnFamilyType().getName() + "." + descriptor.getName() +
						" must be assignable from List");
			}
			relationEntity = (Class<?>) parametrized.getActualTypeArguments()[0]; 
		} else {
			throw new HelenaException(cf.getColumnFamilyType().getName() + "." + descriptor.getName() + 
					" must be generic collection");
		}		
		if (! classesSet.contains(relationEntity)) {
			throw new IllegalStateException(cf.getColumnFamilyType().getName() + "." + descriptor.getName() + 
					" bad mapping because "	+ relationEntity.getName() + " isn't included as mapped class");
		}
		OneToMany annotation = accessibleObject.getAnnotation(OneToMany.class);
		return new OneToManyRelation(cf, cfDescriptors.get(relationEntity), descriptor, annotation);
	}
	
	private ManyToOneRelation processManyToOne(ColumnFamilyDescriptor cf, ColumnDescriptor descriptor) {
		Class<?> returnType;
		AccessibleObject accessibleObject = descriptor.getAnnotatedObject();
		if (accessibleObject instanceof Field) {
			returnType = ((Field) accessibleObject).getType();
		} else {
			returnType = ((Method) accessibleObject).getReturnType();
		}
		if (! classesSet.contains(returnType)) {
			throw new IllegalStateException(cf.getColumnFamilyType().getName() + "." + descriptor.getName() + 
					" bad mapping because "	+ returnType.getName() + " isn't included as mapped class");
		}
		ManyToOne annotation = accessibleObject.getAnnotation(ManyToOne.class);
		return new ManyToOneRelation(cf, cfDescriptors.get(returnType), descriptor, annotation);
	}
	
	private AccessibleObject getAnnotatedProperty(Class<?> klass, Entry<String, PropertyDescriptor> property) {
		AccessibleObject accessibleObject = null;
		if (property.getValue() != null) {
			accessibleObject = property.getValue().getReadMethod();
		}
		if (accessibleObject == null ||	accessibleObject.getAnnotations().length == 0) {
			try {
				accessibleObject = klass.getDeclaredField(property.getKey());
			} catch (NoSuchFieldException ex) {
				// ignore
			}
		}
		return accessibleObject;
	}
	
	private Map<String, PropertyDescriptor> getClassProperties(Class<?> klass) throws IntrospectionException  {
		Map<String, PropertyDescriptor> properties = Maps.newHashMap();
		BeanInfo info = Introspector.getBeanInfo(klass);
		for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
			boolean hasWrite = descriptor.getWriteMethod() != null;
			if (! hasWrite) {
				try {
					klass.getField(descriptor.getName());
					hasWrite = true;
				} catch (NoSuchFieldException ex) {
					// ignore
				}
			}
			if (descriptor.getReadMethod() != null && hasWrite) {
				properties.put(descriptor.getName(), descriptor);
			}
		}
		for (Field field : klass.getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (! properties.containsKey(field.getName())) {
				properties.put(field.getName(), null);
			}
		}
		return properties;
	}
	
	public ColumnFamilyDescriptor gtCF(Class<?> klass) {
		return cfDescriptors.get(klass);
	}
	
	public Collection<ColumnFamilyDescriptor> getAllCFs() {
		return cfDescriptors.values();
	}
}
