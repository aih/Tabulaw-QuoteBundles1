/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.UserState;

/**
 * Contract to persisting user related data to/from client.
 * @author jpk
 */
@RemoteServiceRelativePath(value = "userData")
public interface IUserDataService extends RemoteService {

	/**
	 * Fetches a chunk of ids categorized by entity type for use in creating new
	 * entitites client-side and subsequently persisting them.
	 * @return a chunk of assignable categorized ids by entity type
	 */
	IdsPayload fetchIdBatch();

	/**
	 * Persists the given user state.
	 * @param userState
	 */
	void saveUserState(UserState userState);

	/**
	 * Saves the quote bundle for the given user.
	 * @param userId
	 * @param bundle
	 * @return payload containing the persisted bundle
	 */
	ModelPayload<QuoteBundle> saveBundleForUser(String userId, QuoteBundle bundle);

	/**
	 * Updates the non-relational bundle properties.
	 * @param userId
	 * @param bundle
	 * @return payload containing the resultant status of the update op
	 */
	Payload updateBundlePropsForUser(String userId, QuoteBundle bundle);

	/**
	 * Deletes the binding between a user and a bundle.
	 * @param userId
	 * @param bundleId
	 * @return payload containing the resultant status of the op
	 */
	Payload addBundleUserBinding(String userId, String bundleId);

	/**
	 * Removes the binding between a user and a bundle.
	 * @param userId
	 * @param bundleId
	 * @return payload containing the resultant status of the op
	 */
	Payload removeBundleUserBinding(String userId, String bundleId);

	/**
	 * Creates a binding between a user and a document.
	 * @param userId
	 * @param docId
	 * @return payload containing the resultant status of the op
	 */
	Payload addDocUserBinding(String userId, String docId);

	/**
	 * Deletes the binding between a user and a document.
	 * @param userId
	 * @param docId
	 * @return payload containing the resultant status of the op
	 */
	Payload removeDocUserBinding(String userId, String docId);

	/**
	 * Adds a bundle for the given user.
	 * @param userId
	 * @param bundle
	 * @return payload containing the persisted bundle
	 */
	ModelPayload<QuoteBundle> addBundleForUser(String userId, QuoteBundle bundle);

	/**
	 * Deletes a bundle for the given user.
	 * <p>
	 * This is an admin related operation.
	 * @param userId
	 * @param bundleId
	 * @param deleteQuotes delete contained qoutes or move them the un-assigned
	 *        quotes bundle?
	 * @return payload containing the status of the operation
	 */
	Payload deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes);

	/**
	 * Adds a quote to the bundle
	 * @param userId the user for which the added quote is bound
	 * @param bundleId id of the bundle to which the quote will be added
	 * @param quote quote to add
	 * @return the persisted quote
	 */
	ModelPayload<Quote> addQuoteToBundle(String userId, String bundleId, Quote quote);

	/**
	 * Permanantly deletes the given quote for the given user (from any and all
	 * bundles containing it).
	 * @param userId
	 * @param quoteId
	 * @return the status of the deletion
	 */
	Payload deleteQuote(String userId, String quoteId);

	/**
	 * Moves an existing quote from an existing source bundle to an existing
	 * target bundle.
	 * @param userId
	 * @param quoteId id of the quote to move
	 * @param sourceBundleId id of the bundle currently containing the quote
	 * @param targetBundleId id of the bundle to which to move the quote
	 * @return the status of the removal
	 */
	Payload moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId);

	/**
	 * Gets the docs associated with a particular user.
	 * @param userId id of the user for which to get docs
	 * @return doc listing payload
	 */
	DocListingPayload getDocsForUser(String userId);

	/**
	 * Requires user administrator priviliges.
	 * @return All docs in the system w/out html content.
	 */
	DocListingPayload getAllDocs();
	
	/**
	 * Fetches both the doc ref and content for a doc.
	 * @param docId id of doc to fetch
	 * @return the doc payload
	 */
	DocPayload getDoc(String docId);

	/**
	 * Removes a doc from the system.
	 * @param docId id of the doc to delete
	 * @return resultant status wrapped in a payload
	 */
	Payload deleteDoc(String docId);

	/**
	 * Creates a new doc on the server given a new doc entity with all required
	 * properties set save for the doc hash which is filled in.
	 * @param docRef new doc ref
	 * @param htmlContent optional html doc content
	 * @return the created doc wrapped in a doc payload
	 */
	DocPayload createDoc(DocRef docRef, String htmlContent);

	/**
	 * Updates the contents of an existing document.
	 * @param docId doc id 
	 * @param htmlContent the replacing html content for the doc
	 * @return the resultant status wrapped in a payload
	 */
	Payload updateDocContent(String docId, String htmlContent);

	/**
	 * Converts the doc identified by the given id to MS Word format emailing it
	 * to the given user.
	 * @param docId id of the doc to export
	 * @param userId id of the user to which the exported doc is emailed
	 * @return the resultant status of the export op wrapped in a payload
	 */
	//Payload exportDoc(String docId, String userId);
}
