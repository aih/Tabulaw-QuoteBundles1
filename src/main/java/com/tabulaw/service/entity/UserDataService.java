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
import com.tabulaw.dao.Sorting;
import com.tabulaw.util.Comparator;

/**
 * Manages the persistence of user related data that is not part of the user
 * entity.
 * @author jpk
 */
public class UserDataService extends AbstractEntityService {

	/**
	 * Constructor
	 * @param dao
	 * @param validationFactory
	 */
	@Inject
	public UserDataService(IEntityDao dao, ValidatorFactory validationFactory) {
		super(dao, validationFactory);
	}

	@Transactional(readOnly = true)
	public List<Quote> getOrphanedQuotesForUser(String userId) {
		if(userId == null) throw new NullPointerException();

		Criteria<QuoteUserBinding> c = new Criteria<QuoteUserBinding>(QuoteUserBinding.class);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		c.getPrimaryGroup().addCriterion("orphaned", Boolean.TRUE, Comparator.EQUALS, true);

		List<QuoteUserBinding> bindings;
		try {
			bindings = dao.findEntities(c, null);
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
		ArrayList<String> ids = new ArrayList<String>(bindings.size());
		for(QuoteUserBinding binding : bindings) {
			assert binding.isOrphaned(); 
			ids.add(binding.getQuoteId());
		}
		List<Quote> list = dao.findByIds(Quote.class, ids, null);
		return list;
	}

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
			return list;
		}
		catch(InvalidCriteriaException e) {
			throw new IllegalStateException(e);
		}
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

	@Transactional(readOnly = true)
	public List<QuoteBundle> getBundlesForUser(String userId) {
		if(userId == null) throw new NullPointerException();
		Criteria<BundleUserBinding> c = new Criteria<BundleUserBinding>(BundleUserBinding.class);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		try {
			List<BundleUserBinding> bindings = dao.findEntities(c, null);
			if(bindings.size() < 1) return new ArrayList<QuoteBundle>(0);
			ArrayList<String> bundleIds = new ArrayList<String>(bindings.size());
			for(BundleUserBinding b : bindings) {
				bundleIds.add(b.getBundleId());
			}
			List<QuoteBundle> list = dao.findByIds(QuoteBundle.class, bundleIds, new Sorting("name"));
			return list;
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
	 * Saves the doc for the given user.
	 * @param userId
	 * @param doc
	 * @return the saved doc
	 * @throws ConstraintViolationException When the given doc isn't valid
	 */
	@Transactional
	public DocRef saveDocForUser(String userId, DocRef doc) throws ConstraintViolationException {
		if(userId == null || doc == null) throw new NullPointerException();

		validate(doc);

		DocRef existing;
		try {
			existing = dao.load(DocRef.class, doc.getId());
		}
		catch(EntityNotFoundException e) {
			// new
			existing = null;
		}

		// save the doc
		if(existing == null) {
			doc = dao.persist(doc);
		}
		else {
			doc = existing;
		}

		// create binding
		try {
			addDocUserBinding(userId, doc.getId());
		}
		catch(EntityExistsException e) {
			// ok
		}

		return doc;
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
	 *        any contained quotes may end up in an orphaned state meaning they
	 *        will not be referenced by any existing bundle
	 * @throws EntityNotFoundException When either the user or bundle can't be
	 *         resolved
	 */
	@Transactional
	public void deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes) throws EntityNotFoundException {
		if(deleteQuotes) {
			QuoteBundle bundle = dao.load(QuoteBundle.class, bundleId);
			List<Quote> quotes = bundle.getQuotes();
			if(quotes != null) {
				for(Quote q : quotes) {
					dao.purge(q);
				}
			}
			dao.purge(bundle);
		}
		else {
			dao.purge(QuoteBundle.class, bundleId);
		}
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
	 * Removes an existing quote from an existing bundle.
	 * <p>
	 * The quote is <em>not</em> deleted rather it is put into a potentially
	 * "orphaned" state meaning no bundles may reference the removed quote.
	 * <p>
	 * @param userId needed for removing the quote/user binding
	 * @param bundleId
	 * @param quoteId
	 * @param deleteQuote delete the quote as well? If <code>false</code>, the
	 *        quote will be orphaned.
	 * @throws EntityNotFoundException when the quote isn't found to exist in the
	 *         bundle
	 */
	@Transactional
	public void removeQuoteFromBundle(String userId, String bundleId, String quoteId, boolean deleteQuote)
			throws EntityNotFoundException {
		if(userId == null || bundleId == null || quoteId == null) throw new NullPointerException();
		QuoteBundle qb = dao.load(QuoteBundle.class, bundleId);
		Quote tormv = null;
		if(qb.getQuotes() != null) {
			for(Quote q : qb.getQuotes()) {
				if(q.getId().equals(quoteId)) {
					tormv = q;
					break;
				}
			}
		}
		if(tormv == null) throw new EntityNotFoundException("Quote: " + quoteId + " not found in bundle: " + bundleId);
		qb.removeQuote(tormv);
		dao.persist(qb);

		if(deleteQuote) {
			dao.purge(tormv);
			removeQuoteUserBinding(userId, quoteId);
		}
		else {
			// mark quote as orphaned
			updateQuoteUserBinding(userId, quoteId, true);
		}
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
		BundleUserBinding binding = new BundleUserBinding(bundleId, userId);
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
		QuoteUserBinding binding = new QuoteUserBinding(quoteId, userId, false);
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
	 * @param quoteId
	 * @param orphan
	 * @throws EntityNotFoundException
	 */
	@Transactional
	public void updateQuoteUserBinding(String userId, String quoteId, boolean orphan) throws EntityNotFoundException {
		QuoteUserBinding binding = findQuoteUserBinding(userId, quoteId);
		binding.setOrphaned(orphan);
		dao.persist(binding);
	}

	/**
	 * Updates an existing quote/user binding's setting the orphan property to
	 * <code>false</code>, then add the quote to the given bundle.
	 * @param userId resolves the quote/user binding
	 * @param quoteId id of quote to un-orphan
	 * @param bundleId id of bundle to which the quote is added
	 * @throws EntityNotFoundException When the quote/user binding or quote or
	 *         bundle doesn't exist
	 */
	public void unorphanQuote(String userId, String quoteId, String bundleId) throws EntityNotFoundException {
		if(bundleId == null) throw new NullPointerException();
		QuoteUserBinding binding = findQuoteUserBinding(userId, quoteId);
		Quote q = dao.load(Quote.class, quoteId);
		QuoteBundle qb = dao.load(QuoteBundle.class, bundleId);
		qb.addQuote(q);
		dao.persist(qb);
		binding.setOrphaned(false);
		dao.persist(binding);
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
}
