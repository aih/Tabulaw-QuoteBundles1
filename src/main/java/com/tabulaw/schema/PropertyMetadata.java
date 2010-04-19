package com.tabulaw.schema;


/**
 * Schema based information for a particular field that exists in the schema.
 * @author jpk
 */
public final class PropertyMetadata extends AbstractSchemaProperty {

	private boolean managed;
	private boolean required;
	/**
	 * The max allowed length. <code>-1</code> indicates undefined.
	 */
	private int maxLen;

	/**
	 * Constructor - Needed for GWT compile
	 */
	public PropertyMetadata() {
		super();
	}

	/**
	 * Constructor
	 * @param propertyType The property type
	 * @param managed Is the proeprty managed?
	 * @param required I.e. not nullable?
	 * @param maxLen The max allowed String-wise length
	 */
	public PropertyMetadata(final PropertyType propertyType, final boolean managed, final boolean required,
			final int maxLen) {
		super(propertyType);
		this.managed = managed;
		this.required = required;
		this.maxLen = maxLen;
	}

	public String descriptor() {
		return "Property Metadata";
	}

	public boolean isManaged() {
		return managed;
	}

	/**
	 * @return the allowed max length or <code>-1</code> if undefined.
	 */
	public int getMaxLen() {
		return maxLen;
	}

	public boolean isRequired() {
		return required;
	}

	@Override
	public String toString() {
		return "type:" + getPropertyType() + "|required:" + isRequired() + "|managed:" + isManaged() + "|maxLen:"
				+ getMaxLen();
	}

}
