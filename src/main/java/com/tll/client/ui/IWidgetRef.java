/**
 * The Logic Lab
 * @author jpk
 * Mar 3, 2009
 */
package com.tll.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.tll.IDescriptorProvider;

/**
 * IWidgetRef - Abstraction for "pointing" to a {@link Widget} and having the
 * ability to provide a textual description of that widget.
 * @author jpk
 */
public interface IWidgetRef extends IDescriptorProvider, IWidgetProvider {

}
