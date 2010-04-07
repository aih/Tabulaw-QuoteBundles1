/**
 * The Logic Lab
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tll.tabulaw.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.common.model.CopyCriteria;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.tabulaw.common.model.PocEntityType;
import com.tll.util.ObjectUtil;

/**
 * @author jpk
 */
public class PocModelCache {

	private static final PocModelCache instance = new PocModelCache();

	public static PocModelCache get() {
		return instance;
	}

	private final HashMap<PocEntityType, List<Model>> cache = new HashMap<PocEntityType, List<Model>>();

	private final HashMap<PocEntityType, Comparator<Model>> comparators = new HashMap<PocEntityType, Comparator<Model>>();

	/**
	 * Constructor
	 */
	private PocModelCache() {

		// init cache
		for(PocEntityType et : PocEntityType.values()) {
			cache.put(et, new ArrayList<Model>());
		}

		comparators.put(PocEntityType.CASE, new Comparator<Model>() {

			@Override
			public int compare(Model o1, Model o2) {
				int year1 = Integer.valueOf(o1.asString("year"));
				int year2 = Integer.valueOf(o2.asString("year"));
				if(year1 > year2) return 1;
				if(year2 > year1) return -1;
				String c1 = o1.asString("citation");
				String c2 = o2.asString("citation");
				return c1.compareTo(c2);
			}
		});

		comparators.put(PocEntityType.DOCUMENT, new Comparator<Model>() {

			@Override
			public int compare(Model o1, Model o2) {
				String title1 = o1.asString("title");
				String title2 = o2.asString("title");
				return title1.compareTo(title2);
			}
		});

		comparators.put(PocEntityType.QUOTE, new Comparator<Model>() {

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

		comparators.put(PocEntityType.QUOTE_BUNDLE, new Comparator<Model>() {

			@Override
			public int compare(Model o1, Model o2) {
				String name1 = o1.getName();
				String name2 = o2.getName();
				return name1.compareTo(name2);
			}
		});
	} // constructor

	/**
	 * Provides the next available id for use in creating new entities.
	 * @param entityType the desired entity type
	 * @return the next available id
	 */
	public String getNextId(PocEntityType entityType) {
		List<Model> mclc = cache.get(entityType);
		int largest = 0;
		for(Model m : mclc) {
			int id = Integer.parseInt(m.getId());
			if(id > largest) largest = id;
		}
		return Integer.toString(largest + 1);
	}

	/**
	 * Gets the model identified by the given key.
	 * <p>
	 * A copy of the model is made retaining reference type relations to other
	 * model instances.
	 * @param key
	 * @return the found model.
	 * @throws IllegalArgumentException When the model can't be found.
	 */
	public Model get(ModelKey key) {
		List<Model> set = cache.get(key.getEntityType());
		for(Model m : set) {
			if(m.getKey().equals(key)) {
				return m.copy(CopyCriteria.keepReferences());
			}
		}
		throw new IllegalArgumentException("Model of key: " + key + " not found in datastore.");
	}

	/**
	 * @param etype the entity type
	 * @return All existing entities of the given type.
	 */
	public List<Model> getAll(PocEntityType etype) {
		List<Model> list = cache.get(etype);
		ArrayList<Model> rlist = new ArrayList<Model>(list.size());
		for(Model m : list) {
			rlist.add(m);
		}
		if(rlist.size() > 1) {
			Comparator<Model> cmp = comparators.get(etype);
			if(cmp != null) {
				Collections.sort(rlist, cmp);
			}
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
	 */
	public void persist(Model m, Widget source) {
		List<Model> list = cache.get(m.getEntityType());

		Model existing = null;

		if(m.getId() != null) {
			for(Model em : list) {
				if(em.getKey().equals(m.getKey())) {
					existing = em;
					break;
				}
			}
		}
		else {
			m.setId(getNextId((PocEntityType) m.getEntityType()));
		}

		ModelChangeOp op = existing == null ? ModelChangeOp.ADDED : ModelChangeOp.UPDATED;

		if(existing != null) {
			list.remove(existing);
		}

		Model copy = m.copy(CopyCriteria.keepReferences());
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
	public void persistAll(Collection<Model> clc) {
		for(Model m : clc) {
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
	public void persistAll(Collection<Model> clc, Widget source) {
		for(Model m : clc) {
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
	public Model remove(ModelKey key, Widget source) {
		List<Model> set = cache.get(key.getEntityType());
		Model t = null;
		for(Model m : set) {
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
	 * Compares two quotes for equality by an implicit business key:
	 * <ul>
	 * <li>same doc ref
	 * <li>same quote text
	 * </ul>
	 * @param q1
	 * @param q2
	 * @return <code>true</code> if they are business key equal
	 */
	public boolean compareQuotes(Model q1, Model q2) {
		Model doc1 = q1.relatedOne("document").getModel();
		Model doc2 = q2.relatedOne("document").getModel();
		if(doc1.getKey().equals(doc2.getKey())) {
			return ObjectUtil.equals(q1.asString("quote"), q2.asString("quote"));
		}
		return false;
	}

	public Model getCaseDocByRemoteUrl(String remoteCaseUrl) {
		List<Model> list = cache.get(PocEntityType.DOCUMENT);
		for(Model m : list) {
			try {
				String surl = m.asString("case.url");
				if(surl != null && surl.equals(remoteCaseUrl)) return m;
			}
			catch(IllegalArgumentException e) {
				// ok - continue
			}
		}
		return null;
	}
}
