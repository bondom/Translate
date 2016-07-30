package ua.translate.model.customvalidators;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import ua.translate.model.ad.Language;

public class FieldNotMatchValidator implements ConstraintValidator<FieldNotMatch, Object>{

	private String firstFieldName;
	private String secondFieldName;
	private String message;
	  
	@Override
	public void initialize(FieldNotMatch constraintAnnotation) {
		firstFieldName = constraintAnnotation.first();
	    secondFieldName = constraintAnnotation.second();
	    message = constraintAnnotation.message(); 
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		boolean validationPassed = false;
		try {
			final Object firstObj = BeanUtils.getProperty(value, firstFieldName );
			final Object secondObj = BeanUtils.getProperty(value, secondFieldName );
			
			validationPassed = firstObj == null && secondObj == null ||
							   firstObj != null && !firstObj.equals(secondObj);
							   
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!validationPassed) {
	        context.disableDefaultConstraintViolation();
	        context.buildConstraintViolationWithTemplate(message).addPropertyNode(firstFieldName).addConstraintViolation();
	    }
	    return validationPassed;
		
		
	}

}
