package ua.translate.service;


import java.util.List;
import java.util.Set;

import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.NonExistedTranslatorException;
import ua.translate.service.exception.NumberExceedsException;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.exception.WrongPageNumber;

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
     * @throws NonExistedTranslatorException if retrieved {@code translator} with such id
     * doesn't exist in data storage
	 */
	public abstract Translator getTranslatorById(long id) throws NonExistedTranslatorException;
	
	 /**
	 * Gets {@code Set} of {@link Translator}s from data storage, ordered by {@link Translator#getPublishingTime()} 
	 * from latest to earliest.
	 * Size of result {@code Set} is not more than {@code numberTranslatorsOnPage}
	 * <p><b>NOTE:</b>AfterReturning Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberOfTranslatorsOnPage - number of {@link Translator}s, which can be displayed on 1 page
	 * @return set of {@code Translator}s, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less then 1
	 */
	public abstract Set<Translator> getTranslators(int page,int numberOfTranslatorsOnPage) 
								throws WrongPageNumber;
	
	/**
	 * Gets {@link Ad} from data storage, if such exists creates {@link RespondedAd},
	 * and sets all fields of new object.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated {@link Translator}, <b>must</b> be retrieved from
	 * {@code Principal} object
	 * @param adId - id of {@code Ad}
	 * @return generated id of created RespondedAd
	 * @throws NonExistedAdException if {@code Ad} with id={@code adId} doesn't exist
	 * @throws NumberExceedsException if number of RespondedAds with SENDED status exceeds
	 * {@code maxNumberOfSendedRespondedAds}
	 * @throws TranslatorDistraction if translator has ACCEPTED RespondedAd
	 */
	public abstract long saveRespondedAd(String email,long adId, int maxNumberOfSendedRespondedAds) 
										throws NonExistedAdException, NumberExceedsException,TranslatorDistraction;
	
	/**
	 * Returns number of pages for all existed {@link Translator}s with {@code status=SHOWED},
	 * if on one page can be displayed only {@code numberOfTranslators} {@code Translator}s
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param numberOfTranslators - number of {@code Translator}s, which can be displayed on one page
	 */
	public abstract long getNumberOfPagesForTranslators(int numberOfTranslators);
	
	/**
	 * Returns {@code RespondedAd} with ACCEPTED status, or {@code null},
	 * if translator haven't such {@code RespondedAd}  
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated {@link Translator}, 
	 * <b>must</b> be retrieved from {@code Principal} object
	 */
	public abstract RespondedAd getCurrentOrder(String email);
	
	/**
	 * Gets {@link Ad} {@code ad} from data storage by id, if translator with email owns {@code ad},
	 * and {@code ad} have ACCEPTED status, changes status to NOTCHECKED.
	 * <p>RespondedAd which is related to this Ad and Translator, still has ACCEPTED status,
	 * for avoiding situation when translator Ad needs to be completed or edited, and translator
	 * have already taken one more Ad.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param adId - id of ACCEPTED {@code Ad}
	 * @param email - email of authenticated {@link Translator}, 
	 * <b>must</b> be retrieved from {@code Principal} object
	 * @return true, if status is changed to NOTCHECKED, else false
	 */
	public abstract boolean markAsNotChecked(String email,long adId);
	
}
