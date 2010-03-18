/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tll.tabulaw.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.common.model.CopyCriteria;
import com.tll.common.model.DatePropertyValue;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.common.model.ObjectPropertyValue;
import com.tll.common.model.RelatedManyProperty;
import com.tll.common.model.RelatedOneProperty;
import com.tll.common.model.StringPropertyValue;
import com.tll.schema.PropertyMetadata;
import com.tll.schema.PropertyType;
import com.tll.tabulaw.common.model.PocEntityType;
import com.tll.util.ObjectUtil;

/**
 * Provides the mocked data backing the application.
 * <p>
 * <b>schema definition</b>:
 * 
 * <pre>
 * CASE props
 * ----------
 *  id
 *  parties
 *  citation
 *  url
 *  year
 *  date
 * 
 * DOCUMENT props
 * --------------
 *   id
 *   title
 *   case (ref)
 *   
 * QUOTE props
 * --------------
 *   id
 *   document (ref)
 *   quote (text selection)
 *   tags (set of keywords)
 *   mark (ref - MarkOverlay js object)
 *   
 * QUOTE_BUNDLE props
 * ------------------
 *   id
 *   user (ref)
 *   name
 *   description
 *   quotes (ref)
 * 
 * NOTE props
 * -----------
 *   id
 *   user (ref - the creating user)
 *   date_created
 *   name
 *   description
 * 
 * </pre>
 * @author jpk
 */
public class PocModelStore {

	private static final PocModelStore instance = new PocModelStore();

	public static PocModelStore get() {
		return instance;
	}
	
	private final HashMap<PocEntityType, List<Model>> map = new HashMap<PocEntityType, List<Model>>();
	
	private final HashMap<PocEntityType, Comparator<Model>> comparators = new HashMap<PocEntityType, Comparator<Model>>();

	/**
	 * The app-wide current quote bundle.
	 */
	private Model currentQuoteBundle;
	
	/**
	 * Constructor
	 */
	private PocModelStore() {
		
		/* *** metadata *** */
		HashMap<PocEntityType, Map<String, PropertyMetadata>> metadata =
				new HashMap<PocEntityType, Map<String, PropertyMetadata>>();

		// case metadata
		HashMap<String, PropertyMetadata> metaCase = new HashMap<String, PropertyMetadata>();
		metadata.put(PocEntityType.CASE, metaCase);
		metaCase.put("citation", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaCase.put("parties", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaCase.put("year", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		// metaDocument.put("extractHtml", new PropertyMetadata(PropertyType.STRING,
		// false, true, -1));
		metaCase.put("url", new PropertyMetadata(PropertyType.STRING, false, true, -1));
		metaCase.put("date", new PropertyMetadata(PropertyType.DATE, false, true, -1));

		String[][] cases = new String[][] {
			// id, citation, parties, year, url, date
			{ "1", "424 U.S. 1 S. Ct. 612, 46 L. Ed. 2d 659 (1976)", "Buckley v. Valeo", "1976", null, "7/7/1976" }, 
			{ "2", "558 U.S. 50 (2010)", "Citizens United v. FEC", "2010", null, "5/5/2010" }, 
			{ "3", "435 U.S. 765 (1978)", "First National v. Belotti", "1978", null, "4/26/1978" },
			{ "4", "540 U.S. 93 (2003)", "McConnell v. Federal Election Commission", "2003", null, "2/3/2003" },
			{ "5", "376 U.S. 254 (1964)", "New York Times v. Sullivan", "1964", null, "4/1/1964" },
		};

		// document metadata
		HashMap<String, PropertyMetadata> metaDocument = new HashMap<String, PropertyMetadata>();
		metadata.put(PocEntityType.DOCUMENT, metaDocument);
		metaDocument.put("title", new PropertyMetadata(PropertyType.STRING, false, true, 64));

		String[][] documents = new String[][] {
			// id, case_id, title
			{ "1", "1", "Buckley v. Valeo" }, 
			{ "2", "2", "Citizens United v. FEC" }, 
			{ "3", "3", "First National v. Belotti" },
			{ "4", "4", "McConnell v. Federal Election Commission" }, 
			{ "5", "5", "New York Times v. Sullivan" }, 
		};

		// quote metadata
		HashMap<String, PropertyMetadata> metaQuote = new HashMap<String, PropertyMetadata>();
		metadata.put(PocEntityType.QUOTE, metaQuote);
		metaQuote.put("quote", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaQuote.put("tags", new PropertyMetadata(PropertyType.STRING, false, false, 255));
		metaQuote.put("mark", new PropertyMetadata(PropertyType.OBJECT, false, false, -1));

		String[][] quotes = new String[][] {
			// id, doc_id, tags, quote
			{ "1", "1", "1st Amendment", "More than a century ago the \"sober-minded Elihu Root\" advocated legislation that would prohibit political contributions by corporations in order to prevent \"`the great aggregations of wealth, from using their corporate funds, directly or indirectly,'\" to elect legislators who would \"`vote for their protection and the advancement of their interests as against those of the public.'\"" },
			{ "2", "1", "1st Amendment, Government", "BCRA is the most recent federal" },
			{ "3", "2", "1st Amendment", "Heed Their Rising Voices" },
			{ "4", "3", "Climate, Political", "The District Judge denied the application for a three-judge court and directed that the case be transmitted to the Court of Appeals." },
			{ "5", "3", "Climate, Political, Government", "The intricate statutory scheme adopted by Congress to regulate federal election campaigns includes restrictions 13 on political contributions and expenditures that apply broadly to all phases of and all participants in the election process." },
			{ "6", "4", "Free Speech", "The basic premise underlying the Court�s ruling is its iteration, and constant reiteration, of the proposition that the First Amendment bars regulatory distinctions based on a speaker�s identity, including its �identity� as a corporation." },
			{ "7", "5", "Political", "770 Appellants argued that � 8 violates the First Amendment, the Due Process and Equal Protection Clauses of the Fourteenth Amendment, and similar provisions of the Massachusetts Constitution." }, 
		};

		// quote bundle metadata
		HashMap<String, PropertyMetadata> metaQuoteBundle = new HashMap<String, PropertyMetadata>();
		metadata.put(PocEntityType.QUOTE_BUNDLE, metaQuoteBundle);
		metaQuoteBundle.put("name", new PropertyMetadata(PropertyType.STRING, false, true, 50));
		metaQuoteBundle.put("description", new PropertyMetadata(PropertyType.STRING, false, false, 255));

		String[][] quoteBundles = new String[][] {
			// id, name, description
			{ "1", "1st Amendment", "Quotes related to the first Amendment." }, 
			{ "2", "Broadcast Regulations", "Quotes related to Broadcast Regulations." }, 
			{ "3", "Campaign Finance", "Quotes related to political advertising and free speech." }, 
			{ "4", "Climate Regulation", "Quotes pertinent to Climate Regulation." }, 
			{ "5", "FCC Quotes", "Quotes related to the FCC." }, 
		};

		// cases
		ArrayList<Model> caseList = new ArrayList<Model>();
		map.put(PocEntityType.CASE, caseList);
		for(String[] acase : cases) {
			Model m = new Model(PocEntityType.CASE);
			caseList.add(m);
			// id, citation, parties, year, url, date
			m.setId(acase[0]);
			m.set(new StringPropertyValue("citation", metaDocument.get("citation"), acase[1]));
			m.set(new StringPropertyValue("parties", metaDocument.get("parties"), acase[2]));
			m.set(new StringPropertyValue("year", metaDocument.get("year"), acase[3]));
			m.set(new StringPropertyValue("url", metaDocument.get("url"), acase[4]));
			Date caseDate = DateTimeFormat.getShortDateFormat().parse(acase[5]);
			m.set(new DatePropertyValue("date", metaDocument.get("date"), caseDate));
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

		// documents
		ArrayList<Model> docList = new ArrayList<Model>();
		map.put(PocEntityType.DOCUMENT, docList);
		for(String[] doc : documents) {
			Model m = new Model(PocEntityType.DOCUMENT);
			docList.add(m);
			// id, case_id, title

			m.setId(doc[0]);
			for(Model mCase : caseList) {
				if(mCase.getId().equals(doc[1])) {
					m.set(new RelatedOneProperty(PocEntityType.CASE, mCase, "case", true));
				}
			}

			m.set(new StringPropertyValue("title", metaDocument.get("title"), doc[2]));
		}
		comparators.put(PocEntityType.DOCUMENT, new Comparator<Model>() {
			
			@Override
			public int compare(Model o1, Model o2) {
				String title1 = o1.asString("title");
				String title2 = o2.asString("title");
				return title1.compareTo(title2);
			}
		});

		// quotes
		ArrayList<Model> quoteList = new ArrayList<Model>();
		map.put(PocEntityType.QUOTE, quoteList);
		for(String[] q : quotes) {
			Model m = new Model(PocEntityType.QUOTE);
			quoteList.add(m);
			// id, doc_id, tags, quote
			m.setId(q[0]);

			// resolve ref'd doc
			String docId = q[1];
			for(Model mDoc : docList) {
				if(mDoc.getId().equals(docId)) {
					m.set(new RelatedOneProperty(PocEntityType.DOCUMENT, mDoc, "document", true));
					break;
				}
			}

			m.set(new StringPropertyValue("tags", metaQuote.get("tags"), q[2]));
			m.set(new StringPropertyValue("quote", metaQuote.get("quote"), q[3]));
			m.set(new ObjectPropertyValue("mark", metaQuote.get("mark"), null));
		}
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

		// quote bundles
		ArrayList<Model> bundleList = new ArrayList<Model>();
		map.put(PocEntityType.QUOTE_BUNDLE, bundleList);
		for(String[] qb : quoteBundles) {
			Model m = new Model(PocEntityType.QUOTE_BUNDLE);
			bundleList.add(m);
			// id, name, description
			String name = qb[1];
			m.setId(qb[0]);
			m.set(new StringPropertyValue("name", metaQuoteBundle.get("name"), name));
			m.set(new StringPropertyValue("description", metaQuoteBundle.get("description"), qb[2]));

			// arbitrarily add some quotes to our bundles
			ArrayList<Model> someQuotes = new ArrayList<Model>();
			if("Climate Regulation".equals(name)) {
				someQuotes.add(quoteList.get(0));
				someQuotes.add(quoteList.get(1));
			}
			else if("Campaign Finance".equals(name)) {
				someQuotes.add(quoteList.get(1));
				someQuotes.add(quoteList.get(2));
			}
			else if("1st Amendment".equals(name)) {
				someQuotes.add(quoteList.get(2));
				someQuotes.add(quoteList.get(3));
			}
			else if("FCC Quotes".equals(name)) {
				someQuotes.add(quoteList.get(0));
				someQuotes.add(quoteList.get(4));
			}

			m.set(new RelatedManyProperty(PocEntityType.QUOTE, "quotes", true, someQuotes));
		}
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
		Collection<Model> mclc = map.get(entityType);
		if(mclc == null) throw new IllegalStateException("Can't resolve next id: Unhandled entity type: " + entityType);
		int largest = 0;
		for(Model m : mclc) {
			int id = Integer.parseInt(m.getId());
			if(id > largest) largest = id;
		}
		return Integer.toString(largest + 1);
	}

	/**
	 * Creates a new {@link Model} instance of the given entity type. <br>
	 * <br>
	 * Reference (non-owned) relational properties are not set. NOTE: the created
	 * model is <em>NOT</em> persisted.
	 * @param entityType
	 * @return Newly created model with id set and cleared properties
	 */
	public Model create(PocEntityType entityType) {
		Model proto = map.get(entityType).get(0).copy(CopyCriteria.noReferences());
		// stub related props if needed
		switch(entityType) {
			case CASE:
				break;
			case DOCUMENT:
				break;
			case NOTE:
				break;
			case QUOTE:
				proto.set(new RelatedOneProperty(PocEntityType.DOCUMENT, null, "document", true));
				break;
			case QUOTE_BUNDLE:
				// add quotes prop
				proto.set(new RelatedManyProperty(PocEntityType.QUOTE, "quotes", true, null));
				break;
		}
		proto.clearPropertyValues(false, false);
		proto.setId(getNextId(entityType));
		return proto;
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
		List<Model> set = map.get(key.getEntityType());
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
		List<Model> list = map.get(etype);
		ArrayList<Model> rlist = new ArrayList<Model>(list.size());
		for(Model m : list) {
			rlist.add(m);
		}
		Comparator<Model> cmp = comparators.get(etype);
		if(cmp != null) {
			Collections.sort(rlist, cmp);
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
		List<Model> list = map.get(m.getEntityType());
		Model existing = null;
		for(Model em : list) {
			if(em.getKey().equals(m.getKey())) {
				existing = em;
				break;
			}
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
		List<Model> set = map.get(key.getEntityType());
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
	 * @return The current quote bundle ref.
	 */
	public Model getCurrentQuoteBundle() {
		return currentQuoteBundle.copy(CopyCriteria.keepReferences());
	}

	/**
	 * Sets the current quote bundle ref.
	 * <p>
	 * Fires a {@link ModelChangeEvent} when successful.
	 * @param mQuoteBundle non-null
	 * @param source optional source widget when specified, a model change event
	 *        is fired on it
	 * @return <code>true</code> if the current quote bundle ref was actually
	 *         updated and a model change event was fired.
	 */
	public boolean setCurrentQuoteBundle(Model mQuoteBundle, Widget source) {
		if(mQuoteBundle == null) throw new NullPointerException();
		if(currentQuoteBundle == null || !currentQuoteBundle.getKey().equals(mQuoteBundle)) {
			currentQuoteBundle = mQuoteBundle;
			// fire model change event
			if(source != null) source.fireEvent(new ModelChangeEvent(ModelChangeOp.UPDATED, mQuoteBundle, null));
			return true;
		}
		return false;
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
}
