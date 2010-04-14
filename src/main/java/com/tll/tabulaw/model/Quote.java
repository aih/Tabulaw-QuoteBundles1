/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tll.model.IEntity;
import com.tll.model.TimeStampEntity;
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
	public Class<? extends IEntity> entityClass() {
		return Quote.class;
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

	@NotNull
	@Valid
	@Reference
	public DocRef getDocument() {
		return document;
	}

	public void setDocument(DocRef doc) {
		this.document = doc;
	}
}
