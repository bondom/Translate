package ua.translate.service.exception;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.AdService;
import ua.translate.service.OralAdService;
import ua.translate.service.TranslatorService;
import ua.translate.service.WrittenAdService;

/**
 * Thrown when user attempts to execute some action:
 * <ul>
 * 	<li>showing</li> 
 * 	<li>updating</li>  
 *  <li>deleting</li> 
 *  <li>{@link TranslatorService#createAndSaveRespondedAd(String, long, int) responding}</li>
 * 	<li>{@link AdService#refreshPubDate(String, long, int) refreshing}</li> 
 * 	<li>{@link WrittenAdService#markAsChecked(String, long) checking}</li> 
 * 	<li>{@link WrittenAdService#transferRestPriceAndChangeStatus(String, long, int) paying for WrittenAd}</li> 
 * 	<li>{@link OralAdService#transferRestPrice(String, long, int) paying for OralAd)</li> 
 * </ul>
 * with {@link Ad} {@code ad}, but {@link AdStatus} of this {@code ad}
 * doesn't permit that particular action.
 * <p> List of actions for every {@code AdStatus}:
 * <ul>
 * 	<li>{@link AdStatus#SHOWED} - all actions are permitted</li>
 * 	<li>{@link AdStatus#ACCEPTED} -only "paying for OralAd" is permitted</li>
 * 	<li>{@link AdStatus#NOTCHECKED} - only checking is permitted</li>
 *  <li>{@link AdStatus#CHECKED} - only "paying for WrittenAd" is permitted</li>
 * 	<li>{@link AdStatus#PAYED} - showing, refreshing, responding and editing are prohibited</li>
 * </ul>
 * <p>If at least one {@link RespondedAd}s, related to {@code ad}(with {@code SHOWED} status), 
 * has {@link RespondedAdStatus#SENDED SENDED} status, updating is prohibited as well. 
 * @author Yuriy Phediv
 *
 */
public class IllegalActionForAd extends Exception {

	public IllegalActionForAd(){
		super();
	}
	
	public IllegalActionForAd(String msg){
		super(msg);
	}
}
