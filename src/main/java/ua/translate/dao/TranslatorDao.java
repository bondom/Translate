package ua.translate.dao;

import java.util.List;
import java.util.Set;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;

public abstract class TranslatorDao  extends AbstractUserDao<Long, Translator>{
	
	/**
	 * Gets {@link Translator} {@code translator} from data storage by {@code email}
	 * @param email - email of translator
	 * @return {@code Translator} object or {@code null}, if translator with such email doesn't
	 * exist in data storage
	 */
	public abstract Translator getTranslatorByEmail(String email);
	
	/**
	 * Gets {@code Set} of {@link Translator}s from data storage, ordered by {@link Translator#getPublishingTime()} 
	 * from latest to earliest.
	 * size of result {@code Set} is not more than {@code numberTranslatorsOnPage}
	 * @param page -  page number, can't be less than 1
	 * @param numberTranslatorsOnPage - number {@link Ad}s, which can be displayed on 1 page
	 * @return set of {@code Translator}s, never {@code null}
	 */
	public abstract Set<Translator> getTranslators(int page,int numberTranslatorsOnPage);
	
	/**
	 * Returns number of all {@link Translator}s , which exist in data storage
	 */
	public abstract long getNumberOfTranslators();
}
