/**
 * The Logic Lab
 * @author jpk
 * @since Sep 27, 2009
 */
package com.tabulaw.client.ui.field;


/**
 * IHasFieldGroup
 * @author jpk
 */
public interface IHasFieldGroup extends IFieldGroupProvider {

	/**
	 * Sets the field group.
	 * @param group the field group to set
	 */
	void setFieldGroup(FieldGroup group);
}
