package com.tabulaw.cassandra.om.annotations;

public @interface ManyToMany {
	
	String mappedBy() default "";
	
	boolean readonly() default false;
	
	JoinColumnFamily columnFamily() default @JoinColumnFamily(columnFamily = "");
	
	JoinColumnFamily inverseColumnFamily() default @JoinColumnFamily(columnFamily = "");
}
