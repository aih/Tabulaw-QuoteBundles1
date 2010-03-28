/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tll.tabulaw.common.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tll.common.model.DatePropertyValue;
import com.tll.common.model.Model;
import com.tll.common.model.ObjectPropertyValue;
import com.tll.common.model.RelatedManyProperty;
import com.tll.common.model.RelatedOneProperty;
import com.tll.common.model.StringPropertyValue;
import com.tll.schema.PropertyMetadata;
import com.tll.schema.PropertyType;
import com.tll.tabulaw.client.model.MarkOverlay;

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
 *   hash (unique identifier token)
 *   [htmlContent] (transient)
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
public class PocModelFactory {

	private static final PocModelFactory instance = new PocModelFactory();

	public static PocModelFactory get() {
		return instance;
	}

	private final HashMap<PocEntityType, Map<String, PropertyMetadata>> metadata =
			new HashMap<PocEntityType, Map<String, PropertyMetadata>>();

	/**
	 * Constructor
	 */
	private PocModelFactory() {

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

		/*
		String[][] cases = new String[][] {
			// id, citation, parties, year, url, date
			{ "1", "424 U.S. 1 S. Ct. 612, 46 L. Ed. 2d 659 (1976)", "Buckley v. Valeo", "1976", null, "7/7/1976" }, 
			{ "2", "558 U.S. 50 (2010)", "Citizens United v. FEC", "2010", null, "5/5/2010" }, 
			{ "3", "435 U.S. 765 (1978)", "First National v. Belotti", "1978", null, "4/26/1978" },
			{ "4", "540 U.S. 93 (2003)", "McConnell v. Federal Election Commission", "2003", null, "2/3/2003" },
			{ "5", "376 U.S. 254 (1964)", "New York Times v. Sullivan", "1964", null, "4/1/1964" },
		};
		*/

		// document metadata
		HashMap<String, PropertyMetadata> metaDocument = new HashMap<String, PropertyMetadata>();
		metadata.put(PocEntityType.DOCUMENT, metaDocument);
		metaDocument.put("title", new PropertyMetadata(PropertyType.STRING, false, true, 64));
		metaDocument.put("hash", new PropertyMetadata(PropertyType.STRING, false, true, 64));

		/*
		String[][] documents = new String[][] {
			// id, case_id, title, hash
			{ "1", "1", "Buckley v. Valeo", "Buckley-v-Valeo.htm"}, 
			{ "2", "2", "Citizens United v. FEC", "CitizensUnited-v-FEC.htm"}, 
			{ "3", "3", "First National v. Belotti", "FirstNatl-v-Belotti.htm"},
			{ "4", "4", "McConnell v. Federal Election Commission", "McConnell-v-FEC.htm"}, 
			{ "5", "5", "New York Times v. Sullivan", "Times-v-Sullivan.htm"}, 
		};
		*/

		// quote metadata
		HashMap<String, PropertyMetadata> metaQuote = new HashMap<String, PropertyMetadata>();
		metadata.put(PocEntityType.QUOTE, metaQuote);
		metaQuote.put("quote", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaQuote.put("tags", new PropertyMetadata(PropertyType.STRING, false, false, 255));
		metaQuote.put("mark", new PropertyMetadata(PropertyType.OBJECT, false, false, -1));

		/*
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
		*/

		// quote bundle metadata
		HashMap<String, PropertyMetadata> metaQuoteBundle = new HashMap<String, PropertyMetadata>();
		metadata.put(PocEntityType.QUOTE_BUNDLE, metaQuoteBundle);
		metaQuoteBundle.put("name", new PropertyMetadata(PropertyType.STRING, false, true, 50));
		metaQuoteBundle.put("description", new PropertyMetadata(PropertyType.STRING, false, false, 255));

		/*
		String[][] quoteBundles = new String[][] {
			// id, name, description
			{ "1", "1st Amendment", "Quotes related to the first Amendment." }, 
			{ "2", "Broadcast Regulations", "Quotes related to Broadcast Regulations." }, 
			{ "3", "Campaign Finance", "Quotes related to political advertising and free speech." }, 
			{ "4", "Climate Regulation", "Quotes pertinent to Climate Regulation." }, 
			{ "5", "FCC Quotes", "Quotes related to the FCC." }, 
		};
		*/

		/*
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
		*/

		/*
		// documents
		ArrayList<Model> docList = new ArrayList<Model>();
		map.put(PocEntityType.DOCUMENT, docList);
		for(String[] doc : documents) {
			Model m = new Model(PocEntityType.DOCUMENT);
			docList.add(m);
			// id, case_id, title, hash

			m.setId(doc[0]);
			for(Model mCase : caseList) {
				if(mCase.getId().equals(doc[1])) {
					m.set(new RelatedOneProperty(PocEntityType.CASE, mCase, "case", true));
				}
			}

			m.set(new StringPropertyValue("title", metaDocument.get("title"), doc[2]));
			m.set(new StringPropertyValue("hash", metaDocument.get("hash"), doc[3]));
		}
		*/

		/*
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
		*/

		/*
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
		*/
	} // constructor

	/**
	 * Creates a new {@link Model} instance of the given entity type. <br>
	 * <br>
	 * Reference (non-owned) relational properties are not set. NOTE: the created
	 * model is <em>NOT</em> persisted.
	 * @param entityType
	 * @return Newly created model with id set and cleared properties
	 */
	private Model create(PocEntityType entityType) {
		Model proto = new Model(entityType);
		Map<String, PropertyMetadata> meta;
		// stub related props if needed
		switch(entityType) {
			case CASE:
				meta = metadata.get(PocEntityType.CASE);
				proto.set(new StringPropertyValue("citation", meta.get("citation"), null));
				proto.set(new StringPropertyValue("parties", meta.get("parties"), null));
				proto.set(new StringPropertyValue("year", meta.get("year"), null));
				proto.set(new StringPropertyValue("url", meta.get("url"), null));
				proto.set(new DatePropertyValue("date", meta.get("date"), new Date()));
				break;
			case DOCUMENT:
				meta = metadata.get(PocEntityType.DOCUMENT);
				proto.set(new StringPropertyValue("title", meta.get("title"), null));
				proto.set(new StringPropertyValue("hash", meta.get("hash"), null));
				break;
			case NOTE:
				break;
			case QUOTE:
				meta = metadata.get(PocEntityType.QUOTE);
				proto.set(new StringPropertyValue("tags", meta.get("tags"), null));
				proto.set(new StringPropertyValue("quote", meta.get("quote"), null));
				proto.set(new ObjectPropertyValue("mark", meta.get("mark"), null));
				proto.set(new RelatedOneProperty(PocEntityType.DOCUMENT, null, "document", true));
				break;
			case QUOTE_BUNDLE:
				meta = metadata.get(PocEntityType.QUOTE_BUNDLE);
				proto.set(new StringPropertyValue("name", meta.get("name"), null));
				proto.set(new StringPropertyValue("description", meta.get("description"), null));
				proto.set(new RelatedManyProperty(PocEntityType.QUOTE, "quotes", true, null));
				break;
		}
		proto.clearPropertyValues(false, false);
		// proto.setId(getNextId(entityType));
		return proto;
	}

	/**
	 * Creates a new Model representing a document that references a case.
	 * @param docTitle
	 * @param docHash the server-side filename
	 * @param parties
	 * @param citation
	 * @param url
	 * @param year
	 * @param date
	 * @return newly created model
	 */
	public Model buildCaseDoc(String docTitle, String docHash, String parties, String citation, String url, String year,
			Date date) {
		Model docCase = create(PocEntityType.CASE);
		docCase.setString("parties", parties);
		docCase.setString("citation", citation);
		docCase.setString("url", url);
		docCase.setString("year", year);
		docCase.setProperty("date", date, PropertyType.DATE);

		Model doc = create(PocEntityType.DOCUMENT);
		doc.setString("title", docTitle);
		doc.setString("hash", docHash);
		doc.set(new RelatedOneProperty(PocEntityType.CASE, docCase, "case", true));

		return doc;
	}

	public Model buildContractDoc() {
		Model docContract = create(PocEntityType.DOCUMENT);
		// TODO
		return docContract;
	}

	/**
	 * Creates a new and empty quote bundle.
	 * @param name
	 * @param description
	 * @return newly created model
	 */
	public Model buildQuoteBundle(String name, String description) {
		Model m = create(PocEntityType.QUOTE_BUNDLE);
		m.setString("name", name);
		m.setString("description", description);
		return m;
	}

	/**
	 * Creates a new and empty quote bundle.
	 * @param quote the quote text
	 * @param document referenced doc model
	 * @param mark highlight js overlay object
	 * @return newly created model
	 */
	public Model buildQuote(String quote, Model document, MarkOverlay mark) {
		Model m = create(PocEntityType.QUOTE);
		m.setString("quote", mark.getText());
		m.relatedOne("document").setModel(document);
		m.setProperty("mark", mark, PropertyType.OBJECT);
		return m;
	}
}
