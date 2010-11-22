/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jkirton
 * May 13, 2008
 */
package com.tabulaw.schema;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Managed - Indicates that an entity property is managed meaning the
 * properties' value is controlled by the data store in which its persisted
 * state resides. This type of info is useful for property requiredness when
 * creating entities.
 * @author jpk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Managed {

}
