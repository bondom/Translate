package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AdDao;
import ua.translate.dao.ClientDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.dao.TranslatorDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.EmailStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.model.status.UserStatus;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.NonExistedTranslatorException;
import ua.translate.service.exception.NumberExceedsException;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.exception.WrongPageNumber;

@Service
@Transactional(propagation = Propagation.REQUIRED,
			   rollbackFor = {DuplicateEmailException.class,
					   		  NumberExceedsException.class})
public class TranslatorServiceImpl extends TranslatorService {

	@Autowired
	private TranslatorDao translatorDao;
	
	@Autowired
	private AdDao adDao;
	
	@Autowired
	private RespondedAdDao respondedAdDao;
	
	/**
	 * It is default number of responded ads on one page
	 */
	private static final int DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE=3;
	
	@Override
	public Translator getTranslatorByEmail(String email) {
		Translator translator = translatorDao.getTranslatorByEmail(email);
		return translator;
	}

	@Override
	public Translator getTranslatorById(long id) throws NonExistedTranslatorException {
		Translator translator = translatorDao.get(id);
		if(translator==null){
			throw new NonExistedTranslatorException();
		}
		return translator;
	}

	@Override
	public Set<Translator> getTranslators(int page,int numberTranslatorsOnPage) 
			throws WrongPageNumber {
		if(page<1){
			throw new WrongPageNumber();
		}
		return translatorDao.getTranslators(page,numberTranslatorsOnPage);
	}
	
	@Override
	public long getNumberOfPagesForTranslators(int numberOfTranslatorsOnPage) {
		long numberOfTranslators = translatorDao.getNumberOfTranslators();
		long numberOfPages = 
				(long) Math.ceil(((double)numberOfTranslators)/numberOfTranslatorsOnPage);
		return numberOfPages;
	}
	
	@Override
	public long saveRespondedAd(
			String email,long adId, int maxNumberOfSendedRespondedAds) 
									throws NonExistedAdException, 
										   NumberExceedsException, 
										   TranslatorDistraction{
		Ad ad = adDao.get(adId);
		if(ad == null){
			throw new NonExistedAdException();
		}
		
		Translator translator = translatorDao.getTranslatorByEmail(email);
		
		if(hasAcceptedAd(translator)){
			throw new TranslatorDistraction();
		}
		
		if(!numberRespondedAdsInNormalRange(translator,maxNumberOfSendedRespondedAds)){
			throw new NumberExceedsException();
		}
		
		
		RespondedAd respondedAd = new RespondedAd();
		
		Client client = ad.getClient();
		
		translator.addRespondedAd(respondedAd);
		client.addRespondedAd(respondedAd);
		ad.addRespondedAd(respondedAd);
		
		respondedAd.setStatus(RespondedAdStatus.SENDED);
		respondedAd.setDateTimeOfResponse(LocalDateTime.now());
		
		return respondedAdDao.save(respondedAd);
		
	}
	
	@Override
	public void registerUser(Translator newUser) throws DuplicateEmailException {
		
		newUser.setPassword(encodePassword(newUser.getPassword()));
		newUser.setRole(UserRole.ROLE_TRANSLATOR);
		newUser.setStatus(UserStatus.ACTIVE);
		newUser.setEmailStatus(EmailStatus.NOTCONFIRMED);
		newUser.setRegistrationTime(LocalDateTime.now());
		newUser.setPublishingTime(LocalDateTime.now());
		try{
			translatorDao.save(newUser);
			translatorDao.flush();
		}catch(ConstraintViolationException e){
			throw new DuplicateEmailException("User with the same email is registered"
					+ " in system already");
		}
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
		
		try{
			translator.setEmail(newEmail);
			translator.setEmailStatus(EmailStatus.NOTCONFIRMED);
			translatorDao.flush();
		}catch(ConstraintViolationException e){
			throw new DuplicateEmailException();
		}
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
	public Set<RespondedAd> getRespondedAds(String email,
										    int page,
										    int numberOfRespondedAdsOnPage) throws WrongPageNumber{
		if(page<1){
			throw new WrongPageNumber();
		}
		
		if(numberOfRespondedAdsOnPage<1){
			//default value is used
			numberOfRespondedAdsOnPage = DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE;
		}
		Translator translator = getTranslatorByEmail(email);
		Set<RespondedAd> respondedAds = respondedAdDao
				.getRespondedAdsByTranslator(translator, page, numberOfRespondedAdsOnPage);
		return respondedAds;
	}
	
	@Override
	public long getNumberOfPagesForRespondedAds(String email, int numberOfRespondedAdsOnPage) {
		Translator translator = getTranslatorByEmail(email);
		long numberOfRespondedAds = respondedAdDao.getNumberOfRespondedAdsByTranslator(translator);
		long numberOfPages = (long) Math
				.ceil(((double)numberOfRespondedAds)/numberOfRespondedAdsOnPage);
		return numberOfPages;
		
	}
	
	@Override
	public RespondedAd getCurrentOrder(String email) {
		Translator translator = getTranslatorByEmail(email);
		Set<RespondedAd> respondedAds = translator.getRespondedAds();
		Optional<RespondedAd> wrapperRespondedAd = 
				respondedAds.stream()
					.filter(rad -> rad.getStatus().equals(RespondedAdStatus.ACCEPTED)).findFirst();
		if(wrapperRespondedAd.isPresent()){
			return wrapperRespondedAd.get();
		}else return null;
	}
	
	@Override
	public boolean markAsNotChecked(String email, long adId) {
		Ad ad = adDao.get(adId);
		if(ad==null){
			return false;
		}
		if(!ad.getStatus().equals(AdStatus.ACCEPTED)){
			return false;
		}
		if(translatorOwnsAd(email, adId)){
			ad.setStatus(AdStatus.NOTCHECKED);
			return true;
		}
		
		
		return false;
	}
	
	/**
	 * Checks if {@link Translator} {@code translator} has RespondedAd with ACCEPTED status
	 * @param translator - {@code Translator} object, representation of authenticated user with Translator role
	 * @return true - if {@code translator} has RespondedAd with ACCEPTED status, else false
	 */
	private boolean hasAcceptedAd(Translator translator) {
		Set<RespondedAd> respondedAds = translator.getRespondedAds();
		boolean acceptedAdExists = 
				respondedAds.stream()
							.anyMatch(rad -> 
							 	rad.getStatus().equals(RespondedAdStatus.ACCEPTED));
		return acceptedAdExists;
	}
	
	
	
	/**
	 * Check if number of {@link RespondedAd}s of {@code translator} in normal range,
	 * doesn't exceed {@code maxNumberOfSendedRespondedAds}
	 * @param translator - {@link Translator} object, who represents authenticated translator
	 * @return true - if number of {@code RespondedAd}s in normal range, else false
	 */
	private boolean numberRespondedAdsInNormalRange(Translator translator,
													int maxNumberOfSendedRespondedAds){
		Set<RespondedAd> respondedAds = translator.getRespondedAds();
		int numberOfSendedRespondedAds = 
					respondedAds.stream()
					.filter(rad -> rad.getStatus().equals(RespondedAdStatus.SENDED))
					.collect(Collectors.toSet()).size();
		if(numberOfSendedRespondedAds>=maxNumberOfSendedRespondedAds){
			return false;
		}
		return true;
			
	}

	
	
	/**
	 * Checks if {@link Ad} with id={@code adId} belongs to translator with {@code email}
	 * @param email - email of authenticated translator, <b>must</b> be retrieved from Principal object
	 * @param adId - id of {@code Ad}
	 * @return true if translator owns Ad with id={@code adId}, else false
	 */
	private boolean translatorOwnsAd(String email,long adId){
		Translator translator = translatorDao.getTranslatorByEmail(email);
		Set<RespondedAd> respondedAd = translator.getRespondedAds();
		boolean translatorOwns = respondedAd.stream()
										    .map(rad -> rad.getAd())
										    .anyMatch(ad -> 
										    	(new Long(ad.getId())).equals(adId));
		return translatorOwns;
	}

	

	


}
