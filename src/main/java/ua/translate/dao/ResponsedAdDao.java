package ua.translate.dao;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.service.exception.WrongPageNumber;

public interface ResponsedAdDao extends AbstractDao<Long, ResponsedAd>{
	/**
	 * Returns {@link ResponsedAd}s, related to {@code client},
	 * ordered by {@link ResponsedAd#getDateTimeOfResponse()} 
	 * from latest to earliest.
	 * size of result {@code Set} is not more than {@code numberResponsedAdsOnPage}
	 * @param client - {@link Client} {@code client}
	 * @param page -  page number, can't be less than 1
	 * @param numberResponsedAdsOnPage - 
	 * 			number of {@code ResponsedAd}s, which can be displayed on 1 page
	 */
	public Set<ResponsedAd> getResponsedAdsByClient(Client client,
											int page,
											int numberResponsedAdsOnPage);
	
	/**
	 * Returns number of all {@link ResponsedAd}s, related to {@code client}
	 * @param client - {@link Client} {@code client}
	 */
	public long getNumberOfResponsedAdsByClient(Client client);
	
	
	
	/**
	 * Returns {@link ResponsedAd}s, related to {@code translator},
	 * ordered by {@link ResponsedAd#getDateTimeOfResponse()} 
	 * from latest to earliest.
	 * size of result {@code Set} is not more than {@code numberResponsedAdsOnPage}
	 * @param translator - {@link Translator} {@code translator}
	 * @param page -  page number, can't be less than 1
	 * @param numberResponsedAdsOnPage - 
	 * 			number of {@code ResponsedAd}s, which can be displayed on 1 page
	 */
	public Set<ResponsedAd> getResponsedAdsByTranslator(Translator translator,
											int page,
											int numberResponsedAdsOnPage);
	
	/**
	 * Returns number of all {@link ResponsedAd}s, related to {@code translator}
	 * @param translator - {@link Translator} {@code translator}
	 */
	public long getNumberOfResponsedAdsByTranslator(Translator translator);
	
	
	
}
