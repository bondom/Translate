package ua.translate.service;

import java.security.Principal;

import ua.translate.model.ResponsedAd;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.service.exception.NonExistedAdException;

public abstract class TranslatorService extends UserService<Translator>{
	
	/**
	 * Gets {@link Translator} object by id from data storage, never null
	 * @param email - email of authenticated translator, <b>must</b> be retrieved from
	 * {@code Principal} object
	 */
	public abstract Translator getTranslatorByEmail(String email);
	
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
