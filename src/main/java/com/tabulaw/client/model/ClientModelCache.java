/**
 * The Logic Lab
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tabulaw.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.mortbay.log.Log;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.ModelKey;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.User;
import com.tll.util.ObjectUtil;

/**
 * @author jpk
 */
public class ClientModelCache {

	private static final ClientModelCache instance = new ClientModelCache();

	public static ClientModelCache get() {
		return instance;
	}

	private final HashMap<EntityType, List<IEntity>> cache = new HashMap<EntityType, List<IEntity>>();

	//private final HashMap<EntityType, Comparator<? extends IEntity>> comparators = new HashMap<EntityType, Comparator<? extends IEntity>>();

	/**
	 * Constructor
	 */
	private ClientModelCache() {

		// init cache
		for(EntityType et : EntityType.values()) {
			cache.put(et, new ArrayList<IEntity>());
		}

		/*
		comparators.put(EntityType.CASE, new Comparator<CaseRef>() {

			@Override
			public int compare(CaseRef o1, CaseRef o2) {
				int year1 = o1.getYear();
				int year2 = o2.getYear();
				if(year1 > year2) return 1;
				if(year2 > year1) return -1;
				String c1 = o1.getCitation();
				String c2 = o2.getCitation();
				return c1.compareTo(c2);
			}
		});

		comparators.put(EntityType.DOCUMENT, new Comparator<Model>() {

			@Override
			public int compare(Model o1, Model o2) {
				String title1 = o1.asString("title");
				String title2 = o2.asString("title");
				return title1.compareTo(title2);
			}
		});

		comparators.put(EntityType.QUOTE, new Comparator<Model>() {

			@Override
			public int compare(Model o1, Model o2) {
				String docTitle1 = o1.asString("document.title");
				String docTitle2 = o2.asString("document.title");
				int rcmp = docTitle1.compareTo(docTitle2);
				if(rcmp != 0) return rcmp;
				// TODO sort by mark!
				return rcmp;
			}
		});

		comparators.put(EntityType.QUOTE_BUNDLE, new Comparator<Model>() {

			@Override
			public int compare(Model o1, Model o2) {
				String name1 = o1.getName();
				String name2 = o2.getName();
				return name1.compareTo(name2);
			}
		});
		*/

	} // constructor
	
	/**
	 * Clears out the entire model cache.
	 * <p>No model change event is fired.
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
		List<?> list = cache.get(EntityType.USER);
		return list.size() < 1 ? null : (User) list.get(0);
	}

	/**
	 * Provides the next available id for use in creating new entities.
	 * @param entityType the desired entity type
	 * @return the next available id
	 */
	/*
	public String getNextId(EntityType entityType) {
		Log.error("ERROR: calling getNextId() for: " + entityType);
		List<? extends IEntity> mclc = cache.get(entityType);
		int largest = 0;
		for(IEntity m : mclc) {
			int id = m.getId();
			if(id > largest) largest = id;
		}
		return Integer.toString(largest + 1);
	}
	*/

	/**
	 * Gets the model identified by the given key.
	 * <p>
	 * A copy of the model is made retaining reference type relations to other
	 * model instances.
	 * @param key
	 * @return the found model.
	 * @throws IllegalArgumentException When the model can't be found.
	 */
	public IEntity get(ModelKey key) {
		List<? extends IEntity> list = cache.get(key.getEntityType());
		for(IEntity m : list) {
			if(m.getKey().equals(key)) {
				return m.clone();
			}
		}
		throw new IllegalArgumentException("Model of key: " + key + " not found in datastore.");
	}

	/**
	 * @param etype the entity type
	 * @return All existing entities of the given type.
	 */
	public List<IEntity> getAll(EntityType etype) {
		List<? extends IEntity> list = cache.get(etype);
		ArrayList<IEntity> rlist = new ArrayList<IEntity>(list.size());
		for(IEntity m : list) {
			rlist.add(m.clone());
		}
		/*
		if(rlist.size() > 1) {
			Collections.sort(rlist, new Comparator<IEntity>() {
				
				@Override
				public int compare(IEntity o1, IEntity o2) {
					return o1.compareTo(o2);
				}
			});
		}
		*/
		return rlist;
	}

	/**
	 * Persits the given model.
	 * <p>
	 * Fires a model change event if successful.
	 * @param m model to persist
	 * @param source optional source widget when specified, a model change event
	 *        is fired on it
	 */
	public void persist(IEntity m, Widget source) {
		List<IEntity> list = cache.get(m.getEntityType());

		IEntity existing = null;

		if(!m.isNew()) {
			for(IEntity em : list) {
				if(em.getKey().equals(m.getKey())) {
					existing = em;
					break;
				}
			}
		}
		else {
			Log.warn("No id set for just persisted entity: " + m);
			//m.setId(getNextId((EntityType) m.getEntityType()));
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
	 */
	public IEntity remove(ModelKey key, Widget source) {
		List<IEntity> set = cache.get(key.getEntityType());
		IEntity t = null;
		for(IEntity m : set) {
			if(m.getKey().equals(key)) {
				t = m;
				break;
			}
		}
		if(t != null) {
			if(set.remove(t)) {
				// fire model change event
				if(source != null) source.fireEvent(new ModelChangeEvent(ModelChangeOp.DELETED, t, null));
				return t;
			}
		}
		return null;
	}

	/**
	 * Removes all model instances of the given entity type firing a model change
	 * event for <em>each</em> removed model only if the given source is not
	 * <code>null</code>.
	 * @param etype the entity type of which to remove all instances
	 * @param source the sourcing widget that will source a model change event
	 */
	public void removeAll(EntityType etype, Widget source) {
		List<IEntity> list = cache.get(etype);
		if(list == null || list.size() < 1) return;
		ArrayList<IEntity> rlist = new ArrayList<IEntity>(list);
		for(IEntity m : rlist) {
			if(list.remove(m)) {
				// fire model change event
				if(source != null) source.fireEvent(new ModelChangeEvent(ModelChangeOp.DELETED, m, null));
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
		if(doc1.getKey().equals(doc2.getKey())) {
			return ObjectUtil.equals(q1.getQuote(), q2.getQuote());
		}
		return false;
	}

	public DocRef getCaseDocByRemoteUrl(String remoteCaseUrl) {
		List<IEntity> list = cache.get(EntityType.DOCUMENT);
		for(IEntity m : list) {
			CaseRef caseRef = ((DocRef)m).getCaseRef();
			if(caseRef == null) continue;
			String surl = caseRef.getUrl();
			if(surl != null && surl.equals(remoteCaseUrl)) return (DocRef) m;
		}
		return null;
	}
}
