/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.ClauseBundle;
import com.tabulaw.model.ContractDoc;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityBase;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.Reference;
import com.tabulaw.model.UserState;

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
	 * permanently deletes the given quote for the given user (from any and all
	 * bundles containing it).
	 * @param userId
	 * @param quoteId
	 * @return the status of the deletion
	 */
	Payload deleteQuote(String userId, String bundleId, String quoteId);

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
     * adds an association of an existing quote to an existing
     * bundle.
     *
     * @param userId
     * @param quoteId        id of the quote to move
     * @param bundleId id of the bundle to which to add the quote
	 * @return the status of the add operation
     */
	Payload attachQuote(String userId, String quoteId, String bundleId);
	

	/**
	 * Gets the docs associated with a particular user.
	 * @param userId id of the user for which to get docs
	 * @return model list payload
	 */
	ModelListPayload<DocRef> getDocsForUser(String userId);

	/**
	 * Requires user administrator priviliges.
	 * @return All doc refs in the system.
	 */
	ModelListPayload<DocRef> getAllDocs();

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
	 * properties set.
	 * @param docRef new doc ref
	 * @param htmlContent optional html doc content
	 * @return the created doc wrapped in a doc payload
	 */
	DocPayload createDoc(DocRef docRef, String htmlContent);

	/**
	 * Adds new quote to specified bundle and creates appropriate doc   
	 * @param userId
	 * @param title title for doc
	 * @param quoteBundleId quote bundle id
	 * @return the persisted quote
	 */
	ModelListPayload<EntityBase> addOrphanQuote(String userId, String title, Reference reference, String quoteText, String quoteBundleId);
	
	/**
	 * Updates the contents of an existing document.
	 * @param docId doc ref id 
	 * @param htmlContent the replacing html content for the doc
	 * @return the resultant status wrapped in a payload
	 */
	Payload updateDocContent(String docId, String htmlContent);
	
}
