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

	private String quoteText, mark;

	private DocRef doc;

	@Override
	public Class<? extends IEntity> entityClass() {
		return Quote.class;
	}

	@NotEmpty
	@Length(max = 4000)
	public String getQuoteText() {
		return quoteText;
	}

	public void setQuoteText(String quoteText) {
		this.quoteText = quoteText;
	}

	/**
	 * @return The serialized token holding data for client-side text to be
	 *         highlighted.
	 */
	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	@NotNull
	@Valid
	@Reference
	public DocRef getDoc() {
		return doc;
	}

	public void setDoc(DocRef doc) {
		this.doc = doc;
	}
}
