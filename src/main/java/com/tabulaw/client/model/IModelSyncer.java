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
	
	void updateBundleProps(QuoteBundle bundle);

	void addBundle(QuoteBundle bundle);

	void deleteBundle(String bundleId, boolean deleteQuotes);

	void addQuoteToBundle(String bundleId, Quote quote);

	void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote);

	void addBundleUserBinding(String bundleId);

	void removeBundleUserBinding(String bundleId);

	void addDocUserBinding(String docId);

	void removeDocUserBinding(String docId);
}
