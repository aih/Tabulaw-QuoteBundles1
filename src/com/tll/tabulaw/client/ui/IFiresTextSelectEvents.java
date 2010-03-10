/**
 * The Logic Lab
 * @author jpk
 * @since Feb 24, 2010
 */
package com.tll.tabulaw.client.ui;

/**
 * IFiresTextSelectEvents
 * @author jpk
 */
public interface IFiresTextSelectEvents {

	/**
	 * Adds a text select handler.
	 * @param handler the handler to add
	 */
	void addTextSelectHandler(ITextSelectHandler handler);

	/**
	 * Removes the text change handler.
	 * @param handler the handler to remove
	 */
	void removeTextSelectHandler(ITextSelectHandler handler);
}
