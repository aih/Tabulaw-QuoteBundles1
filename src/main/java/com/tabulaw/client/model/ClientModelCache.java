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
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
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
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.util.ObjectUtil;

/**
 * @author jpk
 */
public class ClientModelCache implements IModelSyncer {

	private static final IUserDataServiceAsync userDataService;

	static {
		userDataService = (IUserDataServiceAsync) GWT.create(IUserDataService.class);
	}

	private static final ClientModelCache instance = new ClientModelCache();

	public static ClientModelCache get() {
		return instance;
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

	private static void handleXhrPersistError(Throwable caught) {
		String emsg = caught.getMessage();
		Notifier.get().error(emsg);
	}
	
	private static void handlePersistResponse(Payload payload) {
		if(payload.hasErrors()) {
			// error
			List<Msg> errorMsgs = payload.getStatus().getMsgs(MsgAttr.EXCEPTION.flag);
			if(errorMsgs.size() > 0) {
				Notifier.get().post(errorMsgs, -1);
			}
		}
		else {
			// success
			List<Msg> msgs = payload.getStatus().getMsgs();
			if(msgs == null) msgs = new ArrayList<Msg>();
			if(msgs.size() < 1) {
				msgs.add(new Msg("Persist operation successful.", MsgLevel.INFO));
			}
			Notifier.get().post(msgs, 1000);

			// TODO what do we do with the persisted entity in the payload ???
			// we don't want a collision in accessing the sole entity map which 
			// as i see it is possible 
		}
	}

	private final HashMap<EntityType, IdCache> nextIdCache = new HashMap<EntityType, IdCache>();

	private final HashMap<EntityType, List<IEntity>> entities = new HashMap<EntityType, List<IEntity>>();
	
	private final boolean doServerPersist;

	/**
	 * Constructor
	 */
	private ClientModelCache() {

		// init entities map
		for(EntityType et : EntityType.values()) {
			entities.put(et, new ArrayList<IEntity>());
		}
		
		doServerPersist = true;

	} // constructor
	
	@Override
	public void saveBundle(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.saveBundleForUser(userId, bundle, new AsyncCallback<ModelPayload>() {

			@Override
			public void onFailure(Throwable caught) {
				handleXhrPersistError(caught);
			}

			@Override
			public void onSuccess(ModelPayload result) {
				handlePersistResponse(result);
			}
		});
	}

	@Override
	public void updateBundleProps(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.updateBundlePropsForUser(userId, bundle, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				handleXhrPersistError(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	@Override
	public void addBundle(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.addBundleForUser(userId, bundle, new AsyncCallback<ModelPayload>() {

			@Override
			public void onFailure(Throwable caught) {
				handleXhrPersistError(caught);
			}

			@Override
			public void onSuccess(ModelPayload result) {
				handlePersistResponse(result);
			}
		});
	}

	@Override
	public void deleteBundle(String bundleId, boolean deleteQuotes) {
		if(!doServerPersist) return;
		String userId = getUser().getId();
		userDataService.deleteBundleForUser(userId, bundleId, deleteQuotes, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				handleXhrPersistError(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	@Override
	public void addQuoteToBundle(String bundleId, Quote quote) {
		if(!doServerPersist) return;
		userDataService.addQuoteToBundle(bundleId, quote, new AsyncCallback<ModelPayload>() {

			@Override
			public void onFailure(Throwable caught) {
				handleXhrPersistError(caught);
			}

			@Override
			public void onSuccess(ModelPayload result) {
				handlePersistResponse(result);
			}
		});
	}

	@Override
	public void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote) {
		if(!doServerPersist) return;
		userDataService.removeQuoteFromBundle(bundleId, quoteId, deleteQuote, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				handleXhrPersistError(caught);
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
	public void setNextIdBatch(Map<EntityType, Integer[]> nextIdBatch) {
		for(EntityType et : nextIdBatch.keySet()) {
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
	public String getNextId(EntityType entityType) throws IllegalArgumentException {
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
		List<? extends IEntity> list = entities.get(EntityType.USER);
		return list.size() < 1 ? null : (User) list.get(0).clone();
	}

	/**
	 * @return The logged in user's sole {@link UserState} instance.
	 */
	public UserState getUserState() {
		List<?> list = entities.get(EntityType.USER_STATE);
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
				QuoteBundle qb = (QuoteBundle) get(EntityType.QUOTE_BUNDLE, qbid);
				return qb;
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
		EntityType et = EntityType.fromString(key.getEntityType());
		return get(et, key.getId());
	}

	/**
	 * Gets the model identified by the given entity type and id.
	 * <p>
	 * A copy of the model is made retaining reference type relations to other
	 * model instances.
	 * @param entityType
	 * @param id entity id
	 * @return the found model.
	 * @throws IllegalArgumentException When one or more of the args is
	 *         un-recognized
	 * @throws EntityNotFoundException When the model can't be found
	 */
	public IEntity get(EntityType entityType, String id) throws IllegalArgumentException, EntityNotFoundException {
		if(entityType == null || id == null) throw new NullPointerException();
		List<? extends IEntity> list = entities.get(entityType);
		for(IEntity m : list) {
			if(m.getId().equals(id)) {
				return m.clone();
			}
		}
		throw new EntityNotFoundException("Entity of type: " + entityType + " and id: " + id
				+ " not found in client datastore.");
	}

	/**
	 * @param etype the entity type
	 * @return All existing entities of the given type.
	 */
	@SuppressWarnings("unchecked")
	public List<?> getAll(EntityType etype) {
		List<? extends IEntity> list = entities.get(etype);
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
			EntityType et = EntityType.fromString(m.getEntityType());
			String nextId = getNextId(et);
			m.setId(nextId);
			if(Log.isDebugEnabled()) Log.debug("Set entity id on: " + m);
		}
		EntityType et = EntityType.fromString(m.getEntityType());
		List<IEntity> list = entities.get(et);

		IEntity existing = null;

		// NO this logic if now bad since we are persisting server-side in parallel
		// w/o any callback based updating to the entities held in this cache!
		//if(!m.isNew()) {
			for(IEntity em : list) {
				if(em.getId().equals(m.getId())) {
					existing = em;
					break;
				}
			}
		//}

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
		EntityType et = EntityType.fromString(key.getEntityType());
		return remove(et, key.getId(), source);
	}
	
	/**
	 * Removes the model identified by the given entity type and id from this datastore.
	 * <p>
	 * Fires a {@link ModelChangeEvent} if successful.
	 * @param entityType
	 * @param id
	 * @param source optional source widget when specified, a model change event
	 *        is fired on it
	 * @return The deleted model or <code>null</code> if the model was not
	 *         deleted.
	 * @throws EntityNotFoundException When the entity to purge is not found
	 */
	public IEntity remove(EntityType entityType, String id, Widget source) throws EntityNotFoundException {
		if(entityType == null || id == null) throw new NullPointerException();
		List<IEntity> list = entities.get(entityType);
		IEntity t = null;
		for(IEntity m : list) {
			if(m.getId().equals(id)) {
				t = m;
				break;
			}
		}
		if(t == null)
			throw new EntityNotFoundException("Entity of key: '" + new ModelKey(entityType.name(), id)
					+ "' not found for remove.");
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
		List<IEntity> list = entities.get(etype);
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
		List<IEntity> list = entities.get(EntityType.DOCUMENT);
		for(IEntity m : list) {
			CaseRef caseRef = ((DocRef) m).getCaseRef();
			if(caseRef == null) continue;
			String surl = caseRef.getUrl();
			if(surl != null && surl.equals(remoteCaseUrl)) return (DocRef) m;
		}
		return null;
	}
}
