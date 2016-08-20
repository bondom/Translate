package ua.translate.service.exception;

import ua.translate.model.status.RespondedAdStatus;

/**
 * Thrown when {@link Ad} {@code ad} has responses with {@code RespondedAdStatus#SENDED} status
 * and {@code client} want to update this {@code ad} 
 * @author Yuriy Phediv
 *
 */
public class IllegalActionForAd extends Exception {

}
