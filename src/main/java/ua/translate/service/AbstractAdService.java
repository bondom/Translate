package ua.translate.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AdDao;
import ua.translate.dao.ArchievedAdDao;
import ua.translate.dao.ClientDao;
import ua.translate.dao.GetAdDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.dao.TranslatorDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TooManyAds;
import ua.translate.service.exception.TooManyRefreshings;

/**
 * This interface contains methods for interaction with {@link Ad}s 
 * @author Yuriy Phediv
 *
 */
@Service("abstractAdService")
@Transactional(propagation = Propagation.REQUIRED)
public abstract class AbstractAdService implements AdService{
	
	private static Logger logger = LoggerFactory.getLogger(AbstractAdService.class);
	
	/**
	 * This variable contains maximum number of {@link Ad}s, that can belong to 
	 * one client
	 */
	private static final long DEFAULT_MAX_NUMBER_OF_ADS = 3;
	
	/**
	 * This variable contains minimum number of hours, that must elapse from
	 * last refreshing for allowing next one {@link Ad}
	 */
	private static final int DEFAULT_HOURS_BETWEEN_REFRESHING = 12;
	
	@Autowired
	@Qualifier("getAdDao")
	private GetAdDao adDao;
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private TranslatorDao translatorDao;
	
	@Autowired
	private ArchievedAdDao archievedAdDao;
	
	private final ReentrantLock archieveAdLock = new ReentrantLock();
	@Override
	public Ad get(long id) throws InvalidIdentifier {
		Ad ad = adDao.get(id);
		if(ad==null){
			throw new InvalidIdentifier();
		}
		return ad;
	}
	
	
	@Override
	public void deleteById(long id) throws InvalidIdentifier,IllegalActionForAd{
		Ad ad = get(id);
		if(!ad.getStatus().equals(AdStatus.SHOWED) &&
				!ad.getStatus().equals(AdStatus.PAYED)){
			throw new IllegalActionForAd();
		}
		Set<RespondedAd> respondedAds= ad.getRespondedAds();
		Client client = ad.getClient();
		
		//For every RespondedAd we must delete associations with translator and client
		//But while doing this we MUSTN'T modificate respondedAds and respondedAd.
		//Because:
		//1.ConcurrentModificationException
		//2.If to delete from respondedAd link to translator,
		//  object is not equal to object from client collection of RespondedAds 
		respondedAds.stream().forEach(rad->{
			logger.debug("{},{},{},{}",rad.getId(),rad.getAd(),rad.getClient(),rad.getTranslator());
			Translator translator = rad.getTranslator();
			//"orphan removal=true" for collection of RespondedAds in Translator.class does work
			translator.removeRespondedAd(rad);
			//is necessary for prevention re-save by cascade.
			client.removeRespondedAd(rad);
		});
		client.removeAd(ad);
		adDao.delete(ad);
	}
	@Override
	public long saveAd(Ad ad, String email,long maxNumberOfAds)
								throws TooManyAds,DuplicateAdException{
		Client client = clientDao.getClientByEmail(email);
		Set<Ad> ads = client.getAds();
		long numberOfAds = ads.size();
		if(maxNumberOfAds<1){
			logger.debug("maxNumberOfAds = {}, default value={} is used",
					maxNumberOfAds,DEFAULT_MAX_NUMBER_OF_ADS);
			maxNumberOfAds = DEFAULT_MAX_NUMBER_OF_ADS;
		}
		if(numberOfAds>=maxNumberOfAds){
			throw new TooManyAds();
		}
		if(!isAdOriginal(ad, client)){
			throw new DuplicateAdException();
		}
		ad.setPublicationDateTime(LocalDateTime.now());
		ad.setStatus(AdStatus.SHOWED);
		client.addAd(ad);
		Long adId = adDao.save(ad);
		return adId;
	}
	
	@Override
	public void refreshPubDate(String email, long adId,int hoursBetweenRefreshing) throws 
							InvalidIdentifier, IllegalActionForAd, TooManyRefreshings {
		Ad adFromDb= get(adId);
		if(adFromDb==null){
			throw new InvalidIdentifier();
		}
		Client authClient = clientDao.getClientByEmail(email);
		if(!adFromDb.getClient().equals(authClient)){
			throw new InvalidIdentifier();
		}
		if(!adFromDb.getStatus().equals(AdStatus.SHOWED)){
			throw new IllegalActionForAd();
		}
		if(hoursBetweenRefreshing<1){
			logger.debug("hoursBetweenRefreshing = {}, default value={} is used",
					hoursBetweenRefreshing,DEFAULT_HOURS_BETWEEN_REFRESHING);
			hoursBetweenRefreshing = DEFAULT_HOURS_BETWEEN_REFRESHING;
		}
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastRefreshing = adFromDb.getPublicationDateTime();
		long hours = ChronoUnit.HOURS.between(lastRefreshing, now);
		if(hours<hoursBetweenRefreshing){
			throw new TooManyRefreshings();
		}
		adFromDb.setPublicationDateTime(LocalDateTime.now());
	}

	@Override
	public Ad getForShowing(long id) throws InvalidIdentifier, IllegalActionForAd {
		Ad ad = adDao.get(id);
		if(ad==null){
			throw new InvalidIdentifier();
		}
		
		if(!ad.getStatus().equals(AdStatus.SHOWED)){
			throw new IllegalActionForAd();
		}
		
		return ad;
	}

	@Override
	public Ad getForUpdating(String email,long id) throws InvalidIdentifier, 
														  IllegalActionForAd{
		Ad ad = adDao.get(id);
		if(ad==null){
			throw new InvalidIdentifier();
		}
		Client authClient = clientDao.getClientByEmail(email);
		if(!ad.getClient().equals(authClient)){
			throw new InvalidIdentifier();
		}
		
		if(!ad.getStatus().equals(AdStatus.SHOWED)){
			throw new IllegalActionForAd(
					"Ad has " + ad.getStatus() + " status, which is different from SHOWED");
		}
		
		if(adContainsSendedResponses(ad)){
			throw new IllegalActionForAd("Ad contains at least one RespondedAd with SENDED status");
		}
		return ad;
	}
	
	@Override
	public void deleteFromAssociationsWithClientAndArchieveAd(final long id,
															  final String email) 
			throws InvalidIdentifier{
		
		archieveAdLock.lock();
		try{
			Ad ad = adDao.get(id);
			if(ad == null){
				throw new InvalidIdentifier("Ad with id="+id+" doesn't exist in data storage");
			}
			Client client = clientDao.getClientByEmail(email);
			if(client == null){
				throw new InvalidIdentifier("Client with email="+email+
						" doesn't exist in data storage");
			}
			if(!client.equals(ad.getClient())){
				throw new InvalidIdentifier("Ad with id={}"+id+
						" doesn't belong to client with email={}"+email);
			}
			
			client.removeAd(ad);
			ad.setClient(null);
			Set<RespondedAd> respondedAdsRelatedToAd = ad.getRespondedAds();
			//if translator didn't deletes relationships, this set is not empty
			//So we delete relationships between client and all RespondedAds, related to Ad 
			respondedAdsRelatedToAd.forEach(rad ->{
				client.removeRespondedAd(rad);
				rad.setClient(null);
			});
			
			
			
			ArchievedAd archievedAd = archievedAdDao.getArchievedAdByAdId(id);
			if(archievedAd != null){
				client.addArchievedAd(archievedAd);
			}else{
				ArchievedAd newArchievedAd = new ArchievedAd();
				client.addArchievedAd(newArchievedAd);
				ad.addArchievedAd(newArchievedAd);
				newArchievedAd.setCreatingDateTime(LocalDateTime.now());
				archievedAdDao.save(newArchievedAd);
			}
		}finally{
			archieveAdLock.unlock();
		}
		
	}
	
	@Override
	public void deleteFromAssociationsWithTranslatorAndArchieveAd(final long id,
															  final String email) 
			throws InvalidIdentifier{
		
		archieveAdLock.lock();
		try{
			Ad ad = adDao.get(id);
			if(ad == null){
				throw new InvalidIdentifier("Ad with id="+id+" doesn't exist in data storage");
			}
			Translator translator = translatorDao.getTranslatorByEmail(email);
			if(translator == null){
				throw new InvalidIdentifier("Translator with email="+email+
						" doesn't exist in data storage");
			}
			if(!ad.getTranslator().equals(translator)){
				throw new InvalidIdentifier("Ad with id="+id+
						" isn't linked to translator with email="+email);
			}
			
			//deleting all related RespondedAds from data storage
			ad.getRespondedAds().clear();
			
			//deleting relationships between ad and translator
			ad.setTranslator(null);
			translator.setAd(null);
			
		
			ArchievedAd archievedAd = archievedAdDao.getArchievedAdByAdId(id);
			
			if(archievedAd != null){
				translator.addArchievedAd(archievedAd);
			}else{
				ArchievedAd newArchievedAd = new ArchievedAd();
				translator.addArchievedAd(newArchievedAd);
				ad.addArchievedAd(newArchievedAd);
				newArchievedAd.setCreatingDateTime(LocalDateTime.now());
				archievedAdDao.save(newArchievedAd);
			}
		}finally{
			archieveAdLock.unlock();
		}
		
		
	}
	
	/**
	 * Checks if {@link Ad} {@code ad}  and {@link Translator}
	 * {@code translator}  
	 * have the same {@link RespondedAd} {@code respondedAd} with ACCEPTED status
	 * <p>If {@code translator} has more then 1 {@code RespondedAd}, exception is thrown - 
	 * such situation is unacceptable and perhaps is concurrency issue.
	 * @param translator 
	 * @param ad 
	 * @return true if {@code translator} and {@code ad} have the same {@code RespondedAd} object
	 * with ACCEPTED status
	 *//*
	private boolean translatorInteractsWithAd(final Translator translator,
											  final Ad ad){
		Set<RespondedAd> respondedAds = translator.getRespondedAds();
		Set<RespondedAd> acceptedRespondedAds = 
							respondedAds.stream()
			    						.filter(rad -> rad.getStatus()
			    											.equals(RespondedAdStatus.ACCEPTED))
			    						.collect(Collectors.toSet());
				  
		long numberOfAcceptedRespondedAd= acceptedRespondedAds.size();
		if(numberOfAcceptedRespondedAd==1){
			RespondedAd respondedAd = acceptedRespondedAds.iterator().next();
			if(respondedAd.getAd().equals(ad)){
				return true;
			}else{
				logger.debug("Translator has one ACCCEPTED RespondedAd, but "
						+ "he wants to mark as NOTCHECKED another Ad");
				return false;
			}
		}

		if(numberOfAcceptedRespondedAd>1){
			//such situation is very serious and unacceptable error
			logger.error("Translator with email={} has {} ACCEPTED"
					+ " RespondedAd",translator.getEmail(),numberOfAcceptedRespondedAd);
			!!!!Throwing error, must be created and handled!!!! 
			
		}
		
		//this Translator has no ACCEPTED RespondedAd
		return false;
	}*/
	
	/**
	 * Checks if {@code ad} contains at least one {@link RespondedAd} {@code respondedAd}
	 * with SENDED status
	 * @return true if {@code ad} contains at least one {@code respondedAd}
	 * with SENDED status, otherwise false
	 */
	protected boolean adContainsSendedResponses(Ad ad){
		Set<RespondedAd> respondedAds = ad.getRespondedAds();
		boolean sendedRespondedAdsExist  =  respondedAds.stream().anyMatch(
				rad -> rad.getStatus().equals(RespondedAdStatus.SENDED));
		return sendedRespondedAdsExist;
	}
	
	
	/*!!!! Method definiton of duplicated Ads must be improved!!!!*/
	/**
	 * Checks if {@link Ad} {@code ad} differs from another {@code Ad}s
	 * of this {@link Client} {@code client}.
	 * Rules for Original Ad:
	 * <ul>
	 * 	<li>Its name must be different from other names</li>
	 * 	<li>Its description must be different from other descriptions</li>
	 * 	<li></li>
	 * </ul> 
	 * <br>...
	 * @return true, if {@code ad} differs from another {@code Ad}s, which belongs
	 * to {@code client}, otherwise false.
	 */
	protected boolean isAdOriginal(Ad ad, Client client){
		Set<Ad> ads = client.getAds();
		if(ads.isEmpty()){
			return true;
		}
		boolean isSimilarAd= ads
							.stream()
							//delete from stream Ad with the same id - when client
							// updates Ad
							.filter(adFromClient -> adFromClient.getId()!=ad.getId())
							.anyMatch(adFromClient -> 
									adFromClient.getName().equals(ad.getName()) ||
									adFromClient.getDescription().equals(ad.getDescription()));
		if(isSimilarAd){
			return false;
		}
		return true;
	}
}
