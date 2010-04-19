/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tabulaw.schema.BusinessKeyDef;
import com.tabulaw.schema.BusinessObject;
import com.tabulaw.schema.Reference;

/**
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Doc Hash and Mark", properties = {
	"document.hash", "serializedMark"
}))
public class Quote extends TimeStampEntity implements Comparable<Quote> {

	private static final long serialVersionUID = -2887172300623884436L;

	private String id;

	private String quote, serializedMark;

	/**
	 * The mark overlay used client-side only.
	 */
	private transient Object mark;

	private DocRef document;

	/**
	 * Constructor
	 */
	public Quote() {
		super();
	}

	/**
	 * Constructor
	 * @param quote
	 * @param serializedMark
	 * @param document
	 */
	public Quote(String quote, String serializedMark, DocRef document) {
		super();
		this.quote = quote;
		this.serializedMark = serializedMark;
		this.document = document;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Quote clone() {
		// NOTE: keep the doc ref
		return new Quote(quote, serializedMark, document);
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.QUOTE;
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

	public void setSerializedMark(String serializedMark) {
		this.serializedMark = serializedMark;
	}

	public Object getMark() {
		return mark;
	}

	public void setMark(Object mark) {
		this.mark = mark;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((serializedMark == null) ? 0 : serializedMark.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!super.equals(obj)) return false;
		if(getClass() != obj.getClass()) return false;
		Quote other = (Quote) obj;
		if(document == null) {
			if(other.document != null) return false;
		}
		else if(!document.equals(other.document)) return false;
		if(serializedMark == null) {
			if(other.serializedMark != null) return false;
		}
		else if(!serializedMark.equals(other.serializedMark)) return false;
		return true;
	}
}
