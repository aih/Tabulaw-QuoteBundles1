/**
 * The Logic Lab
 * @author jkirton
 * May 13, 2008
 */
package com.tll.schema;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nested - A non-entity "value object" an entity may have as a persistent
 * property.
 * <p>
 * A common use case for using {@link Nested} is to store sensitive information
 * in a datastore as a blob usually by serializing the nested value object and
 * eccrypting this data.
 * <p>
 * This annotation is method bound where the following conventions are in
 * effect:
 * <ol>
 * <li>The method is assumed to be a property accessor bean method conforming to
 * java beans spec.
 * <li>The resolved property name of this accessor method is used to
 * subsequently resolve properties in the nested object.
 * <li>The return type of the method is taken to be the "nested" value object
 * type.
 * </ol>
 * <p>
 * The
 * @author jpk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nested {
}
