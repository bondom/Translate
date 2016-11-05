package ua.translate.service.exception;

import ua.translate.model.ad.Ad;

/**
 * Thrown when some entity is being retrieved from data storage by id, but 
 * entity with such id doesn't exist
 * 
 * @author Yuriy Phediv
 *
 */
public class InvalidIdentifier extends Exception{

	public InvalidIdentifier(){
		super();
	}
	
	public InvalidIdentifier(String msg){
		super(msg);
	}
}
