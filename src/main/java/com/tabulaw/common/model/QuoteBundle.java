/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

/**
 * NOTE: there are no natural business keys defined for quote bundles.
 * @author jpk
 */
public class QuoteBundle extends TimeStampEntity implements INamedEntity {

	private static final long serialVersionUID = -6606826756860275551L;

	private String id;

	private String name, description;

	private List<Quote> quotes;

	/**
	 * Constructor
	 */
	public QuoteBundle() {
		super();
	}

	/**
	 * Constructor
	 * @param name
	 * @param description
	 * @param quotes
	 */
	public QuoteBundle(String name, String description, List<Quote> quotes) {
		super();
		this.name = name;
		this.description = description;
		this.quotes = quotes;
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
	public final ModelKey getModelKey() {
		ModelKey mk = super.getModelKey();
		mk.setName(getName());
		return mk;
	}

	@Override
	public QuoteBundle clone() {
		ArrayList<Quote> cquotes = null;
		if(quotes != null) {
			cquotes = new ArrayList<Quote>(quotes.size());
			for(Quote q : quotes) {
				cquotes.add(q.clone());
			}
		}
		QuoteBundle cln = new QuoteBundle(name, description, cquotes);
		cln.id = id;
		cloneTimestamping(cln);
		return cln;
	}

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
		if(quotes == null) quotes = new ArrayList<Quote>();
		return quotes;
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}
	
	public void addQuote(Quote quote) {
		getQuotes().add(quote);
	}
	
	public boolean removeQuote(Quote quote) {
		if(quotes == null) return false;
		return quotes.remove(quote);
	}

	@Length(max = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		QuoteBundle other = (QuoteBundle) obj;
		if(id == null) {
			if(other.id != null) return false;
		}
		else if(!id.equals(other.id)) return false;
		return true;
	}
}
