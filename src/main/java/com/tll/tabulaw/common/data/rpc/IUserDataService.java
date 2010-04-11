/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tll.common.data.ModelPayload;
import com.tll.common.data.Payload;
import com.tll.common.model.Model;

/**
 * Contract to persisting user related data to/from client.
 * @author jpk
 */
@RemoteServiceRelativePath(value = "userData")
public interface IUserDataService extends RemoteService {
	
	/**
	 * Saves the quote bundle for the given user.
	 * @param userId
	 * @param bundle
	 * @return
	 */
	ModelPayload saveBundleForUser(long userId, Model bundle);
	
	/**
	 * Adds a bundle for the given user.
	 * @param userId 
	 * @param bundle 
	 * @return payload containing the persisted marshaled bundle
	 */
	ModelPayload addBundleForUser(long userId, Model bundle);
	
	/**
	 * Deletes a bundle for the given user.
	 * @param userId
	 * @param bundle
	 * @return payload containing the status of the operation
	 */
	Payload deleteBundleForUser(long userId, Model bundle);

	/**
	 * Adds a quote to the bundle
	 * @param bundleId id of the bundle to which the quote will be added
	 * @param mQuote quote to add
	 * @return the persisted and marshaled quote
	 */
	ModelPayload addQuoteToBundle(long bundleId, Model mQuote);
	
	/**
	 * @param bundleId id of the bundle containing the quote to remove.
	 * @param quoteId id of the quote to remove
	 * @return the status of the removal
	 */
	Payload removeQuoteFromBundle(long bundleId, long quoteId);
	
}
