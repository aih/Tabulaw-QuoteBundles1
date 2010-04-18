/**
 * The Logic Lab
 * @author jpk
 * @since Mar 13, 2010
 */
package com.tll.client.convert;

/**
 * Indicates {@link IConverter} get/set aware.
 * @param <I> input type
 * @param <O> output type
 * @author jpk
 */
public interface IHasConverter<I, O> {

	IConverter<O, I> getConverter();

	void setConverter(IConverter<O, I> converter);
}
