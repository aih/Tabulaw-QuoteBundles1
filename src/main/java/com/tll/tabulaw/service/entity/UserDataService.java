/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.service.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidatorFactory;

import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tll.criteria.Criteria;
import com.tll.criteria.InvalidCriteriaException;
import com.tll.dao.EntityExistsException;
import com.tll.dao.EntityNotFoundException;
import com.tll.dao.IEntityDao;
import com.tll.dao.Sorting;
import com.tll.model.IEntityAssembler;
import com.tll.tabulaw.model.BundleUserBinding;
import com.tll.tabulaw.model.Quote;
import com.tll.tabulaw.model.QuoteBundle;
import com.tll.util.Comparator;

/**
 * Manages the persistence of user related data that is not part of the user
 * entity.
 * @author jpk
 */
public class UserDataService extends AbstractEntityService {

	/**
	 * Constructor
	 * @param dao
	 * @param entityAssembler
	 * @param validationFactory
	 */
	@Inject
	public UserDataService(IEntityDao dao, IEntityAssembler entityAssembler, ValidatorFactory validationFactory) {
		super(dao, entityAssembler, validationFactory);
	}

	@Transactional(readOnly = true)
	public List<QuoteBundle> getBundlesForUser(Long userId) {
		Criteria<BundleUserBinding> c = new Criteria<BundleUserBinding>(BundleUserBinding.class);
		c.getPrimaryGroup().addCriterion("userId", userId, Comparator.EQUALS, true);
		try {
			List<BundleUserBinding> bindings = dao.findEntities(c, null);
			if(bindings.size() < 1) return new ArrayList<QuoteBundle>(0);
			ArrayList<Long> bundleIds = new ArrayList<Long>(bindings.size());
			for(BundleUserBinding b : bindings) {
				bundleIds.add(b.getBundleId());
			}
			return dao.findByPrimaryKeys(QuoteBundle.class, bundleIds, new Sorting("name"));
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
	 */
	@Transactional
	public QuoteBundle saveBundleForUser(Long userId, QuoteBundle bundle) {

		validate(bundle);

		boolean isNew = bundle.isNew();

		// clear out existing
		if(!isNew) {
			dao.purge(QuoteBundle.class, bundle.getId());
		}
		List<Quote> existingQuotes = bundle.getQuotes();
		if(existingQuotes != null) {
			for(Quote eq : existingQuotes) {
				if(!eq.isNew()) dao.purge(eq);
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
	 */
	@Transactional
	public QuoteBundle addBundleForUser(long userId, QuoteBundle bundle) {
		if(!bundle.isNew()) throw new IllegalArgumentException("Bundle already added.");
		validate(bundle);
		QuoteBundle persistedBundle = dao.persist(bundle);
		addBundleUserBinding(persistedBundle.getId(), userId);
		return persistedBundle;
	}

	/**
	 * Deletes a quote bundle and its association to the given user.
	 * @param userId
	 * @param bundleId
	 * @throws EntityNotFoundException When either the user or bundle can't be
	 *         resolved
	 */
	@Transactional
	public void deleteBundleForUser(long userId, long bundleId) throws EntityNotFoundException {
		dao.purge(QuoteBundle.class, bundleId);
		removeBundleUserBinding(bundleId, userId);
	}

	/**
	 * Adds the given quote to the quote bundle identified by the given bundle id.
	 * @param bundleId
	 * @param quote
	 * @return the persisted quote
	 */
	@Transactional
	public Quote addQuoteToBundle(long bundleId, Quote quote) {
		if(!quote.isNew()) throw new IllegalArgumentException("Quote isn't new");
		validate(quote);
		QuoteBundle qb = dao.load(QuoteBundle.class, Long.valueOf(bundleId));
		assert qb != null;
		Quote persistedQuote = dao.persist(quote);
		qb.getQuotes().add(quote);
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
	public void removeQuoteFromBundle(long bundleId, long quoteId, boolean deleteQuote) throws EntityNotFoundException {
		QuoteBundle qb = dao.load(QuoteBundle.class, Long.valueOf(bundleId));
		Quote tormv = null;
		if(qb.getQuotes() != null) {
			for(Quote q : qb.getQuotes()) {
				if(q.getId().longValue() == quoteId) {
					tormv = q;
					break;
				}
			}
		}
		if(tormv == null) throw new EntityNotFoundException("Quote: " + quoteId + " not found in bundle: " + bundleId);
		qb.getQuotes().remove(tormv);
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
	public void addBundleUserBinding(long bundleId, long userId) throws EntityExistsException {
		BundleUserBinding binding = entityAssembler.assembleEntity(BundleUserBinding.class, null);
		binding.setBundleId(bundleId);
		binding.setUserId(userId);
		dao.persist(binding);
	}

	/**
	 * Removes a user bundle association.
	 * @param bundleId
	 * @param userId
	 * @throws EntityNotFoundException when the association doesn't exist
	 */
	@Transactional
	public void removeBundleUserBinding(long bundleId, long userId) throws EntityNotFoundException {
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
