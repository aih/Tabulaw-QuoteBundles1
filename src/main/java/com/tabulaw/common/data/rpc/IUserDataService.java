/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.UserState;

/**
 * Contract to persisting user related data to/from client.
 * @author jpk
 */
@RemoteServiceRelativePath(value = "userData")
public interface IUserDataService extends RemoteService {
	
	/**
	 * Persists the given user state.
	 * @param userState
	 */
	void saveUserState(UserState userState);

	/**
	 * Fetches a chunk of ids categorized by entity type for use in creating new
	 * entitites client-side and subsequently persisting them.
	 * @return a chunk of assignable categorized ids by entity type
	 */
	IdsPayload fetchIdBatch();

	/**
	 * Saves the quote bundle for the given user.
	 * @param userId
	 * @param bundle
	 * @return payload containing the persisted bundle
	 */
	ModelPayload saveBundleForUser(String userId, QuoteBundle bundle);

	/**
	 * Adds a bundle for the given user.
	 * @param userId
	 * @param bundle
	 * @return payload containing the persisted bundle
	 */
	ModelPayload addBundleForUser(String userId, QuoteBundle bundle);

	/**
	 * Deletes a bundle for the given user.
	 * @param userId
	 * @param bundleId
	 * @return payload containing the status of the operation
	 */
	Payload deleteBundleForUser(String userId, String bundleId);

	/**
	 * Adds a quote to the bundle
	 * @param bundleId id of the bundle to which the quote will be added
	 * @param quote quote to add
	 * @return the persisted quote
	 */
	ModelPayload addQuoteToBundle(String bundleId, Quote quote);

	/**
	 * @param bundleId id of the bundle containing the quote to remove.
	 * @param quoteId id of the quote to remove
	 * @param deleteQuote Permanantly delete the quote as well?
	 * @return the status of the removal
	 */
	Payload removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote);
}