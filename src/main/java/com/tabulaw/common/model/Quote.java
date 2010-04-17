/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tll.schema.Reference;

/**
 * NOTE: Quotes have no natural business key. They are freely addable.
 * @author jpk
 */
public class Quote extends TimeStampEntity {

	private static final long serialVersionUID = -2887172300623884436L;

	private String quote, serializedMark;

	private DocRef document;

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
}
