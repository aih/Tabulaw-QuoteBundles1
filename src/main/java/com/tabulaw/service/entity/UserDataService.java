/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.service.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import com.google.inject.Inject;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.dao.NonUniqueResultException;
import com.tabulaw.dao.Sorting;
import com.tabulaw.model.BundleUserBinding;
import com.tabulaw.model.ClauseBundle;
import com.tabulaw.model.ContractDoc;
import com.tabulaw.model.ContractDocUserBinding;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.DocUserBinding;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.QuoteUserBinding;
import com.tabulaw.model.Reference;
import com.tabulaw.model.UserState;
import com.tabulaw.service.sanitizer.ISanitizer;

/**
 * Manages the persistence of user related data that is not part of the user
 * entity.
 * @author jpk
 */
public class UserDataService {

	/**
	 * A simple way to provide a list of bundles in addition to conveying which of
	 * them is the orphan qoute container.
	 * @author jpk
	 */
	public static class BundleContainer {

		private final List<QuoteBundle> bundles;
		private final String orphanBundleId;

		public BundleContainer(List<QuoteBundle> bundles, String orphanBundleId) {
			super();
			this.bundles = bundles;
			this.orphanBundleId = orphanBundleId;
		}

		public List<QuoteBundle> getBundles() {
			return bundles;
		}

		/**
		 * @return the id of the bundle in the contained list of bundles that is the
		 *         one designated for holding orphaned quotes.
		 */
		public String getOrphanBundleId() {
			return orphanBundleId;
		}
	}
	
	private ISanitizer sanitizer;
	/**
	 * Constructor
	 * @param dao
	 * @param validationFactory
	 */
	@Inject
	public UserDataService(ValidatorFactory validationFactory, ISanitizer sanitizer) {
		this.sanitizer = sanitizer; 
	}


	/**
	 * Gets a list of all docs for a given user.
	 * @param userId user id
	 * @return list of docs
	 */
	public List<DocRef> getDocsForUser(String userId) {
		return null;
	}

	/**
	 * Gets a list of all contract docs for a given user.
	 * @param userId user id
	 * @return list of docs
	 */
	public List<ContractDoc> getContractDocsForUser(String userId) {
		return null;
	}

	/**
	 * Provides a list of all doc refs in the system.
	 * @return doc list
	 */
	public List<DocRef> getAllDocs() {
		return null;
	}

	/**
	 * Provides a list of all contract doc in the system.
	 * @return doc list
	 */
	public List<ContractDoc> getAllContractDocs() {
		return null;
	}

	/**
	 * Gets the doc ref given the doc id.
	 * @param docId
	 * @return to loaded doc ref
	 * @throws EntityNotFoundException
	 */

	public DocRef getDoc(String docId) throws EntityNotFoundException {
		return null;
	}

	/**
	 * Gets the contract doc given the id.
	 * @param id
	 * @return to loaded doc ref
	 * @throws EntityNotFoundException
	 */
	public ContractDoc getContractDoc(String id) throws EntityNotFoundException {
		return null;
	}

	/**
	 * Gets the doc <em>content</em> given the doc id.
	 * @param docId {@link DocRef} id
	 * @return to loaded doc content
	 * @throws EntityNotFoundException
	 */
	public DocContent getDocContent(String docId) throws EntityNotFoundException {
		return null;
	}

	/**
	 * Generates assignable surrogate primary keys for quote bundles guaranteed to
	 * be unique throughout the life of the datastore.
	 * @param numIds the number of ids to generate
	 * @return list of generated ids
	 */
	public long[] generateQuoteBundleIds(int numIds) {
		return null;
	}

	/**
	 * Generates assignable surrogate primary keys for quotes guaranteed to be
	 * unique throughout the life of the datastore.
	 * @param numIds the number of ids to generate
	 * @return list of generated ids
	 */
	public long[] generateQuoteIds(int numIds) {
		return null;
	}

	/**
	 * Gets the user state for the given user id
	 * @param userId
	 * @return the user's state entity
	 * @throws EntityNotFoundException
	 */
	public UserState getUserState(String userId) throws EntityNotFoundException {
		return null;
	}

	/**
	 * Saves user state.
	 * @param userState
	 * @throws EntityExistsException
	 */
	public void saveUserState(UserState userState) throws EntityExistsException {
		
	}

	/**
	 * Gets the sole bundle dedicated to housing orphaned quotes for the given
	 * user id.
	 * <p>
	 * Auto-creates this bundle if it is found not to exist.
	 * @param userId user id
	 * @return non-<code>null</code> {@link QuoteBundle} instance
	 */
	public QuoteBundle getOrphanedQuoteBundleForUser(String userId) {
		return null;
	}

	/**
	 * Gets all bundles for a given user.
	 * <p>
	 * Auto-creates an orphaned quote bundle if one doesn't exist for the user.
	 * @param userId
	 * @return list of quote bundles
	 */
	public BundleContainer getBundlesForUser(String userId) {
		return null;
	}

	/**
	 * Gets the quote bundle given the bundle id.
	 * @param bundleId
	 * @return
	 * @throws EntityNotFoundException
	 */
	public QuoteBundle getQuoteBundle(String bundleId) throws EntityNotFoundException {
		return null;
	}

	/**
	 * Updates the non-relational bundle properties in the given bundle. The
	 * bundle must already exist.
	 * @param userId
	 * @param bundle ref to an existing bundle whose properties are persisted to
	 *        that held in the datastore.
	 * @throws IllegalArgumentException When the given bundle is not new
	 * @throws ConstraintViolationException When the bundle's properties don't
	 *         validate
	 * @throws EntityNotFoundException When the quote bundle isn't found in the
	 *         datastore
	 */
	public void updateBundlePropsForUser(String userId, QuoteBundle bundle) throws IllegalArgumentException,
			ConstraintViolationException, EntityNotFoundException {


	}

	/**
	 * Updates a quote.
	 * @param quote
	 * @return the persisted quote
	 */
	public Quote updateQuote(Quote quote) {
		return null;
	}

	/**
	 * Creates or updates the given doc.
	 * @param doc the doc to save
	 * @return the saved doc
	 * @throws ConstraintViolationException When the given doc isn't valid
	 */
	public DocRef saveDoc(DocRef doc) throws ConstraintViolationException {
		return null;
	}

	public void saveDocContent(DocContent docContent) throws ConstraintViolationException {
		
	}

	private void sanitize(DocContent docContent) throws ConstraintViolationException{
		
	}


	/**
	 * Creates or updates the given contract doc.
	 * @param doc the doc to save
	 * @return the saved doc
	 * @throws ConstraintViolationException When the given contract doc isn't
	 *         valid
	 */
	public ContractDoc saveContractDoc(ContractDoc doc) throws ConstraintViolationException {
		return null;
	}

	/**
	 * Deletes the doc and doc content given its id as well as all doc/user
	 * bindings as well as any referenced quotes <em>permanently</em>.
	 * <p>
	 * Both the Doc and DocContent entities are deleted.
	 * <p>
	 * NOTE: Quotes (which may point to the target doc) are also permanently
	 * deleted!
	 * @param docId id of the doc to delete
	 * @throws EntityNotFoundException when the doc of the given id can't be found
	 */
	public void deleteDoc(String docId) throws EntityNotFoundException {
		
	}

	/**
	 * Deletes the contract doc given its id as well as all contract doc/user
	 * bindings.
	 * @param docId id of the contract doc to delete
	 * @throws EntityNotFoundException when the contract doc of the given id can't
	 *         be found
	 */
	public void deleteContractDoc(String docId) throws EntityNotFoundException {
		
	}

	/**
	 * Finds a case type doc by its remote url property.
	 * @param remoteUrl the unique remote url
	 * @return the found doc
	 * @throws EntityNotFoundException
	 */
	public DocRef findCaseDocByRemoteUrl(String remoteUrl) throws EntityNotFoundException {
		return null;
	}

	/**
	 * Saves the quote bundle for the given user as well as any referenced quotes
	 * doing a full replacement of the bundle with that given as well as the child
	 * quotes.
	 * @param userId
	 * @param bundle
	 * @return the saved bundle
	 * @throws ConstraintViolationException When the given bundle isn't valid
	 */
	public QuoteBundle saveBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
		return null;
	}

	/**
	 * Adds a quote bundle. No user associations are made and any quotes present
	 * in the given bundle are ignored.
	 * @param bundle
	 * @throws EntityExistsException When the quote already exists by business
	 *         key.
	 */
	public void addQuoteBundle(QuoteBundle bundle) throws EntityExistsException {
		// TODO impl
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes the quote bundle identified by the given id and <em>all</em>
	 * associations it has to users. If the bundle references any quotes, an
	 * exception is thrown since this would put those quotes in an orphaned state.
	 * @param bundleId
	 * @throws EntityExistsException When one or more quotes are currently
	 *         associated with the quote bundle
	 */
	public void removeQuoteBundle(long bundleId) throws EntityExistsException {
		// TODO impl
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds the given bundle and associates it with the given user.
	 * <p>
	 * Any quotes contained in the bundle are ignored.
	 * @param userId
	 * @param bundle
	 * @return the persisted bundle
	 * @throws ConstraintViolationException When the givne bundle isn't valid
	 */
	public QuoteBundle addBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
		return null;
	}

	/**
	 * Deletes a quote bundle and its association to the given user.
	 * @param userId
	 * @param bundleId
	 * @param deleteQuotes delete contained quotes as well? if <code>false</code>,
	 *        any contained quotes will be moved to the un-assigned bundle for the
	 *        given user
	 * @throws EntityNotFoundException When either the user or bundle can't be
	 *         resolved
	 */
	public void deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes) throws EntityNotFoundException {

	}

	/**
	 * Gets the quote given the quote id.
	 * @param quoteId
	 * @return the loaded quote
	 * @throws EntityNotFoundException
	 */
	public Quote getQuote(String quoteId) throws EntityNotFoundException {
		return null;
	}
	
	public Quote addOrphanQuote(String userId, String title, Reference reference, String quoteText, String quoteBundleId) throws ConstraintViolationException,EntityNotFoundException {
		return null; 
	}
	

	/**
	 * Adds the given quote to the quote bundle identified by the given bundle id.
	 * @param userId needed for creating quote/user binding
	 * @param bundleId
	 * @param quote
	 * @return the persisted quote
	 * @throws ConstraintViolationException When the quote doesn't validate
	 * @throws EntityNotFoundException When the bundle can't be found from the
	 *         given id
	 */
	public Quote addQuoteToBundle(String userId, String bundleId, Quote quote) throws ConstraintViolationException,
			EntityNotFoundException {
		return null;
	}

	/**
	 * permanently deletes an existing quote for a given user from all bundles
	 * that contain it.
	 * @param userId needed for removing the quote/user binding
	 * @param quoteId
	 * @throws EntityNotFoundException when the quote isn't found to exist in the
	 *         bundle
	 */
	public void deleteQuote(String userId, String quoteId) throws EntityNotFoundException {
		
	}

	/**
	 * Moves an existing quote from an existing source bundle to an existing
	 * target bundle.
	 * @param userId
	 * @param quoteId id of the quote to move
	 * @param sourceBundleId id of the bundle currently containing the quote
	 * @param targetBundleId id of the bundle to which to move the quote
	 * @throws EntityNotFoundException When a participating entity is not found
	 */
	public void moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId)
			throws EntityNotFoundException {
		
	}

	/**
	 * Adds an association of an existing quote bundle to an existing user.
	 * @param userId
	 * @param bundleId
	 * @throws EntityExistsException if the association already exists
	 */
	public void addBundleUserBinding(String userId, String bundleId) throws EntityExistsException {
		
	}

	/**
	 * Removes a user bundle association.
	 * @param userId
	 * @param bundleId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	public void removeBundleUserBinding(String userId, String bundleId) throws EntityNotFoundException {
		
	}

	/**
	 * Adds an association of an existing doc to an existing user.
	 * @param userId
	 * @param docId
	 * @throws EntityExistsException if the association already exists
	 */
	public void addDocUserBinding(String userId, String docId) throws EntityExistsException {
		
	}

	/**
	 * Adds an association of an existing contract doc to an existing user.
	 * @param userId
	 * @param docId
	 * @throws EntityExistsException if the association already exists
	 */
	public void addContractDocUserBinding(String userId, String docId) throws EntityExistsException {
		
	}

	/**
	 * Removes a user doc association.
	 * @param userId
	 * @param docId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	public void removeDocUserBinding(String userId, String docId) throws EntityNotFoundException {
		
	}

	/**
	 * Removes a user contract doc association.
	 * @param userId
	 * @param docId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	public void removeContractDocUserBinding(String userId, String docId) throws EntityNotFoundException {
		
	}

	/**
	 * Returns all user/doc bindings that exist for a given doc
	 * @param docId id of the doc
	 * @return list of doc user bindings
	 */
	public List<DocUserBinding> getDocUserBindingsForDoc(String docId) {
		return null;
	}

	/**
	 * Returns all contract doc/user bindings that exist for a given contract doc
	 * @param docId id of the contract doc
	 * @return list of contract doc user bindings
	 */
	public List<ContractDocUserBinding> getContractDocUserBindingsForDoc(String docId) {
		return null;
	}

	/**
	 * Adds an association of an existing quote to an existing user.
	 * @param userId
	 * @param quoteId
	 * @throws EntityExistsException if the association already exists
	 */
	public void addQuoteUserBinding(String userId, String quoteId) throws EntityExistsException {
		
	}

	/**
	 * Removes a user quote association.
	 * @param userId
	 * @param quoteId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	public void removeQuoteUserBinding(String userId, String quoteId) throws EntityNotFoundException {
		
	}

	public List<QuoteUserBinding> getQuoteUserBindingsForQuote(String quoteId) {
		return null;
	}

	/**
	 * Updates an existing quote/user binding's orphan property.
	 * <p>
	 * Use for orphaning and un-orphaning a quote.
	 * @param userId
	 * @param bundleId
	 * @param orphan
	 * @throws EntityNotFoundException
	 */
	public void updateBundleUserBinding(String userId, String bundleId, boolean orphan) throws EntityNotFoundException {

	}

	/**
	 * Checks does the specified document available for the user
	 * @param userId
	 * @param docId
	 * @return
	 */
	public boolean isDocAvailableForUser(String userId, String docId) {
		return false;
	}

	/**
	 * Checks does the bundle available for the user
	 * @param userId
	 * @param bundleId
	 * @return
	 */
	public boolean isBundleAvailableForUser(String userId, String bundleId) {
		return false;
	}

	/**
	 * Checks does the bundle available for the user
	 * @param userId
	 * @param quoteId
	 * @return true/false
	 */
	public boolean isQuoteAvailableForUser(String userId, String quoteId) {
		return false;
	}

	/**
	 * Gets all quotes that point to the doc and available for current user having
	 * the given doc id.
	 * @param docId
	 * @param userId
	 * @return non-<code>null</code> list of quotes
	 */
	public List<Quote> findQuotesByDocForUser(String docId, String userId) {
		return null;
	}

	/**
	 * Gets all quotes that point to the doc having the given doc id.
	 * @param docId
	 * @return non-<code>null</code> list of quotes
	 */
	public List<Quote> findQuotesByDoc(String docId) {
		return findQuotesByDocForUser(docId, null);
	}

	public List<Quote> findQuotesForUser(String userId) {
		return null;
	}

	/**
	 * Creates or updates a clause bundle
	 * @param cb the clause bundle to persist
	 * @return the persisted bundle
	 * @throws ConstraintViolationException
	 * @throws EntityExistsException
	 */
	public ClauseBundle persistClauseBundle(ClauseBundle cb) throws ConstraintViolationException, EntityExistsException {
		return null;
	}

	/**
	 * Deletes a clause bundle from the system.
	 * @param id id of the clause bundle to be deleted
	 * @throws EntityNotFoundException
	 */
	public void deleteClauseBundle(String id) throws EntityNotFoundException {

	}

	/**
	 * Retrieves the clause bundle of the given id
	 * @param id id of the clause bundle
	 * @return clause bundle
	 * @throws EntityNotFoundException
	 */
	public ClauseBundle getClauseBundle(String id) throws EntityNotFoundException {
		return null;
	}
	
	/**
	 * @return list of all defined clause bundles in the system.
	 */
	public List<ClauseBundle> getAllClauseBundles() {
		return null;
	}

}
