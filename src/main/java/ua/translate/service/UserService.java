package ua.translate.service;

import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.dao.AbstractDao;
import ua.translate.dao.UserDao;
import ua.translate.model.Client;
import ua.translate.model.EmailStatus;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.UserStatus;

@Service("userService")
@Transactional(propagation = Propagation.REQUIRED)
public abstract class UserService<T extends User> {
	
	@Autowired
	@Qualifier("userDao")
	private UserDao<User> userDao;
	
	@Autowired
	private JavaMailSender mailSender;
	
	/**
	 * Registers user in system, adds his to db
	 * 
	 * @param user
	 * @return true if registration is successful or 
	 * false if registration fails because some data are not unique
	 */
	@Transactional(propagation  = Propagation.REQUIRES_NEW)
	public abstract long registerUser(T user);
	
	
	
	/**
	 * Edits user's profile
	 * 
	 * @param email - email of authenticated user, get from {Principal.class} object
	 * @param newUser - user with some new values of fields,
	 * which must update old one
	 */
	public abstract  void editUserProfile(String email,T newUser);
	
	/**
	 * Replaces user's {@code email} with new one and changes emailStatus to {@code NOTCONFIRMED}
	 * <p>{@code newEmail} must be unique email
	 */
	public abstract void editUserEmail(String oldEmail,String newEmail);
	
	/**
	 * Replaces user's password
	 *@param email - email of authenticated user, get from {Principal.class} object
	 */
	public abstract void editUserPassword(String email,String newPassword);
	
	/**
	 * Gets user from DB by confirmUrl
	 */
	public abstract User getUserByConfirmedUrl(String confirmUrl);
	
	/**
	 * Gets user from db by email
	 * If email of user is retrieved from {Principal.class} object,
	 * user can be casted to concrete subclass without checking.
	 * 
	 * @param email - email of user
	 * @return registered user, or {@code null} if user with that email
	 * is not registered
	 */
	public User getUserByEmail(String email){
		User userFromDB = userDao.getUserByEmail(email);
		return userFromDB;
	}
	
	
	/**
	 * Gets user from DB by id
	 */
	public User getUserById(long id){
		User userFromDB = ((AbstractDao<Long, User>)userDao).get(id);
		return userFromDB;
	}
	
	/**
	 * Generates random confirmation url, sends letter to user and save confirmation url
	 * in db
	 */
	public void sendConfirmLetter(String email){
		User userFromDB = getUserByEmail(email);
		String ecu=generateConfirmUrl(userFromDB.getId());
		userFromDB.setConfirmedUrl(ecu);

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mimeMessage,false,"UTF-8");
			String htmlMsg = "<html><body><p>Please, go to "
					+ "<a href='localhost:8080/university/client/confirmation?ecu="+ecu+"'>"
							+ "languages.ru/confirmation?ecu="+ecu+"</a> for confirmation "
					+ "your registration</body></html> ";
			helper.setTo(email);
			helper.setSubject("Email confirmation");
			helper.setFrom("admin@languages.ru");
			helper.setText(htmlMsg, true);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Confirms user's e-mail - changes email status to {@link EmailStatus#CONFIRMED}
	 */
	public void confirmEmail(String email){
		User userFromDB = getUserByEmail(email);
		userFromDB.setEmailStatus(EmailStatus.CONFIRMED);
	}
	
	/**
	 * Updates avatar of user
	 * 
	 * @param email - email of existed user,
	 * usually is retrieving from {Principal.class} object 
	 * @param avatar
	 * @return updated user, never {@code null}
	 */
	public User updateAvatar(String email,byte[] avatar){
		User userFromDB = getUserByEmail(email);
		userFromDB.setAvatar(avatar);
		return userFromDB;
	}
	/**
	 * Encodes user's password before registering
	 * @param password
	 * @return
	 */
	
	protected String encodePassword(String password){
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
	public boolean isEmailUnique(String newEmail){
		User userFromDB = getUserByEmail(newEmail);
		if(userFromDB==null){
			//such email doesn't exist
			return true;
		}else{
			//user with the same email is registered in system already
			return false;
		}
	}
	/**
	 * Checks if new email differs from old one
	 * @param oldEmail
	 * @param newEmail
	 * @return true if they differ, else false
	 */
	public boolean isEmailChanged(String oldEmail,String newEmail){
		return !(oldEmail.equals(newEmail));
	}
	
	/**
	 * Checks if password inputted by user matches to real user's password 
	 * @return true if passwords match, otherwise false
	 */
	public boolean isPasswordRight(String passwordFromPage,String passwordFromDB){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(passwordFromPage, passwordFromDB);
	}

	public String generateConfirmUrl(long id){
		String url = id + UUID.randomUUID().toString();
		return url;
	}

}