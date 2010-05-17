/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * NOTE: there are no natural business keys defined for quote bundles.
 * @author jpk
 */
public class QuoteBundle extends TimeStampEntity implements INamedEntity, Comparable<QuoteBundle> {

	private static final long serialVersionUID = -6606826756860275551L;
	
	private static final String ORPHANED_QUOTES_BUNDLE_NAME = "Orphaned Quotes";

	/**
	 * @return Newly created instance with hard-coded properties signifying a
	 *         container for orphaned qoutes.
	 */
	public static QuoteBundle newOrphanedQuoteBundle() {
		QuoteBundle oqb = new QuoteBundle();
		oqb.setName(ORPHANED_QUOTES_BUNDLE_NAME);
		oqb.setDescription("All orphaned quotes");
		return oqb;
	}

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
	 * Is this a container for orphaned quotes?
	 * @return true/false
	 */
	public boolean isOrphanedQuoteContainer() {
		return ORPHANED_QUOTES_BUNDLE_NAME.equals(getName());
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
	public final ModelKey getModelKey() {
		ModelKey mk = super.getModelKey();
		mk.setName(getName());
		return mk;
	}

	@Override
	protected IEntity newInstance() {
		return new QuoteBundle();
	}

	@Override
	public void doClone(IEntity cln) {
		super.doClone(cln);
		QuoteBundle qb = (QuoteBundle) cln;

		ArrayList<Quote> cquotes = null;
		if(quotes != null) {
			cquotes = new ArrayList<Quote>(quotes.size());
			for(Quote q : quotes) {
				cquotes.add((Quote) q.clone());
			}
		}

		qb.id = id;
		qb.name = name;
		qb.description = description;
		qb.quotes = cquotes;
	}

	@Override
	public String descriptor() {
		return typeDesc() + " (" + getName() + ")";
	}

	@Override
	public String getEntityType() {
		return EntityType.QUOTE_BUNDLE.name();
	}

	@NotEmpty
	@Length(max = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return A newly created list containing the referenced quotes in this
	 *         bundle.
	 */
	public List<Quote> getQuotes() {
		if(quotes == null) quotes = new ArrayList<Quote>();
		return quotes;
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}

	/**
	 * Adds a qoute at the end of the quote list.
	 * @param quote
	 */
	public void addQuote(Quote quote) {
		if(quote == null) throw new NullPointerException();
		getQuotes().add(quote);
	}

	/**
	 * Inserts a qoute at the given index.
	 * @param quote
	 * @param index
	 */
	public void insertQuote(Quote quote, int index) {
		if(quote == null) throw new NullPointerException();
		getQuotes().add(index, quote);
	}

	/**
	 * Removes the given quote.
	 * @param quote
	 * @return <code>true</code> if the quote was removed
	 */
	public boolean removeQuote(Quote quote) {
		if(quotes == null) return false;
		return quotes.remove(quote);
	}

	/**
	 * Removes a quote from the given index.
	 * @param index
	 * @return the removed quote or <code>null</code> if not found
	 */
	public Quote removeQuote(int index) {
		if(quotes == null) return null;
		return quotes.remove(index);
	}

	@Length(max = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(QuoteBundle o) {
		return name != null && o.name != null ? name.compareTo(o.name) : 0;
	}

	/*
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
	*/
}
