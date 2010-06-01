/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.service.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tabulaw.common.model.BundleUserBinding;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.DocUserBinding;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.QuoteUserBinding;
import com.tabulaw.common.model.UserState;
import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.dao.IEntityDao;
import com.tabulaw.dao.NonUniqueResultException;
import com.tabulaw.dao.Sorting;
import com.tabulaw.util.Comparator;

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
	 * <p>
	 * The <code>htmlContent</code> property is cleared out for each doc.
	 * @param userId user id
	 * @return list of docs
	 */
	@Transactional(readOnly = true)
	public List<DocRef> getDocsForUser(String userId) {
		if(userId == null) throw new NullPointerException();
		Criteria<DocUserBinding> c = new Criteria<DocUserBinding>(DocUserBinding.class);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		try {
			List<DocUserBinding> bindings = dao.findEntities(c, null);
			if(bindings.size() < 1) return new ArrayList<DocRef>(0);
			ArrayList<String> docIds = new ArrayList<String>(bindings.size());
			for(DocUserBinding b : bindings) {
				docIds.add(b.getDocId());
			}
			List<DocRef> list = dao.findByIds(DocRef.class, docIds, new Sorting("name"));
			if(list.size() != docIds.size())
				throw new IllegalStateException("Doc id list and doc entity list size mis-match.");

			return cloneDocList(list);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Provides a list of all docs in the system.
	 * <p>
	 * Each doc element has their <code>htmlContent</code> property set to
	 * <code>null</code>.
	 * @return doc list
	 */
	@Transactional(readOnly = true)
	public List<DocRef> getAllDocs() {
		List<DocRef> docs = dao.loadAll(DocRef.class);
		return cloneDocList(docs);
	}

	/**
	 * Gets the doc of the given id.
	 * @param docId
	 * @return to loaded doc
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public DocRef getDoc(String docId) throws EntityNotFoundException {
		if(docId == null) throw new NullPointerException();
		DocRef dr = dao.load(DocRef.class, docId);
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
	public QuoteBundle getOrphanedQuotesBundleForUser(String userId) {
		if(userId == null) throw new NullPointerException();

		QuoteBundle oqc = null;

		Criteria<BundleUserBinding> c = new Criteria<BundleUserBinding>(BundleUserBinding.class);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		c.getPrimaryGroup().addCriterion("orphaned", Boolean.TRUE, Comparator.EQUALS, true);
		try {
			BundleUserBinding binding = dao.findEntity(c);
			oqc = dao.load(QuoteBundle.class, binding.getBundleId());
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
		catch(EntityNotFoundException e) {
			// create the orphaned quote container
			oqc = new QuoteBundle();
			oqc.setName("Un-Assigned Quotes");
			oqc.setDescription("Quotes not currently assigned to a bundle");
			oqc = dao.persist(oqc);
			BundleUserBinding binding = new BundleUserBinding(oqc.getId(), userId, true);
			dao.persist(binding);
		}

		return oqc;
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
		getOrphanedQuotesBundleForUser(userId);

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

		QuoteBundle existingQb = dao.load(QuoteBundle.class, bundle.getId());
		existingQb.setName(bundle.getName());
		existingQb.setDescription(bundle.getDescription());
		dao.persist(existingQb);
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

		// remove existing (db4o-ism)
		try {
			DocRef existing = dao.load(DocRef.class, doc.getId());
			dao.purge(existing);
		}
		catch(EntityNotFoundException e) {
			// ok
		}

		// save the doc
		doc = dao.persist(doc);

		return doc;
	}

	/**
	 * Deletes the doc given its id as well as all doc/user bindings.
	 * <p>
	 * NOTE: Quotes (which may point to the target doc) are un-affected.
	 * @param docId id of the doc to delete
	 * @throws EntityNotFoundException when the doc of the given id can't be found
	 */
	@Transactional
	public void deleteDoc(String docId) throws EntityNotFoundException {
		if(docId == null) throw new NullPointerException();
		dao.purge(DocRef.class, docId);
		removeAllDocUserBindingsForDoc(docId);
	}

	/**
	 * Finds a case type doc by its remote url property.
	 * @param remoteUrl the unique remote url
	 * @return the found doc
	 * @throws EntityNotFoundException
	 */
	@Transactional(readOnly = true)
	public DocRef findCaseDocByRemoteUrl(String remoteUrl) throws EntityNotFoundException {
		if(remoteUrl == null) throw new NullPointerException();
		Criteria<DocRef> c = new Criteria<DocRef>(DocRef.class);
		c.getPrimaryGroup().addCriterion("caseRef.url", remoteUrl, Comparator.EQUALS, true);
		try {
			DocRef doc = dao.findEntity(c);
			return doc;
		}
		catch(NonUniqueResultException e) {
			throw new IllegalStateException("Non-unique remote url: " + remoteUrl);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}

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

		QuoteBundle existing;
		try {
			existing = dao.load(QuoteBundle.class, bundle.getId());
		}
		catch(EntityNotFoundException e) {
			// new
			existing = null;
		}

		// clear out existing
		if(existing != null) {
			dao.purge(QuoteBundle.class, existing.getId());
			List<Quote> existingQuotes = existing.getQuotes();
			if(existingQuotes != null) {
				for(Quote eq : existingQuotes) {
					dao.purge(eq);
				}
			}
		}

		// save the bundle
		QuoteBundle persistedBundle = dao.persist(bundle);

		// save the quotes
		List<Quote> quotes = persistedBundle.getQuotes();
		if(quotes != null && quotes.size() > 0) {
			ArrayList<Quote> savedQuotes = new ArrayList<Quote>(quotes.size());
			for(Quote q : quotes) {
				savedQuotes.add(dao.persist(q));
			}
			persistedBundle.setQuotes(savedQuotes);
		}

		// create binding if this is a new bundle
		if(existing == null) addBundleUserBinding(userId, bundle.getId());

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

		try {
			dao.load(QuoteBundle.class, bundle.getId());
			throw new IllegalArgumentException("Bundle already exists.");
		}
		catch(EntityNotFoundException e) {
			// desired
			validate(bundle);
			QuoteBundle persistedBundle = dao.persist(bundle);
			addBundleUserBinding(userId, persistedBundle.getId());
			return persistedBundle;
		}
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

		QuoteBundle bundle = dao.load(QuoteBundle.class, bundleId);

		List<Quote> quotes = bundle.getQuotes();
		if(quotes != null) {

			// get handle to user's orphaned quotes container if we're not deleting
			// quotes
			QuoteBundle oqc = deleteQuotes ? null : getOrphanedQuotesBundleForUser(userId);

			for(Quote q : quotes) {
				if(deleteQuotes) {
					removeQuoteUserBinding(userId, q.getId());
					dao.purge(q);
				}
				else {
					// bundle.removeQuote(q);
					assert oqc != null;
					oqc.addQuote(q);
				}
			}

			if(oqc != null) dao.persist(oqc);
		}

		dao.purge(bundle);
		removeBundleUserBinding(userId, bundleId);
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

		QuoteBundle qb = dao.load(QuoteBundle.class, bundleId);
		assert qb != null;

		// ensure quote is new
		try {
			dao.load(Quote.class, quote.getId());
			throw new IllegalArgumentException("Quote already exists");
		}
		catch(EntityNotFoundException e) {
			// expected
		}

		validate(quote);

		// get the doc ref from the db to avoid having multiple docs of the same id
		// persisted!
		// NOTE: this is a db40 specific issue
		DocRef persistedDoc = null;
		try {
			persistedDoc = dao.load(DocRef.class, quote.getDocument().getId());
			assert persistedDoc != null;
			quote.setDocument(persistedDoc);
		}
		catch(EntityNotFoundException e) {
			// presume doc exists on filesystem but not in db
			// persist it
			persistedDoc = dao.persist(quote.getDocument());
		}

		Quote persistedQuote = dao.persist(quote);
		qb.addQuote(persistedQuote);
		dao.persist(qb);

		// add quote/user binding
		addQuoteUserBinding(userId, persistedQuote.getId());

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

		removeQuoteUserBinding(userId, quoteId);

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
				dao.purge(tormv);
			}
		}
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
		/*BundleUserBinding srcBub = */findBundleUserBinding(userId, sourceBundleId);
		/*BundleUserBinding tgtBub = */findBundleUserBinding(userId, targetBundleId);
		/*QuoteUserBinding qub = */findQuoteUserBinding(userId, quoteId);

		QuoteBundle srcBundle = dao.load(QuoteBundle.class, sourceBundleId);
		QuoteBundle tgtBundle = dao.load(QuoteBundle.class, targetBundleId);
		Quote q = dao.load(Quote.class, quoteId);
		if(!srcBundle.removeQuote(q)) {
			throw new EntityNotFoundException("Quote: " + q + " not in source bundle: " + srcBundle);
		}
		tgtBundle.addQuote(q);
		dao.persist(srcBundle);
		dao.persist(tgtBundle);
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
		BundleUserBinding binding = new BundleUserBinding(bundleId, userId, false);
		dao.persist(binding);
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
		Criteria<BundleUserBinding> c = new Criteria<BundleUserBinding>(BundleUserBinding.class);
		c.getPrimaryGroup().addCriterion("bundleId", bundleId, Comparator.EQUALS, true);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		BundleUserBinding binding;
		try {
			binding = dao.findEntity(c);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
		dao.purge(binding);
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
		DocUserBinding binding = new DocUserBinding(docId, userId);
		dao.persist(binding);
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
		Criteria<DocUserBinding> c = new Criteria<DocUserBinding>(DocUserBinding.class);
		c.getPrimaryGroup().addCriterion("docId", docId, Comparator.EQUALS, true);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		DocUserBinding binding;
		try {
			binding = dao.findEntity(c);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
		dao.purge(binding);
	}

	/**
	 * Removes all user/doc bindings that exist for a given doc
	 * @param docId id of the doc
	 */
	@Transactional
	public void removeAllDocUserBindingsForDoc(String docId) {
		if(docId == null) throw new NullPointerException();
		Criteria<DocUserBinding> c = new Criteria<DocUserBinding>(DocUserBinding.class);
		c.getPrimaryGroup().addCriterion("docId", docId, Comparator.EQUALS, true);
		Collection<DocUserBinding> bindings;
		try {
			bindings = dao.findEntities(c, null);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
		if(bindings != null && bindings.size() > 0) {
			dao.purgeAll(bindings);
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
		QuoteUserBinding binding = new QuoteUserBinding(quoteId, userId);
		dao.persist(binding);
	}

	/**
	 * Removes a user quote association.
	 * @param userId
	 * @param quoteId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	@Transactional
	public void removeQuoteUserBinding(String userId, String quoteId) throws EntityNotFoundException {
		QuoteUserBinding binding = findQuoteUserBinding(userId, quoteId);
		dao.purge(binding);
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
	@Transactional
	public void updateBundleUserBinding(String userId, String bundleId, boolean orphan) throws EntityNotFoundException {
		BundleUserBinding binding = findBundleUserBinding(userId, bundleId);
		binding.setOrphaned(orphan);
		dao.persist(binding);
	}

	private BundleUserBinding findBundleUserBinding(String userId, String bundleId) throws EntityNotFoundException {
		if(bundleId == null || userId == null) throw new NullPointerException();
		Criteria<BundleUserBinding> c = new Criteria<BundleUserBinding>(BundleUserBinding.class);
		c.getPrimaryGroup().addCriterion("bundleId", bundleId, Comparator.EQUALS, true);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		try {
			return dao.findEntity(c);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
	}

	private QuoteUserBinding findQuoteUserBinding(String userId, String quoteId) throws EntityNotFoundException {
		if(quoteId == null || userId == null) throw new NullPointerException();
		Criteria<QuoteUserBinding> c = new Criteria<QuoteUserBinding>(QuoteUserBinding.class);
		c.getPrimaryGroup().addCriterion("quoteId", quoteId, Comparator.EQUALS, true);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		try {
			return dao.findEntity(c);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Returns a new list whose elements are clones of those in the given doc
	 * list.
	 * <p>
	 * Consequently, the <code>htmlContent</code> property is not specified in the
	 * new cloned elements.
	 * @param dlist doc list to clone
	 * @return new list of cloned doc elements
	 */
	private List<DocRef> cloneDocList(List<DocRef> dlist) {
		ArrayList<DocRef> clist = new ArrayList<DocRef>(dlist.size());
		for(DocRef d : dlist) {
			clist.add((DocRef) d.clone());
		}
		return clist;
	}
}
