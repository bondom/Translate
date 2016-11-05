package ua.translate.service;


import java.util.Set;

import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.NumberExceedsException;
import ua.translate.service.exception.TranslatorDistraction;

public abstract class TranslatorService extends UserService<Translator>{
	
	/**
	 * Gets {@link Translator} object by email from data storage, never {@code null}
	 * @param email - email of authenticated translator, <b>must</b> be retrieved from
	 * {@code Principal} object
	 */
	public abstract Translator getTranslatorByEmail(String email);
	
	/**
	 * Gets {@link Translator} {@code translator} by id from data storage
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
     * @return {@code Translator} object, never {@code null} 
     * @throws InvalidIdentifier if retrieved {@code translator} with such id
     * doesn't exist in data storage
	 */
	public abstract Translator getTranslatorById(long id) throws InvalidIdentifier;
	
	 /**
	 * Gets {@code Set} of {@link Translator}s from data storage, ordered by {@link Translator#getPublishingTime()} 
	 * from latest to earliest.
	 * Size of result {@code Set} is not more than {@code numberTranslatorsOnPage}
	 * <p><b>NOTE:</b>AfterReturning Logging via Spring AOP is present
	 * <p>If {@code numberOfTranslatorsOnPage} is less than 1, default value is used
	 * <p>If {@code page} is less than 1, 1 is being used instead
	 * @param page -  page number
	 * @param numberOfTranslatorsOnPage - number of {@link Translator}s, which can be displayed on 1 page
	 * @return set of {@code Translator}s, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less then 1
	 */
	public abstract Set<Translator> getTranslators(int page,int numberOfTranslatorsOnPage);
	
	/**
	 * First Gets {@link Ad} {@code ad} by id and {@link Translator} {@code translator} by email
	 * from data storage. 
	 * <br>If {@code ad} exists and its status is {@link AdStatus#SHOWED SHOWED}
	 * creates {@link RespondedAd} {@code repondedAd}, gets {@link Client} from {@code ad}
	 * and sets it to {@code respondedAd}. Sets {@code ad} and {@code translator} 
	 * to {@code respondedAd} as well.
	 * <br>Finally saves {@code respondedAd} in data storage.
	 * <p>If {@code maxNumberOfSendedRespondedAds} is less than 1, default value is used
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated {@link Translator}, <b>must</b> be retrieved from
	 * {@code Principal} object
	 * @param adId - id of {@code Ad}
	 * @return generated id of created RespondedAd
	 * @throws InvalidIdentifier if {@code Ad} with id={@code adId} doesn't exist
	 * @throws NumberExceedsException if number of RespondedAds with SENDED status exceeds
	 * {@code maxNumberOfSendedRespondedAds}
	 * @throws TranslatorDistraction if translator has ACCEPTED RespondedAd
	 * @throws IllegalActionForAd if status of {@code ad} is different from {@code SHOWED}
	 */
	public abstract long createAndSaveRespondedAd(String email,long adId, int maxNumberOfSendedRespondedAds) 
										throws InvalidIdentifier, NumberExceedsException,TranslatorDistraction,
																						IllegalActionForAd;
	
	/**
	 * Returns number of pages for all existed {@link Translator}s with {@code status=SHOWED},
	 * if on one page can be displayed only {@code numberOfTranslators} Translator.
	 * <p>If {@code numberOfTranslatorsOnPage} is less than 1, default value is used
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param numberTranslatorsOnPage - number of {@code Translator}s, which can be displayed on one page
	 */
	public abstract long getNumberOfPagesForTranslators(int numberTranslatorsOnPage);
	
	
	
}
