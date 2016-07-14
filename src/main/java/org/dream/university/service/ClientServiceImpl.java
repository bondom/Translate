package org.dream.university.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.dream.university.dao.AbstractDao;
import org.dream.university.dao.UserDao;
import org.dream.university.model.Client;
import org.dream.university.model.User;
import org.dream.university.model.UserRole;
import org.dream.university.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("clientService")
@Transactional(propagation = Propagation.REQUIRED)
public class ClientServiceImpl extends UserDetailsServiceImpl implements UserService<Client> {

	@Autowired
	@Qualifier("clientDao")
	private UserDao<Client> clientDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean registerUser(Client user) {
		Client clientWithTheSameEmail = getUserByEmail(user.getEmail());
		if(!Objects.isNull(clientWithTheSameEmail)){
			//if user with the same email registered already
			return false;
		}else{
			//if user's credentials are unique
			String encodedPassword = encodePassword(user.getPassword());
			user.setPassword(encodedPassword);
			user.setRole(UserRole.ROLE_CLIENT);
			user.setStatus(UserStatus.ACTIVE);
			user.setRegistrationTime(LocalDateTime.now());
			((AbstractDao<Integer, Client>)clientDao).persist(user);
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
		Client client = getUserByEmail(oldEmail);
		client.setFirstName(newUser.getFirstName());
		client.setLastName(newUser.getLastName());
		client.setBirthday(newUser.getBirthday());
		client.setCity(newUser.getCity());
		client.setCountry(newUser.getCountry());
		client.setPhoneNumber(newUser.getPhoneNumber());
		client.setEmail(newUser.getEmail());
		return client;
	}

	@Override
	public Client getUserByEmail(String email) {
		Client userFromDB = clientDao.getUserByEmail(email);
		return userFromDB;
	}

	@Override
	public Client updateAvatar(String email, byte[] avatar) {
		Client userFromDB = clientDao.getUserByEmail(email);
		userFromDB.setAvatar(avatar);
	//	Client savedUser = ((AbstractDao<Integer,Client>)clientDao).update(userFromDB);
		return userFromDB;
	}

	private String encodePassword(String password){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedString = passwordEncoder.encode(password);
		return encodedString;
	}
	
	/**
	 * Checks if new email is unique, must be invoked only 
	 * after isEmailChanged(), because we must check on uniqueness only newEmail
	 * @param newEmail - new email, different from the old one
	 * @return true, if user with the same email doesn't exist, otherwise false
	 */
	private boolean isEmailUnique(String newEmail){
		User userFromDB = getUserByEmail(newEmail);
		if(userFromDB==null){
			//such email doesn't exist
			return true;
		}else{
			//user with the same email is registered in system already
			return false;
		}
	}
	
	private boolean isEmailChanged(String oldEmail,String newEmail){
		return !(oldEmail.equals(newEmail));
	}
	

}
