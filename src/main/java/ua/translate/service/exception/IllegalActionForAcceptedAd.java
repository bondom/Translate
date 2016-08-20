package ua.translate.service.exception;

import ua.translate.model.ad.Ad;

/**
 * Thrown if {@code ad.status==ACCEPTED} and {@link User} want to get {@link Ad} to view, or
 * {@link Client} want to delete,update this {@code Ad}
 * 
 * @author Yuriy Phediv
 *
 */
public class IllegalActionForAcceptedAd extends Exception{

	public IllegalActionForAcceptedAd(){
		super();
	}
}
