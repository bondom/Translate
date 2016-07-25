package ua.translate.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AbstractDao;
import ua.translate.dao.UserDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.UserStatus;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.ad.ResponsedAdStatus;

@Service("translatorService")
@Transactional(propagation = Propagation.REQUIRED)
public class TranslatorServiceImpl extends UserService<Translator>{

	@Autowired
	@Qualifier("translatorDao")
	private UserDao<Translator> translatorDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public long registerUser(Translator user) {
		User userWithTheSameEmail = getUserByEmail(user.getEmail());
		if(!Objects.isNull(userWithTheSameEmail)){
			//if user with the same email registered already
			return 0;
		}else{
			//if user's credentials are unique
			String encodedPassword = encodePassword(user.getPassword());
			user.setPassword(encodedPassword);
			user.setRole(UserRole.ROLE_TRANSLATOR);
			user.setStatus(UserStatus.ACTIVE);
			user.setRegistrationTime(LocalDateTime.now());
			user.setRating(0);
			user.setNumberOfExecutedAds((short)0);
			((AbstractDao<Integer, Translator>)translatorDao).save(user);
			return user.getId();
		}
	}

	@Override
	public Translator editUserProfile(String email, Translator newUser,boolean changeEmail) {
		String oldEmail = email;
		String newEmail = newUser.getEmail();
		
		//isEmailUnique(newEmail) is invoked only if email is changed
		if(isEmailChanged(oldEmail, newEmail) &&
				!isEmailUnique(newEmail)){
			//new email is registered in system already
			return null;
		}
		Translator translator = (Translator) getUserByEmail(oldEmail);
		translator.setFirstName(newUser.getFirstName());
		translator.setLastName(newUser.getLastName());
		translator.setBirthday(newUser.getBirthday());
		translator.setCity(newUser.getCity());
		translator.setCountry(newUser.getCountry());
		translator.setPhoneNumber(newUser.getPhoneNumber());
		translator.setEmail(newUser.getEmail());
		translator.setAddedInfo(newUser.getAddedInfo());
		return translator;
	}
	
	public void saveResponsedAd(Ad ad, String email){
		Translator translator = translatorDao.getUserByEmail(email);
		ResponsedAd responsedAd = new ResponsedAd();
		responsedAd.setDateTimeOfResponse(LocalDateTime.now());
		responsedAd.setStatus(ResponsedAdStatus.SENDED);
		Client client = ad.getClient();
		ad.addResponsedAd(responsedAd);
		client.addResponsedAd(responsedAd);
		translator.addResponsedAd(responsedAd);
	}

	@Override
	public void confirmRegistration(String email) {
		// TODO Auto-generated method stub
		
	}

}
