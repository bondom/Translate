package org.dream.university.customValidators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Documented
@Constraint(validatedBy = GroupValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsGroupValid {
	String listOfGroup() default "RT-41|RA-41|RS-41";
	
	String message() default "Please type valid group, such as RT-41,RA-41,RS-41";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}