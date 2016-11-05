package ua.translate.test.service;

import static org.junit.Assert.assertEquals;
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
import ua.translate.dao.WrittenAdDao;
import ua.translate.model.Admin;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.impl.DocumentServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {
	

	@Mock private ClientDao clientDao;
	@Mock private WrittenAdDao adDao;
	
	private Client client;
	private WrittenAd writtenAd;
	private Translator translator;
	
	@InjectMocks
	private DocumentServiceImpl documentService;
	
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
	}
	
	@Test
	public void testGetDocumentForDownloadingByAdmin() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetDocumentForDownloadingByAdmin------------");
		final Long adId = writtenAd.getId();
		final String userEmail = "user@mail.ru";
		final String clientEmail = client.getEmail();
		
		final Document initDoc = new Document();
		writtenAd.setDocument(initDoc);
		
		Admin admin = new Admin();
		
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Admin",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(admin);
		
		logger.debug("Calling WrittenAdService.getDocumentForDownloading({},{})"
				+ " should return Document object",adId,userEmail);
		Document expected = initDoc;
		Document actual = documentService.getDocumentForDownloading(adId,userEmail);
		assertEquals(expected, actual);
	}
	
	
	@Test
	public void testGetDocumentForDownloadingByTranslator() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetDocumentForDownloadingByTranslator------------");
		final Long adId = writtenAd.getId();
		final String userEmail = "user@mail.ru";
		final String clientEmail = client.getEmail();
		
		final Document initDoc = new Document();
		writtenAd.setDocument(initDoc);
		
		Translator admin = new Translator();
		
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Translator",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(admin);
		
		logger.debug("Calling WrittenAdService.getDocumentForDownloading({},{})"
				+ " should return Document object",adId,userEmail);
		Document expected = initDoc;
		Document actual = documentService.getDocumentForDownloading(adId,userEmail);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetDocumentForDownloadingByOwnerOfAd() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetDocumentForDownloadingByOwnerOfAd------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = clientEmail;
		
		final Document initDoc = new Document();
		writtenAd.setDocument(initDoc);
		
		
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Client",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(client);
		
		logger.debug("Calling WrittenAdService.getDocumentForDownloading({},{})"
				+ " should return Document object",adId,userEmail);
		Document expected = initDoc;
		Document actual = documentService.getDocumentForDownloading(adId,userEmail);
		assertEquals(expected, actual);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetDocumentForDownloadingWithInvalidId() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetDocumentForDownloadingWithInvalidId------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = "anotherClient@mail.ru";
		
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return null",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(null);
		
		
		try{
			logger.debug("Calling WrittenAdService.getDocumentForDownloading({},{})"
					+ " should throw InvalidIdentifier",adId,userEmail);
			documentService.getDocumentForDownloading(adId,userEmail);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
		
	}
	
	@Test(expected = DownloadFileAccessDenied.class)
	public void testGetDocumentForDownloadingByAnotherClient() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetDocumentForDownloadingByAnotherClient------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = "anotherClient@mail.ru";
		
		final Document initDoc = new Document();
		writtenAd.setDocument(initDoc);
		
		Client anotherClient = new Client();
		anotherClient.setEmail(userEmail);
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Client",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(anotherClient);
		
		try{
			logger.debug("Calling WrittenAdService.getDocumentForDownloading({},{})"
					+ " should throw DownloadFileAccessDenied",adId,userEmail);
			documentService.getDocumentForDownloading(adId,userEmail);
		}catch(DownloadFileAccessDenied ex){
			logger.debug("DownloadFileAccessDenied has been thrown");
			throw ex;
		}
		
	}
	
	@Test(expected = DownloadFileAccessDenied.class)
	public void testGetDocumentForDownloadingByNotAuthClient() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetDocumentForDownloadingByNotAuthClient------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = "anotherClient@mail.ru";
		
		final Document initDoc = new Document();
		writtenAd.setDocument(initDoc);
		
		Client anotherClient = new Client();
		anotherClient.setEmail(userEmail);
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return null",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(null);
		
		try{
			logger.debug("Calling WrittenAdService.getDocumentForDownloading({},{})"
					+ " should throw DownloadFileAccessDenied",adId,userEmail);
			documentService.getDocumentForDownloading(adId,userEmail);
		}catch(DownloadFileAccessDenied ex){
			logger.debug("DownloadFileAccessDenied has been thrown");
			throw ex;
		}
		
	}
	
	
	/*@Test
	public void testGetResultDocumentForDownloadingByAdmin() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetResultDocumentForDownloadingByAdmin------------");
		final Long adId = writtenAd.getId();
		final String userEmail = "user@mail.ru";
		final String clientEmail = client.getEmail();
		
		final ResultDocument resultDoc = new ResultDocument();
		writtenAd.setResultDocument(resultDoc);
		//for comparing ResultDocuments via equals
		translator.addResultDocument(resultDoc);
		
		Admin admin = new Admin();
		
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Admin",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(admin);
		
		logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
				+ " should return ResultDocument object",adId,userEmail);
		ResultDocument expected = resultDoc;
		ResultDocument actual = documentService.getResultDocumentForDownloading(adId,userEmail);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetResultDocumentForDownloadingByOwnerOfAd() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetDocumentForDownloadingByOwnerOfAd------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = clientEmail;
		
		final ResultDocument resultDoc = new ResultDocument();
		writtenAd.setResultDocument(resultDoc);
		writtenAd.setStatus(AdStatus.PAYED);
		translator.addResultDocument(resultDoc);
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={} and has PAYED status",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Client",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(client);
		
		logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
				+ " should return ResultDocument object",adId,userEmail);
		ResultDocument expected = resultDoc;
		ResultDocument actual = documentService.getResultDocumentForDownloading(adId,userEmail);
		assertEquals(expected, actual);
	}
	*/
	/*@Test
	public void testGetResultDocumentForDownloadingByTranslatorOfAd() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetResultDocumentForDownloadingByTranslatorOfAd------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = translator.getEmail();
		
		final ResultDocument resultDoc = new ResultDocument();
		writtenAd.setResultDocument(resultDoc);
		resultDoc.setDownloader(translator);
		
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Translator, who download ResultDocument for "
				+ " WrittenAd with id={}",userEmail,adId);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(translator);
		
		logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
				+ " should return ResultDocument object",adId,userEmail);
		ResultDocument expected = resultDoc;
		ResultDocument actual = documentService.getResultDocumentForDownloading(adId,userEmail);
		assertEquals(expected, actual);
	}*/
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetResultDocumentForDownloadingWithInvalidId() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetResultDocumentForDownloadingWithInvalidId------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = "anotherClient@mail.ru";
		
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return null",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(null);
		
		
		try{
			logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
					+ " should throw InvalidIdentifier",adId,userEmail);
			documentService.getResultDocumentForDownloading(adId,userEmail);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
		
	}
	
	
	@Test(expected = DownloadFileAccessDenied.class)
	public void testGetResultDocumentForDownloadingByNotAuthUser() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetResultDocumentForDownloadingByNotAuthUser------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = "anotherClient@mail.ru";
		
		Client anotherClient = new Client();
		anotherClient.setEmail(userEmail);
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={}",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return null",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(null);
		
		try{
			logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
					+ " should throw DownloadFileAccessDenied",adId,userEmail);
			documentService.getResultDocumentForDownloading(adId,userEmail);
		}catch(DownloadFileAccessDenied ex){
			logger.debug("DownloadFileAccessDenied has been thrown");
			throw ex;
		}
		
	}
	
	/*@Test(expected = DownloadFileAccessDenied.class)
	public void testGetResultDocumentForDownloadingByOwnerOfAdWithoutPAYEDStatus() throws InvalidIdentifier, DownloadFileAccessDenied{
		logger.debug("-----------------testGetResultDocumentForDownloadingByOwnerOfAdWithoutPAYEDStatus------------");
		final Long adId = writtenAd.getId();
		final String clientEmail = client.getEmail();
		final String userEmail = clientEmail;
		
		final ResultDocument resultDoc = new ResultDocument();
		writtenAd.setResultDocument(resultDoc);
		writtenAd.setStatus(AdStatus.ACCEPTED);
		//translator.addResultDocument(resultDoc);
		
		logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
				+ "to return WrittenAd, which belongs "
				+ "to Client with email={} and has ACCEPTED status",adId,clientEmail);
		when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
		
		logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
				+ "to return Client",userEmail);
		when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(client);
		
		try{
			logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
					+ " should throw DownloadFileAccessDenied",adId,userEmail);
			documentService.getResultDocumentForDownloading(adId,userEmail);
		}catch(DownloadFileAccessDenied ex){
			logger.debug("DownloadFileAccessDenied has been thrown");
			throw ex;
		}
	 }*/
	
		/*@Test(expected = DownloadFileAccessDenied.class)
		public void testGetResultDocumentForDownloadingByAnotherTranslator() throws InvalidIdentifier, DownloadFileAccessDenied{
			logger.debug("-----------------testGetResultDocumentForDownloadingByAnotherTranslator------------");
			final Long adId = writtenAd.getId();
			final String clientEmail = client.getEmail();
			final String userEmail = translator.getEmail();
			
			final ResultDocument resultDoc = new ResultDocument();
			writtenAd.setResultDocument(resultDoc);
			resultDoc.setDownloader(translator);
			
			final Translator anotherTranslator = new Translator();
			
			logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
					+ "to return WrittenAd, which belongs "
					+ "to Client with email={}",adId,clientEmail);
			when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
			
			logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
					+ "to return Translator, who DOESN'T translate this text",userEmail);
			when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(anotherTranslator);
			
			try{
				logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
						+ " should throw DownloadFileAccessDenied",adId,userEmail);
				documentService.getResultDocumentForDownloading(adId,userEmail);
			}catch(DownloadFileAccessDenied ex){
				logger.debug("DownloadFileAccessDenied has been thrown");
				throw ex;
			}
		}*/
		
		@Test(expected = DownloadFileAccessDenied.class)
		public void testGetResultDocumentForDownloadingByAnotherClient() throws InvalidIdentifier, DownloadFileAccessDenied{
			logger.debug("-----------------testGetDocumentForDownloadingByAnotherClient------------");
			final Long adId = writtenAd.getId();
			final String clientEmail = client.getEmail();
			final String userEmail = "anotherClient@mail.ru";
			
			final Document initDoc = new Document();
			writtenAd.setDocument(initDoc);
			
			Client anotherClient = new Client();
			anotherClient.setEmail(userEmail);
			
			logger.debug("Stubbing WrittenAdDao.getWrittenAdById({}) "
					+ "to return WrittenAd, which belongs "
					+ "to Client with email={}",adId,clientEmail);
			when(adDao.getWrittenAdById(adId)).thenReturn(writtenAd);
			
			logger.debug("Stubbing ClientDao.getUserEntityByEmail({}) "
					+ "to return Client",userEmail);
			when(clientDao.getUserEntityByEmail(userEmail)).thenReturn(anotherClient);
			
			try{
				logger.debug("Calling WrittenAdService.getResultDocumentForDownloading({},{})"
						+ " should throw DownloadFileAccessDenied",adId,userEmail);
				documentService.getResultDocumentForDownloading(adId,userEmail);
			}catch(DownloadFileAccessDenied ex){
				logger.debug("DownloadFileAccessDenied has been thrown");
				throw ex;
			}
			
		}
}
		
	