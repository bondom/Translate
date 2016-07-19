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
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.UserStatus;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.AdStatus;

@Service("clientService")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ClientServiceImpl extends UserService<Client>{

	@Autowired
	@Qualifier("clientDao")
	private UserDao<Client> clientDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean registerUser(Client user) {
		User userWithTheSameEmail = getUserByEmail(user.getEmail());
		if(!Objects.isNull(userWithTheSameEmail)){
			//if user with the same email registered already
			return false;
		}else{
			//if user's credentials are unique
			String encodedPassword = encodePassword(user.getPassword());
			user.setPassword(encodedPassword);
			user.setRole(UserRole.ROLE_CLIENT);
			user.setStatus(UserStatus.ACTIVE);
			user.setRegistrationTime(LocalDateTime.now());
			((AbstractDao<Integer, Client>)clientDao).save(user);
			return true;
		}
	}

	@Override
	public Client editUserProfile(String email, Client newUser) {
		String oldEmail = email;
		String newEmail = newUser.getEmail();
		
		//isEmailUnique(newEmail) is invoked only if email is changed
		if(isEmailChanged(oldEmail, newEmail) &&
				!isEmailUnique(newEmail)){
			//new email is registered in system already
			return null;
		}
		Client client = (Client)getUserByEmail(oldEmail);
		client.setFirstName(newUser.getFirstName());
		client.setLastName(newUser.getLastName());
		client.setBirthday(newUser.getBirthday());
		client.setCity(newUser.getCity());
		client.setCountry(newUser.getCountry());
		client.setPhoneNumber(newUser.getPhoneNumber());
		client.setEmail(newUser.getEmail());
		return client;
	}
	
	/*public Ad saveAd(String email, Ad ad){
		Client client = (Client)getUserByEmail(email);
		ad.setCreationDateTime(LocalDateTime.now());
		ad.setStatus(AdStatus.CREATED);
		client.addAd(ad);
		return ad;
	}*/

}
