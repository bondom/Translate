package ua.translate.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AdStatusMessageDao;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;
import ua.translate.service.AdStatusMessageService;
import ua.translate.service.exception.InvalidIdentifier;

@Service
@Transactional
public class AdStatusMessageServiceImpl implements AdStatusMessageService{
	
	@Autowired
	private AdStatusMessageDao adStatusMessageDao;

	@Override
	public Set<AdStatusMessage> getAllAdStatusMessages() {
		return adStatusMessageDao.getAllAdStatusMessages();
	}

	@Override
	public void updateAdStatusMessage(AdStatusMessage adStatusMessage) throws InvalidIdentifier {
		AdStatus adStatus = adStatusMessage.getAdStatus();
		if(adStatus == null){
			throw new InvalidIdentifier("");
		}
		TranslateType translateType = adStatusMessage.getTranslateType();
		if(translateType == null){
			throw new InvalidIdentifier();
		}
		AdStatusMessage adStatusMessageFromDb = 
				getAdStatusMessageByAdStatusAndTranslateType(adStatus,translateType);
		adStatusMessageFromDb.setMessageForClient(adStatusMessage.getMessageForClient());
		adStatusMessageFromDb.setMessageForTranslator(adStatusMessage.getMessageForTranslator());
	}

	@Override
	public AdStatusMessage getAdStatusMessageByAdStatusAndTranslateType(AdStatus adStatus,
														TranslateType translateType) {
		AdStatusMessage adStatusMessage = 
				adStatusMessageDao.getAdStatusMessageByStatusAndTranslateType(adStatus,translateType);
		return adStatusMessage;
	}

}
