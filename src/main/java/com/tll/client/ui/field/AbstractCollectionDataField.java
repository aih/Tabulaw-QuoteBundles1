/**
 * The Logic Lab
 * @author jpk
 * Feb 26, 2009
 */
package com.tll.client.ui.field;

import java.util.Collection;

import com.tll.client.convert.IConverter;

/**
 * AbstractCollectionDataField - Enables a collection of data values to be the
 * value type.
 * @param <V> the data <em>element</em> value type
 * @author jpk
 */
public abstract class AbstractCollectionDataField<V> extends AbstractDataField<V, Collection<V>> {
	
	protected final IConverter<String, V> renderer = new IConverter<String, V>() {

		@Override
		public String convert(V in) throws IllegalArgumentException {
			return getToken(in);
		}
	};

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 */
	public AbstractCollectionDataField(String name, String propName, String labelText, String helpText) {
		super(name, propName, labelText, helpText);
	}
}
