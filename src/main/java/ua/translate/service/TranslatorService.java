package ua.translate.service;


import java.util.List;
import java.util.Set;

import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.NonExistedTranslatorException;
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
     * @return {@code Translator} object, never {@code null} 
     * if translator with such id doesn't exist in data storage 
     * @throws NonExistedTranslatorException if retrieved {@code translator} with such id
     * doesn't exist in data storage
	 */
	public abstract Translator getTranslatorById(long id) throws NonExistedTranslatorException;
	
	 /**
	 * Gets {@code Set} of {@link Translator}s from data storage, ordered by {@link Translator#getPublishingTime()} 
	 * from latest to earliest.
	 * Size of result {@code Set} is not more than {@code numberTranslatorsOnPage}
	 * @param page -  page number, can't be less than 1
	 * @param numberOfTranslatorsOnPage - number of {@link Translator}s, which can be displayed on 1 page
	 * @return set of {@code Translator}s, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less then 1
	 */
	public abstract Set<Translator> getTranslators(int page,int numberOfTranslatorsOnPage) 
								throws WrongPageNumber;
	
	/**
	 * Gets {@link Ad} from data storage, if such exists creates {@link ResponsedAd},
	 * and sets all fields of new object.
	 * @param email - email of authenticated {@link Translator}, <b>must</b> be retrieved from
	 * {@code Principal} object
	 * @param adId - id of {@code Ad}
	 * @return generated id of created ResponsedAd
	 * @throws NonExistedAdException if {@code Ad} with id={@code adId} doesn't exist
	 */
	public abstract long saveResponsedAd(String email,long adId) throws NonExistedAdException;
	
	/**
	 * Returns number of pages for all existed {@link Translator}s with {@code status=SHOWED},
	 * if on one page can be displayed only {@code numberOfTranslators} {@code Translator}s
	 * @param numberOfTranslators - number of {@code Translator}s, which can be displayed on one page
	 */
	public abstract long getNumberOfPagesForTranslators(int numberOfTranslators);
}
