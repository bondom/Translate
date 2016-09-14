package ua.translate.test.service;

import static org.mockito.Mockito.*;

import java.sql.SQLException;

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

import ua.translate.dao.ClientDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.model.Client;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.WrongPageNumber;
import ua.translate.service.impl.ClientServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {
	
	private Client clientFromDao;
	private String passwordOfClient = "password";
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Mock private ClientDao clientDao;
	@Mock private RespondedAdDao respondedAdDao;

	@InjectMocks
	private ClientServiceImpl clientService;
	Logger logger = LoggerFactory.getLogger(ClientServiceTest.class);

	@Before
	public void init(){
		clientFromDao = new Client();
		clientFromDao.setPassword(encoder.encode(passwordOfClient));
		clientFromDao.setEmail("test1@mail.ru");
		clientFromDao.setId(123);
		clientService = new ClientServiceImpl();
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test(expected = DuplicateEmailException.class)
	public void testRegisterUserWithExistedEmail() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------testRegisterUserWithExistedEmail------------");
		String existedEmail = "existed@mail.ru";
		clientFromDao.setEmail(existedEmail);
		logger.debug("Stubbing ClientDao.flush() "
				+ " to throw ConstraintViolationException");
		doThrow(new ConstraintViolationException(
				"",new SQLException("integrity constraint"),"integrity constraint")).when(clientDao).flush();
		try{
			logger.debug("Calling ClientDao.registerUser(client with existed email={})"
					+ " should throw DuplicateEmailException()",
					clientFromDao.getEmail());
			clientService.registerUser(clientFromDao);
		}catch(DuplicateEmailException ex){
			logger.debug("DuplicateEmailException has been thrown");
			verify(clientDao,times(1)).flush();
			logger.debug("Verified ClientDao.flush() is called once");
			throw ex;
		}
	}
	@Test
	public void testUpdateUserEmail() throws DuplicateEmailException,
													InvalidPasswordException{
		logger.debug("-----------------testUpdateUserEmail------------");
		String rightPassword=passwordOfClient;
		String oldEmail = clientFromDao.getEmail();
		String newEmail = "testupdate@mail.ru";
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client with password={}",oldEmail,
				passwordOfClient);
		when(clientDao.getClientByEmail(oldEmail)).thenReturn(clientFromDao);
		logger.debug("Calling ClientService.updateUserEmail({},{},{})",
				oldEmail,newEmail,rightPassword);
		clientService.updateUserEmail(oldEmail, newEmail, rightPassword);
		logger.debug("Verifying ClientDao.flush() is called"); 
		verify(clientDao).flush();
		logger.debug("Verifying ClientDao.getClientByEmail({}) "
				+ "is called at least once",oldEmail);
		verify(clientDao,atLeastOnce()).getClientByEmail(oldEmail);
	}
	
	@Test(expected = InvalidPasswordException.class)
	public void updateEmailWithInvalidPassword() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------updateEmailWithInvalidPassword------------");
		String wrongPassword = "wrongPassword";
		String oldEmail = clientFromDao.getEmail();
		String newEmail = "testupdate@mail.ru";
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client with password={}",oldEmail,passwordOfClient);
		when(clientDao.getClientByEmail(oldEmail)).thenReturn(clientFromDao);
		try{
			logger.debug("Calling ClientService.updateUserEmail({},{},{})"
					+ " should throw InvalidPasswordException()",
					oldEmail,newEmail,wrongPassword);
			clientService.updateUserEmail(clientFromDao.getEmail(), "testupdate@mail.ru",
					wrongPassword);
		}catch(InvalidPasswordException ex){
			logger.debug("InvalidPasswordException has been thrown");
			verify(clientDao,times(0)).flush();
			logger.debug("Verified ClientDao.flush() is not called");
			throw ex;
		}
	}
	
	@Test(expected = DuplicateEmailException.class)
	public void updateEmailWithExistedEmail() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------updateEmailWithExistedEmail------------");
		String rightPassword=passwordOfClient;
		String oldEmail = clientFromDao.getEmail();
		String existedEmail = "existed@mail.ru";
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client ",oldEmail);
		when(clientDao.getClientByEmail(oldEmail)).thenReturn(clientFromDao);
		logger.debug("Stubbing ClientDao.flush() "
				+ " to throw ConstraintViolationException");
		doThrow(new ConstraintViolationException(
				"",new SQLException("integrity constraint"),"integrity constraint")).when(clientDao).flush();
		try{
			logger.debug("Calling ClientDao.updateUserEmail({},{},{})"
					+ " should throw DuplicateEmailException()",
					oldEmail,existedEmail,rightPassword);
			clientService.updateUserEmail(oldEmail, existedEmail,
					rightPassword);
		}catch(DuplicateEmailException ex){
			logger.debug("DuplicateEmailException has been thrown");
			verify(clientDao,times(1)).flush();
			logger.debug("Verified ClientDao.flush() is called once");
			throw ex;
		}
	}
	
	@Test
	public void testUpdateUserPassword() throws InvalidPasswordException{
		logger.debug("-----------------testUpdateUserPassword------------");
		String rightPassword=passwordOfClient;
		String email = clientFromDao.getEmail();
		String newPassword = "newPassword";
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client with password={}",email,
				passwordOfClient);
		when(clientDao.getClientByEmail(email)).thenReturn(clientFromDao);
		logger.debug("Calling ClientService.updateUserPassword({},{},{})",
				email,rightPassword,newPassword);
		clientService.updateUserPassword(email, rightPassword, newPassword);
		logger.debug("Verifying ClientDao.getClientByEmail({}) "
				+ "is called at least once",email);
		verify(clientDao,atLeastOnce()).getClientByEmail(email);
	}
	
	@Test(expected = InvalidPasswordException.class)
	public void updatePasswordWithInvalidPassword() throws DuplicateEmailException,
														InvalidPasswordException{
		logger.debug("-----------------updatePasswordWithInvalidPassword------------");
		String wrongPassword = "wrongPassword";
		String email = clientFromDao.getEmail();
		String newPassword = "newPassword";
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client with password={}",email,
				passwordOfClient);
		when(clientDao.getClientByEmail(email)).thenReturn(clientFromDao);
		try{
			logger.debug("Calling ClientService.updateUserPassword({},{},{})"
					+ " should throw InvalidPasswordException()",
					email,wrongPassword,newPassword);
			clientService.updateUserPassword(email, wrongPassword,
					newPassword);
		}catch(InvalidPasswordException ex){
			logger.debug("InvalidPasswordException has been thrown");
			throw ex;
		}
	}
	
	
	@Test
	public void testGetRespondedAds() throws WrongPageNumber{
		logger.debug("-----------------testGetRespondedAds------------");
		String email = clientFromDao.getEmail();
		int page = 2;
		int numberOfRespondedAdsOnPage = 10;
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return client with id={}",email,
				clientFromDao.getId());
		when(clientDao.getClientByEmail(email)).thenReturn(clientFromDao);
		logger.debug("Calling ClientService.getRespondedAds({},{},{})",
				email,page,numberOfRespondedAdsOnPage);
		clientService.getRespondedAds(email, page, numberOfRespondedAdsOnPage);
		logger.debug("Verifying ClientDao.getClientByEmail({}) "
				+ "is called at least once",email);
		verify(clientDao,atLeastOnce()).getClientByEmail(email);
		logger.debug("Verifying RespondedAdDao.getRespondedAdsByClient(client object with id={},{},{}) "
				+ "is called once",clientFromDao.getId(),page,numberOfRespondedAdsOnPage);
		verify(respondedAdDao).
			getRespondedAdsByClient(clientFromDao, page, numberOfRespondedAdsOnPage);
	}
	
	@Test(expected = WrongPageNumber.class)
	public void testGetRespondedAdsWithWrongPageNumber() throws WrongPageNumber{
		logger.debug("-----------------testGetRespondedAdsWithWrongPageNumber------------");
		String email = clientFromDao.getEmail();
		int invalidNumberOfPage = 0;
		int numberOfRespondedAdsOnPage = 10;
		try{
			logger.debug("Calling ClientService.getRespondedAds({},{},{})"
					+ " should throw WrongPageNumber",
					email,invalidNumberOfPage,numberOfRespondedAdsOnPage);
					clientService.getRespondedAds(email, invalidNumberOfPage, 
							numberOfRespondedAdsOnPage);
		}catch(WrongPageNumber ex){
			logger.debug("WrongPageNumber has been thrown");
			verify(clientDao,times(0)).getClientByEmail(email);
			logger.debug("Verified ClientDao.getClientByEmail() is not called");
			verify(respondedAdDao,times(0)).
				getRespondedAdsByClient(clientFromDao, invalidNumberOfPage, numberOfRespondedAdsOnPage);
			logger.debug("Verified RespondedAdDao.getRespondedAdsByClient(client object with id={},{},{}) "
					+ "is not called",clientFromDao.getId(),invalidNumberOfPage,numberOfRespondedAdsOnPage);
			throw ex;
		}
	}
}
