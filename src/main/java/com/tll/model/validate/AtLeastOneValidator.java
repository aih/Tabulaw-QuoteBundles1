/**
 * The Logic Lab
 * @author jpk Dec 22, 2007
 */
package com.tll.model.validate;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * AtLeastOneValidator - Ensures at least one elements exists in a
 * {@link Collection}.
 * @see AtLeastOne
 * @author jpk
 */
public class AtLeastOneValidator implements ConstraintValidator<AtLeastOne, Collection<?>> {

	public void initialize(AtLeastOne parameters) {
		// no-op
	}

	public boolean isValid(Collection<?> clc, ConstraintValidatorContext constraintContext) {
		return clc.size() > 0;
	}
}
