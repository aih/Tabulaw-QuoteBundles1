/**
 * The Logic Lab
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tabulaw.client.app.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.model.IHasModelChangeHandlers;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.rpc.IUserDataService;
import com.tabulaw.common.data.rpc.IUserDataServiceAsync;
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

	private static final IUserDataServiceAsync userDataService;

	static {
		userDataService = (IUserDataServiceAsync) GWT.create(IUserDataService.class);
	}
	
	public static IUserDataServiceAsync getUserDataService() {
		return userDataService;
	}

	private static ClientModelCache instance;

	public static ClientModelCache get() {
		if(instance == null) throw new IllegalStateException("Init must be called.");
		return instance;
	}
	
	public static void init(IHasModelChangeHandlers modelChangeDispatcher) {
		instance = new ClientModelCache(modelChangeDispatcher);
	}

	static class IdCache {

		int current, max;

		/**
		 * Constructor
		 * @param startEndRange 2 element array where first element is start, second
		 *        element is end
		 */
		public IdCache(Integer[] startEndRange) {
			super();
			this.current = startEndRange[0].intValue();
			this.max = startEndRange[1].intValue();
		}

		int getNextId() {
			if(current > max) {
				// TODO xhr call to get next batch
				throw new IllegalStateException("Ran out of ids");
			}
			return current++;
		}
	}

	private static void handlePersistResponse(Payload payload) {
		Notifier.get().showFor(payload, "Persist op successful.");

		// TODO what do we do with the persisted entity in the payload ???
		// we don't want a collision in accessing the sole entity map which
		// as i see it is possible
	}
	
	private final IHasModelChangeHandlers modelChangeDispatcher;

	private final HashMap<String, IdCache> nextIdCache = new HashMap<String, IdCache>();

	private final HashMap<String, List<IEntity>> entities = new HashMap<String, List<IEntity>>();

	private final boolean doServerPersist;

	/**
	 * Constructor
	 */
	private ClientModelCache(IHasModelChangeHandlers modelChangeDispatcher) {
		if(modelChangeDispatcher == null) throw new NullPointerException();
		this.modelChangeDispatcher = modelChangeDispatcher;

		// init entities map
		for(EntityType et : EntityType.values()) {
			entities.put(et.name(), new ArrayList<IEntity>());
		}

		doServerPersist = true;

	} // constructor

	public void saveBundle(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.saveBundleForUser(userId, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(ModelPayload<QuoteBundle> result) {
				handlePersistResponse(result);
			}
		});
	}

	public void updateBundleProps(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.updateBundlePropsForUser(userId, bundle, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addBundle(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.addBundleForUser(userId, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(ModelPayload<QuoteBundle> result) {
				handlePersistResponse(result);
			}
		});
	}

	public void deleteBundle(String bundleId, boolean deleteQuotes) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.deleteBundleForUser(userId, bundleId, deleteQuotes, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addQuoteToBundle(String bundleId, Quote quote) {
		if(!doServerPersist) return;
		userDataService.addQuoteToBundle(bundleId, quote, new AsyncCallback<ModelPayload<Quote>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(ModelPayload<Quote> result) {
				handlePersistResponse(result);
			}
		});
	}

	public void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote) {
		if(!doServerPersist) return;
		userDataService.removeQuoteFromBundle(bundleId, quoteId, deleteQuote, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addBundleUserBinding(String bundleId) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.addBundleUserBinding(userId, bundleId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addDocUserBinding(String docId) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.addDocUserBinding(userId, docId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void removeBundleUserBinding(String bundleId) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.removeBundleUserBinding(userId, bundleId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void removeDocUserBinding(String docId) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.removeBundleUserBinding(userId, docId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	/**
	 * Sets a batch of ids for use in entity creation.
	 * @param nextIdBatch
	 */
	public void setNextIdBatch(Map<String, Integer[]> nextIdBatch) {
		for(String et : nextIdBatch.keySet()) {
			Integer[] rng = nextIdBatch.get(et);
			IdCache idCache = new IdCache(rng);
			nextIdCache.put(et, idCache);
		}
	}

	/**
	 * Gets the next available id for use in newly created entities
	 * @param entityType
	 * @return
	 * @throws IllegalArgumentException When there are no cached ids for the given
	 *         entity type
	 */
	public String getNextId(String entityType) throws IllegalArgumentException {
		IdCache idCache = nextIdCache.get(entityType);
		if(idCache == null) throw new IllegalArgumentException("No ids cached for entity type: " + entityType);
		int nextId = idCache.getNextId();
		return Integer.toString(nextId);
	}

	/**
	 * Clears out the entire model entities.
	 * <p>
	 * No model change event is fired.
	 */
	public void clear() {
		for(List<?> list : entities.values()) {
			list.clear();
		}
	}

	/**
	 * @return The currently logged in user.
	 */
	public User getUser() {
		List<? extends IEntity> list = entities.get(EntityType.USER.name());
		return list.size() < 1 ? null : (User) list.get(0).clone();
	}

	/**
	 * @return The logged in user's sole {@link UserState} instance.
	 */
	public UserState getUserState() {
		List<?> list = entities.get(EntityType.USER_STATE.name());
		UserState userState = list.size() < 1 ? null : (UserState) list.get(0);
		return userState;
	}

	/**
	 * Persists the user state to the server.
	 * @param cmd optional command to execute upon return irregardless of error.
	 */
	public void saveUserState(final Command cmd) {
		UserState userState = getUserState();
		if(userState != null) {
			userDataService.saveUserState(userState, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					if(cmd != null) cmd.execute();
				}

				@Override
				public void onFailure(Throwable caught) {
					if(cmd != null) cmd.execute();
				}
			});
		}
	}

	/**
	 * @return the current quote bundle or <code>null</code> if not set.
	 */
	public QuoteBundle getCurrentQuoteBundle() {
		UserState userState = getUserState();
		if(userState != null) {
			String qbid = userState.getCurrentQuoteBundleId();
			if(qbid != null) {
				try {
					return (QuoteBundle) get(new ModelKey(EntityType.QUOTE_BUNDLE.name(), qbid));
				}
				catch(EntityNotFoundException e) {
					// ok - fall through
				}
			}
		}
		return null;
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
		List<? extends IEntity> list = entities.get(key.getEntityType());
		final String id = key.getId();
		for(IEntity m : list) {
			if(m.getId().equals(id)) {
				return m.clone();
			}
		}
		throw new EntityNotFoundException("Entity of type: " + key.getEntityType() + " and id: " + id
				+ " not found in client datastore.");
	}

	/**
	 * @param etype the entity type
	 * @return All existing entities of the given type.
	 */
	@SuppressWarnings("unchecked")
	public List<?> getAll(EntityType etype) {
		List<? extends IEntity> list = entities.get(etype.name());
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
			String nextId = getNextId(m.getEntityType());
			m.setId(nextId);
			if(Log.isDebugEnabled()) Log.debug("Set entity id on: " + m);
		}
		List<IEntity> list = entities.get(m.getEntityType());

		IEntity existing = null;

		// NO this logic if now bad since we are persisting server-side in parallel
		// w/o any callback based updating to the entities held in this cache!
		// if(!m.isNew()) {
		for(IEntity em : list) {
			if(em.equals(m)) {
				existing = em;
				break;
			}
		}
		// }

		ModelChangeOp op = existing == null ? ModelChangeOp.ADDED : ModelChangeOp.UPDATED;

		if(existing != null) {
			list.remove(existing);
		}

		// TODO figure out how to avoid multiple clone() calls!
		IEntity copy = m.clone(), copy2 = m.clone();
		list.add(copy);

		// fire model change event
		if(source != null) modelChangeDispatcher.fireEvent(new ModelChangeEvent(source, op, copy2, null));
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
	 * @param key
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
		List<IEntity> list = entities.get(key.getEntityType());
		final String id = key.getId();
		IEntity t = null;
		for(IEntity m : list) {
			if(m.getId().equals(id)) {
				t = m;
				break;
			}
		}
		if(t == null) throw new EntityNotFoundException("Entity of key: '" + key + "' not found for remove.");
		list.remove(t);
		// fire model change event
		if(source != null) modelChangeDispatcher.fireEvent(new ModelChangeEvent(source, ModelChangeOp.DELETED, t, null));
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
		List<IEntity> list = entities.get(etype.name());
		if(list == null || list.size() < 1) return;
		ArrayList<IEntity> rlist = new ArrayList<IEntity>(list);
		for(IEntity m : rlist) {
			list.remove(m);
		}

		// fire model change event(s)
		if(source != null) {
			for(IEntity e : rlist) {
				modelChangeDispatcher.fireEvent(new ModelChangeEvent(source, ModelChangeOp.DELETED, e, null));
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
		if(doc1.equals(doc2)) {
			return ObjectUtil.equals(q1.getQuote(), q2.getQuote());
		}
		return false;
	}

	public DocRef getCaseDocByRemoteUrl(String remoteCaseUrl) {
		List<IEntity> list = entities.get(EntityType.DOCUMENT.name());
		for(IEntity m : list) {
			CaseRef caseRef = ((DocRef) m).getCaseRef();
			if(caseRef == null) continue;
			String surl = caseRef.getUrl();
			if(surl != null && surl.equals(remoteCaseUrl)) return (DocRef) m;
		}
		return null;
	}
}
