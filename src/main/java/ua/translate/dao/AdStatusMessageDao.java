package ua.translate.dao;

import java.util.Set;

import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;

public interface AdStatusMessageDao extends AbstractDao<Long, AdStatusMessage>{
	
	
	/**
	 * Gets {@link AdStatusMessage} object from data storage by its adStatus and translateType
	 * @param adStatus - {@link AdStatus}
	 * @param translateType - {@link TranslateType}
	 * @return {@code AdStatusMessage}, or {@code null} if {@code AdStatusMessage}
	 * with {@link AdStatusMessage#getAdStatus() AdStatusMessage.adStatus} = {@code adStatus}
	 * and {@link AdStatusMessage#getTranslateType() AdStatusMessage.translateType} = {@code translateType}
	 * doesn't exist in data storage
	 */
	public AdStatusMessage getAdStatusMessageByStatusAndTranslateType(AdStatus adStatus,
																	  TranslateType translateType);
	
	/**
	 * Gets all {@link AdStatusMessage} objects, which exists in data storage
	 * @return {@code Set}, never {@code null}
	 */
	public Set<AdStatusMessage> getAllAdStatusMessages();
}
