package ua.translate.service.exception;

import java.time.LocalDateTime;

import ua.translate.model.ad.OralAd;

/**
 * Thrown when client attempts to pay for executing of {@link OralAd},
 * but {@link LocalDateTime#now() LocalDateTime.now()} is more than 
 * {@link OralAd#getFinishDateTime() OralAd.finishDateTime}
 * 
 * @author Yuriy Phediv
 *
 */
public class TooEarlyPaying extends Exception {

}
