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
 * BusinessObject - Defines one or more business keys for a particular domain
 * object.
 * @author jpk
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessObject {

	/**
	 * @return All defined business keys for this business object.
	 */
	BusinessKeyDef[] businessKeys();
}
