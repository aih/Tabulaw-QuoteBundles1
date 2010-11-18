/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Dec 22, 2007
 */
package com.tabulaw.model.validate;

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
