/**
 * The Logic Lab
 * @author jpk
 * @since Apr 24, 2010
 */
package com.tabulaw.client.model;

import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Contract for syncing client model data to server.
 * <p>
 * These methods are tightly coupled to those methods defined in
 * <code>IUserDataService</code>.
 * <p>
 * The user is the currently logged in user.
 * @author jpk
 */
public interface IModelSyncer {

	void saveBundle(QuoteBundle bundle);

	void addBundle(QuoteBundle bundle);

	void deleteBundle(String bundleId);

	void addQuoteToBundle(String bundleId, Quote quote);

	void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote);

}
