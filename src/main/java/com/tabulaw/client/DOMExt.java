/**
 * The Logic Lab
 * @author jpk Dec 30, 2007
 */
package com.tabulaw.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * DOMExt - "extra" DOM methods
 * @author jpk
 */
public abstract class DOMExt {

	/**
	 * Determines if the given element is hidden by virtue of itself OR one of its
	 * ancestors.
	 * <p>
	 * This is handy when considering such widgets as a {@link TabPanel} or
	 * {@link DisclosurePanel}.
	 * @param elem
	 * @return true/false
	 */
	public static native boolean isCloaked(Element elem)/*-{
	    var visible = false;
	    var curr = elem;
	    while ( curr && (curr.nodeType == 1) && (visible = (curr.style.display != 'none')) ) {
	      curr = curr.parentNode;
	    }
	    return !visible;
		}-*/;
}
