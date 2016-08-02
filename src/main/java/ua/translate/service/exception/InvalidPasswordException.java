package ua.translate.service.exception;

/**
 * Thrown when user attempts do some action, which need password confirmation,
 * but password, entered by user, is invalid
 * @author Морф
 *
 */
public class InvalidPasswordException extends Exception {
	
	public InvalidPasswordException(String msg){
		super(msg);
	}
}
