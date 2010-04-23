/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.common.model;

import com.tabulaw.INameValueProvider;

/**
 * EntityType
 * @author jpk
 */
public enum EntityType implements INameValueProvider<String> {

	AUTHORITY("Authority"),
	USER("User"),
	USER_STATE("User State"),
	
	BUNDLE_USER_BINDING("Bundle User Binding"),
	
	CASE("Case"),
	DOCUMENT("Document"),
	
	QUOTE("Quote"),
	QUOTE_BUNDLE("Quote Bundle"),
	
	NOTE("Note");

	private final String name;

	private EntityType(String name) {
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
