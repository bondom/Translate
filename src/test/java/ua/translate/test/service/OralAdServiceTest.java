package ua.translate.test.service;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.translate.dao.ClientDao;
import ua.translate.dao.OralAdDao;
import ua.translate.model.Client;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.impl.OralAdServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class OralAdServiceTest {
	
	@Mock private ClientDao clientDao;
	@Mock private OralAdDao adDao;
	
	private Client client;
	private OralAd oralAd;
	
	@InjectMocks
	private OralAdServiceImpl adService;
		
	Logger logger = LoggerFactory.getLogger(OralAdServiceTest.class);
	
	@Before
	public void init(){
		client = new Client();
		client.setEmail("test@mail.ru");
		oralAd = new OralAd();
		oralAd.setId(1L);
		oralAd.setName("Test");
		client.addAd(oralAd);
	}
	
	@Test
	public void testUpdateOralAd() throws InvalidIdentifier, IllegalActionForAd, DuplicateAdException{
		logger.debug("-----------------testUpdateOralAd------------");
		final Long adId = oralAd.getId();
		final String clientEmail = client.getEmail();
		oralAd.setStatus(AdStatus.SHOWED);
		
		logger.debug("Stubbing OralAdDao.getOralAdById({}) "
				+ "to return SHOWED OralAd, which belongs "
				+ "to Client with email={} and doesn't have SENDED"
				+ " RespondedAds",adId,clientEmail);
		when(adDao.getOralAdById(adId)).thenReturn(oralAd);
		
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",clientEmail);
		when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
		
		logger.debug("Calling OralAdService.updateOralAd({},{})"
				+ " shouldn't throw exception",clientEmail,adId);
		adService.updateOralAd(clientEmail,oralAd);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testUpdateOralAdWithInvalidId() throws InvalidIdentifier, 
														IllegalActionForAd,
														DuplicateAdException{
		logger.debug("-----------------testUpdateOralAdWithInvalidId------------");
		final Long adId = oralAd.getId();
		final String clientEmail = client.getEmail();
		
		
		logger.debug("Stubbing OralAdDao.getOralAdById({}) "
				+ "to return null",adId,clientEmail);
		when(adDao.getOralAdById(adId)).thenReturn(oralAd);
		
		try{
			logger.debug("Calling OralAdService.updateOralAd({},{})"
					+ " should throw InvalidIdentifier",clientEmail,adId);
			adService.updateOralAd(clientEmail,oralAd);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testUpdateOralAdByNotOwner() throws InvalidIdentifier, IllegalActionForAd,DuplicateAdException{
		logger.debug("-----------------testUpdateOralAdByNotOwner------------");
		final Long adId = oralAd.getId();
		final String clientEmail = client.getEmail();
		oralAd.setStatus(AdStatus.SHOWED);
		
		final String anotherClientEmail = "another@mail.ru";
		Client anotherClient = new Client();
		anotherClient.setEmail(anotherClientEmail);
		
		logger.debug("Stubbing OralAdDao.getOralAdById({}) "
				+ "to return SHOWED OralAd, which belongs "
				+ "to Client with email={} and doesn't have SENDED"
				+ " RespondedAds",adId,clientEmail);
		when(adDao.getOralAdById(adId)).thenReturn(oralAd);
		
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",anotherClientEmail);
		when(clientDao.getClientByEmail(anotherClientEmail)).thenReturn(anotherClient);
		
		try{
			logger.debug("Calling OralAdService.updateOralAd({},{})"
					+ " should throw InvalidIdentifier",clientEmail,adId);
			adService.updateOralAd(clientEmail,oralAd);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testUpdateOralAdWithouSHOWEDstatus() throws InvalidIdentifier, DuplicateAdException,IllegalActionForAd{
		logger.debug("-----------------testUpdateOralAdWithouSHOWEDstatus------------");
		final Long adId = oralAd.getId();
		final String clientEmail = client.getEmail();
		oralAd.setStatus(AdStatus.ACCEPTED);
		
		
		logger.debug("Stubbing OralAdDao.getOralAdById({}) "
				+ "to return ACCEPTED OralAd, which belongs "
				+ "to Client with email={} and doesn't have SENDED"
				+ " RespondedAds",adId,clientEmail);
		when(adDao.getOralAdById(adId)).thenReturn(oralAd);
		
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",clientEmail);
		when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
		
		try{
			logger.debug("Calling OralAdService.updateOralAd({},{})"
					+ " should throw IllegalActionForAd",clientEmail,adId);
			adService.updateOralAd(clientEmail,oralAd);
		}catch(IllegalActionForAd ex){
			logger.debug("IllegalActionForAd has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testUpdateOralAdWithSENDEDRespondedAds() throws InvalidIdentifier, DuplicateAdException,IllegalActionForAd{
		logger.debug("-----------------testUpdateOralAdWithSENDEDRespondedAds------------");
		final Long adId = oralAd.getId();
		final String clientEmail = client.getEmail();
		oralAd.setStatus(AdStatus.SHOWED);
		
		RespondedAd respondedAd = new RespondedAd();
		respondedAd.setStatus(RespondedAdStatus.SENDED);
		oralAd.addRespondedAd(respondedAd);
		
		logger.debug("Stubbing OralAdDao.getOralAdById({}) "
				+ "to return SHOWED OralAd, which belongs "
				+ "to Client with email={} and have SENDED"
				+ " RespondedAd",adId,clientEmail);
		when(adDao.getOralAdById(adId)).thenReturn(oralAd);
		
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client",clientEmail);
		when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
		
		try{
			logger.debug("Calling OralAdService.updateOralAd({},{})"
					+ " should throw IllegalActionForAd",clientEmail,adId);
			adService.updateOralAd(clientEmail,oralAd);
		}catch(IllegalActionForAd ex){
			logger.debug("IllegalActionForAd has been thrown");
			throw ex;
		}
	}
}
