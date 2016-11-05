package ua.translate.service;

import java.util.Set;

import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;
import ua.translate.service.exception.InvalidIdentifier;

public interface AdStatusMessageService {
	
	/**
	 * Gets all {@link AdStatusMessage} objects, which exists in data storage
	 * @return {@code Set}, never {@code null}
	 */
	public Set<AdStatusMessage> getAllAdStatusMessages();
	
	/**
	 * 
	 * Gets {@link AdStatusMessage} {@code objectFromDb} from data storage by
	 * {@code adStatusMessage.adStatus} and {@code adStatusMessage.translateType}
	 *
	 * <p>If {@code objectFromDb} is {@code null} exception is thrown.
	 * <p>Updates {@link AdStatusMessage#getMessageForClient() AdStatusMessage.messageForClient}
	 * and {@link AdStatusMessage#getMessageForTranslator() AdStatusMessage.messageForTranslator}
	 * of {@code objectFromDb} with values, retrieved from {@code adStatusMessage}
	 * 
	 * @param adStatusMessage - {@code AdStatusMessage} object
	 * @throws InvalidIdentifier if {@code adStatusMessage.adStatus} is {@code null} or 
	 * {@code adStatusMessage.translateType} is {@code null} or {@code objectFromDb} is {@code null}
	 */
	public void updateAdStatusMessage(AdStatusMessage adStatusMessage) throws InvalidIdentifier;
	
	/**
	 * Gets {@link AdStatusMessage} {@code adStatusMessage} from data storage, 
	 * which meets following demands: {@code adStatusMessage.adStatus} equals to {@code adStatus};
	 * {@code adStatusMessage.translateType} equals to {@code translateType}
	 * 
	 * @param adStatus - {@link AdStatus}
	 * @param translateType - {@link TranslateType}
	 * @return {@code AdStatusMessage} object, or {@code null} if no one {@code AdStatusMessage} in data storage
	 * doesn't meet demands(see above).
	 */
	public AdStatusMessage getAdStatusMessageByAdStatusAndTranslateType(AdStatus adStatus,
																		TranslateType translateType);
}
