/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.service.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tabulaw.common.model.BundleUserBinding;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
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
	 * Generates assignable surrogate primary keys for quotes guaranteed to
	 * be unique throughout the life of the datastore.
	 * @param numIds the number of ids to generate
	 * @return list of generated ids
	 */
	@Transactional
	public long[] generateQuoteIds(int numIds) {
		long[] idRange = dao.generatePrimaryKeyBatch(Quote.class, numIds);
		return idRange;
	}

	@Transactional(readOnly = true)
	public UserState getUserState(String userId) throws EntityNotFoundException {
		return dao.load(UserState.class, userId);
	}

	@Transactional
	public void saveUserState(UserState userState) throws EntityExistsException {
		dao.persist(userState);
	}

	@Transactional(readOnly = true)
	public List<QuoteBundle> getBundlesForUser(String userId) {
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
	 * Saves the quote bundle for the given user as well as any referenced quotes
	 * doing a full replacement of the bundle with that given as well as the child
	 * quotes.
	 * @param userId
	 * @param bundle
	 * @return the saved bundle
	 * @throws ConstraintViolationException When the given bundle isn't valid
	 */
	@Transactional
	public QuoteBundle saveBundleForUser(String userId, QuoteBundle bundle) 
	throws ConstraintViolationException {
		if(userId == null) throw new NullPointerException();

		validate(bundle);

		boolean isNew = bundle.isNew();

		// clear out existing
		if(!isNew) {
			if(bundle.getId() == null) throw new IllegalArgumentException();
			dao.purge(QuoteBundle.class, bundle.getId());
		}
		List<Quote> existingQuotes = bundle.getQuotes();
		if(existingQuotes != null) {
			for(Quote eq : existingQuotes) {
				if(!eq.isNew()) {
					if(eq.getId() == null) throw new IllegalArgumentException();
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
		if(isNew) addBundleUserBinding(bundle.getId(), userId);

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
	public QuoteBundle addBundleForUser(String userId, QuoteBundle bundle) 
	throws ConstraintViolationException {
		if(!bundle.isNew()) throw new IllegalArgumentException("Bundle already added.");
		validate(bundle);
		QuoteBundle persistedBundle = dao.persist(bundle);
		addBundleUserBinding(persistedBundle.getId(), userId);
		return persistedBundle;
	}

	/**
	 * Deletes a quote bundle and its association to the given user.
	 * <p>
	 * <b>WARNING:</b> any referenced quotes under the bundle will be orphaned if
	 * they do not belong to any other bundles!
	 * @param userId
	 * @param bundleId
	 * @throws EntityNotFoundException When either the user or bundle can't be
	 *         resolved
	 */
	@Transactional
	public void deleteBundleForUser(String userId, String bundleId) throws EntityNotFoundException {
		dao.purge(QuoteBundle.class, bundleId);
		removeBundleUserBinding(bundleId, userId);
	}

	/**
	 * Adds the given quote to the quote bundle identified by the given bundle id.
	 * @param bundleId
	 * @param quote
	 * @return the persisted quote
	 * @throws ConstraintViolationException When the quote doesn't validate
	 * @throws EntityNotFoundException When the bundle can't be found from the given id
	 */
	@Transactional
	public Quote addQuoteToBundle(String bundleId, Quote quote) 
	throws ConstraintViolationException, EntityNotFoundException {
		if(!quote.isNew()) throw new IllegalArgumentException("Quote isn't new");
		validate(quote);
		QuoteBundle qb = dao.load(QuoteBundle.class, bundleId);
		assert qb != null;
		Quote persistedQuote = dao.persist(quote);
		qb.addQuote(persistedQuote);
		dao.persist(qb);
		return persistedQuote;
	}

	/**
	 * Removes an existing quote from an existing bundle.
	 * <p>
	 * The quote is <em>not</em> deleted rather it is put into a potentially
	 * "orphaned" state meaning no bundles may reference the removed quote.
	 * <p>
	 * TODO handle orphaned quotes case
	 * @param bundleId
	 * @param quoteId
	 * @param deleteQuote delete the quote as well?
	 * @throws EntityNotFoundException when the quote isn't found to exist in the
	 *         bundle
	 */
	@Transactional
	public void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote)
			throws EntityNotFoundException {
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
		}
	}

	/**
	 * Adds an association of an existing quote bundle to an existing user.
	 * @param bundleId
	 * @param userId
	 * @throws EntityExistsException if the association already exists
	 */
	@Transactional
	public void addBundleUserBinding(String bundleId, String userId) throws EntityExistsException {
		if(bundleId == null || userId == null) throw new NullPointerException();
		BundleUserBinding binding = new BundleUserBinding(bundleId, userId);
		dao.persist(binding);
	}

	/**
	 * Removes a user bundle association.
	 * @param bundleId
	 * @param userId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	@Transactional
	public void removeBundleUserBinding(String bundleId, String userId) throws EntityNotFoundException {
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
}
