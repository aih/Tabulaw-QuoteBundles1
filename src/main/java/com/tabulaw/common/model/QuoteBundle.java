/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;

/**
 * NOTE: there are no natural business keys defined for quote bundles.
 * @author jpk
 */
public class QuoteBundle extends TimeStampEntity {

	private static final long serialVersionUID = -6606826756860275551L;

	private String name, description;

	private List<Quote> quotes;

	@Override
	public EntityType getEntityType() {
		return EntityType.QUOTE_BUNDLE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
