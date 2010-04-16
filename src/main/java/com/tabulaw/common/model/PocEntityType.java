/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.common.model;

import com.tll.INameValueProvider;
import com.tll.common.model.IEntityType;

/**
 * PocEntityType
 * @author jpk
 */
public enum PocEntityType implements IEntityType, INameValueProvider<String> {

	USER("User"),
	
	CASE("Case"),
	DOCUMENT("Document"),
	
	QUOTE("Quote"),
	QUOTE_BUNDLE("Quote Bundle"),
	
	NOTE("Note");

	private String name;

	private PocEntityType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return name();
	}

	public String descriptor() {
		return getName();
	}
}
