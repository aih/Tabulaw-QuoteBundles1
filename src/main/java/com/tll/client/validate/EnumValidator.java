/**
 * The Logic Lab
 * @author jpk
 * Mar 1, 2009
 */
package com.tll.client.validate;

import com.tll.INameValueProvider;

/**
 * EnumValidator
 * @author jpk
 */
public class EnumValidator implements IValidator {

	private final Class<? extends Enum<?>> enumType;

	/**
	 * Constructor
	 * @param enumType
	 */
	public EnumValidator(Class<? extends Enum<?>> enumType) {
		super();
		this.enumType = enumType;
	}

	@Override
	public Object validate(Object value) throws ValidationException {
		if(value == null) return null;
		final String sval = value.toString();
		for(final Enum<?> e : enumType.getEnumConstants()) {
			if(e instanceof INameValueProvider<?> && ((INameValueProvider<?>) e).getValue().equals(sval)) {
				return e;
			}
			else if(e.toString().equals(sval)) {
				return e;
			}
		}
		throw new ValidationException("Unsupported value.");
	}

}
