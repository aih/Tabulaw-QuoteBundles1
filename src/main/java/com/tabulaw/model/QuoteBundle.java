/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * NOTE: there are no natural business keys defined for quote bundles.
 * @author jpk
 */
@XmlRootElement(name = "quoteBundle")
public class QuoteBundle extends TimeStampEntity implements INamedEntity, Comparable<QuoteBundle> {

	private static final long serialVersionUID = -6606826756860275551L;

	private String id;

	private String parentBundleId;

	public String getParentBundleId() {
		return parentBundleId;
	}

	public void setParentBundleId(String parentBundleId) {
		this.parentBundleId = parentBundleId;
	}

	private String name, description;

	private List<Quote> quotes;

	private List<QuoteBundle> childQuoteBundles;

	/**
	 * Constructor
	 */
	public QuoteBundle() {
		super();
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
		ArrayList<QuoteBundle> cchildQuoteBundles = null;
		if(childQuoteBundles != null) {
			cchildQuoteBundles = new ArrayList<QuoteBundle>(childQuoteBundles.size());
			for(QuoteBundle cqb : childQuoteBundles) {
				cchildQuoteBundles.add((QuoteBundle) cqb.clone());
			}
		}

		qb.id = id;
		qb.name = name;
		qb.parentBundleId = parentBundleId;
		qb.description = description;
		qb.quotes = cquotes;
		qb.childQuoteBundles = cchildQuoteBundles;
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
	 * Does the given quote exist in this bundle?
	 * @param q quote to check
	 * @return true/false
	 */
	public boolean hasQuote(Quote q) {
		if(quotes == null) return false;
		for(Quote eq : quotes) {
			if(eq.equals(q)) return true;
		}
		return false;
	}

	/**
	 * @return A newly created list containing the referenced quotes in this
	 *         bundle.
	 */
	@XmlElementWrapper
	@XmlElement(name = "quote")
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
	 * Adds all quotes in the given collection to the internally managed list of
	 * quotes.
	 * @param clc collection of quotes to add
	 */
	public void addQuotes(Collection<Quote> clc) {
		if(clc != null) getQuotes().addAll(clc);
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
	public void addChildQuoteBundle(QuoteBundle childQuoteBundle){
		getChildQuoteBundles().add(childQuoteBundle);
	}

	public List<QuoteBundle> getChildQuoteBundles() {
		if(childQuoteBundles == null) childQuoteBundles= new ArrayList<QuoteBundle>();
		return childQuoteBundles;
	}

	public void setChildQuoteBundles(List<QuoteBundle> childQuoteBundles) {
		this.childQuoteBundles = childQuoteBundles;
	}
	
	
	/**
	 * Removes any and all contained quotes.
	 */
	public void clearQuotes() {
		if(quotes != null) quotes.clear();
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
}
