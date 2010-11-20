package com.tabulaw.cassandra.om.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD } )
public @interface ManyToOne {
	
	String mappedBy() default "";
	
	boolean readonly() default false;
	
	JoinColumnFamily inverseColumnFamily() default @JoinColumnFamily(columnFamily = "");
	
	String column() default "";
}
