package ua.translate.service;


import java.util.List;

import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.NonExistedTranslatorException;

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
	 * Gets {@code List} of all {@link Translator} objects from data storage, never {@code null}
	 */
	public abstract List<Translator> getAllTranslators();
	
	/**
	 * Gets {@link Ad} from data storage, if such exists creates {@link ResponsedAd},
	 * and sets all fields of new object.
	 * @param email - email of authenticated {@link Translator}, <b>must</b> be retrieved from
	 * {@code Principal} object
	 * @param adId - id of {@code Ad}
	 * @throws NonExistedAdException if {@code Ad} with id={@code adId} doesn't exist
	 */
	public abstract void saveResponsedAd(String email,long adId) throws NonExistedAdException;
}
