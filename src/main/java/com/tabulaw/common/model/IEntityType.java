/**
 * The Logic Lab
 * @author jpk
 * Feb 10, 2009
 */
package com.tabulaw.common.model;

import com.tll.IDescriptorProvider;
import com.tll.IMarshalable;


/**
 * IEntityType - Generic way of identifying a particular entity type capable of
 * being resolved to a single {@link Class} that identifies the same entity
 * type.
 * @author jpk
 */
public interface IEntityType extends IMarshalable, IDescriptorProvider {

}
