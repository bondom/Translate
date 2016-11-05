package ua.translate.dao;

import java.util.Set;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.RespondedAd;

public interface RespondedAdDao extends AbstractDao<Long, RespondedAd>{
	/**
	 * Returns {@link RespondedAd}s, related to {@code client},
	 * ordered by {@link RespondedAd#getDateTimeOfResponse()} 
	 * from latest to earliest.
	 * size of result {@code Set} is not more than {@code numberRespondedAdsOnPage}
	 * @param client - {@link Client} {@code client}
	 * @param page -  page number, can't be less than 1
	 * @param numberRespondedAdsOnPage - 
	 * 			number of {@code RespondedAd}s, which can be displayed on 1 page
	 */
	public Set<RespondedAd> getRespondedAdsByClient(Client client,
											int page,
											int numberRespondedAdsOnPage);
	
	/**
	 * Returns number of all {@link RespondedAd}s, related to {@code client}
	 * @param client - {@link Client} {@code client}
	 */
	public long getNumberOfRespondedAdsByClient(Client client);
	
	
	
	/**
	 * Returns {@link RespondedAd}s, related to {@code translator},
	 * ordered by {@link RespondedAd#getDateTimeOfResponse()} 
	 * from latest to earliest.
	 * size of result {@code Set} is not more than {@code numberRespondedAdsOnPage}
	 * @param translator - {@link Translator} {@code translator}
	 * @param page -  page number, can't be less than 1
	 * @param numberRespondedAdsOnPage - 
	 * 			number of {@code RespondedAd}s, which can be displayed on 1 page
	 */
	public Set<RespondedAd> getRespondedAdsByTranslator(Translator translator,
											int page,
											int numberRespondedAdsOnPage);
	
	/**
	 * Returns number of all {@link RespondedAd}s, related to {@code translator}
	 * @param translator - {@link Translator} {@code translator}
	 */
	public long getNumberOfRespondedAdsByTranslator(Translator translator);
	
	
	
}
