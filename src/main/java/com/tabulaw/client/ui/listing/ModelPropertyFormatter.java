/**
 * The Logic Lab
 * @author jpk Jan 19, 2008
 */
package com.tabulaw.client.ui.listing;

import com.tll.client.util.Fmt;
import com.tll.client.util.GlobalFormat;
import com.tll.common.model.IPropertyValue;
import com.tll.common.model.ISelfFormattingPropertyValue;
import com.tll.common.model.Model;
import com.tll.common.model.NullNodeInPropPathException;
import com.tll.common.model.PropertyPathException;
import com.tll.common.model.UnsetPropertyException;

/**
 * ModelPropertyFormatter - Helper for use by {@link ITableCellRenderer}
 * implementations.
 * @author jpk
 */
public abstract class ModelPropertyFormatter {

	/**
	 * Extracts a model property value, optionally formats it then returns it.
	 * @param model the model
	 * @param propName the model property name to extract
	 * @param format the optional format
	 * @return The formatted value of the model property or <code>null</code>
	 *         when: <br>
	 *         <ol>
	 *         <li>the model property is unset
	 *         <li>the model property is un-reachable due to a parent node in the
	 *         prop path not being present in the given model tree.
	 *         </ol>
	 * @throws IllegalStateException When the given property name is malformed.
	 */
	public static String pformat(Model model, String propName, GlobalFormat format) {
		if(model == null) return null;
		try {
			// resolve the property
			final IPropertyValue pv = model.getValueProperty(propName);

			// self formatting type?
			if(pv.getType().isSelfFormatting()) {
				return ((ISelfFormattingPropertyValue) pv).asString();
			}

			// format the value..
			return Fmt.format(pv.getValue(), format);
		}
		catch(final UnsetPropertyException e) {
			return null;
		}
		catch(final NullNodeInPropPathException e) {
			return null;
		}
		catch(final PropertyPathException e) {
			throw new IllegalStateException(e);
		}
	}
}
