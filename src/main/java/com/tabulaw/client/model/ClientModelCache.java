/**
 * The Logic Lab
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tabulaw.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.ModelKey;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.UserState;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.util.ObjectUtil;

/**
 * @author jpk
 */
public class ClientModelCache {

	private static final ClientModelCache instance = new ClientModelCache();

	public static ClientModelCache get() {
		return instance;
	}

	private final HashMap<String, List<IEntity>> cache = new HashMap<String, List<IEntity>>();

	/**
	 * Constructor
	 */
	private ClientModelCache() {

		// init cache
		for(EntityType et : EntityType.values()) {
			cache.put(et.name(), new ArrayList<IEntity>());
		}

	} // constructor

	/**
	 * Clears out the entire model cache.
	 * <p>
	 * No model change event is fired.
	 */
	public void clear() {
		for(List<?> list : cache.values()) {
			list.clear();
		}
	}

	/**
	 * @return The currently logged in user.
	 */
	public User getUser() {
		List<?> list = cache.get(EntityType.USER.name());
		return list.size() < 1 ? null : (User) list.get(0);
	}

	/**
	 * @return The current quote bundle as a function of the cached
	 *         {@link UserState}.
	 */
	public QuoteBundle getCurrentQuoteBundle() {
		List<?> list = cache.get(EntityType.USER_STATE.name());
		UserState userState = list.size() < 1 ? null : (UserState) list.get(0);
		if(userState == null) throw new IllegalStateException();
		String bundleId = userState.getCurrentQuoteBundleId();
		if(bundleId != null) {
			return (QuoteBundle) get(new ModelKey(EntityType.QUOTE_BUNDLE.name(), bundleId));
		}
		return null;
	}

	/**
	 * Sets the current quote bundle for the logged in user.
	 * <p>
	 * <b>NOTE:</b> this method does not fire a model change event.
	 * @param bundleId id of the quote bundle for which to set as current
	 * @return <code>true</code> if the current quote bundle was set meaning the
	 *         current value wasn't set or is different that that given
	 */
	public boolean setCurrentQuoteBundle(String bundleId) {
		if(bundleId == null) throw new NullPointerException();
		List<?> list = cache.get(EntityType.USER_STATE.name());
		UserState userState = list.size() < 1 ? null : (UserState) list.get(0);
		if(userState == null) throw new IllegalStateException();
		String currentBundleId = userState.getCurrentQuoteBundleId();
		if(currentBundleId == null || !currentBundleId.equals(bundleId)) {
			userState.setCurrentQuoteBundleId(bundleId);
			return true;
		}
		return false;
	}

	/**
	 * Gets the model identified by the given key.
	 * <p>
	 * A copy of the model is made retaining reference type relations to other
	 * model instances.
	 * @param key
	 * @return the found model.
	 * @throws IllegalArgumentException When the given key is not set
	 * @throws EntityNotFoundException When the model can't be found
	 */
	public IEntity get(ModelKey key) throws IllegalArgumentException, EntityNotFoundException {
		if(key == null) throw new NullPointerException();
		if(!key.isSet()) throw new IllegalArgumentException("Key not set");
		List<? extends IEntity> list = cache.get(key.getEntityType());
		for(IEntity m : list) {
			if(m.getId().equals(key.getId())) {
				return m.clone();
			}
		}
		throw new EntityNotFoundException("Model of key: " + key + " not found in datastore.");
	}

	/**
	 * @param etype the entity type
	 * @return All existing entities of the given type.
	 */
	@SuppressWarnings("unchecked")
	public List<?> getAll(EntityType etype) {
		List<? extends IEntity> list = cache.get(etype.name());
		ArrayList<IEntity> rlist = new ArrayList<IEntity>(list.size());
		for(IEntity m : list) {
			rlist.add(m.clone());
		}
		if(rlist.size() > 1) {
			Collections.sort(rlist, new Comparator<Object>() {

				@Override
				public int compare(Object o1, Object o2) {
					return ((Comparable<Object>) o1).compareTo(o2);
				}
			});
		}
		return rlist;
	}

	/**
	 * Persits the given model.
	 * <p>
	 * Fires a model change event if successful.
	 * @param m model to persist
	 * @param source optional source widget when specified, a model change event
	 *        is fired on it
	 * @throws IllegalArgumentException When the entity has no id.
	 */
	public void persist(IEntity m, Widget source) throws IllegalArgumentException {
		if(m == null) throw new NullPointerException();
		if(m.getId() == null) {
			throw new IllegalArgumentException("Entity '" + m + "' has no id");
		}
		List<IEntity> list = cache.get(m.getEntityType().name());

		IEntity existing = null;

		if(!m.isNew()) {
			for(IEntity em : list) {
				if(em.getId().equals(m.getId())) {
					existing = em;
					break;
				}
			}
		}

		ModelChangeOp op = existing == null ? ModelChangeOp.ADDED : ModelChangeOp.UPDATED;

		if(existing != null) {
			list.remove(existing);
		}

		IEntity copy = m.clone();
		list.add(copy);

		// fire model change event
		if(source != null) source.fireEvent(new ModelChangeEvent(op, copy, null));
	}

	/**
	 * Cache all model instances in the given collection.
	 * <p>
	 * NO model change event will be fired.
	 * @param clc collection containing model data to persist
	 */
	public void persistAll(Collection<? extends IEntity> clc) {
		for(IEntity m : clc) {
			persist(m, null);
		}
	}

	/**
	 * Cache all model instances in the given collection firing a model change
	 * event for <em>each</em> model in the given collection if the given source
	 * is not <code>null</code>.
	 * @param clc collection containing model data to persist
	 * @param source the sourcing widget that will source a model change event
	 */
	public void persistAll(Collection<? extends IEntity> clc, Widget source) {
		for(IEntity m : clc) {
			persist(m, source);
		}
	}

	/**
	 * Removes the model identified by the given key from this datastore.
	 * <p>
	 * Fires a {@link ModelChangeEvent} if successful.
	 * @param key The model key identifying the model to delete
	 * @param source optional source widget when specified, a model change event
	 *        is fired on it
	 * @return The deleted model or <code>null</code> if the model was not
	 *         deleted.
	 * @throws IllegalArgumentException When the given key is not set
	 * @throws EntityNotFoundException When the entity to purge is not found
	 */
	public IEntity remove(ModelKey key, Widget source) throws IllegalArgumentException, EntityNotFoundException {
		if(key == null) throw new NullPointerException();
		if(!key.isSet()) throw new IllegalArgumentException("Key not set");
		List<IEntity> list = cache.get(key.getEntityType());
		IEntity t = null;
		for(IEntity m : list) {
			if(m.getId().equals(key.getId())) {
				t = m;
				break;
			}
		}
		if(t == null) throw new EntityNotFoundException("Entity of key: '" + key + "' not found for purge.");
		list.remove(t);
		// fire model change event
		if(source != null) source.fireEvent(new ModelChangeEvent(ModelChangeOp.DELETED, t, null));
		return t;
	}

	/**
	 * Removes all model instances of the given entity type firing a model change
	 * event for <em>each</em> removed model only if the given source is not
	 * <code>null</code>.
	 * @param etype the entity type of which to remove all instances
	 * @param source the sourcing widget that will source a model change event
	 */
	public void removeAll(EntityType etype, Widget source) {
		List<IEntity> list = cache.get(etype.name());
		if(list == null || list.size() < 1) return;
		ArrayList<IEntity> rlist = new ArrayList<IEntity>(list);
		for(IEntity m : rlist) {
			list.remove(m);
		}

		// fire model change event(s)
		if(source != null) {
			for(IEntity e : rlist) {
				source.fireEvent(new ModelChangeEvent(ModelChangeOp.DELETED, e, null));
			}
		}
	}

	/**
	 * Compares two quotes for equality by an implicit business key:
	 * <ul>
	 * <li>same doc ref
	 * <li>same quote text
	 * </ul>
	 * @param q1
	 * @param q2
	 * @return <code>true</code> if they are business key equal
	 */
	public boolean compareQuotes(Quote q1, Quote q2) {
		DocRef doc1 = q1.getDocument();
		DocRef doc2 = q2.getDocument();
		if(doc1.getId().equals(doc2.getId())) {
			return ObjectUtil.equals(q1.getQuote(), q2.getQuote());
		}
		return false;
	}

	public DocRef getCaseDocByRemoteUrl(String remoteCaseUrl) {
		List<IEntity> list = cache.get(EntityType.DOCUMENT.name());
		for(IEntity m : list) {
			CaseRef caseRef = ((DocRef) m).getCaseRef();
			if(caseRef == null) continue;
			String surl = caseRef.getUrl();
			if(surl != null && surl.equals(remoteCaseUrl)) return (DocRef) m;
		}
		return null;
	}
}
