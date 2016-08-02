package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ua.translate.dao.AdDao;
import ua.translate.dao.ClientDao;
import ua.translate.dao.ResponsedAdDao;
import ua.translate.dao.TranslatorDao;
import ua.translate.model.Client;
import ua.translate.model.ResponsedAd;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.status.EmailStatus;
import ua.translate.model.status.ResponsedAdStatus;
import ua.translate.model.status.UserStatus;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.NonExistedAdException;

@Service
public class TranslatorServiceImpl extends TranslatorService {

	
	@Autowired
	private TranslatorDao translatorDao;
	
	@Autowired
	private AdDao adDao;
	
	@Autowired
	private ResponsedAdDao responsedAdDao;
	
	@Override
	public Translator getTranslatorByEmail(String email) {
		Translator translator = translatorDao.getTranslatorByEmail(email);
		return translator;
	}

	@Override
	public void saveResponsedAd(String email,long adId) throws NonExistedAdException {
		Ad ad = adDao.get(adId);
		if(ad == null){
			throw new NonExistedAdException();
		}
		
		ResponsedAd responsedAd = new ResponsedAd();
		
		Translator translator = translatorDao.getTranslatorByEmail(email);
		Client client = ad.getClient();
		
		translator.addResponsedAd(responsedAd);
		client.addResponsedAd(responsedAd);
		ad.addResponsedAd(responsedAd);
		
		responsedAd.setStatus(ResponsedAdStatus.SENDED);
		responsedAd.setDateTimeOfResponse(LocalDateTime.now());
		
		responsedAdDao.save(responsedAd);
		
	}
	
	@Override
	public void registerUser(Translator newUser) throws DuplicateEmailException {
		User user = translatorDao.getUserByEmail(newUser.getEmail());
		if(user!=null){
			throw new DuplicateEmailException("User with the same email is registered"
					+ " in system already");
		}
		
		newUser.setPassword(encodePassword(newUser.getPassword()));
		newUser.setRole(UserRole.ROLE_TRANSLATOR);
		newUser.setStatus(UserStatus.ACTIVE);
		newUser.setEmailStatus(EmailStatus.NOTCONFIRMED);
		newUser.setRegistrationTime(LocalDateTime.now());
		
		translatorDao.save(newUser);
	}

	@Override
	public void updateUserProfile(String email, Translator updatedUser) {
		Translator translator = getTranslatorByEmail(email);
		
		translator.setFirstName(updatedUser.getFirstName());
		translator.setLastName(updatedUser.getLastName());
		translator.setBirthday(updatedUser.getBirthday());
		translator.setCity(updatedUser.getCity());
		translator.setCountry(updatedUser.getCountry());
		translator.setPhoneNumber(updatedUser.getPhoneNumber());
		translator.setAddedInfo(updatedUser.getAddedInfo());
		translator.setLanguages(updatedUser.getLanguages());
		
	}

	@Override
	public void updateUserEmail(String email, String newEmail, String password)
			throws InvalidPasswordException, DuplicateEmailException {
		Translator translator = getTranslatorByEmail(email);
		if(!isPasswordRight(password, translator.getPassword())){
			throw new InvalidPasswordException("Password doesn't match to real");
		}
		
		if(email.equals(newEmail)){
			return;
		}
		
		if(!isEmailUnique(newEmail)){
			throw new DuplicateEmailException(
					"Such email is registered in system already");
		}
		
		translator.setEmail(newEmail);
		translator.setEmailStatus(EmailStatus.NOTCONFIRMED);
		
	}

	@Override
	public void updateUserPassword(String email, String password, String newPassword) throws InvalidPasswordException {
		Translator translator= getTranslatorByEmail(email);
		if(!isPasswordRight(password, translator.getPassword())){
			throw new InvalidPasswordException("Password doesn't match to real");
		}
		String encodedPassword = encodePassword(newPassword);
		translator.setPassword(encodedPassword);
		
	}

	@Override
	public void updateAvatar(String email, byte[] avatar) {
		Translator translator = getTranslatorByEmail(email);
		translator.setAvatar(avatar);
		
	}


	@Override
	public String saveConfirmationUrl(String email) throws EmailIsConfirmedException {
		Translator translator = translatorDao.getTranslatorByEmail(email);
		if(translator.getEmailStatus().equals(EmailStatus.CONFIRMED)){
			throw new EmailIsConfirmedException();
		}
		String url = translator.getId() + UUID.randomUUID().toString();
		translator.setConfirmedUrl(url);
		return url;
	}

	

}
