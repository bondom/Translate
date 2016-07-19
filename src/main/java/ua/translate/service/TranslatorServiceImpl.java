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
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.UserStatus;

@Service("translatorService")
@Transactional(propagation = Propagation.REQUIRED)
public class TranslatorServiceImpl extends UserService<Translator>{

	@Autowired
	@Qualifier("translatorDao")
	private UserDao<Translator> translatorDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean registerUser(Translator user) {
		User userWithTheSameEmail = getUserByEmail(user.getEmail());
		if(!Objects.isNull(userWithTheSameEmail)){
			//if user with the same email registered already
			return false;
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
			return true;
		}
	}

	@Override
	public Translator editUserProfile(String email, Translator newUser) {
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

}