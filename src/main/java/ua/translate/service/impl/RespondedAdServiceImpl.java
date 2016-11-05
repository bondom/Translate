package ua.translate.service.impl;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ClientDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.BalanceService;
import ua.translate.service.RespondedAdService;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.IllegalActionForRespondedAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TranslatorDistraction;

@Service
@Transactional(propagation = Propagation.REQUIRED )
public class RespondedAdServiceImpl implements RespondedAdService {

	@Autowired
	private RespondedAdDao respondedAdDao;
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private BalanceService balanceService;
	
	
	
	private static Logger logger = LoggerFactory.getLogger(RespondedAdServiceImpl.class);
	
	/**
	 * Percentage pledge for transfering from client's account to translator's account
	 */
	public static int DEFAULT_PLEDGE = 20;

	public RespondedAd get(long id) throws InvalidIdentifier{
		RespondedAd respondedAd = respondedAdDao.get(id);
		if(respondedAd==null){
			throw new InvalidIdentifier();
		}
		return respondedAd;
	}
	
	public RespondedAd acceptRespondedAdAndTransferPledge(final String email,
												   final long id, 
												   final int pledge) 
															throws InvalidIdentifier,
																   IllegalActionForRespondedAd,
																   TranslatorDistraction,
																   InsufficientFunds{
		final ReentrantLock lock = new ReentrantLock();
		lock.lock();
		try{
			final RespondedAd respondedAd = get(id);
			final Client client = clientDao.getClientByEmail(email);
			if(!clientOwnsRespondedAd(client, id)){
				throw new InvalidIdentifier();
			}
			if(!respondedAd.getStatus().equals(RespondedAdStatus.SENDED)){
				throw new IllegalActionForRespondedAd();
			}
			
			final Ad mainAd = respondedAd.getAd();
			if(pledge>0){
				if(client.getBalance()<mainAd.getCost()*pledge/100){
					throw new InsufficientFunds();
				}
			}else{
				if(client.getBalance()<mainAd.getCost()*DEFAULT_PLEDGE/100){
					throw new InsufficientFunds();
				}
			}
			
			final Translator translator = respondedAd.getTranslator();
			if(hasAcceptedAd(translator)){
				throw new TranslatorDistraction();
			}
			
			mainAd.setStatus(AdStatus.ACCEPTED);
			mainAd.setTranslator(translator);
			translator.setAd(mainAd);
			
			Set<RespondedAd> respondedAds = mainAd.getRespondedAds();
			respondedAds.forEach(rad->{
				if(respondedAd.equals(rad)){
					rad.setStatus(RespondedAdStatus.ACCEPTED);
				}else{
					rad.setStatus(RespondedAdStatus.REJECTED);
				}
			});
			
			
			if(pledge>0){
				balanceService.transferMoneyFromClientToTranslator(client, translator, 
						mainAd.getCost()*pledge/100);
			}else{
				balanceService.transferMoneyFromClientToTranslator(client, translator, 
						mainAd.getCost()*DEFAULT_PLEDGE/100);
				logger.debug("pledge = {}, default value={} is used",
						pledge,DEFAULT_PLEDGE);
			}
			
			respondedAdDao.flush();
		}finally{
			lock.unlock();
		}
		RespondedAd updatedRespondedAd = respondedAdDao.get(id);
		return updatedRespondedAd;
	}
	
	public void reject(String email,long id) throws InvalidIdentifier,
													IllegalActionForRespondedAd{
		RespondedAd respondedAd= get(id);
		if(respondedAd.getStatus().equals(RespondedAdStatus.REJECTED)){
			return;
		}
		final Client client = clientDao.getClientByEmail(email);
		if(!clientOwnsRespondedAd(client, id)){
			throw new InvalidIdentifier();
		}
		
		if(respondedAd.getStatus().equals(RespondedAdStatus.ACCEPTED)){
			throw new IllegalActionForRespondedAd();
		}
		respondedAd.setStatus(RespondedAdStatus.REJECTED);
	}
	
	/**
	 * Checks if {@link Translator} {@code translator} has RespondedAd with ACCEPTED status
	 * @param translator - {@code Translator} object, representation of authenticated user with Translator role
	 * @return true - if {@code translator} has RespondedAd with ACCEPTED status, else false
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
	 * @param client - {@link Client} object, represents authenticated user
	 * @param radId - id of respondedAd
	 * @return true if client owns respondedAd with id={@code radId}, else false
	 */
	private boolean clientOwnsRespondedAd(Client client,long radId){
		Set<RespondedAd> respondedAd = client.getRespondedAds();
		boolean clientOwns = 
				respondedAd.stream().anyMatch(rad -> (new Long(rad.getId())).equals(radId));
		return clientOwns;
	}
	
}

