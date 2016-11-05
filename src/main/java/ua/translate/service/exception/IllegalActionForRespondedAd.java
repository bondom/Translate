package ua.translate.service.exception;

import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;

/**
 * Thrown when user attempts to execute some action(rejecting, accepting, )
 * with {@link RespondedAd} {@code respondedAd}, but {@link RespondedAdStatus}
 *  of this {@code respondedAd}
 * doesn't permit that particular action.
 * <p> List of actions for every {@code RespondedAdStatus}:
 * <ul>
 * 	<li>{@link RespondedAdStatus#SENDED} - all actions are permitted</li>
 * 	<li>{@link RespondedAdStatus#ACCEPTED} - all actions are prohibited</li>
 * 	<li>{@link RespondedAdStatus#REJECTED} - all actions are prohibited</li>
 * </ul>
 * @author Yuriy Phediv
 *
 */
public class IllegalActionForRespondedAd extends Exception {

}
