package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ArchievedAdDao;
import ua.translate.dao.ClientDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.UserEntity;
import ua.translate.model.UserEntity.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.EmailStatus;
import ua.translate.model.status.UserStatus;
import ua.translate.service.AbstractAdService;
import ua.translate.service.ClientService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.WrongPageNumber;

@Service
@Transactional(propagation = Propagation.REQUIRED,rollbackFor = DuplicateEmailException.class)
public class ClientServiceImpl extends ClientService{

	private static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private ArchievedAdDao archievedAdDao;
	
	@Autowired
	private RespondedAdDao respondedAdDao;
	
	/**
	 * It is default number of responded ads on one page
	 */
	private static final int DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE=3;
	
	@Override
	public Client getClientByEmail(String email) {
		Client client = clientDao.getClientByEmail(email);
		return client;
	}

	@Override
	public Set<RespondedAd> getRespondedAds(String email,
										    int page,
										    int numberOfRespondedAdsOnPage) throws WrongPageNumber{
		if(page<1){
			throw new WrongPageNumber();
		}
		if(numberOfRespondedAdsOnPage<1){
			logger.debug("numberOfRespondedAdsOnPage = {}, default value={} is used",
					numberOfRespondedAdsOnPage,DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE);
			numberOfRespondedAdsOnPage = DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE;
		}
		Client client = getClientByEmail(email);
		Set<RespondedAd> responsedAds = respondedAdDao
				.getRespondedAdsByClient(client, page, numberOfRespondedAdsOnPage);
		return responsedAds;
	}
	
	@Override
	public long getNumberOfPagesForRespondedAds(String email, int numberOfRespondedAdsOnPage) {
		if(numberOfRespondedAdsOnPage<1){
			logger.debug("numberOfRespondedAdsOnPage = {}, default value={} is used",
					numberOfRespondedAdsOnPage,DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE);
			numberOfRespondedAdsOnPage = DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE;
		}
		Client client = getClientByEmail(email);
		long numberOfRespondedAds = respondedAdDao.getNumberOfRespondedAdsByClient(client);
		long numberOfPages = (long) Math
				.ceil(((double)numberOfRespondedAds)/numberOfRespondedAdsOnPage);
		return numberOfPages;
		
	}
	
	@Override
	public Set<Ad> getAds(String email) {
		Client client = getClientByEmail(email);
		Set<Ad> ads = client.getAds();
		return ads;
	}

	@Override
	public void registerUser(Client newUser) throws DuplicateEmailException{
		
		newUser.setPassword(encodePassword(newUser.getPassword()));
		
		try{
			clientDao.save(newUser);
			clientDao.flush();
		}catch(ConstraintViolationException e){
			throw new DuplicateEmailException();
		}
		
	}

	@Override
	public void updateUserProfile(String email, Client updatedUser) {
		Client client = getClientByEmail(email);
		
		client.setFirstName(updatedUser.getFirstName());
		client.setLastName(updatedUser.getLastName());
		client.setBirthday(updatedUser.getBirthday());
		client.setCity(updatedUser.getCity());
		client.setCountry(updatedUser.getCountry());
		client.setPhoneNumber(updatedUser.getPhoneNumber());
		
		
	}

	@Override
	public void updateUserEmail(String email, String newEmail, String password)
			throws InvalidPasswordException,DuplicateEmailException {
		Client client = getClientByEmail(email);
		if(!isPasswordRight(password, client.getPassword())){
			throw new InvalidPasswordException("Password doesn't match to real");
		}
		
		if(email.equals(newEmail)){
			return;
		}
		
		try{
			client.setEmail(newEmail);
			client.setEmailStatus(EmailStatus.NOTCONFIRMED);
			clientDao.flush();
		}catch(ConstraintViolationException e){
			throw new DuplicateEmailException();
		}
		
	}

	@Override
	public void updateUserPassword
			(String email, String password, String newPassword) 
									throws InvalidPasswordException{
		Client client = getClientByEmail(email);
		if(!isPasswordRight(password, client.getPassword())){
			throw new InvalidPasswordException("Password doesn't match to real");
		}
		
		if(password.equals(newPassword)){
			return;
		}
		
		String encodedPassword = encodePassword(newPassword);
		client.setPassword(encodedPassword);
	}

	@Override
	public void updateAvatar(String email, byte[] avatar) {
		Client client = getClientByEmail(email);
		client.setAvatar(avatar);
	}

}
