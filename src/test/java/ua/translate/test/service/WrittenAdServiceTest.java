package ua.translate.test.service;

import static org.junit.Assert.assertEquals;
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
import ua.translate.dao.TranslatorDao;
import ua.translate.dao.WrittenAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.impl.WrittenAdServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class WrittenAdServiceTest {
	
	@Mock private ClientDao clientDao;
	@Mock private TranslatorDao translatorDao;
	@Mock private WrittenAdDao adDao;
	
	private Client client;
	private WrittenAd writtenAd;
	private Translator translator;
	private ResultDocument resultDocument;
	
	@InjectMocks
	private WrittenAdServiceImpl adService;
	
	Logger logger = LoggerFactory.getLogger(WrittenAdServiceTest.class);
	
	@Before
	public void init(){
		translator = new Translator();
		translator.setEmail("test1@mail.ru");
		translator.setId(123);
		
		
		client = new Client();
		client.setEmail("test@mail.ru");
		writtenAd = new WrittenAd();
		writtenAd.setId(1L);
		writtenAd.setName("Test");
		client.addAd(writtenAd);
		
		
		resultDocument = new ResultDocument(new byte[]{}, "demo", "demo");
	}
	
	@Test
	public void testSaveResultDocAndMarkAsNotChecked() throws InvalidIdentifier, IllegalActionForAd{
		logger.debug("-----------------testSaveResultDocAndMarkAsNotChecked------------");
		
		final String email = translator.getEmail();
		final Long adId = writtenAd.getId();
		
		//Preparing right relations between Translator, Ad and RespondedAd
		writtenAd.setStatus(AdStatus.ACCEPTED);
		writtenAd.setName("Test");
		RespondedAd respondedAd = new RespondedAd();
		respondedAd.setStatus(RespondedAdStatus.ACCEPTED);
		writtenAd.addRespondedAd(respondedAd);
		translator.addRespondedAd(respondedAd);
		writtenAd.setTranslator(translator);
		translator.setAd(writtenAd);
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return ACCEPTED Ad, which has ACCEPTED RespondedAd, which"
				+ " belongs to translator with email={}",adId,
				email);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return Translator, which has one ACCEPTED RespondedAd",
				email);
		when(translatorDao.getTranslatorByEmail(email))
							.thenReturn(translator);
		
		
		logger.debug("Calling WrittenAdService.saveResultDocAndMarkAsNotChecked({},{},ResultDocument object)"
				+ " should return true",email,adId);
		boolean expected = true;
		boolean actual= adService.saveResultDocAndMarkAsNotChecked(email, adId,resultDocument);
		assertEquals(expected, actual);
	
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testSaveResultDocAndMarkAsNotCheckedAdWithInvalidId() 
								throws InvalidIdentifier, IllegalActionForAd{
		logger.debug("-----------------testSaveResultDocAnd"
				+ "MarkAsNotCheckedAdWithInvalidId------------");
		final String email = translator.getEmail();
		final Long adId = writtenAd.getId();
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return null",adId);
		when(adDao.getWrittenAdById(adId)).thenReturn(null);
		try{
			logger.debug("Calling WrittenAdService.saveResultDocAndMarkAsNotChecked({},{},ResultDocument object)"
					+ " should throw InvalidIdentifier",email,adId);
			adService.saveResultDocAndMarkAsNotChecked(email, adId,resultDocument);
		}catch(InvalidIdentifier e){
			logger.debug("InvalidIdentifier has been thrown");
			throw e;
		}

	}
	
	@Test(expected = IllegalActionForAd.class)
	public void testSaveResultDocAndMarkAsNotCheckedNotACCEPTEDAd() 
								throws InvalidIdentifier, IllegalActionForAd{
		logger.debug("-----------------testSaveResultDocAndMarkAsNotCheckedNotACCEPTEDAd------------");
		final String email = translator.getEmail();
		final Long adId = writtenAd.getId();
		writtenAd.setStatus(AdStatus.SHOWED);
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return SHOWED WrittenAd",adId);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		try{
			logger.debug("Calling WrittenAdService.saveResultDocAndMarkAsNotChecked({},{},ResultDocument object)"
					+ " should throw IllegalActionForAd",email,adId);
			adService.saveResultDocAndMarkAsNotChecked(email, adId,resultDocument);
		}catch(IllegalActionForAd e){
			logger.debug("IllegalActionForAd has been thrown");
			throw e;
		}

	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testSaveResultDocAndMarkAsNotCheckedAnotherAcceptedAd() throws IllegalActionForAd, InvalidIdentifier{
		logger.debug("-----------------testSaveResultDocAndMarkAsNotCheckedAnotherAcceptedAd------------");
		
		final String email = translator.getEmail();
		final Long anotherAdId = 99L;
		WrittenAd anotherAd = new WrittenAd();
		anotherAd.setId(anotherAdId);
		anotherAd.setStatus(AdStatus.ACCEPTED);
		Translator anotherTranslator = new Translator();
		anotherAd.setTranslator(anotherTranslator);
		
		//Preparing right relations between Translator, Ad and RespondedAd
		writtenAd.setStatus(AdStatus.ACCEPTED);
		RespondedAd respondedAd = new RespondedAd();
		respondedAd.setStatus(RespondedAdStatus.ACCEPTED);
		writtenAd.addRespondedAd(respondedAd);
		translator.addRespondedAd(respondedAd);
		
		writtenAd.setTranslator(translator);
		translator.setAd(writtenAd);
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return ACCEPTED Ad, which doesn't belong "
				+ "to Translator with email={}",anotherAdId,email);
		when(adDao.getWrittenAdById(anotherAdId)).thenReturn(anotherAd);
		
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return Translator, which has one ACCEPTED RespondedAd",
				email);
		when(translatorDao.getTranslatorByEmail(email))
							.thenReturn(translator);
		
		try{
			logger.debug("Calling WrittenAdService.saveResultDocAndMarkAsNotChecked({},{},ResultDocument object)"
					+ " should throw InvalidIdentifier",email,anotherAdId);
			adService.saveResultDocAndMarkAsNotChecked(email, anotherAdId,resultDocument);
		}catch(InvalidIdentifier e){
			logger.debug("InvalidIdentifier has been thrown");
			throw e;
		}
			
	
	}
	
	
	@Test
	public void testSaveResultDocAndMarkAsNotCheckedNullResultDoc() throws InvalidIdentifier, IllegalActionForAd{
		logger.debug("-----------------testSaveResultDocAndMarkAsNotCheckedNullResultDoc------------");
		
		final String email = translator.getEmail();
		final Long adId = writtenAd.getId();
		
		logger.debug("Calling WrittenAdService.saveResultDocAndMarkAsNotChecked({},{},null)"
				+ " should return false",email,adId);
		boolean expected = false;
		boolean actual= adService.saveResultDocAndMarkAsNotChecked(email, adId,null);
		assertEquals(expected, actual);
	
	}
	
	
	@Test
		public void testUpdateWrittenAd() throws InvalidIdentifier, DuplicateAdException,IllegalActionForAd{
			logger.debug("-----------------testUpdateWrittenAd------------");
			final Long adId = writtenAd.getId();
			final String clientEmail = client.getEmail();
			writtenAd.setStatus(AdStatus.SHOWED);
			writtenAd.setName("InitName");
			
			WrittenAd updatedWrittenAd = new WrittenAd();
			updatedWrittenAd.setName("EndName");
			updatedWrittenAd.setId(adId);
			
			logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
					+ "to return SHOWED WrittenAd, which belongs "
					+ "to Client with email={} and doesn't have SENDED"
					+ " RespondedAds",adId,clientEmail);
			when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
			
			logger.debug("Stubbing ClientDao.getClientByEmail({}) "
					+ "to return Client",clientEmail);
			when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
			
			logger.debug("Calling WrittenAdService.updateWrittenAd({},{})"
					+ " shouldn't throw exception",clientEmail,adId);
			adService.updateWrittenAdByClient(clientEmail,updatedWrittenAd);
		}
		
		@Test(expected = InvalidIdentifier.class)
		public void testUpdateWrittenAdWithInvalidId() throws InvalidIdentifier,DuplicateAdException, IllegalActionForAd{
			logger.debug("-----------------testUpdateWrittenAdWithInvalidId------------");
			final Long adId = writtenAd.getId();
			final String clientEmail = client.getEmail();
			
			
			logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
					+ "to return null",adId,clientEmail);
			when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
			
			try{
				logger.debug("Calling WrittenAdService.updateWrittenAd({},{})"
						+ " should throw InvalidIdentifier",clientEmail,adId);
				adService.updateWrittenAdByClient(clientEmail,writtenAd);
			}catch(InvalidIdentifier ex){
				logger.debug("InvalidIdentifier has been thrown");
				throw ex;
			}
		}
		
		@Test(expected = InvalidIdentifier.class)
		public void testUpdateWrittenAdByNotOwner() throws InvalidIdentifier, DuplicateAdException,IllegalActionForAd{
			logger.debug("-----------------testUpdateWrittenAdByNotOwner------------");
			final Long adId = writtenAd.getId();
			final String clientEmail = client.getEmail();
			writtenAd.setStatus(AdStatus.SHOWED);
			
			final String anotherClientEmail = "another@mail.ru";
			Client anotherClient = new Client();
			anotherClient.setEmail(anotherClientEmail);
			
			logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
					+ "to return SHOWED WrittenAd, which belongs "
					+ "to Client with email={} and doesn't have SENDED"
					+ " RespondedAds",adId,clientEmail);
			when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
			
			logger.debug("Stubbing ClientDao.getClientByEmail({}) "
					+ "to return Client",anotherClientEmail);
			when(clientDao.getClientByEmail(anotherClientEmail)).thenReturn(anotherClient);
			
			try{
				logger.debug("Calling WrittenAdService.updateWrittenAd({},{})"
						+ " should throw InvalidIdentifier",clientEmail,adId);
				adService.updateWrittenAdByClient(clientEmail,writtenAd);
			}catch(InvalidIdentifier ex){
				logger.debug("InvalidIdentifier has been thrown");
				throw ex;
			}
		}
		
		@Test(expected = IllegalActionForAd.class)
		public void testUpdateWrittenAdWithouSHOWEDstatus() throws InvalidIdentifier, DuplicateAdException,IllegalActionForAd{
			logger.debug("-----------------testUpdateWrittenAdWithouSHOWEDstatus------------");
			final Long adId = writtenAd.getId();
			final String clientEmail = client.getEmail();
			writtenAd.setStatus(AdStatus.ACCEPTED);
			
			
			logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
					+ "to return ACCEPTED WrittenAd, which belongs "
					+ "to Client with email={} and doesn't have SENDED"
					+ " RespondedAds",adId,clientEmail);
			when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
			
			logger.debug("Stubbing ClientDao.getClientByEmail({}) "
					+ "to return Client",clientEmail);
			when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
			
			try{
				logger.debug("Calling WrittenAdService.updateWrittenAd({},{})"
						+ " should throw IllegalActionForAd",clientEmail,adId);
				adService.updateWrittenAdByClient(clientEmail,writtenAd);
			}catch(IllegalActionForAd ex){
				logger.debug("IllegalActionForAd has been thrown");
				throw ex;
			}
		}
		
		@Test(expected = IllegalActionForAd.class)
		public void testUpdateWrittenAdWithSENDEDRespondedAds() throws InvalidIdentifier, DuplicateAdException,IllegalActionForAd{
			logger.debug("-----------------testUpdateWrittenAdWithSENDEDRespondedAds------------");
			final Long adId = writtenAd.getId();
			final String clientEmail = client.getEmail();
			writtenAd.setStatus(AdStatus.SHOWED);
			
			RespondedAd respondedAd = new RespondedAd();
			respondedAd.setStatus(RespondedAdStatus.SENDED);
			writtenAd.addRespondedAd(respondedAd);
			
			logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
					+ "to return SHOWED WrittenAd, which belongs "
					+ "to Client with email={} and have SENDED"
					+ " RespondedAd",adId,clientEmail);
			when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
			
			logger.debug("Stubbing ClientDao.getClientByEmail({}) "
					+ "to return Client",clientEmail);
			when(clientDao.getClientByEmail(clientEmail)).thenReturn(client);
			
			try{
				logger.debug("Calling WrittenAdService.updateWrittenAd({},{})"
						+ " should throw IllegalActionForAd",clientEmail,adId);
				adService.updateWrittenAdByClient(clientEmail,writtenAd);
			}catch(IllegalActionForAd ex){
				logger.debug("IllegalActionForAd has been thrown");
				throw ex;
			}
		}
		
		
	}

