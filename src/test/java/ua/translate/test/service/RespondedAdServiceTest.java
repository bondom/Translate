package ua.translate.test.service;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.translate.dao.ClientDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.BalanceService;
import ua.translate.service.exception.IllegalActionForRespondedAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.impl.RespondedAdServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class RespondedAdServiceTest {
	
	@Mock private RespondedAdDao respondedAdDao;
	@Mock private ClientDao clientDao;
	@Mock private BalanceService balanceService;
	
	private RespondedAd respondedAd;
	private Client client;
	private Translator translator;
	private Ad ad;

	@InjectMocks
	private RespondedAdServiceImpl respondedService;
	Logger logger = LoggerFactory.getLogger(RespondedAdServiceTest.class);

	@Before
	public void init(){
		ad = new Ad();
		ad.setCost(150);
		respondedAd = new RespondedAd();
		client = new Client();
		client.setEmail("test@mail.ru");
		client.setBalance(1000.0);
		translator = new Translator();
		translator.setBalance(0.0);
		ad.addRespondedAd(respondedAd);
		client.addRespondedAd(respondedAd);
		translator.addRespondedAd(respondedAd);
	}
	
	@Test
	public void testGetRespondedAdById() throws InvalidIdentifier{
		long adId = 3L;
		logger.debug("-----------------testGetRespondedAdById------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return RespondedAd",adId);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Calling RespondedAdService.get({})",
				adId);
		respondedService.get(adId);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(respondedAdDao).get(adId);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetRespondedAdByInvalidId() throws InvalidIdentifier{
		long nonExistedAdId = 2L;
		logger.debug("-----------------testGetRespondedAdByInvalidId------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return null",nonExistedAdId);
		when(respondedAdDao.get(nonExistedAdId)).thenReturn(null);
		try{
			logger.debug("Calling RespondedAdService.get({}) should"
					+ " throw InvalidIdentifier",
					nonExistedAdId);
			respondedService.get(nonExistedAdId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test
	public void testAcceptAd() throws InvalidIdentifier,IllegalActionForRespondedAd, 
										TranslatorDistraction, InsufficientFunds{
		long adId = 3L;
		String email = client.getEmail();
		respondedAd.setStatus(RespondedAdStatus.SENDED);
		respondedAd.setId(adId);
		logger.debug("-----------------testAcceptAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return SENDED RespondedAd which belongs to "
				+ " client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which containts SENDED RespondedAd with id={}\n"
				+ "and translator, which haven't ACCEPTED RespondedAd",email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		logger.debug("Calling RespondedAdService.accept({},{})",
				email,adId);
		respondedService.acceptRespondedAdAndTransferPledge(email,adId,0);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(respondedAdDao,times(2)).get(adId);
		logger.debug("Verifying clientDao.getClientByEmail({}) is called",email); 
		verify(clientDao).getClientByEmail(email);
		logger.debug("Verifying respondedAdDao.flush() is called"); 
		verify(respondedAdDao).flush();
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testAcceptNonExistedAd() throws InvalidIdentifier,
										IllegalActionForRespondedAd, 
										TranslatorDistraction,
										InsufficientFunds{
		long nonExistedadId = 3L;
		String email = client.getEmail();
		respondedAd.setId(nonExistedadId);
		logger.debug("-----------------testAcceptNonExistedAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return null",nonExistedadId);
		when(respondedAdDao.get(nonExistedadId)).thenReturn(null);
		
		try{
			logger.debug("Calling RespondedAdService.accept({},{}) should"
					+ " throw InvalidIdentifier",
					email,nonExistedadId);
			respondedService.acceptRespondedAdAndTransferPledge(email,nonExistedadId,0);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			verify(clientDao,times(0)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is not called",email);
			verify(respondedAdDao,times(0)).flush();
			logger.debug("Verified respondedAdDao.flush() is not called");
			throw ex;
		}
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testAcceptAdWithoutRight() throws InvalidIdentifier,
											IllegalActionForRespondedAd, 
											TranslatorDistraction,
											InsufficientFunds{
		long adId = 3L;
		Client notOwnerOfAd= new Client();
		notOwnerOfAd.setEmail("notOwner@mail.ru");
		String email = notOwnerOfAd.getEmail();
		respondedAd.setId(adId);
		logger.debug("-----------------testAcceptAdWithoutRight------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return RespondedAd, which doesn't belong "
				+ "to client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which doesn't contain RespondedAd with id={}"
				,email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(notOwnerOfAd);
		
		try{
			logger.debug("Calling RespondedAdService.accept({},{}) should"
					+ " throw InvalidIdentifier",
					email,adId);
			respondedService.acceptRespondedAdAndTransferPledge(email,adId,0);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			verify(clientDao,times(1)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is called",email);
			verify(respondedAdDao,times(1)).get(adId);
			logger.debug("Verified respondedAdDao.get({}) is called",adId);
			verify(respondedAdDao,times(0)).flush();
			logger.debug("Verified respondedAdDao.flush() is not called");
			throw ex;
		}
		
	}
	
	@Test(expected = IllegalActionForRespondedAd.class)
	public void testAcceptAlreadyAcceptedAd() throws InvalidIdentifier,
													IllegalActionForRespondedAd, 
													TranslatorDistraction,
													InsufficientFunds{
		long adId = 3L;
		String email = client.getEmail();
		respondedAd.setId(adId);
		respondedAd.setStatus(RespondedAdStatus.ACCEPTED);
		logger.debug("-----------------testAcceptAlreadyAcceptedAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return ACCEPTED RespondedAd, which belongs "
				+ "to client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which contains RespondedAd with id={}"
				,email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		
		try{
			logger.debug("Calling RespondedAdService.accept({},{}) should"
					+ " throw IllegalActionForRespondedAd",
					email,adId);
			respondedService.acceptRespondedAdAndTransferPledge(email,adId,0);
		}catch(IllegalActionForRespondedAd ex){
			logger.debug("IllegalActionForRespondedAd has been thrown");
			verify(clientDao,times(1)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is called",email);
			verify(respondedAdDao,times(1)).get(adId);
			logger.debug("Verified respondedAdDao.get({}) is called",adId);
			verify(respondedAdDao,times(0)).flush();
			logger.debug("Verified respondedAdDao.flush() is not called");
			throw ex;
		}
		
	}
	
	
	@Test(expected = IllegalActionForRespondedAd.class)
	public void testAcceptRejectedAd() throws InvalidIdentifier,
												IllegalActionForRespondedAd, 
												TranslatorDistraction,
												InsufficientFunds{
		long adId = 3L;
		String email = client.getEmail();
		respondedAd.setId(adId);
		respondedAd.setStatus(RespondedAdStatus.REJECTED);
		logger.debug("-----------------testAcceptRejectedAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return REJECTED RespondedAd, which belongs "
				+ "to client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which contains RespondedAd with id={}"
				,email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		
		try{
			logger.debug("Calling RespondedAdService.accept({},{}) should"
					+ " throw IllegalActionForRespondedAd",
					email,adId);
			respondedService.acceptRespondedAdAndTransferPledge(email,adId,0);
		}catch(IllegalActionForRespondedAd ex){
			logger.debug("IllegalActionForRespondedAd has been thrown");
			verify(clientDao,times(1)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is called",email);
			verify(respondedAdDao,times(1)).get(adId);
			logger.debug("Verified respondedAdDao.get({}) is called",adId);
			verify(respondedAdDao,times(0)).flush();
			logger.debug("Verified respondedAdDao.flush() is not called");
			throw ex;
		}
		
	}
	
	@Test(expected = TranslatorDistraction.class)
	public void testAcceptOneMoreAdForTranslator() throws InvalidIdentifier,
										IllegalActionForRespondedAd, 
										TranslatorDistraction,
										InsufficientFunds{
		long adId = 3L;
		String email = client.getEmail();
		respondedAd.setId(adId);
		respondedAd.setStatus(RespondedAdStatus.SENDED);
		RespondedAd acceptedRespondedAd = new RespondedAd();
		acceptedRespondedAd.setStatus(RespondedAdStatus.ACCEPTED);
		translator.addRespondedAd(acceptedRespondedAd);
		logger.debug("-----------------testAcceptOneMoreAdForTranslator------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return SENDED RespondedAd, which belongs "
				+ "to client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which contains RespondedAd with id={} "
				+ " and translator, who have already another ACCEPTED RespondedAd"
				,email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		
		try{
			logger.debug("Calling RespondedAdService.accept({},{}) should"
					+ " throw TranslatorDistraction",
					email,adId);
			respondedService.acceptRespondedAdAndTransferPledge(email,adId,0);
		}catch(TranslatorDistraction ex){
			logger.debug("TranslatorDistraction has been thrown");
			verify(clientDao,times(1)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is called",email);
			verify(respondedAdDao,times(1)).get(adId);
			logger.debug("Verified respondedAdDao.get({}) is called",adId);
			verify(respondedAdDao,times(0)).flush();
			logger.debug("Verified respondedAdDao.flush() is not called");
			throw ex;
		}
	}
	
	
	@Test
	public void testRejectAd() throws InvalidIdentifier,
										IllegalActionForRespondedAd{
		long adId = 3L;
		String email = client.getEmail();
		respondedAd.setStatus(RespondedAdStatus.SENDED);
		respondedAd.setId(adId);
		logger.debug("-----------------testRejectAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return SENDED RespondedAd which belongs to "
				+ " client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which containts SENDED RespondedAd with id={}",
				email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		logger.debug("Calling RespondedAdService.reject({},{})",
				email,adId);
		respondedService.reject(email,adId);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(respondedAdDao).get(adId);
		logger.debug("Verifying clientDao.getClientByEmail({}) is called",email); 
		verify(clientDao).getClientByEmail(email);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testRejectNonExistedAd() throws InvalidIdentifier,
												IllegalActionForRespondedAd{
		long nonExistedadId = 3L;
		String email = client.getEmail();
		respondedAd.setId(nonExistedadId);
		logger.debug("-----------------testRejectNonExistedAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return null",nonExistedadId);
		when(respondedAdDao.get(nonExistedadId)).thenReturn(null);
		
		try{
			logger.debug("Calling RespondedAdService.reject({},{}) should"
					+ " throw InvalidIdentifier",
					email,nonExistedadId);
			respondedService.reject(email,nonExistedadId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			verify(clientDao,times(0)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is not called",email);
			verify(respondedAdDao,times(0)).flush();
			throw ex;
		}
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testRejectAdWithoutRight() throws InvalidIdentifier,
													IllegalActionForRespondedAd{
		long adId = 3L;
		Client notOwnerOfAd= new Client();
		notOwnerOfAd.setEmail("notOwner@mail.ru");
		String email = notOwnerOfAd.getEmail();
		respondedAd.setId(adId);
		respondedAd.setStatus(RespondedAdStatus.SENDED);
		logger.debug("-----------------testRejectAdWithoutRight------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return SENDED RespondedAd, which doesn't belong "
				+ "to client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which doesn't contain RespondedAd with id={}"
				,email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(notOwnerOfAd);
		
		try{
			logger.debug("Calling RespondedAdService.reject({},{}) should"
					+ " throw InvalidIdentifier",
					email,adId);
			respondedService.reject(email,adId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			verify(clientDao,times(1)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is called",email);
			verify(respondedAdDao,times(1)).get(adId);
			logger.debug("Verified respondedAdDao.get({}) is called",adId);
			throw ex;
		}
		
	}
	
	@Test(expected = IllegalActionForRespondedAd.class)
	public void testRejectAlreadyAcceptedAd() throws InvalidIdentifier,
														IllegalActionForRespondedAd{
		long adId = 3L;
		String email = client.getEmail();
		respondedAd.setId(adId);
		respondedAd.setStatus(RespondedAdStatus.ACCEPTED);
		logger.debug("-----------------testRejectAlreadyAcceptedAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return ACCEPTED RespondedAd, which belongs "
				+ "to client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which contains RespondedAd with id={}"
				,email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		
		try{
			logger.debug("Calling RespondedAdService.reject({},{}) should"
					+ " throw IllegalActionForRespondedAd",
					email,adId);
			respondedService.reject(email,adId);
		}catch(IllegalActionForRespondedAd ex){
			logger.debug("IllegalActionForRespondedAd has been thrown");
			verify(clientDao,times(1)).getClientByEmail(email);
			logger.debug("Verified clientDao.getClientByEmail({}) is called",email);
			verify(respondedAdDao,times(1)).get(adId);
			logger.debug("Verified respondedAdDao.get({}) is called",adId);
			throw ex;
		}
	}
	
	@Test
	public void testRejectAlreadyRejectedAd() throws InvalidIdentifier,
												IllegalActionForRespondedAd{
		long adId = 3L;
		String email = client.getEmail();
		respondedAd.setStatus(RespondedAdStatus.REJECTED);
		respondedAd.setId(adId);
		logger.debug("-----------------testRejectAlreadyRejectedAd------------");
		logger.debug("Stubbing RespondedAdDao.get({}) "
				+ "to return REJECTED RespondedAd which belongs to "
				+ " client with email={}",adId,email);
		when(respondedAdDao.get(adId)).thenReturn(respondedAd);
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client, which containts REJECTED RespondedAd with id={}",
				email,adId);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		logger.debug("Calling RespondedAdService.reject({},{})",
				email,adId);
		respondedService.reject(email,adId);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(respondedAdDao).get(adId);
		verify(clientDao,times(0)).getClientByEmail(email);
		logger.debug("Verified clientDao.getClientByEmail({}) is not called",email); 
	}
}
