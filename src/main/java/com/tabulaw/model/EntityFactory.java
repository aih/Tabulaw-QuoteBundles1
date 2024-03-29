/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.model;

import com.tabulaw.util.UUID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 *  firstPageNumber
 *  lastPageNumber
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
 *   start page
 *   end page
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
	private EntityFactory() {
	}

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
			case DOC_CONTENT:
				e = new DocContent();
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
	 * @param reftoken
	 * @param docLoc
	 * @param court
	 * @param url
	 * @param year
	 * @return
	 */
	public CaseRef buildCase(String parties, String reftoken, String docLoc, String court, String url, int year, int firstPage, int lastPage) {
		CaseRef e = (CaseRef) create(EntityType.CASE);
        e.setId(UUID.uuid());
		e.setReftoken(reftoken);
		e.setParties(parties);
		e.setDocLoc(docLoc);
		e.setCourt(court);
		e.setUrl(url);
		e.setYear(year);
		e.setFirstPageNumber(firstPage);
		e.setLastPageNumber(lastPage);
		return e;
	}

	/**
	 * Create a new non-case type doc.
	 * @param docTitle
	 * @param docDate
	 * @return newly created model
	 */
	public DocRef buildDoc(String docTitle, Date docDate, boolean referenceDoc) {
		DocRef doc = (DocRef) create(EntityType.DOCUMENT);
        doc.setId(UUID.uuid());
		doc.setTitle(docTitle);
		doc.setDate(docDate);
		doc.setReferenceDoc(referenceDoc);
		return doc;
	}
	
	/**
	 * Create a new doc content entity.
	 * @param docId required - the doc ref id
	 * @param htmlContent doc html content
	 * @return newly created model
	 */
	public DocContent buildDocContent(String docId, String htmlContent) {
		return buildDocContent(docId, htmlContent, new ArrayList<int[]>(), 1);
	}
	
	public DocContent buildDocContent(String docId, String htmlContent, List<int[]> pagesXPath, int firstPageNumber) {
		DocContent doc = (DocContent) create(EntityType.DOC_CONTENT);
		doc.setId(docId);
		doc.setHtmlContent(htmlContent);
		doc.setPagesXPath(pagesXPath);
		doc.setFirstPageNumber(firstPageNumber);
		return doc;
	}

	/**
	 * Creates a new Model representing a document that references a case.
	 * @param docTitle
	 * @param docDate
	 * @param parties
	 * @param reftoken
	 * @param docLoc
	 * @param court
	 * @param url
	 * @param year
	 * @return newly created model
	 */
	public DocRef buildCaseDoc(String docTitle, Date docDate, boolean referenceDoc, String parties, String reftoken,
			String docLoc, String court, String url, int year, int firstPage, int lastPage) {
		DocRef doc = buildDoc(docTitle, docDate, referenceDoc);
        doc.setId(UUID.uuid());
		doc.setReference(buildCase(parties, reftoken, docLoc, court, url, year, firstPage, lastPage));
		return doc;
	}

	/**
	 * Creates a new and empty quote bundle.
	 * @param name
	 * @param description
	 * @return newly created model
	 */
	public QuoteBundle buildBundle(String name, String description) {
		QuoteBundle m = (QuoteBundle) create(EntityType.QUOTE_BUNDLE);
        m.setId(UUID.uuid());
		m.setName(name);
		m.setDescription(description);
		return m;
	}

	/**
	 * Creates a new and empty quote.
	 * @param quoteText the quote text
	 * @param document optional referenced doc model
	 * @param serializedMark optional serialized highlight token
	 * @param startPage
	 * @param endPage
	 * @return newly created model
	 */
	public Quote buildQuote(String quoteText, DocRef document, String serializedMark, int startPage, int endPage) {
		Quote m = (Quote) create(EntityType.QUOTE);
        m.setId(UUID.uuid());
		m.setQuote(quoteText);
		if(document != null) m.setDocument(document);
		m.setSerializedMark(serializedMark);
		m.setStartPage(startPage);
		m.setEndPage(endPage);
		return m;
	}
}
