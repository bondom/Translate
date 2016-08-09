package ua.translate.service.exception;

import ua.translate.model.Translator;

/**
 * Thrown when {@link Translator} {@code translator}, 
 * retrieved from data storage is {@code null}
 * @author Yuriy Phediv
 *
 */
public class NonExistedTranslatorException extends Exception{
	
	public NonExistedTranslatorException(){
		super();
	}
}
