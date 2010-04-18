/**
 * The Logic Lab
 * @author jpk Dec 22, 2007
 */
package com.tll.model.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * AtLeastOne - Signifies "at least one" requirement.
 * @author jpk
 */
@Constraint(validatedBy = AtLeastOneValidator.class)
@Target(value = {
	ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AtLeastOne {

	String message() default "{validator.at_least_one}";

	Class<?>[] groups() default {};

	String type();

	Class<? extends Payload>[] payload() default {};
}
