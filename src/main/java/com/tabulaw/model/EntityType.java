/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.model;

import com.tabulaw.IMarshalable;
import com.tabulaw.ITypeDescriptorProvider;

/**
 * The defined entity types in the app expressed as an enum.
 * <p>
 * <b>IMPT: </b>The cannonical entity type token is the
 * <code>EntityType.name()</code>. This name is used client-side.
 * @author jpk
 */
public enum EntityType implements IMarshalable, ITypeDescriptorProvider {

	USER("User"),
	USER_STATE("User State"),

	BUNDLE_USER_BINDING("Bundle User Binding"),
	QUOTE_USER_BINDING("Quote User Binding"),
	DOC_USER_BINDING("Document User Binding"),

	CASE("Case"),
	DOCUMENT("Document"),
	DOC_CONTENT("Doc Content"),
	
	DOC_CONTRACT("Contract Document"),
	
	CLAUSE_DEF("Clause Definition"),
	CLAUSE_BUNDLE("Clause Bundle"),

	QUOTE("Quote"),
	QUOTE_BUNDLE("Quote Bundle"),

	NOTE("Note");

	private final String desc;

	private EntityType(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name();
	}

	@Override
	public String typeDesc() {
		return desc;
	}

	public static EntityType fromString(String set) {
		return Enum.valueOf(EntityType.class, set);
	}
}
