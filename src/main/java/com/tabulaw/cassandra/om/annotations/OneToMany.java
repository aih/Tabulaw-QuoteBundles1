package com.tabulaw.cassandra.om.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD } )
public @interface OneToMany {	
	
	String mappedBy() default "";
	
	boolean readonly() default false;
	
	JoinColumnFamily columnFamily() default @JoinColumnFamily(columnFamily = "");
	
	String inverseColumn() default "";
}
