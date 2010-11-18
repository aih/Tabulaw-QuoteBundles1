/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 5, 2010
 */
package com.tabulaw.client.view;

/**
 * Contract for providing view initializer instances.
 * @author jpk
 */
public interface IViewInitializerProvider {

	/**
	 * @return The view initializer.
	 */
	IViewInitializer getViewInitializer();
}
