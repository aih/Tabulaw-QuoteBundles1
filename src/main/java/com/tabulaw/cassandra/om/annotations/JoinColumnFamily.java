package com.tabulaw.cassandra.om.annotations;


public @interface JoinColumnFamily {
	
	String columnFamily();
	
	boolean byValue() default false;
}
