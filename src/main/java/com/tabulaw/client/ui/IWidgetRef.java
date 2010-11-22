/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Mar 3, 2009
 */
package com.tabulaw.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.IDescriptorProvider;

/**
 * IWidgetRef - Abstraction for "pointing" to a {@link Widget} and having the
 * ability to provide a textual description of that widget.
 * @author jpk
 */
public interface IWidgetRef extends IDescriptorProvider, IWidgetProvider {

}
