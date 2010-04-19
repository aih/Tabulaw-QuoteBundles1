/**
 * 
 */
package com.tabulaw.client.validate;

/**
 * CharacterValidator
 * @author jpk
 */
public class CharacterValidator implements IValidator {

	public static final CharacterValidator INSTANCE = new CharacterValidator();

	/**
	 * Constructor
	 */
	private CharacterValidator() {
		super();
	}

	public Object validate(Object value) throws ValidationException {
		if(value == null || value instanceof Character) return value;
		final String s = value.toString();
		if(s.length() == 0) return null;
		if(s.length() != 1) {
			throw new ValidationException("The value must be a single character.");
		}
		return Character.valueOf(s.charAt(0));
	}

}
