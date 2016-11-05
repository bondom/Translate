package ua.translate.test.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.translate.dao.ClientDao;
import ua.translate.dao.GetAdDao;
import ua.translate.dao.TranslatorDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TooManyAds;
import ua.translate.service.exception.TooManyRefreshings;
import ua.translate.service.impl.DefaultAdServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {
	
	@Mock private ClientDao clientDao;
	@Mock private TranslatorDao translatorDao;
	@Mock private GetAdDao adDao;
	
	private Client client;
	private Ad ad;
	private Ad anotherAd;
	private Translator translator;
	
	@InjectMocks
	private DefaultAdServiceImpl adService;
	
	Logger logger = LoggerFactory.getLogger(AdServiceTest.class);
	
	@Before
	public void init(){
		translator = new Translator();
		translator.setEmail("test1@mail.ru");
		translator.setId(123);
		
		
		client = new Client();
		client.setEmail("test@mail.ru");
		ad = new Ad();
		ad.setId(1L);
		ad.setName("Test");
		ad.setDescription("Test");
		client.addAd(ad);
		
		anotherAd = new Ad();
		anotherAd.setId(999L);
		anotherAd.setName("Test another");
		anotherAd.setStatus(AdStatus.ACCEPTED);
		Client anotherClient = new Client();
		anotherClient.setEmail("another@mail.ru");
		anotherClient.addAd(anotherAd);
		
	}
	
	@Test
	public void testGetAdById() throws InvalidIdentifier{
		long adId = 3L;
		logger.debug("-----------------testGetAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad",adId);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Calling AdService.get({})",
				adId);
		adService.get(adId);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(adDao).get(adId);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetAdByInvalidId() throws InvalidIdentifier{
		long nonExistedAdId = 2L;
		logger.debug("-----------------testGetAdByInvalidId------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",nonExistedAdId);
		when(adDao.get(nonExistedAdId)).thenReturn(null);
		try{
			logger.debug("Calling AdService.get({}) should"
					+ " throw InvalidIdentifier",
					nonExistedAdId);
			adService.get(nonExistedAdId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test
	public void testDeleteAdById() throws InvalidIdentifier, 
											IllegalActionForAd{
		long adId = 3L;
		ad.setStatus(AdStatus.SHOWED);
		logger.debug("-----------------testDeleteAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return SHOWED Ad",adId);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Calling AdService.deleteById({})",
				adId);
		adService.deleteById(adId);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(adDao).get(adId);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testDeleteNotExistedAdById() throws InvalidIdentifier, 
													IllegalActionForAd{
		long nonExistedAdId = 3L;
		logger.debug("-----------------testDeleteNotExistedAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",nonExistedAdId);
		when(adDao.get(nonExistedAdId)).thenReturn(null);
		
		try{
			logger.debug("Calling AdService.deleteById({}) should throw"
					+ " InvalidIdentifier",nonExistedAdId);
			adService.deleteById(nonExistedAdId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			verify(adDao).get(nonExistedAdId);
			logger.debug("Verified AdDao.get({}) is called",nonExistedAdId); 
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testDeleteAcceptedAdById() throws InvalidIdentifier, 
	IllegalActionForAd{
		long adId = 3L;
		ad.setStatus(AdStatus.ACCEPTED);
		logger.debug("-----------------testDeleteAcceptedAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return ACCEPTED Ad",adId);
		when(adDao.get(adId)).thenReturn(ad);
		
		try{
			logger.debug("Calling AdService.deleteById({}) should throw"
					+ " IllegalActionForAd",adId);
			adService.deleteById(adId);
		}catch(IllegalActionForAd ex){
			logger.debug("IllegalActionForAd has been thrown");
			verify(adDao).get(adId);
			logger.debug("Verified AdDao.get({}) is called",adId); 
			throw ex;
		}
	}
	
	@Test
	public void testSaveAd() throws TooManyAds, DuplicateAdException{
		long generatedAdId = 3L;
		long maxNumberOfAds = 3;
		String email = client.getEmail();
		ad.setStatus(AdStatus.SHOWED);
		
		Ad newAd = new Ad();
		newAd.setName("Original name");
		newAd.setDescription("Original desc");
		
		logger.debug("-----------------testSaveAd------------");
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client, who has {} ads, it is  less than {} ",
				email,client.getAds().size(),maxNumberOfAds);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		logger.debug("Stubbing AdDao.save(Ad with SHOWED status) "
				+ "to return generated id of saved ad={}",generatedAdId);
		when(adDao.save(newAd)).thenReturn(generatedAdId);
		logger.debug("Calling AdService.saveAd(Ad with SHOWED status,{},{})",
				email,maxNumberOfAds);
		adService.saveAd(newAd, email, maxNumberOfAds);
		logger.debug("Verifying ClientDao.getClientByEmail({}) is called",email); 
		verify(clientDao).getClientByEmail(email);
		logger.debug("Verifying AdDao.save(Ad with SHOWED status) is called"); 
		verify(adDao).save(newAd);
	}
	
	@Test(expected = TooManyAds.class)
	public void testSaveTooManyAds() throws TooManyAds, DuplicateAdException{
		long maxNumberOfAds = 1;
		String email = client.getEmail();
		ad.setStatus(AdStatus.SHOWED);
		client.addAd(new Ad());
		
		logger.debug("-----------------testSaveTooManyAds------------");
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client, who has {} ads, max number={} ",
				email,client.getAds().size(),maxNumberOfAds);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		try{
			logger.debug("Calling AdService.saveAd(Ad with SHOWED status,{},{})"
					+ "	should throw TooManyAds",
					email,maxNumberOfAds);
			adService.saveAd(ad, email, maxNumberOfAds);
		}catch(TooManyAds ex){
			logger.debug("TooManyAds has been thrown"); 
			throw ex;
		}
	}
	
	
	@Test
	public void testRefreshPubDate() throws InvalidIdentifier, IllegalActionForAd, TooManyRefreshings{
		logger.debug("-----------------testRefreshPubDate------------");
		
		final String email = client.getEmail();
		final long adId = ad.getId();
		final int hoursBetweenRefreshings = 10;
		
		ad.setStatus(AdStatus.SHOWED);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime refreshingDateTime = now.withDayOfYear(now.getDayOfYear()-hoursBetweenRefreshings);
		ad.setPublicationDateTime(refreshingDateTime);
		
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad, which has SHOWED status and was refreshed "
				+ "more than {} hours ago",
				adId,hoursBetweenRefreshings);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client, which has one SHOWED Ad with id={}",
				email,adId);
		when(clientDao.getClientByEmail(email))
							.thenReturn(client);
		logger.debug("Calling AdService.refreshPubDate({},{},{}) "
				+ "shouldn't throw exception",email,adId,hoursBetweenRefreshings);
		adService.refreshPubDate(email, adId, hoursBetweenRefreshings);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testRefreshPubDateOfNotExistedAd() throws InvalidIdentifier, IllegalActionForAd, TooManyRefreshings{
		logger.debug("-----------------testRefreshPubDateOfNotExistedAd------------");
		
		final String email = client.getEmail();
		final long adId = ad.getId();
		final int hoursBetweenRefreshings = 10;
		
		
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",
				adId);
		when(adDao.get(adId)).thenReturn(null);
		try{
			logger.debug("Calling AdService.refreshPubDate({},{},{}) "
					+ "should throw InvalidIdentifier",email,adId,hoursBetweenRefreshings);
			adService.refreshPubDate(email, adId, hoursBetweenRefreshings);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testRefreshPubDateOfAnotherAd() throws InvalidIdentifier, IllegalActionForAd, TooManyRefreshings{
		logger.debug("-----------------testRefreshPubDateOfAnotherAd------------");
		
		final String email = client.getEmail();
		final long anotherAdId = anotherAd.getId();
		final int hoursBetweenRefreshings = 10;
		
		ad.setStatus(AdStatus.SHOWED);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime refreshingDateTime = now.withDayOfYear(now.getDayOfYear()-hoursBetweenRefreshings);
		ad.setPublicationDateTime(refreshingDateTime);
		
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad, which has SHOWED status and was refreshed "
				+ "more than {} hours ago",
				anotherAdId,hoursBetweenRefreshings);
		when(adDao.get(anotherAdId)).thenReturn(anotherAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client, which has NOT Ad with id={}",
				email,anotherAdId);
		when(clientDao.getClientByEmail(email))
							.thenReturn(client);
		try{
			logger.debug("Calling AdService.refreshPubDate({},{},{}) "
					+ "should throw InvalidIdentifier",email,anotherAdId,hoursBetweenRefreshings);
			adService.refreshPubDate(email, anotherAdId, hoursBetweenRefreshings);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testRefreshPubDateOfNotShowedAd() throws InvalidIdentifier, IllegalActionForAd, TooManyRefreshings{
		logger.debug("-----------------testRefreshPubDateOfNotShowedAd------------");
		
		final String email = client.getEmail();
		final long adId = ad.getId();
		final int hoursBetweenRefreshings = 10;
		
		ad.setStatus(AdStatus.ACCEPTED);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime refreshingDateTime = now.withDayOfYear(now.getDayOfYear()-hoursBetweenRefreshings);
		ad.setPublicationDateTime(refreshingDateTime);
		
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad, which has ACCEPTED status and was refreshed "
				+ "more than {} hours ago",
				adId,hoursBetweenRefreshings);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client, which has Ad with id={}",
				email,adId);
		when(clientDao.getClientByEmail(email))
							.thenReturn(client);
		try{
			logger.debug("Calling AdService.refreshPubDate({},{},{}) "
					+ "should throw IllegalActionForAd",email,adId,hoursBetweenRefreshings);
			adService.refreshPubDate(email, adId, hoursBetweenRefreshings);
		}catch(IllegalActionForAd ex){
			logger.debug("IllegalActionForAd has been thrown");
			throw ex;
		}
	}
	
	@Ignore
	@Test(expected = TooManyRefreshings.class)
	public void testRefreshPubDateTooFast() throws InvalidIdentifier, IllegalActionForAd, TooManyRefreshings{
		logger.debug("-----------------testRefreshPubDateTooFast------------");
		
		final String email = client.getEmail();
		final long adId = ad.getId();
		final int hoursBetweenRefreshings = 7;
		final int minHoursBetweenRefreshings = 10;
		
		ad.setStatus(AdStatus.SHOWED);
		final LocalDateTime now = LocalDateTime.now();
		final LocalDateTime refreshingDateTime = now.
							minusHours(hoursBetweenRefreshings);
		ad.setPublicationDateTime(refreshingDateTime);
		
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad, which has SHOWED status and was refreshed "
				+ "{} hours ago",
				adId,hoursBetweenRefreshings);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client, which has Ad with id={}",
				email,adId);
		when(clientDao.getClientByEmail(email))
							.thenReturn(client);
		try{
			logger.debug("Calling AdService.refreshPubDate({},{},{}) "
					+ "should throw TooManyRefreshings",email,adId,minHoursBetweenRefreshings);
			adService.refreshPubDate(email, adId, minHoursBetweenRefreshings);
		}catch(TooManyRefreshings ex){
			logger.debug("TooManyRefreshings has been thrown");
			throw ex;
		}
	}
	
	@Test
	public void testGetForShowing() throws InvalidIdentifier, IllegalActionForAd{
		logger.debug("-----------------testGetForShowing------------");
		final long adId = ad.getId();
		ad.setStatus(AdStatus.SHOWED);
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad with SHOWED status",adId);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Calling AdService.getForShowing({}) shouldn't throw exception",
				adId);
		adService.getForShowing(adId);
		logger.debug("Calling AdService.getForShowing({}) was executed without exception",
				adId);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetForShowingByInvalidId() throws InvalidIdentifier,IllegalActionForAd{
		long nonExistedAdId = 2L;
		logger.debug("-----------------testGetForShowingByInvalidId------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",nonExistedAdId);
		when(adDao.get(nonExistedAdId)).thenReturn(null);
		try{
			logger.debug("Calling AdService.getForShowing({}) should"
					+ " throw InvalidIdentifier",
					nonExistedAdId);
			adService.getForShowing(nonExistedAdId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testGetForShowingAdWithoutSHOWEDStatus() throws InvalidIdentifier,IllegalActionForAd{
		final long adId = ad.getId();
		ad.setStatus(AdStatus.ACCEPTED);
		logger.debug("-----------------testGetForShowingAdWithoutSHOWEDStatus------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return ad with ACCEPTED status",adId);
		when(adDao.get(adId)).thenReturn(ad);
		try{
			logger.debug("Calling AdService.getForShowing({}) should"
					+ " throw IllegalActionForAd",
					adId);
			adService.getForShowing(adId);
		}catch(IllegalActionForAd ex){
			logger.debug("IllegalActionForAd has been thrown");
			throw ex;
		}
	}
	
	@Test
	public void testGetForUpdating() throws InvalidIdentifier, IllegalActionForAd{
		logger.debug("-----------------testGetForUpdating------------");
		final long adId = ad.getId();
		final String clientEmail = client.getEmail();
		ad.setStatus(AdStatus.SHOWED);
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad with SHOWED status, which belongs"
				+ " to Client with email={}",adId,clientEmail);
		when(adDao.get(adId)).thenReturn(ad);
		
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",clientEmail);
		when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
		logger.debug("Calling AdService.getForUpdating({},{}) shouldn't throw exception",
				clientEmail,adId);
		adService.getForUpdating(clientEmail,adId);
		logger.debug("Calling AdService.getForUpdating({},{}) was executed without exception",
				clientEmail,adId);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetForUpdatingByInvalidId() throws InvalidIdentifier,IllegalActionForAd{
		final long nonExistedAdId = 2L;
		final String clientEmail = client.getEmail();
		logger.debug("-----------------testGetForUpdatingByInvalidId------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",nonExistedAdId);
		when(adDao.get(nonExistedAdId)).thenReturn(null);
		try{
			logger.debug("Calling AdService.getForUpdating({},{}) should"
					+ " throw InvalidIdentifier",
					clientEmail,nonExistedAdId);
			adService.getForUpdating(clientEmail,nonExistedAdId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetForUpdatingAnotherAd() throws InvalidIdentifier,IllegalActionForAd{
		final long adId = anotherAd.getId();
		final String ownerEmail = anotherAd.getClient().getEmail();
		final String invokerEmail = client.getEmail();
		logger.debug("-----------------testGetForUpdatingAnotherAd------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad, which belongs to Client with email={}",
				adId,ownerEmail);
		when(adDao.get(adId)).thenReturn(anotherAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",invokerEmail);
		when(clientDao.getClientByEmail(invokerEmail)).thenReturn(client);
		try{
			logger.debug("Calling AdService.getForUpdating({},{}) should"
					+ " throw InvalidIdentifier",
					invokerEmail,adId);
			adService.getForUpdating(invokerEmail,adId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testGetForUpdatingAdWithoutSHOWEDStatus() throws InvalidIdentifier,IllegalActionForAd{
		final long adId = ad.getId();
		final String clientEmail = client.getEmail();
		ad.setStatus(AdStatus.ACCEPTED);
		logger.debug("-----------------testGetForUpdatingAdWithoutSHOWEDStatus------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return ad with ACCEPTED status, which "
				+ "belongs to client with email={}",adId,clientEmail);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",clientEmail);
		when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
		try{
			logger.debug("Calling AdService.getForUpdating({},{}) should"
					+ " throw IllegalActionForAd",
					clientEmail,adId);
			adService.getForUpdating(clientEmail,adId);
		}catch(IllegalActionForAd ex){
			logger.debug("IllegalActionForAd has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testGetForUpdatingAdWithSENDEDResponses() throws InvalidIdentifier,IllegalActionForAd{
		final long adId = ad.getId();
		final String clientEmail = client.getEmail();
		ad.setStatus(AdStatus.SHOWED);
		RespondedAd respondedAd = new RespondedAd();
		respondedAd.setStatus(RespondedAdStatus.SENDED);
		ad.addRespondedAd(respondedAd);
		logger.debug("-----------------testGetForUpdatingAdWithSENDEDResponses------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad with SHOWED status and RespondedAd with "
				+ "SENDED status, Ad belongs to client with email={}",adId,clientEmail);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",clientEmail);
		when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
		try{
			logger.debug("Calling AdService.getForUpdating({},{}) should"
					+ " throw IllegalActionForAd",
					clientEmail,adId);
			adService.getForUpdating(clientEmail,adId);
		}catch(IllegalActionForAd ex){
			logger.debug("IllegalActionForAd has been thrown");
			throw ex;
		}
	}
}
