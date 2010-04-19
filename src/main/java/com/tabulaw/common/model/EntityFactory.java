/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.common.model;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;

/**
 * Provides the mocked data backing the application.
 * <p>
 * <b>schema definition</b>:
 * 
 * <pre>
 * USER props
 * ----------
 *  id
 *  username
 *  password
 *  dateCreated
 *  dateModified
 *  enabled
 *  role
 *  
 * CASE props
 * ----------
 *  id
 *  parties
 *  citation
 *  url
 *  year
 * 
 * DOCUMENT props
 * --------------
 *   id
 *   title
 *   date
 *   case (ref)
 *   hash (unique identifier token)
 *   [htmlContent] (transient)
 *   
 * QUOTE props
 * --------------
 *   id
 *   date created/modified
 *   document (ref)
 *   quote (text selection)
 *   tags (set of keywords)
 *   serializedMark (serialized MarkOverlay)
 *   mark (MarkOverlay) [transient]
 *   
 * QUOTE_BUNDLE props
 * ------------------
 *   id
 *   date created/modified
 *   name
 *   description
 *   user (ref)
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
public class EntityFactory {

	private static final EntityFactory instance = new EntityFactory();

	public static EntityFactory get() {
		return instance;
	}

	private final HashMap<EntityType, Map<String, PropertyMetadata>> metadata =
			new HashMap<EntityType, Map<String, PropertyMetadata>>();

	/**
	 * Constructor
	 */
	private EntityFactory() {

		// case metadata
		HashMap<String, PropertyMetadata> metaCase = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.CASE, metaCase);
		metaCase.put("citation", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaCase.put("parties", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaCase.put("year", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		// metaDocument.put("extractHtml", new PropertyMetadata(PropertyType.STRING,
		// false, true, -1));
		metaCase.put("url", new PropertyMetadata(PropertyType.STRING, false, true, -1));

		// document metadata
		HashMap<String, PropertyMetadata> metaDocument = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.DOCUMENT, metaDocument);
		metaDocument.put("title", new PropertyMetadata(PropertyType.STRING, false, true, 64));
		metaDocument.put("date", new PropertyMetadata(PropertyType.DATE, false, true, -1));
		metaDocument.put("hash", new PropertyMetadata(PropertyType.STRING, false, true, 64));

		// quote metadata
		HashMap<String, PropertyMetadata> metaQuote = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.QUOTE, metaQuote);
		metaQuote.put("quote", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaQuote.put("tags", new PropertyMetadata(PropertyType.STRING, false, false, 255));
		metaQuote.put("serializedMark", new PropertyMetadata(PropertyType.STRING, false, false, -1));

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
		metadata.put(EntityType.QUOTE_BUNDLE, metaQuoteBundle);
		metaQuoteBundle.put("name", new PropertyMetadata(PropertyType.STRING, false, true, 50));
		metaQuoteBundle.put("description", new PropertyMetadata(PropertyType.STRING, false, false, 255));
	} // constructor

	/**
	 * Creates a new entity instance of the given an entity type. <br>
	 * <br>
	 * Reference (non-owned) relational properties are not set. NOTE: the created
	 * model is <em>NOT</em> persisted.
	 * @param entityType
	 * @return Newly created model with id set and cleared properties
	 */
	private IEntity create(EntityType entityType) {
		IEntity e = null;
		//Map<String, PropertyMetadata> meta;
		// stub related props if needed
		switch(entityType) {
			case CASE:
				//meta = metadata.get(EntityType.CASE);
				e = new CaseRef();
				break;
			case DOCUMENT:
				//meta = metadata.get(EntityType.DOCUMENT);
				e = new DocRef();
				break;
			case NOTE:
				break;
			case QUOTE:
				//meta = metadata.get(EntityType.QUOTE);
				e = new Quote();
				break;
			case QUOTE_BUNDLE:
				//meta = metadata.get(EntityType.QUOTE_BUNDLE);
				e = new QuoteBundle();
				break;
		}
		return e;
	}

	/**
	 * Create a new contract type doc.
	 * @param docTitle
	 * @param docHash
	 * @param docDate
	 * @return newly created model
	 */
	public DocRef buildDoc(String docTitle, String docHash, Date docDate) {
		DocRef doc = (DocRef) create(EntityType.DOCUMENT);
		doc.setTitle(docTitle);
		doc.setDate(docDate);
		doc.setHash(docHash);
		return doc;
	}

	/**
	 * Creates a new Model representing a document that references a case.
	 * @param docTitle
	 * @param docHash the server-side filename
	 * @param docDate
	 * @param parties
	 * @param citation
	 * @param url
	 * @param year
	 * @return newly created model
	 */
	public DocRef buildCaseDoc(String docTitle, String docHash, Date docDate, String parties, String citation, String url,
			String year) {
		DocRef doc = buildDoc(docTitle, docHash, docDate);
		
		CaseRef caseRef = (CaseRef) create(EntityType.CASE);
		caseRef.setParties(parties);
		caseRef.setCitation(citation);
		caseRef.setUrl(url);
		caseRef.setYear(Integer.parseInt(year));
		doc.setCaseRef(caseRef);

		return doc;
	}

	/**
	 * Creates a new and empty quote bundle.
	 * @param name
	 * @param description
	 * @return newly created model
	 */
	public QuoteBundle buildQuoteBundle(String name, String description) {
		QuoteBundle m = (QuoteBundle) create(EntityType.QUOTE_BUNDLE);
		m.setName(name);
		m.setDescription(description);
		return m;
	}

	/**
	 * Creates a new and empty quote.
	 * @param quoteText the quote text
	 * @param document optional referenced doc model
	 * @param serializedMark optional serialized highlight token
	 * @return newly created model
	 */
	public Quote buildQuote(String quoteText, DocRef document, String serializedMark) {
		Quote m = (Quote) create(EntityType.QUOTE);
		m.setQuote(quoteText);
		if(document != null) m.setDocument(document);
		m.setSerializedMark(serializedMark);
		return m;
	}
}
