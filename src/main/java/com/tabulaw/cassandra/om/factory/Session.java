package com.tabulaw.cassandra.om.factory;

import java.util.List;


public interface Session {

	boolean isClosed();
	
	<T> boolean isAttached(T object);
	
	<T> T find(Class<T> klass, Object key);
	
	<T> List<T> find(Class<T> klass, Object... keys);
	
	<T> List<T> findAll(Class<T> klass);
	
	<T> List<T> findRange(Class<T> klass, Object lastKey, int limit);
	
	<T> T merge(T object);
	
	<T> void refresh(T object);
	
	<T> void persist(T object);
	
	<T> void remove(T object);
	
	void flush();
	
	void close();
	
	SessionFactory getFactory();
}
