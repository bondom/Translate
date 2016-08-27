package ua.translate.service.exception;

import ua.translate.model.ad.Ad;

/**
 * Thrown when client attempts to refresh {@link Ad#getPublicationDateTime()} 
 * of {@code Ad}, but required time for that has not yet passed
 * @author Yuriy Phediv
 *
 */
public class TooManyRefreshings extends Exception{

}
