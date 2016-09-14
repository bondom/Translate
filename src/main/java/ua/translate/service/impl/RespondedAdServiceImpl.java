package ua.translate.service.impl;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ClientDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.RespondedAdService;
import ua.translate.service.exception.IllegalActionForAcceptedAd;
import ua.translate.service.exception.IllegalActionForRejectedAd;
import ua.translate.service.exception.NonExistedRespondedAdException;
import ua.translate.service.exception.TranslatorDistraction;

@Service
@Transactional(propagation = Propagation.REQUIRED )
public class RespondedAdServiceImpl implements RespondedAdService {

	@Autowired
	private RespondedAdDao respondedAdDao;
	
	@Autowired
	private ClientDao clientDao;

	public RespondedAd get(long id) throws NonExistedRespondedAdException{
		RespondedAd respondedAd = respondedAdDao.get(id);
		if(respondedAd==null){
			throw new NonExistedRespondedAdException();
		}
		return respondedAd;
	}
	
	public void accept(String email,long id) throws NonExistedRespondedAdException,
									   IllegalActionForAcceptedAd,
									   IllegalActionForRejectedAd,
									   TranslatorDistraction{
		final ReentrantLock lock = new ReentrantLock();
		lock.lock();
		try{
			RespondedAd respondedAd = get(id);
			if(!clientOwnsRespondedAd(email, id)){
				throw new NonExistedRespondedAdException();
			}
			if(respondedAd.getStatus().equals(RespondedAdStatus.ACCEPTED)){
				throw new IllegalActionForAcceptedAd();
			}
			
			if(respondedAd.getStatus().equals(RespondedAdStatus.REJECTED)){
				throw new IllegalActionForRejectedAd();
			}
			Translator translator = respondedAd.getTranslator();
			if(hasAcceptedAd(translator)){
				throw new TranslatorDistraction();
			}
			
			Ad mainAd = respondedAd.getAd();
			mainAd.setStatus(AdStatus.ACCEPTED);
			Set<RespondedAd> respondedAds = mainAd.getRespondedAds();
			respondedAds.forEach(rad->{
				if(respondedAd.equals(rad)){
					rad.setStatus(RespondedAdStatus.ACCEPTED);
				}else{
					rad.setStatus(RespondedAdStatus.REJECTED);
				}
			});
			
			respondedAdDao.flush();
		}finally{
			lock.unlock();
		}
	}
	
	public void reject(String email,long id) throws NonExistedRespondedAdException,
									   IllegalActionForAcceptedAd{
		RespondedAd respondedAd= get(id);
		if(respondedAd.getStatus().equals(RespondedAdStatus.REJECTED)){
			return;
		}
		
		if(!clientOwnsRespondedAd(email, id)){
			throw new NonExistedRespondedAdException();
		}
		
		if(respondedAd.getStatus().equals(RespondedAdStatus.ACCEPTED)){
			throw new IllegalActionForAcceptedAd();
		}
		respondedAd.setStatus(RespondedAdStatus.REJECTED);
	}
	
	/**
	 * Checks if {@link Translator} {@code translator} has ResponsedAd with ACCEPTED status
	 * @param translator - {@code Translator} object, representation of authenticated user with Translator role
	 * @return true - if {@code translator} has ResponsedAd with ACCEPTED status, else false
	 */
	private boolean hasAcceptedAd(Translator translator) {
		Set<RespondedAd> respondedAds = translator.getRespondedAds();
		boolean acceptedAdExists = 
				respondedAds.stream()
							.anyMatch(rad -> 
							 	rad.getStatus().equals(RespondedAdStatus.ACCEPTED));
		return acceptedAdExists;
	}
	
	/**
	 * Checks if {@link RespondedAd} with id={@code radId} belongs to client with email={@code email}
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param radId - id of respondedAd
	 * @return true if client owns respondedAd with id={@code radId}, else false
	 */
	private boolean clientOwnsRespondedAd(String email,long radId){
		Client client = clientDao.getClientByEmail(email);
		Set<RespondedAd> respondedAd = client.getRespondedAds();
		boolean clientOwns = 
				respondedAd.stream().anyMatch(rad -> (new Long(rad.getId())).equals(radId));
		return clientOwns;
	}
	

}

