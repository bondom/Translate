package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AdDao;
import ua.translate.dao.RespondedAdDao;
import ua.translate.dao.TranslatorDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.EmailStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.NumberExceedsException;
import ua.translate.service.exception.TranslatorDistraction;

@Service
@Transactional(propagation = Propagation.REQUIRED,
			   rollbackFor = {DuplicateEmailException.class,
					   		  NumberExceedsException.class})
public class TranslatorServiceImpl extends TranslatorService {

	@Autowired
	private TranslatorDao translatorDao;
	
	@Autowired
	@Qualifier("adDaoImpl")
	private AdDao adDao;
	
	@Autowired
	private RespondedAdDao respondedAdDao;
	
	/**
	 * It is default number of responded ads on one page
	 */
	private static final int DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE=3;
	
	/**
	 * It is default number of translators on one page
	 */
	private static final int DEFAULT_NUMBER_TRANSLATORS_ON_PAGE=5;
	
	/**
	 * It is default max number of {@link RespondedAd}s with 
	 * {@link RespondedAdStatus#SENDED SENDED} status, which may have 1 translator
	 */
	private static final int DEFAULT_MAX_NUMBER_SENDED_RADS=3;
	
	Logger logger = LoggerFactory.getLogger(TranslatorServiceImpl.class);
	
	@Override
	public Translator getTranslatorByEmail(String email) {
		Translator translator = translatorDao.getTranslatorByEmail(email);
		return translator;
	}

	@Override
	public Translator getTranslatorById(long id) throws InvalidIdentifier {
		Translator translator = translatorDao.get(id);
		if(translator==null){
			throw new InvalidIdentifier();
		}
		return translator;
	}

	@Override
	public Set<Translator> getTranslators(int page,int numberTranslatorsOnPage){
		if(page<1){
			page=1;
		}
		if(numberTranslatorsOnPage<1){
			logger.debug("numberTranslatorsOnPage = {}, default value={} is used",
					numberTranslatorsOnPage,DEFAULT_NUMBER_TRANSLATORS_ON_PAGE);
			numberTranslatorsOnPage = DEFAULT_NUMBER_TRANSLATORS_ON_PAGE;
		}
		return translatorDao.getTranslators(page,numberTranslatorsOnPage);
	}
	
	@Override
	public long getNumberOfPagesForTranslators(int numberTranslatorsOnPage) {
		if(numberTranslatorsOnPage<1){
			logger.debug("numberTranslatorsOnPage = {}, default value={} is used",
					numberTranslatorsOnPage,DEFAULT_NUMBER_TRANSLATORS_ON_PAGE);
			numberTranslatorsOnPage = DEFAULT_NUMBER_TRANSLATORS_ON_PAGE;
		}
		long numberOfTranslators = translatorDao.getNumberOfTranslators();
		long numberOfPages = 
				(long) Math.ceil(((double)numberOfTranslators)/numberTranslatorsOnPage);
		return numberOfPages;
	}
	
	@Override
	public long createAndSaveRespondedAd(
			String email,long adId, int maxNumberOfSendedRespondedAds) 
									throws InvalidIdentifier, 
										   NumberExceedsException, 
										   TranslatorDistraction{
		Ad ad = adDao.get(adId);
		if(ad == null){
			throw new InvalidIdentifier();
		}
		
		Translator translator = translatorDao.getTranslatorByEmail(email);
		
		if(hasAcceptedAd(translator)){
			throw new TranslatorDistraction();
		}
		
		if(maxNumberOfSendedRespondedAds<1){
			logger.debug("maxNumberOfSendedRespondedAds = {}, default value={} is used",
					maxNumberOfSendedRespondedAds,DEFAULT_MAX_NUMBER_SENDED_RADS);
			maxNumberOfSendedRespondedAds = DEFAULT_MAX_NUMBER_SENDED_RADS;
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
										    int numberOfRespondedAdsOnPage) {
		if(page<1){
			page=1;
		}
		
		if(numberOfRespondedAdsOnPage<1){
			logger.debug("numberOfRespondedAdsOnPage = {}, default value={} is used",
					numberOfRespondedAdsOnPage,DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE);
			numberOfRespondedAdsOnPage = DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE;
		}
		Translator translator = getTranslatorByEmail(email);
		Set<RespondedAd> respondedAds = respondedAdDao
				.getRespondedAdsByTranslator(translator, page, numberOfRespondedAdsOnPage);
		return respondedAds;
	}
	
	@Override
	public long getNumberOfPagesForRespondedAds(String email, int numberOfRespondedAdsOnPage) {
		if(numberOfRespondedAdsOnPage<1){
			logger.debug("numberOfRespondedAdsOnPage = {}, default value={} is used",
					numberOfRespondedAdsOnPage,DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE);
			numberOfRespondedAdsOnPage = DEFAULT_NUMBER_RESPONDED_ADS_ON_PAGE;
		}
		Translator translator = getTranslatorByEmail(email);
		long numberOfRespondedAds = respondedAdDao.getNumberOfRespondedAdsByTranslator(translator);
		long numberOfPages = (long) Math
				.ceil(((double)numberOfRespondedAds)/numberOfRespondedAdsOnPage);
		return numberOfPages;
		
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

}
