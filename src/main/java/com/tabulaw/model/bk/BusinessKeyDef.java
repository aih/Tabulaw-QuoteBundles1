/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Jan 19, 2009
 */
package com.tabulaw.model.bk;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BusinessKeyDef
 * @author jpk
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessKeyDef {
	
	/**
	 * @return The presentation worthy name of this business key.
	 */
	String name();

	/**
	 * @return The bean properties that define this business key declaration.
	 */
	String[] properties();
}
