/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.view;

import com.tabulaw.client.view.ViewClass;
import com.tabulaw.client.view.ViewOptions;


/**
 * Base view class for all poc views.
 * @author jpk
 */
public abstract class PocViewClass extends ViewClass {
	
	private static final ViewOptions POC_VIEW_OPTIONS = new ViewOptions(false, false, false, false, false, false);

	@Override
	public ViewOptions getViewOptions() {
		return POC_VIEW_OPTIONS;
	}

	
}
