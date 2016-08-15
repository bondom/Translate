package ua.translate.service;

 
import java.security.Principal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.UserDao;
import ua.translate.model.Client;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.status.EmailStatus;
import ua.translate.model.status.UserStatus;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.WrongPageNumber;

/**
 * 
 * @author Yuriy Phediv
 *
 * @param <T> - class, which represents a concrete subclass of {@code User}
 */
@Transactional(propagation = Propagation.REQUIRED)
public abstract class UserService<T extends User> {
	
	@Autowired
	private UserDao userDao;
	
	/**
	 * Gets {@link User} by email from data storage
	 * @param email - email of user, usually is retrieved from {@code Principal} object
	 * @return {@code User} or {@code null} if user with
	 * the same email is not registered.
	 * 
	 */
	public User getUserByEmail(String email){
		return userDao.getUserByEmail(email);
	}
	
	/**
	 * Gets {@link User} by confirmationUrl from data storage
	 * @return {@code User} or {@code null} if user with
	 * the same confirmationUrl is not registered
	 */
	public User getUserByConfirmationUrl(String confirmationUrl){
		return userDao.getUserByConfirmationUrl(confirmationUrl);
	}
	
	
	/**
	 * Gets {@link User} by id from data storage
	 * Returns {@code User} or {@code null} if user with
	 * the same id is not registered
	 */
	public User getUserById(long id){
		return userDao.get(id);
	}
	
	/**
	 * Changes {@code emailStatus} to {@code EmailStatus#CONFIRMED}
	 * @param confirmationUrl - url, which is stored in data storage
	 * @return confirmed email
	 * @throws InvalidConfirmationUrl - if user with such confirmationUrl doesn't exist
	 */
	public String confirmUserEmail(String confirmationUrl) throws InvalidConfirmationUrl {
		User user = getUserByConfirmationUrl(confirmationUrl);
		if(user==null){
			throw new InvalidConfirmationUrl();
		}
		user.setEmailStatus(EmailStatus.CONFIRMED);
		return user.getEmail();
	}
	
	
	/**
	 * Saves {@code newUser} in data storage.
	 * <p>Sets appropriate statuses of email and user; sets {@link UserRole},encoded password
	 * and registration time
	 * @throws DuplicateEmailException  if email of user is registered already 
	 */
	public abstract void registerUser(T newUser) throws DuplicateEmailException;
	
	/**
	 * Updates user's profile information, such as first name, last name, date of birth etc.
	 * <b>CAN'T</b> update user's email or password.
	 * @param email - email of authenticated user, usually is retrieved from {@code Principal} object
	 * @param updatedUser - {@code User} with some new values of fields
	 * @see #updateUserEmail(String, String, String)
	 * @see #updateUserPassword(String, String, String)
	 */
	public abstract void updateUserProfile(String email,T updatedUser);
	
	/**
	 * Replaces user's old email with new one and changes {@code emailStatus} to {@code NOTCONFIRMED}
	 * @param email - email of authenticated user, usually is retrieved from {@code Principal} object
	 * @param newEmail - new email
	 * @param password - current user's password
	 * @throws InvalidPasswordException - if {@code password} doesn't equals to real user's password
	 * @throws DuplicateEmailException - if user with {@code newEmail} is registered already
	 */
	public abstract void updateUserEmail(String email,String newEmail,String password) 
			throws InvalidPasswordException,DuplicateEmailException;
	
	/**
	 * Replaces user's password with new one
	 * @param email - email of authenticated user, usually is retrieved from {@code Principal} object
	 * @param password - current password of user
	 * @param newPassword - new password, entered by user
	 * @throws InvalidPasswordException - if {@code password} doesn't match to real user's password
	 */
	public abstract void updateUserPassword(String email,String password,String newPassword)
			throws InvalidPasswordException;
	
	/**
	 * Replaces old avatar of user
	 * @param email - email of authenticated user, usually is retrieved from {@code Principal} object
	 * @param avatar 
	 */
	public abstract void updateAvatar(String email, byte[] avatar);
	
	
	/**
	 * Generates random url and saves it in data storage
	 * @param email - email of authenticated user, usually is retrieved from {@code Principal} object
	 * @return generated url
	 */
	public String saveConfirmationUrl(String email) throws EmailIsConfirmedException {
		User user = userDao.getUserByEmail(email);
		if(user.getEmailStatus().equals(EmailStatus.CONFIRMED)){
			throw new EmailIsConfirmedException();
		}
		String url = user.getId() + UUID.randomUUID().toString();
		user.setConfirmationUrl(url);
		return url;
	}
	
	/**
	 * Returns all {@link ResponsedAd}s, related to object type subclass of {@link User} with email={@code email},
	 * ordered by {@link ResponsedAd#getDateTimeOfResponse()} 
	 * from latest to earliest.
	 * <p>Size of result {@code Set} is not more than {@code numberOfResponsedAdsOnPage}
	 * @param email - email of authenticated client,
	 * 					usually is retrieved from {@link Principal} object
	 * @param page -  page number, can't be less than 1
	 * @param numberOfResponsedAdsOnPage - 
	 * 			number {@code ResponsedAd}s, which can be displayed on 1 page
	 *  @throws WrongPageNumber if {@code page} is less than 1
	 */
	public abstract Set<ResponsedAd> getResponsedAds
					(String email,int page,int numberOfResponsedAdsOnPage) 
														throws WrongPageNumber;
	
	/**
	 * Returns number of pages for all {@link ResponsedAd}s,
	 *   related to object type subclass of {@link User} with email={@code email},
	 * if on one page can be displayed only {@code numberOfResponsedAdsOnPage}  ResponsedAds
	 * @param numberOfResponsedAdsOnPage - number of {@code ResponsedAd}s, which can be displayed on one page
	 * @param email - email of authenticated user,
	 * 					usually is retrieved from {@link Principal} object
	 */
	public abstract long getNumberOfPagesForResponsedAds(String email,
														 int numberOfResponsedAdsOnPage);
	
	/**
	 * Checks if {@code passwordFromPage} matches to encoded password from data storage
	 * @return true if passwords match, otherwise false
	 */
	protected boolean isPasswordRight(String passwordFromPage,String passwordFromDB){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(passwordFromPage, passwordFromDB);
	}
	
	/**
	 * Encodes {@code password} via {@link BCryptPasswordEncoder}
	 * @return encoded password
	 */
	protected String encodePassword(String password){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedString = passwordEncoder.encode(password);
		return encodedString;
	}
	
}
