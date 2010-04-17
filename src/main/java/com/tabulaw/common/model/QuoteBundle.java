/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

/**
 * NOTE: there are no natural business keys defined for quote bundles.
 * @author jpk
 */
public class QuoteBundle extends TimeStampEntity implements INamedEntity {

	private static final long serialVersionUID = -6606826756860275551L;

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
	 * @param dateCreated
	 * @param dateModified
	 * @param name
	 * @param description
	 * @param quotes
	 */
	public QuoteBundle(Date dateCreated, Date dateModified, String name, String description, List<Quote> quotes) {
		super(dateCreated, dateModified);
	}
	
	@Override
	public QuoteBundle clone() {
		Date dc = getDateCreated();
		if(dc != null) dc = new Date(dc.getTime());
		
		Date dm = getDateModified();
		if(dm != null) dm = new Date(dm.getTime());
		
		ArrayList<Quote> cquotes = new ArrayList<Quote>(quotes == null ? 0 : quotes.size());
		for(Quote q : quotes) {
			cquotes.add(q.clone());
		}
		
		return new QuoteBundle(dc, dm, name, description, cquotes);
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.QUOTE_BUNDLE;
	}

	@Override
	protected String getId() {
		return name;
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
