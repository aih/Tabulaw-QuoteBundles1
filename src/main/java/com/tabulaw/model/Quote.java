/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tabulaw.schema.Reference;
import com.tabulaw.util.StringUtil;

/**
 * @author jpk
 */
// NO - we don't enforce this as we want to support "cloned" quotes that
// reference the same quote for a given doc but are distinct existing in different bundles.
/*
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Doc Hash and Mark", properties = {
	"document.hash", "serializedMark"
}))
*/
@XmlRootElement(name = "quote")
public class Quote extends TimeStampEntity implements Comparable<Quote> {

	private static final long serialVersionUID = -2887172300623884436L;

	private String id;

	private int startPage, endPage;
	
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

	@Override
	public String descriptor() {
		return typeDesc() + " (" + StringUtil.abbr(getQuote(), 50) + ")";
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		if(id == null) throw new NullPointerException();
		this.id = id;
	}

	@Override
	protected IEntity newInstance() {
		return new Quote();
	}

	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		Quote q = (Quote) cln;
		q.id = id;
		q.quote = quote;
		q.serializedMark = serializedMark;
		q.mark = null;	// don't transfer over the js mark
		q.document = document;	// NOTE: keep the doc ref
		q.startPage = startPage;
		q.endPage = endPage;
	}

	@Override
	public String getEntityType() {
		return EntityType.QUOTE.name();
	}

	@NotEmpty
	@Length(max = 4000)
	@XmlElement(name = "quoteText")
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
	//@NotNull
	public String getSerializedMark() {
		return serializedMark;
	}

	public void setSerializedMark(String serializedMark) {
		this.serializedMark = serializedMark;
	}

	@XmlTransient
	public Object getMark() {
		return mark;
	}

	public void setMark(Object mark) {
		this.mark = mark;
	}

	@Valid
	@Reference
	@NotNull
	@XmlElement(name = "docRef")
	public DocRef getDocument() {
		return document;
	}

	public void setDocument(DocRef doc) {
		this.document = doc;
	}
	
	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	@Override
	public int compareTo(Quote o) {
		int c = 0;
		
		// by doc
		if(document != null && o.document != null) {
			c = document.compareTo(o.document);
		}
		
		// by quote text
		if(c == 0) {
			if(quote != null) {
				c = quote.compareTo(o.quote);
			}
		}
		
		return c;
	}
}
