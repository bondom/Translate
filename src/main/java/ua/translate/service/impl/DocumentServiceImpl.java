package ua.translate.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ClientDao;
import ua.translate.dao.WrittenAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.UserEntity;
import ua.translate.model.UserEntity.UserRole;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.service.DocumentService;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.InvalidIdentifier;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService{
	
	private static Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private WrittenAdDao adDao;
	
	@Override
	public Document getDocumentForDownloading(long id, String userEmail) throws InvalidIdentifier, DownloadFileAccessDenied {
		WrittenAd ad = adDao.getWrittenAdById(id);
		if(ad == null){
			throw new InvalidIdentifier();
		}
		UserEntity user = clientDao.getUserEntityByEmail(userEmail);
		if(user==null){
			logger.debug("User with unregistered email attempts to download Document");
			throw new DownloadFileAccessDenied();
		}
		UserRole role = user.getRole();
		if(UserRole.ROLE_ADMIN.equals(role) || 
				UserRole.ROLE_TRANSLATOR.equals(role)){
			return ad.getDocument();
		}
		if(user.getEmail().equals(ad.getClient().getEmail())){
			return ad.getDocument();
		}
		throw new DownloadFileAccessDenied();
	}

	@Override
	public ResultDocument getResultDocumentForDownloading(long adId, String userEmail)
			throws InvalidIdentifier, DownloadFileAccessDenied {
		final WrittenAd ad = adDao.getWrittenAdById(adId);
		if(ad == null){
			throw new InvalidIdentifier();
		}
		final UserEntity user = clientDao.getUserEntityByEmail(userEmail);
		if(user==null){
			logger.debug("User with unregistered email attempts to download ResultDocument");
			throw new DownloadFileAccessDenied();
		}
		final ResultDocument resultDocument = ad.getResultDocument();
		if(user.getRole().equals(UserRole.ROLE_ADMIN)){
			return resultDocument;
		}
		if(user.getRole().equals(UserRole.ROLE_CLIENT)){
			Client client = (Client)user;
			if(client.equals(ad.getClient()) && AdStatus.PAYED.equals(ad.getStatus())){
				return resultDocument;
			}else{
				logger.debug("Client attempts to download ResultDocument, but is not owner"
						+ " of advertisement, or he doesn't pay yet");
				throw new DownloadFileAccessDenied();
			}
		}
		if(user.getRole().equals(UserRole.ROLE_TRANSLATOR)){
			Translator translator = (Translator)user;
			if(translator.equals(ad.getTranslator())){
				return resultDocument;
			}else{
				logger.debug("Translator attempts to download ResultDocument, which was "
						+ " downloaded by another translator");
				throw new DownloadFileAccessDenied();
			}
		}
		logger.debug("User with undefined role attempts to download ResultDocument");
		throw new DownloadFileAccessDenied();
	}
}
