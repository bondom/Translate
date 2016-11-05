package ua.translate.test.service;

import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.LinkedHashSet;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ua.translate.dao.TranslatorDao;
import ua.translate.dao.AdDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.NumberExceedsException;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.impl.TranslatorServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class TranslatorServiceTest {
	
	private Translator translatorFromDao;
	private Client clientFromDao;
	private Ad adFromDao;
	private String passwordOfTranslator = "password";
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Mock private TranslatorDao translatorDao;
	@Mock private RespondedAdDao respondedAdDao;
	@Mock private AdDao adDao;

	@InjectMocks
	private TranslatorServiceImpl translatorService;
	Logger logger = LoggerFactory.getLogger(TranslatorServiceTest.class);

	@Before
	public void init(){
		translatorFromDao = new Translator();
		translatorFromDao.setPassword(encoder.encode(passwordOfTranslator));
		translatorFromDao.setEmail("test1@mail.ru");
		translatorFromDao.setId(123);
		
		adFromDao= new Ad();
		adFromDao.setId(1L);
		
		clientFromDao = new Client();
		clientFromDao.addAd(adFromDao);
		
		translatorService = new TranslatorServiceImpl();
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void testGetTranslatorById() throws InvalidIdentifier{
		long translatorId = 3L;
		logger.debug("-----------------testGetTranslatorById------------");
		logger.debug("Stubbing TranslatorDao.get({}) "
				+ "to return Translator",translatorId);
		when(translatorDao.get(translatorId)).thenReturn(translatorFromDao);
		logger.debug("Calling TranslatorService.getTranslatorById({})",
				translatorId);
		translatorService.getTranslatorById(translatorId);
		logger.debug("Verifying translatorDao.get({}) is called",translatorId); 
		verify(translatorDao).get(translatorId);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testGetTranslatorByInvalidId() throws InvalidIdentifier{
		long nonExistedTranslatorId = 2L;
		logger.debug("-----------------testGetTranslatorByInvalidId------------");
		logger.debug("Stubbing TranslatorDao.get({}) "
				+ "to return null",nonExistedTranslatorId);
		when(translatorDao.get(nonExistedTranslatorId)).thenReturn(null);
		try{
			logger.debug("Calling TranslatorService.getTranslatorById({}) should"
					+ " throw InvalidIdentifier",
					nonExistedTranslatorId);
			translatorService.getTranslatorById(nonExistedTranslatorId);
		}catch(InvalidIdentifier ex){
			logger.debug("InvalidIdentifier has been thrown");
			throw ex;
		}
	}
	
	@Test
	public void testGetTranslators(){
		int page=1;
		int numberTranslatorsOnPage = 3;
		logger.debug("-----------------testGetTranslators------------");
		logger.debug("Stubbing TranslatorDao.getTranslators({},{}) "
				+ "to return Set with translators",page,numberTranslatorsOnPage);
		when(translatorDao.getTranslators(page, numberTranslatorsOnPage))
					.thenReturn(new LinkedHashSet<Translator>());
		logger.debug("Calling TranslatorService.getTranslators({},{})",
				page,numberTranslatorsOnPage);
		translatorService.getTranslators(page,numberTranslatorsOnPage);
		logger.debug("Verifying translatorDao.getTranslators({},{}) is called",
				page,numberTranslatorsOnPage); 
		verify(translatorDao).getTranslators(page, numberTranslatorsOnPage);
	}
	
	
	@Test
	public void testSaveRespondedAd(){
		logger.debug("-----------------testSaveRespondedAd------------");
		String translatorEmail = translatorFromDao.getEmail();
		long adId = adFromDao.getId();
		int maxNumberOfSendedRespondedAds = 3;
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return ad",adId);
		when(adDao.get(adId)).thenReturn(adFromDao); 	
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator without ACCEPTED RespondedAd and"
				+ " with number of SENDED RespondedAd, "
				+ "less than max accepted= {}",translatorEmail,maxNumberOfSendedRespondedAds);
		when(translatorDao.getTranslatorByEmail(translatorEmail))
			.thenReturn(translatorFromDao);
	}
	
	@Test(expected = InvalidIdentifier.class)
	public void testCreateAndSaveRespondedAdForInvalidAdId() throws 
				NumberExceedsException, TranslatorDistraction,InvalidIdentifier{
		logger.debug("-----------------testCreateAndSaveRespondedAdForInvalidAdId------------");
		String translatorEmail = translatorFromDao.getEmail();
		long adId = adFromDao.getId();
		int maxNumberOfSendedRespondedAds = 3;
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",adId);
		when(adDao.get(adId)).thenReturn(null); 
		try{
			logger.debug("Calling TranslatorService.saveRespondedAd({},{},{})"
					+ " should throw NonExistedAdException()",
					translatorEmail,adId,maxNumberOfSendedRespondedAds);
			translatorService.createAndSaveRespondedAd(
					translatorEmail,adId,maxNumberOfSendedRespondedAds);
		}catch(InvalidIdentifier ex){
			logger.debug("NonExistedAdException has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = TranslatorDistraction.class)
	public void testCreateAndSaveRespondedAdForTranslatorWithAcceptedRespondedAd() throws 
				NumberExceedsException, TranslatorDistraction,InvalidIdentifier{
		logger.debug("-----------------testCreateAndSaveRespondedAdForTranslatorWithAcceptedRespondedAd------------");
		String translatorEmail = translatorFromDao.getEmail();
		
		//adding ACCEPTED RespondedAd to translator
		RespondedAd acceptedRespondedAd = new RespondedAd();
		acceptedRespondedAd.setStatus(RespondedAdStatus.ACCEPTED);
		translatorFromDao.addRespondedAd(acceptedRespondedAd);
		
		long adId = adFromDao.getId();
		int maxNumberOfSendedRespondedAds = 3;
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return ad",adId);
		when(adDao.get(adId)).thenReturn(adFromDao); 
		
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator with ACCEPTED RespondedAd ",
				translatorEmail);
		when(translatorDao.getTranslatorByEmail(translatorEmail))
			.thenReturn(translatorFromDao);
		try{
			logger.debug("Calling TranslatorService.saveRespondedAd({},{},{})"
					+ " should throw TranslatorDistraction()",
					translatorEmail,adId,maxNumberOfSendedRespondedAds);
			translatorService.createAndSaveRespondedAd(
					translatorEmail,adId,maxNumberOfSendedRespondedAds);
		}catch(TranslatorDistraction ex){
			logger.debug("TranslatorDistraction has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = NumberExceedsException.class)
	public void testCreateAndSaveRespondedAdForTranslatorWithMaxNumberSendedRespondedAd() throws 
				NumberExceedsException, TranslatorDistraction,InvalidIdentifier{
		logger.debug("-----------------testCreateAndSaveRespondedAdForTranslatorWithMaxNumberSendedRespondedAd------------");
		String translatorEmail = translatorFromDao.getEmail();
		int numberOfSendedResponsedAd = 2;
		//adding 2 SENDED RespondedAd to translator
		RespondedAd firstSendedRespondedAd = new RespondedAd();
		firstSendedRespondedAd.setStatus(RespondedAdStatus.SENDED);
		translatorFromDao.addRespondedAd(firstSendedRespondedAd);
		
		RespondedAd secondSendedRespondedAd = new RespondedAd();
		secondSendedRespondedAd.setStatus(RespondedAdStatus.SENDED);
		translatorFromDao.addRespondedAd(secondSendedRespondedAd);
		
		long adId = adFromDao.getId();
		int maxNumberOfSendedRespondedAds = 1;
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return ad",adId);
		when(adDao.get(adId)).thenReturn(adFromDao); 
		
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator with {} SENDED ResponsedAd, it is more then"
				+ " max available = {} ",
				translatorEmail,numberOfSendedResponsedAd,maxNumberOfSendedRespondedAds);
		when(translatorDao.getTranslatorByEmail(translatorEmail))
			.thenReturn(translatorFromDao);
		try{
			logger.debug("Calling TranslatorService.saveRespondedAd({},{},{})"
					+ " should throw NumberExceedsException()",
					translatorEmail,adId,maxNumberOfSendedRespondedAds);
			translatorService.createAndSaveRespondedAd(
					translatorEmail,adId,maxNumberOfSendedRespondedAds);
		}catch(NumberExceedsException ex){
			logger.debug("NumberExceedsException has been thrown");
			throw ex;
		}
	}
	
	@Test(expected = DuplicateEmailException.class)
	public void testRegisterUserWithExistedEmail() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------testRegisterUserWithExistedEmail------------");
		String existedEmail = "existed@mail.ru";
		translatorFromDao.setEmail(existedEmail);
		logger.debug("Stubbing TranslatorDao.flush() "
				+ " to throw ConstraintViolationException");
		doThrow(new ConstraintViolationException(
				"",new SQLException("integrity constraint"),"integrity constraint")).when(translatorDao).flush();
		try{
			logger.debug("Calling TranslatorService.registerUser(translator with existed email={})"
					+ " should throw DuplicateEmailException()",
					translatorFromDao.getEmail());
			translatorService.registerUser(translatorFromDao);
		}catch(DuplicateEmailException ex){
			logger.debug("DuplicateEmailException has been thrown");
			verify(translatorDao,times(1)).flush();
			logger.debug("Verified TranslatorDao.flush() is called once");
			throw ex;
		}
	}
	@Test
	public void testUpdateUserEmail() throws DuplicateEmailException,
													InvalidPasswordException{
		logger.debug("-----------------testUpdateUserEmail------------");
		String rightPassword=passwordOfTranslator;
		String oldEmail = translatorFromDao.getEmail();
		String newEmail = "testupdate@mail.ru";
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator with password={}",oldEmail,
				passwordOfTranslator);
		when(translatorDao.getTranslatorByEmail(oldEmail)).thenReturn(translatorFromDao);
		logger.debug("Calling TranslatorService.updateUserEmail({},{},{})",
				oldEmail,newEmail,rightPassword);
		translatorService.updateUserEmail(oldEmail, newEmail, rightPassword);
		logger.debug("Verifying TranslatorDao.flush() is called"); 
		verify(translatorDao).flush();
		logger.debug("Verifying TranslatorDao.getTranslatorByEmail({}) "
				+ "is called at least once",oldEmail);
		verify(translatorDao,atLeastOnce()).getTranslatorByEmail(oldEmail);
	}
	
	@Test(expected = InvalidPasswordException.class)
	public void updateEmailWithInvalidPassword() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------updateEmailWithInvalidPassword------------");
		String wrongPassword = "wrongPassword";
		String oldEmail = translatorFromDao.getEmail();
		String newEmail = "testupdate@mail.ru";
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator with password={}",oldEmail,passwordOfTranslator);
		when(translatorDao.getTranslatorByEmail(oldEmail)).thenReturn(translatorFromDao);
		try{
			logger.debug("Calling TranslatorService.updateUserEmail({},{},{})"
					+ " should throw InvalidPasswordException()",
					oldEmail,newEmail,wrongPassword);
			translatorService.updateUserEmail(translatorFromDao.getEmail(), "testupdate@mail.ru",
					wrongPassword);
		}catch(InvalidPasswordException ex){
			logger.debug("InvalidPasswordException has been thrown");
			verify(translatorDao,times(0)).flush();
			logger.debug("Verified TranslatorDao.flush() is not called");
			throw ex;
		}
	}
	
	@Test(expected = DuplicateEmailException.class)
	public void updateEmailWithExistedEmail() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------updateEmailWithExistedEmail------------");
		String rightPassword=passwordOfTranslator;
		String oldEmail = translatorFromDao.getEmail();
		String existedEmail = "existed@mail.ru";
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator ",oldEmail);
		when(translatorDao.getTranslatorByEmail(oldEmail)).thenReturn(translatorFromDao);
		logger.debug("Stubbing TranslatorDao.flush() "
				+ " to throw ConstraintViolationException");
		doThrow(new ConstraintViolationException(
				"",new SQLException("integrity constraint"),"integrity constraint")).when(translatorDao).flush();
		try{
			logger.debug("Calling TranslatorDao.updateUserEmail({},{},{})"
					+ " should throw DuplicateEmailException()",
					oldEmail,existedEmail,rightPassword);
			translatorService.updateUserEmail(oldEmail, existedEmail,
					rightPassword);
		}catch(DuplicateEmailException ex){
			logger.debug("DuplicateEmailException has been thrown");
			verify(translatorDao,times(1)).flush();
			logger.debug("Verified TranslatorDao.flush() is called once");
			throw ex;
		}
	}
	
	@Test
	public void testUpdateUserPassword() throws InvalidPasswordException{
		logger.debug("-----------------testUpdateUserPassword------------");
		String rightPassword=passwordOfTranslator;
		String email = translatorFromDao.getEmail();
		String newPassword = "newPassword";
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator with password={}",email,
				passwordOfTranslator);
		when(translatorDao.getTranslatorByEmail(email)).thenReturn(translatorFromDao);
		logger.debug("Calling TranslatorService.updateUserPassword({},{},{})",
				email,rightPassword,newPassword);
		translatorService.updateUserPassword(email, rightPassword, newPassword);
		logger.debug("Verifying TranslatorDao.getTranslatorByEmail({}) "
				+ "is called at least once",email);
		verify(translatorDao,atLeastOnce()).getTranslatorByEmail(email);
	}
	
	@Test(expected = InvalidPasswordException.class)
	public void updatePasswordWithInvalidPassword() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------updatePasswordWithInvalidPassword------------");
		String wrongPassword = "wrongPassword";
		String email = translatorFromDao.getEmail();
		String newPassword = "newPassword";
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator with password={}",email,
				passwordOfTranslator);
		when(translatorDao.getTranslatorByEmail(email)).thenReturn(translatorFromDao);
		try{
			logger.debug("Calling TranslatorService.updateUserPassword({},{},{})"
					+ " should throw InvalidPasswordException()",
					email,wrongPassword,newPassword);
			translatorService.updateUserPassword(email, wrongPassword,
					newPassword);
		}catch(InvalidPasswordException ex){
			logger.debug("InvalidPasswordException has been thrown");
			throw ex;
		}
	}
	
	
	@Test
	public void testGetRespondedAds(){
		logger.debug("-----------------testGetRespondedAds------------");
		String email = translatorFromDao.getEmail();
		int page = 2;
		int numberOfRespondedAdsOnPage = 10;
		logger.debug("Stubbing TranslatorDao.getTranslatorByEmail({}) "
				+ "to return translator with id={}",email,
				translatorFromDao.getId());
		when(translatorDao.getTranslatorByEmail(email)).thenReturn(translatorFromDao);
		logger.debug("Calling TranslatorService.getRespondedAds({},{},{})",
				email,page,numberOfRespondedAdsOnPage);
		translatorService.getRespondedAds(email, page, numberOfRespondedAdsOnPage);
		logger.debug("Verifying TranslatorDao.getTranslatorByEmail({}) "
				+ "is called at least once",email);
		verify(translatorDao,atLeastOnce()).getTranslatorByEmail(email);
		logger.debug("Verifying RespondedAdDao.getRespondedAdsByTranslator(translator object with id={},{},{}) "
				+ "is called once",translatorFromDao.getId(),page,numberOfRespondedAdsOnPage);
		verify(respondedAdDao).
			getRespondedAdsByTranslator(translatorFromDao, page, numberOfRespondedAdsOnPage);
	}
	
	
	
}
