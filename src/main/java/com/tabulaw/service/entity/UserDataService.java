/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.service.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tabulaw.cassandra.om.factory.Session;
import com.tabulaw.criteria.Comparator;
import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.dao.IEntityDao;
import com.tabulaw.dao.Sorting;
import com.tabulaw.model.BundleUserBinding;
import com.tabulaw.model.ContractDocUserBinding;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;
import com.tabulaw.model.UserState;

/**
 * Manages the persistence of user related data that is not part of the user
 * entity.
 * @author jpk
 */
public class UserDataService extends AbstractEntityService {

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

	/**
	 * Constructor
	 * @param dao
	 * @param validationFactory
	 */
	@Inject
	public UserDataService(IEntityDao dao, ValidatorFactory validationFactory) {
		super(dao, validationFactory);
	}

	/**
	 * Gets a list of all docs for a given user.
	 * @param userId user id
	 * @return list of docs
	 */
	@Transactional(readOnly = true)
	public List<DocRef> getDocsForUser(String userId) {
		if(userId == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		session.close();
		return new ArrayList<DocRef>(user.getDocuments());
	}

	/**
	 * Provides a list of all doc refs in the system.
	 * @return doc list
	 */
	@Transactional(readOnly = true)
	public List<DocRef> getAllDocs() {
		Session session = TabulawSession.FACTORY.createSession();
		List<DocRef> docs = session.findAll(DocRef.class);
		session.close();
		return docs;
	}

	/**
	 * Gets the doc ref given the doc id.
	 * @param docId
	 * @return to loaded doc ref
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public DocRef getDoc(String docId) throws EntityNotFoundException {
		if(docId == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();		
		DocRef dr = session.find(DocRef.class, docId);
		session.close();
		return dr;
	}


	/**
	 * Gets the doc <em>content</em> given the doc id.
	 * @param docId {@link DocRef} id
	 * @return to loaded doc content
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public DocContent getDocContent(String docId) throws EntityNotFoundException {
		if(docId == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();	
		DocContent dr = session.find(DocContent.class, docId);
		session.close();
		return dr;
	}

	/**
	 * Generates assignable surrogate primary keys for quote bundles guaranteed to
	 * be unique throughout the life of the datastore.
	 * @param numIds the number of ids to generate
	 * @return list of generated ids
	 */
	@Transactional
	public long[] generateQuoteBundleIds(int numIds) {
		long[] idRange = dao.generatePrimaryKeyBatch(QuoteBundle.class, numIds);
		return idRange;
	}

	/**
	 * Generates assignable surrogate primary keys for quotes guaranteed to be
	 * unique throughout the life of the datastore.
	 * @param numIds the number of ids to generate
	 * @return list of generated ids
	 */
	@Transactional
	public long[] generateQuoteIds(int numIds) {
		long[] idRange = dao.generatePrimaryKeyBatch(Quote.class, numIds);
		return idRange;
	}

	/**
	 * Gets the user state for the given user id
	 * @param userId
	 * @return the user's state entity
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public UserState getUserState(String userId) throws EntityNotFoundException {
		if(userId == null) throw new NullPointerException();
		return dao.load(UserState.class, userId);
	}

	/**
	 * Saves user state.
	 * @param userState
	 * @throws EntityExistsException
	 */
	@Transactional
	public void saveUserState(UserState userState) throws EntityExistsException {
		if(userState == null) throw new NullPointerException();
		dao.persist(userState);
	}

	/**
	 * Gets the sole bundle dedicated to housing orphaned quotes for the given
	 * user id.
	 * <p>
	 * Auto-creates this bundle if it is found not to exist.
	 * @param userId user id
	 * @return non-<code>null</code> {@link QuoteBundle} instance
	 */
	@Transactional
	public QuoteBundle getOrphanedQuoteBundleForUser(String userId) {
		if(userId == null) throw new NullPointerException();

		QuoteBundle oqc = null;

		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		if (user.getOrphanedQuoteBundle() == null) {
			oqc = new QuoteBundle();
			oqc.setName("Un-Assigned Quotes");
			oqc.setDescription("Quotes not currently assigned to a bundle");
			session.persist(oqc);
			user.getBundles().add(oqc);
			user.setOrphanedQuoteBundle(oqc);			
		}
		session.close();
		return user.getOrphanedQuoteBundle();
	}

	/**
	 * Gets all bundles for a given user.
	 * <p>
	 * Auto-creates an orphaned quote bundle if one doesn't exist for the user.
	 * @param userId
	 * @return list of quote bundles
	 */
	@Transactional
	public BundleContainer getBundlesForUser(String userId) {
		if(userId == null) throw new NullPointerException();

		// first ensure an orphaned quotes container exists for user
		getOrphanedQuoteBundleForUser(userId);

		Criteria<BundleUserBinding> c = new Criteria<BundleUserBinding>(BundleUserBinding.class);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		try {
			String orphanQuoteContainerId = null;
			List<BundleUserBinding> bindings = dao.findEntities(c, null);
			ArrayList<String> bundleIds = new ArrayList<String>(bindings.size() + 1);
			for(BundleUserBinding b : bindings) {
				bundleIds.add(b.getBundleId());
				if(b.isOrphaned()) {
					orphanQuoteContainerId = b.getBundleId();
				}
			}

			if(orphanQuoteContainerId == null)
				throw new IllegalStateException("No orphaned quotes container found for user");

			List<QuoteBundle> list = dao.findByIds(QuoteBundle.class, bundleIds, new Sorting("name"));
			if(list.size() != bundleIds.size())
				throw new IllegalStateException("Bundle id list and bundle entity list size mis-match.");
			return new BundleContainer(list, orphanQuoteContainerId);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Gets the quote bundle given the bundle id.
	 * @param bundleId
	 * @return
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public QuoteBundle getQuoteBundle(String bundleId) throws EntityNotFoundException {
		if(bundleId == null) {
			throw new NullPointerException();
		}
		Session session = TabulawSession.FACTORY.createSession();
		QuoteBundle bundle = session.find(QuoteBundle.class, bundleId);
		session.close();
		return bundle;
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
	@Transactional
	public void updateBundlePropsForUser(String userId, QuoteBundle bundle) throws IllegalArgumentException,
			ConstraintViolationException, EntityNotFoundException {

		if(userId == null || bundle == null) throw new NullPointerException();

		validate(bundle);

		Session session = TabulawSession.FACTORY.createSession();
		QuoteBundle existingQb = session.find(QuoteBundle.class, bundle.getId());
		existingQb.setName(bundle.getName());
		existingQb.setDescription(bundle.getDescription());
		session.close();
	}

	/**
	 * Updates a quote.
	 * @param quote
	 * @return the persisted quote
	 */
	@Transactional
	public Quote updateQuote(Quote quote) {
		if(quote == null) throw new NullPointerException();
		validate(quote);
		Session session = TabulawSession.FACTORY.createSession();
		quote = session.merge(quote);
		session.close();
		return quote;
	}

	/**
	 * Creates or updates the given doc.
	 * @param doc the doc to save
	 * @return the saved doc
	 * @throws ConstraintViolationException When the given doc isn't valid
	 */
	@Transactional
	public DocRef saveDoc(DocRef doc) throws ConstraintViolationException {
		if(doc == null) throw new NullPointerException();
		validate(doc);
		Session session = TabulawSession.FACTORY.createSession();
		doc = session.merge(doc);
		session.close();
		return doc;
	}

	@Transactional
	public void saveDocContent(DocContent docContent) throws ConstraintViolationException {
		if(docContent == null) throw new NullPointerException();
		validate(docContent);
		Session session = TabulawSession.FACTORY.createSession();
		session.persist(docContent);
		session.close();
	}

	/**
	 * Deletes the doc and doc content given its id as well as all doc/user
	 * bindings as well as any referenced quotes <em>permanantly</em>.
	 * <p>
	 * Both the Doc and DocContent entities are deleted.
	 * <p>
	 * NOTE: Quotes (which may point to the target doc) are also permanantly
	 * deleted!
	 * @param docId id of the doc to delete
	 * @throws EntityNotFoundException when the doc of the given id can't be found
	 */
	@Transactional
	public void deleteDoc(String docId) throws EntityNotFoundException {
		if(docId == null) throw new NullPointerException();

		Session session = TabulawSession.FACTORY.createSession();
		DocRef doc = session.find(DocRef.class, docId);
		DocContent content = session.find(DocContent.class, docId);		
		for (User user : doc.getUsers()) {
			user.getDocuments().remove(doc);
		}
		for (Quote quote : doc.getQuotes()) {
			session.remove(quote);
		}
		if (content != null) {
			session.remove(content);
		}
		session.remove(doc);
		session.close();
	}

	/**
	 * Deletes the contract doc given its id as well as all contract doc/user
	 * bindings.
	 * @param docId id of the contract doc to delete
	 * @throws EntityNotFoundException when the contract doc of the given id can't
	 *         be found
	 */
/*	@Transactional
	public void deleteContractDoc(String docId) throws EntityNotFoundException {
		if(docId == null) throw new NullPointerException();
		List<ContractDocUserBinding> userBindings = getContractDocUserBindingsForDoc(docId);
		dao.purgeAll(userBindings);
		dao.purge(ContractDoc.class, docId);
	}*/

	/**
	 * Finds a case type doc by its remote url property.
	 * @param remoteUrl the unique remote url
	 * @return the found doc
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public DocRef findCaseDocByRemoteUrl(String remoteUrl) throws EntityNotFoundException {
		if(remoteUrl == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();
		List<DocRef> docs = session.findAll(DocRef.class);
		for (DocRef doc : docs) {
			if (doc.getCaseRef() != null && StringUtils.equals(doc.getCaseRef().getUrl(), remoteUrl)) {
				return doc;
			}
		}
		throw new EntityNotFoundException("Can't find doc with remoteUrl=" + remoteUrl);
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
	@Transactional
	public QuoteBundle saveBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
		if(userId == null || bundle == null) throw new NullPointerException();

		validate(bundle);

		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		QuoteBundle existing = session.find(QuoteBundle.class, bundle.getId());

		// clear out existing
		if(existing != null) {
			for (Quote quote : existing.getQuotes()) {
				session.remove(quote);
			}
			session.remove(existing);
		}		

		// save the bundle
		List<Quote> quotes = bundle.getQuotes();
		bundle.setQuotes(null);
		QuoteBundle persistedBundle = session.merge(bundle);

		// save the quotes
		if(quotes != null && quotes.size() > 0) {
			for(Quote q : quotes) {
				session.persist(q);
				persistedBundle.getQuotes().add(q);
			}
		}

		// create binding if this is a new bundle
		if(existing == null) {
			user.getBundles().add(persistedBundle);
		}
		session.close();

		return persistedBundle;
	}

	/**
	 * Adds a quote bundle. No user associations are made and any quotes present
	 * in the given bundle are ignored.
	 * @param bundle
	 * @throws EntityExistsException When the quote already exists by business
	 *         key.
	 */
	@Transactional
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
	@Transactional
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
	@Transactional
	public QuoteBundle addBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
		if(userId == null || bundle == null) throw new NullPointerException();
		
		Session session = TabulawSession.FACTORY.createSession();
		
		QuoteBundle existing = session.find(QuoteBundle.class, bundle.getId());
		if (existing != null) {
			throw new IllegalArgumentException("Bundle already exists.");
		}
		validate(bundle);

		QuoteBundle persistedBundle = session.merge(bundle);
		User user = session.find(User.class, userId);
		user.getBundles().add(persistedBundle);
		
		session.close();
		
		return persistedBundle;
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
	@Transactional
	public void deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes) throws EntityNotFoundException {
		if(userId == null || bundleId == null) throw new NullPointerException();

		Session session = TabulawSession.FACTORY.createSession();
		QuoteBundle bundle = session.find(QuoteBundle.class, bundleId);
		User user = session.find(User.class, userId);

		List<Quote> quotes = bundle.getQuotes();
		if(quotes != null) {

			// get handle to user's orphaned quotes container if we're not deleting
			// quotes
			QuoteBundle oqc = deleteQuotes ? null : getOrphanedQuoteBundleForUser(userId);

			for(Quote q : quotes) {
				if(deleteQuotes) {
					//user.getQuotes().remove(q);
					session.remove(q);
				}
				else {
					// bundle.removeQuote(q);
					assert oqc != null;
					oqc.addQuote(q);
				}
			}

			if(oqc != null) session.persist(oqc);
		}

		session.remove(bundle);
		user.getBundles().remove(bundle);
		session.close();
	}

	/**
	 * Gets the quote given the quote id.
	 * @param quoteId
	 * @return the loaded quote
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public Quote getQuote(String quoteId) throws EntityNotFoundException {
		if(quoteId == null) {
			throw new NullPointerException();
		}
		Session session = TabulawSession.FACTORY.createSession();
		Quote quote = session.find(Quote.class, quoteId);
		session.close();
		return quote;
	}
	
	@Transactional
	public Quote addOrphanQuote(String userId, String title, String quoteText, String quoteBundleId) throws ConstraintViolationException,EntityNotFoundException {
		DocRef document = EntityFactory.get().buildDoc(title, new Date(), true);
		saveDoc(document);

		DocContent docContent = EntityFactory.get().buildDocContent(document.getId(), quoteText);
		saveDocContent(docContent);

		Quote quote = EntityFactory.get().buildQuote(quoteText, document, null, 1, 1);
		quote = addQuoteToBundle(userId, quoteBundleId, quote);
		return quote; 
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
	@Transactional
	public Quote addQuoteToBundle(String userId, String bundleId, Quote quote) throws ConstraintViolationException,
			EntityNotFoundException {
		if(userId == null || bundleId == null || quote == null) throw new NullPointerException();

		Session session = TabulawSession.FACTORY.createSession();
		QuoteBundle qb = session.find(QuoteBundle.class, bundleId);
		assert qb != null;

		// ensure quote is new
		Quote existing = session.find(Quote.class, quote.getId());
		if (existing != null) {
			throw new IllegalArgumentException("Quote already exists");
		}

		validate(quote);

		// get the doc ref from the db to avoid having multiple docs of the same id
		// persisted!
		// NOTE: this is a db4o specific issue
		DocRef persistedDoc = null;
		persistedDoc = session.find(DocRef.class, quote.getDocument().getId());
		if (persistedDoc != null) {
			quote.setDocument(persistedDoc);
		} else {
			persistedDoc = session.merge(quote.getDocument());
		}

		Quote persistedQuote = session.merge(quote);
		qb.addQuote(persistedQuote);
		session.persist(qb);		
		

		// add quote/user binding
		addQuoteUserBinding(userId, persistedQuote.getId());
		session.close();

		return persistedQuote;
	}

	/**
	 * Permanantly deletes an existing quote for a given user from all bundles
	 * that contain it.
	 * @param userId needed for removing the quote/user binding
	 * @param quoteId
	 * @throws EntityNotFoundException when the quote isn't found to exist in the
	 *         bundle
	 */
	@Transactional
	public void deleteQuote(String userId, String quoteId) throws EntityNotFoundException {
		if(userId == null || quoteId == null) throw new NullPointerException();

		Session session = TabulawSession.FACTORY.createSession();
		Quote quote = session.find(Quote.class, quoteId);
		session.remove(quote);
		session.close();
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
	@Transactional
	public void moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId)
			throws EntityNotFoundException {
		if(userId == null || quoteId == null || sourceBundleId == null || targetBundleId == null)
			throw new NullPointerException();

		// ensure the bundles and quote belong to the given user
		// TODO do we really need to do this?
		/*BundleUserBinding srcBub = *///findBundleUserBinding(userId, sourceBundleId);
		/*BundleUserBinding tgtBub = *///findBundleUserBinding(userId, targetBundleId);
		/*QuoteUserBinding qub = *///findQuoteUserBinding(userId, quoteId);

		Session session = TabulawSession.FACTORY.createSession();		
		QuoteBundle srcBundle = session.find(QuoteBundle.class, sourceBundleId);
		QuoteBundle tgtBundle = session.find(QuoteBundle.class, targetBundleId);
		Quote q = session.find(Quote.class, quoteId);
		if(!srcBundle.removeQuote(q)) {
			throw new EntityNotFoundException("Quote: " + q + " not in source bundle: " + srcBundle);
		}
		session.flush();
		tgtBundle.addQuote(q);
		session.close();
	}

	/**
	 * Adds an association of an existing quote bundle to an existing user.
	 * @param userId
	 * @param bundleId
	 * @throws EntityExistsException if the association already exists
	 */
	@Transactional
	public void addBundleUserBinding(String userId, String bundleId) throws EntityExistsException {
		if(bundleId == null || userId == null) throw new NullPointerException();
		
		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		QuoteBundle bundle = session.find(QuoteBundle.class, bundleId);
		user.getBundles().add(bundle);
		session.close();
	}

	/**
	 * Removes a user bundle association.
	 * @param userId
	 * @param bundleId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	@Transactional
	public void removeBundleUserBinding(String userId, String bundleId) throws EntityNotFoundException {
		if(bundleId == null || userId == null) throw new NullPointerException();
		
		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		QuoteBundle bundle = session.find(QuoteBundle.class, bundleId);
		user.getBundles().remove(bundle);
		session.close();
	}

	/**
	 * Adds an association of an existing doc to an existing user.
	 * @param userId
	 * @param docId
	 * @throws EntityExistsException if the association already exists
	 */
	@Transactional
	public void addDocUserBinding(String userId, String docId) throws EntityExistsException {
		if(docId == null || userId == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		DocRef doc = session.find(DocRef.class, docId);
		user.getDocuments().add(doc);
		session.close();
	}

	/**
	 * Removes a user doc association.
	 * @param userId
	 * @param docId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	@Transactional
	public void removeDocUserBinding(String userId, String docId) throws EntityNotFoundException {
		if(docId == null || userId == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		DocRef doc = session.find(DocRef.class, docId);
		user.getDocuments().remove(doc);
		session.close();
	}


	/**
	 * Returns all user/doc bindings that exist for a given doc
	 * @param docId id of the doc
	 * @return list of doc user bindings
	 */
	@Transactional
	public boolean isOrphanedDoc(String docId) {
		if(docId == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();
		DocRef doc = session.find(DocRef.class, docId);
		boolean result = doc.getUsers().isEmpty();
		session.close();
		return result;
	}

	/**
	 * Returns all contract doc/user bindings that exist for a given contract doc
	 * @param docId id of the contract doc
	 * @return list of contract doc user bindings
	 */
	@Transactional
	public List<ContractDocUserBinding> getContractDocUserBindingsForDoc(String docId) {
		if(docId == null) throw new NullPointerException();
		Criteria<ContractDocUserBinding> c = new Criteria<ContractDocUserBinding>(ContractDocUserBinding.class);
		c.getPrimaryGroup().addCriterion("docId", docId, Comparator.EQUALS, true);
		try {
			return dao.findEntities(c, null);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Adds an association of an existing quote to an existing user.
	 * @param userId
	 * @param quoteId
	 * @throws EntityExistsException if the association already exists
	 */
	@Transactional
	public void addQuoteUserBinding(String userId, String quoteId) throws EntityExistsException {
		if(quoteId == null || userId == null) throw new NullPointerException();
		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		Quote quote = session.find(Quote.class, quoteId);
		//user.getQuotes().add(quote);
		session.close();
	}

	/**
	 * Checks does the specified document available for the user
	 * @param userId
	 * @param docId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean isDocAvailableForUser(String userId, String docId) {
		if(userId == null || docId == null) throw new NullPointerException();

		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		DocRef doc = session.find(DocRef.class, docId);
		boolean result = user.getDocuments().contains(doc);
		session.close();
		return result;
	}

	/**
	 * Checks does the bundle available for the user
	 * @param userId
	 * @param bundleId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean isBundleAvailableForUser(String userId, String bundleId) {
		if(userId == null || bundleId == null) {
			throw new NullPointerException();
		}

		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		QuoteBundle bundle = session.find(QuoteBundle.class, bundleId);
		boolean result = user.getBundles().contains(bundle);
		session.close();
		return result;
	}

	/**
	 * Checks does the bundle available for the user
	 * @param userId
	 * @param quoteId
	 * @return true/false
	 */
	@Transactional(readOnly = true)
	public boolean isQuoteAvailableForUser(String userId, String quoteId) {
		if(userId == null || quoteId == null) {
			throw new NullPointerException();
		}

		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		Quote quote = session.find(Quote.class, quoteId);
		boolean result = true;//user.getQuotes().contains(quote);
		session.close();
		return result;
	}

	/**
	 * Gets all quotes that point to the doc and available for current user having
	 * the given doc id.
	 * @param docId
	 * @param userId
	 * @return non-<code>null</code> list of quotes
	 */
	@Transactional(readOnly = true)
	public List<Quote> findQuotesByDocForUser(String docId, String userId) {
		Session session = TabulawSession.FACTORY.createSession();
		DocRef doc = session.find(DocRef.class, docId);
		User user = null;
		if (userId != null) {
			user = session.find(User.class, userId);
		}
		ArrayList<Quote> result = new ArrayList<Quote>();
		for (Quote quote : doc.getQuotes()) {
			//if (user == null || user.getQuotes().contains(quote)) {
				result.add(quote);
			//}
		}
		session.close();
		return result;
	}

	/**
	 * Gets all quotes that point to the doc having the given doc id.
	 * @param docId
	 * @return non-<code>null</code> list of quotes
	 */
	public List<Quote> findQuotesByDoc(String docId) {
		return findQuotesByDocForUser(docId, null);
	}

	@Transactional(readOnly = true)
	public List<Quote> findQuotesForUser(String userId) {
		if(userId == null) {
			throw new NullPointerException();
		}
		Session session = TabulawSession.FACTORY.createSession();
		User user = session.find(User.class, userId);
		ArrayList<Quote> result = new ArrayList<Quote>();
		//result.addAll(user.getQuotes());
		session.close();
		return result;
	}

	/**
	 * Removes the quote specified by the given id from any and all bundles that
	 * reference the quote so that a null element isn't left in the quotes list
	 * <p>
	 * this is a db4o-ism only.
	 * @param quoteId
	 */
	/*private void removeQuoteRefFromBundles(String quoteId) {
		List<QuoteBundle> qbs = dao.loadAll(QuoteBundle.class);
		for(QuoteBundle qb : qbs) {
			Quote tormv = null;
			if(qb.getQuotes() != null) {
				for(Quote q : qb.getQuotes()) {
					if(q.getId().equals(quoteId)) {
						tormv = q;
						break;
					}
				}
			}
			if(tormv != null) {
				qb.removeQuote(tormv);
				dao.persist(qb);
			}
		}
	}*/
}
