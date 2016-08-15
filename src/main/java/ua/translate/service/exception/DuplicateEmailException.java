package ua.translate.service.exception;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * Thrown when user attempts to save already registered e-mail
 * 
 * @author Yuriy Phediv
 *
 */
public class DuplicateEmailException extends Exception{
	
	public DuplicateEmailException(String msg){
		super(msg);
	}
	
	public DuplicateEmailException(){
		super();
	}
}
