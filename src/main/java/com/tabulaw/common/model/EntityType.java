/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.common.model;

import com.tabulaw.IMarshalable;
import com.tabulaw.ITypeDescriptorProvider;

/**
 * The defined entity types in the app expressed as an enum.
 * <p>
 * <b>IMPT: </b>The corres. cannonical (stirng-wise) entity type is the enum's
 * desc() value.
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
