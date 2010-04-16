/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.tll.model.IEntity;
import com.tll.model.NamedTimeStampEntity;

/**
 * NOTE: there are no natural business keys defined for quote bundles.
 * @author jpk
 */
public class QuoteBundle extends NamedTimeStampEntity {

	private static final long serialVersionUID = -6606826756860275551L;

	private String description;

	private List<Quote> quotes;

	@Override
	public Class<? extends IEntity> entityClass() {
		return QuoteBundle.class;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}

	@Length(max = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
