/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.common.model;


import java.util.Date;

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

	/**
	 * Constructor
	 */
	private EntityFactory() {}

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
		switch(entityType) {
			case CASE:
				e = new CaseRef();
				break;
			case DOCUMENT:
				e = new DocRef();
				break;
			case NOTE:
				break;
			case QUOTE:
				e = new Quote();
				break;
			case QUOTE_BUNDLE:
				e = new QuoteBundle();
				break;
		}
		return e;
	}
	
	/**
	 * Builds a Case ref entity.
	 * @param parties
	 * @param citation
	 * @param url
	 * @param year
	 * @return
	 */
	public CaseRef buildCase(String parties, String citation, String url, String year) {
		CaseRef e = (CaseRef) create(EntityType.CASE);
		e.setCitation(citation);
		e.setParties(parties);
		e.setUrl(url);
		if(year != null) {
			try {
				e.setYear(Integer.parseInt(year));
			}
			catch(NumberFormatException ex) {
				// ok
			}
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
		if(docHash != null) doc.setHash(docHash);
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
		
		CaseRef caseRef = buildCase(parties, citation, url, year);
		caseRef.setParties(parties);
		caseRef.setCitation(citation);
		caseRef.setUrl(url);
		if(year != null) caseRef.setYear(Integer.parseInt(year));
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
