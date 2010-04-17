/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import java.util.Date;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tll.schema.BusinessKeyDef;
import com.tll.schema.BusinessObject;
import com.tll.schema.Reference;

/**
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Mark", properties = { "serializedMark"
}))
public class Quote extends TimeStampEntity implements Comparable<Quote> {

	private static final long serialVersionUID = -2887172300623884436L;

	private String quote, serializedMark;

	private DocRef document;

	/**
	 * Constructor
	 */
	public Quote() {
		super();
	}

	/**
	 * Constructor
	 * @param dateCreated
	 * @param dateModified
	 * @param quote
	 * @param serializedMark
	 * @param docRef
	 */
	public Quote(Date dateCreated, Date dateModified, String quote, String serializedMark, DocRef docRef) {
		super(dateCreated, dateModified);
	}
	
	@Override
	public Quote clone() {
		Date dc = getDateCreated();
		if(dc != null) dc = new Date(dc.getTime());
		
		Date dm = getDateModified();
		if(dm != null) dm = new Date(dm.getTime());
		
		// NOTE: keep the doc ref
		return new Quote(dc, dm, quote, serializedMark, document);
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.QUOTE;
	}

	@Override
	protected String getId() {
		return serializedMark;
	}

	@NotEmpty
	@Length(max = 4000)
	public String getQuote() {
		return quote;
	}

	public void setQuote(String quoteText) {
		this.quote = quoteText;
	}

	/**
	 * @return The serialized token holding data for client-side text to be
	 *         highlighted.
	 */
	public String getSerializedMark() {
		return serializedMark;
	}

	public void setSerializedMark(String mark) {
		this.serializedMark = mark;
	}

	@Valid
	@Reference
	public DocRef getDocument() {
		return document;
	}

	public void setDocument(DocRef doc) {
		this.document = doc;
	}

	@Override
	public int compareTo(Quote o) {
		if(document != null && o.document != null) {
			return document.compareTo(o.document);
		}
		// TODO figure out what to fallback on here
		return 0;
	}
}
