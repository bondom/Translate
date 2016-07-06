package org.dream.university.customValidators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GroupValidator implements ConstraintValidator<IsGroupValid, String>{

	String listOFGroup;
	
	@Override
	public void initialize(IsGroupValid arg0) {
		// TODO Auto-generated method stub
		this.listOFGroup = arg0.listOfGroup();
	}

	@Override
	public boolean isValid(String group, ConstraintValidatorContext arg1) {
		// TODO Auto-generated method stub
		if(group == null){
			return false;
		}
		if(group.matches(listOFGroup)){
			return true;
		}
		return false;
	}

}
