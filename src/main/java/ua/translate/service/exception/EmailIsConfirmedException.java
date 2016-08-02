package ua.translate.service.exception;

/**
 * Thrown when user attempts to send letter for confirmation,
 * but e-mail is confirmed already
 * 
 * @author Yuriy Phediv
 *
 */
public class EmailIsConfirmedException extends Exception {

	public EmailIsConfirmedException(){
		super();
	}
}
