package ua.translate.service.exception;

import ua.translate.model.ad.RespondedAd;

/**
 * Thrown when translator already has ACCEPTED {@link RespondedAd}, but 
 * he performs action for adding one more RespondedAd, or another client wants to accept
 * {@code RespondedAd}
 * @author Yuriy Phediv
 *
 */
public class TranslatorDistraction extends Exception {

}
