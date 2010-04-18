/**
 * The Logic Lab
 * @author jpk
 * Jan 3, 2009
 */
package com.tll.client.ui.field;

/**
 * IFieldGroupProvider - An indirect way of providing {@link FieldGroup}
 * instances.
 * @author jpk
 */
public interface IFieldGroupProvider {

	/**
	 * @return A field group.
	 */
	FieldGroup getFieldGroup();
}
