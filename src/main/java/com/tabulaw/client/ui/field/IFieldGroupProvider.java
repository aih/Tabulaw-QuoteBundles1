/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Jan 3, 2009
 */
package com.tabulaw.client.ui.field;

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
