package org.dream.university.model.customvalidators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
@Documented
public @interface FieldMatch {
	Class<? extends Payload>[] payload() default {};
	Class<?>[] groups() default {};
	
	/**
	 * @return The first field
	 */
	String first();

	/**
	 * @return The second field
	 */
	String second();

	/**
	  @return the Error Message
	 */
	String message();
}
