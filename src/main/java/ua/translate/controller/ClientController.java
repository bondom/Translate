package ua.translate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.controller.support.ControllerHelper;
import ua.translate.controller.support.ResponsedAdComparatorByDate;
import ua.translate.logging.dao.AbstractUserDaoAspect;
import ua.translate.model.Client;
import ua.translate.model.Language;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.ad.TranslateType;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.service.AdService;
import ua.translate.service.ClientService;
import ua.translate.service.ResponsedAdService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.NonExistedResponsedAdException;
import ua.translate.service.exception.UnacceptableActionForAcceptedAd;
import ua.translate.service.exception.WrongPageNumber;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.NonExistedAdException;

@Controller
@RequestMapping("/client")
public class ClientController extends UserController{

	
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private AdService adService;
	
	@Autowired
	private ResponsedAdService responsedAdService;
	
	@Autowired
	ControllerHelper controllerHelper;
	
	private static final int RESPONSED_ADS_ON_PAGE=3;
	
	/**
	 * Returns {@link ModelAndView} client's profile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(Principal user,
					@RequestParam(value="psuccess",required = false) String psuccess) throws UnsupportedEncodingException{
		Client clientFromDB = clientService.getClientByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/profile");
		model.addObject("client", clientFromDB);
		model.addObject("image", controllerHelper.getAvaForRendering(clientFromDB.getAvatar()));
		if(psuccess!=null){
			model.addObject("passSaved","Your password is saved successfully");
		}
		return model;
	}
	
	/**
	 * Returns {@link ModelAndView} client's login form
	 * 
	 * <p>If client is authenticated, he is redirected to {@link #profile(Principal)}.
	 * 
	 * <p>If <tt>error</tt> exists and session has targetUrl parameter, 
	 * refreshes value of targetUrl in returned {@code ModelAndView} object
	 * @see #isCurrentAuthenticationAnonymous()	
	 * @see #getRememberMeTargetUrlFromSession(HttpServletRequest)
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginForm(
				@RequestParam(value = "error", required = false) String error,
				HttpServletRequest request){
		if(!isCurrentAuthenticationAnonymous()){
			return new ModelAndView("redirect:/client/profile");
		}
		ModelAndView model = new ModelAndView("/client/login");
		if(error!=null){
			String targetUrl = getRememberMeTargetUrlFromSession(request);
			if(StringUtils.hasText(targetUrl)){	
				model.addObject("targetUrl", targetUrl);
				model.addObject("loginUpdate",true);
			}
		}
		return model;
	}
	
	/**
	 * Returns {@link ModelAndView} client's registration form
	 * with new {@link Client} object for binding fields on page
	 */
	@RequestMapping(value = "/registration",method = RequestMethod.GET)
	public ModelAndView registrationForm(){
		ModelAndView model = new ModelAndView("/client/registration");
		Client client = new Client();
		model.addObject("client", client);
		return model;
	}
	
	/**
	 * Checks {@code BindingResult} on errors, if such exists returns {@link #registrationForm()}
	 * <p>Attempts to register {@code Client}, if email is registered already returns {@code registrationForm()} with
	 * user-friendly message
	 * @param client - {@link Client}, retrieved from registration form and valided by Hibernate Validator
	 */
	@RequestMapping(value = "/registrationConfirm",method = RequestMethod.POST)
	public ModelAndView registration(@Valid @ModelAttribute("client") Client client,
								BindingResult result){
		if(result.hasErrors()){
			return new ModelAndView("/client/registration");
		}
		try{
			clientService.registerUser(client);
		}catch (DuplicateEmailException e) {
			ModelAndView registrationView = new ModelAndView("/client/registration");
			registrationView.addObject("error","This email is registered in system already");
			return registrationView;
		}
		ModelAndView loginView = new ModelAndView("/client/login");
		loginView.addObject("msg", 
								"You successfully registered!");
		return loginView;
	}
	
	/**
	 * Saves confirmation url in data storage,
	 * returns {@code ModelAndView} with info about sending letter for confirmation
	 */
	@RequestMapping(value = "/email-confirm",method = RequestMethod.GET)
	public ModelAndView saveConfirmationEmail(Principal user){
		
		try {
			clientService.saveConfirmationUrl(user.getName());
		} catch (EmailIsConfirmedException e) {
			ModelAndView model = new ModelAndView("/client/sendedLetter");
			model.addObject("error",true);
			return model;
		}
		
		ModelAndView model = new ModelAndView("/client/sendedLetter");
		return model;
	}
	
	/**
	 * Attempts to confirm client's email, and returns page with info about confirmation
	 */
	@RequestMapping(value = "/confirmation",method = RequestMethod.GET)
	public ModelAndView emailConfirmation(@RequestParam("ecu") String confirmationUrl){
		
		String email="";
		try {
			email = clientService.confirmUserEmail(confirmationUrl);
		} catch (InvalidConfirmationUrl e) {
			ModelAndView model = new ModelAndView("/client/emailConfirmed");
			return model;
		}
		
		ModelAndView model = new ModelAndView("/client/emailConfirmed");
		model.addObject("email",email);
		model.addObject("success", true);
		return model;
		
	}
	
	/**
	 * Returns {@code ModelAndView} page for editing email, if
	 * client is not authenticated via remember-me authentication
	 * <p>if is, return login form page for re-entering credentials
	 */
	@RequestMapping(value = "/email",method = RequestMethod.GET)
	public ModelAndView editEmail(Principal user){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/client/editEmail");
			ChangeEmailBean changeEmailBean = new ChangeEmailBean();
			model.addObject("changeEmailBean",changeEmailBean);
			return model;
		}
	}
	
	/**
	 * Returns {@code ModelAndView} page for editing password, if
	 * client is not authenticated via remember-me authentication
	 * <p>if is, return login form page for re-entering credentials
	 */
	@RequestMapping(value = "/password",method = RequestMethod.GET)
	public ModelAndView editPassword(Principal user){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/client/editPassword");
			ChangePasswordBean changePasswordBean = new ChangePasswordBean();
			model.addObject("changePasswordBean",changePasswordBean);
			return model;
		}
	}
	
	/**
	 * Returns {@link ModelAndView} client's page for editing profile 
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editProfile(Principal user, HttpServletRequest request){
		Client clientFromDB = clientService.getClientByEmail(user.getName());
	
		ModelAndView model = new ModelAndView("/client/edit");
		model.addObject("client", clientFromDB);
		return model;
	}
	
	/**
	 * Attempts to update client's email, if email is registered already or password is invalid,
	 * returns page with appropriate error messages
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEmail",method = RequestMethod.POST)
	public ModelAndView saveEmail(
			@Valid @ModelAttribute("changeEmailBean") ChangeEmailBean changeEmailBean,
			BindingResult changeEmailResult,
			Principal user,
			HttpServletRequest request) throws UnsupportedEncodingException{
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changeEmailResult.hasErrors()){
			ModelAndView model = new ModelAndView("/client/editEmail");
			return model;
		}
		try {
			clientService.updateUserEmail(user.getName(), 
										  changeEmailBean.getNewEmail(), 
										  changeEmailBean.getCurrentPassword());
		}catch (InvalidPasswordException e) {
			ModelAndView model = new ModelAndView("/client/editEmail");
			model.addObject("invalidPassword",e.getMessage());
			return model;
		}catch (DuplicateEmailException e) {
			ModelAndView model = new ModelAndView("/client/editEmail");
			model.addObject("duplicateEmail","This email is registered in system already");
			return model;
		}
		
		refreshUsername(changeEmailBean.getNewEmail());
		Client clientFromDB = clientService.getClientByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/profile");
		model.addObject("client", clientFromDB);
		model.addObject("emailSaved","Your email is saved successfully");
		model.addObject("image", controllerHelper.getAvaForRendering(clientFromDB.getAvatar()));
		return model;
	}
	/**
     * Attempts to update client's password, if password is invalid,
	 * returns page with appropriate error message
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/savePassword",method = RequestMethod.POST)
	public ModelAndView savePassword(
			@Valid @ModelAttribute("changePasswordBean") ChangePasswordBean changePasswordBean,
			BindingResult changePasswordResult,
			Principal user,
			HttpServletRequest request) throws UnsupportedEncodingException{
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changePasswordResult.hasErrors()){
			ModelAndView model = new ModelAndView("/client/editPassword");
			return model;
		}
		try {
			clientService.updateUserPassword(user.getName(), 
											 changePasswordBean.getOldPassword(), 
											 changePasswordBean.getNewPassword());
		} catch (InvalidPasswordException e) {
			ModelAndView model = new ModelAndView("/client/editPassword");
			model.addObject("wrongOldPassword",e.getMessage());
			return model;
		}
		
		ModelAndView model = new ModelAndView("redirect:/client/profile?psuccess");
		/*Client clientFromDB = clientService.getClientByEmail(user.getName());
		model.addObject("client", clientFromDB);
		model.addObject("passSaved","Your password is saved successfully");
		if(clientFromDB.getAvatar() != null){
			model.addObject("image", convertAvaForRendering(clientFromDB.getAvatar()));
		}*/
		return model;
	}
	
	/**
	 * <p>If no errors exist updates the user's profile and redirects to {@link #profile(Principal)}
	 * @param editedClient - {@code Client} object with changes made by client
	 * @param result - {@code BindingResult} object for checking errors
	 * @param user - {@code Principal} object for retrieving {@code Client} from db
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveProfileEdits(
							@Valid @ModelAttribute("client") Client editedClient,
							BindingResult result,
							Principal user) throws UnsupportedEncodingException{
		
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/client/edit");
			return model;
		}
		
		clientService.updateUserProfile(user.getName(), editedClient);
		
		Client clientFromDB = clientService.getClientByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/profile");
		model.addObject("client", clientFromDB);
		model.addObject("profileSaved","Your profile is saved successfully");
		model.addObject("image", controllerHelper.getAvaForRendering(clientFromDB.getAvatar()));
		return model;
	}
	
	/**
	 * Saves new avatar in DB if type is acceptable and {@code file} is not empty
	 * 
	 * @param user - {@code Principal} object for retrieving {@code Client} from db
	 * @param file - file chosen to be a avatar
	 * @return {@code ModelAndView} user's profile page if saving was successfull, otherwise page for editing profile 
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveAvatar",method = RequestMethod.POST)
	public ModelAndView saveAvatar(Principal user, 
						@RequestParam("file") MultipartFile file) throws UnsupportedEncodingException{
		if(!file.isEmpty()){
			Client clientFromDB = clientService.getClientByEmail(user.getName());
			try {
				String contentType = file.getContentType();
				if(contentType.startsWith("image/")){
					byte[] avatar = file.getBytes();
					clientService.updateAvatar(user.getName(), avatar);
				}
				ModelAndView model = new ModelAndView("redirect:/client/profile");
				return model;
			} catch (IOException e) {
				e.printStackTrace();
				ModelAndView model = new ModelAndView("/client/edit");
				model.addObject("client", clientFromDB);
				model.addObject("image", controllerHelper.getAvaForRendering(clientFromDB.getAvatar()));
				model.addObject("error", "Some problem with your avatar, please repeat action");
				return model;
			}
		}
		ModelAndView model = new ModelAndView("redirect:/client/profile");
		return model;
	}
	/**
	 * Returns {@code ModelAndView} page with all client's ads
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/ads",method = RequestMethod.GET)
	public ModelAndView ads(Principal user){
		Set<Ad> ads = clientService.getAds(user.getName());
		ModelAndView model = new ModelAndView("/client/ads");
		if(!ads.isEmpty()){
			model.addObject("ads", ads);
		}
		return model;
	}
	
	/**
	 * Returns {@code ModelAndView} page for creating advertisement.
	 * Adds {@link Ad} object for binding and adds flag which shows that
	 * client wants to create advertisement
	 * <p>Adds {@code Map} objects for rendering available languages,currencies and translate types
	 * @param user - {@code Principal} object for retrieving client from db
	 * @see #addMapsToAdView(ModelAndView)
	 */
	@RequestMapping(value = "/adbuilder",method = RequestMethod.GET)
	public ModelAndView adbuilder(Principal user){
		ModelAndView model = new ModelAndView("/client/adbuilder");
		Ad ad = new Ad();
		model.addObject("ad",ad);
		model.addObject("createAd", true);
		model = addMapsToAdView(model);
		return model;
	}
	
	/**
	 * If some errors exist, returns {@code ModelAndView} page for creating advertisement again,
	 * otherwise saves {@code Ad} in db, returns {@code ModelAndView} page
	 * @param ad - {@code Ad} object with inputted parameters
	 * @param result - {@code BindingResult} for checking errors
	 * @param user - {@code Principal} object for retrieving client from db
	 * @see #addMapsToAdView(ModelAndView)
	 */
	@RequestMapping(value = "/saveAd", method = RequestMethod.POST)
	public ModelAndView saveAd(@Valid @ModelAttribute("ad") Ad ad,
								BindingResult result,
								Principal user){
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/client/adbuilder");
			model = addMapsToAdView(model);
			return model;
		}
		Long adId = adService.saveAd(ad, user.getName());
		ModelAndView model = new ModelAndView("/client/createdAd");
		model.addObject("adId", adId);
		return model;
	}
	
	/**
	 * Deletes {@code Ad}, if such exists, and redirects to {@link #ads(Principal)}
	 * 
	 * @param adId - id of {@code Ad} object
	 */
	@RequestMapping(value = "/ads/delete", method = RequestMethod.GET)
	public ModelAndView deleteAd(@RequestParam("adId") long adId,Principal user){
		ModelAndView model = new ModelAndView("/client/ads");
		
		try {
			adService.deleteById(adId);
			model.addObject("msg", "Adversiment successfully deleted");
		} catch (NonExistedAdException e) {
			model.addObject("error", "You can't delete non existed ad");
		} catch (UnacceptableActionForAcceptedAd e) {
			model.addObject("error", "Sorry, but you can't delete advertisement, "
					+ "which has Accepted status");
		}
		
		Set<Ad> ads = clientService.getAds(user.getName());
		if(!ads.isEmpty()){
			model.addObject("ads", ads);
		}
		
		return model;
	}
	
	/**
	 * Returns {@code ModelAndView} page for editing existed advertisement,
	 * and adds {@code Ad} object and maps of enum types for binding if such ad exists
	 * 
	 * <p>if {@code Ad} with {@code Ad.id=adId} doesn't exist, redirects to {@link #ads(Principal)}
	 * 
	 * @param adId - id of {@code Ad} object
	 * @see ClientController#addMapsToAdView(ModelAndView)
	 */
	@RequestMapping(value = "/ads/edit", method = RequestMethod.GET)
	public ModelAndView editAd(@RequestParam("adId") long adId,Principal user){
		
		ModelAndView errorView= new ModelAndView("/client/ads");
		
		try {
			Ad ad = adService.getForUpdating(user.getName(),adId);
			ModelAndView model = new ModelAndView("/client/adbuilder");
			model.addObject("ad",ad);
			model = addMapsToAdView(model);
			return model;
		} catch (NonExistedAdException e) {
			errorView.addObject("error","You can't edit non existed advertisement");
		} catch (UnacceptableActionForAcceptedAd e) {
			errorView.addObject("error","You can't edit advertisement, which has Accepted status");
		} catch (IllegalActionForAd e) {
			errorView.addObject("error","For editing advertisement, you must reject responses to it");
		}
			
		Set<Ad> ads = clientService.getAds(user.getName());
		if(!ads.isEmpty()){
			errorView.addObject("ads", ads);
		}
		return errorView;
		
	}
	
	/**
	 * Checks {@code BindingResult} for errors, if such exists, 
	 * returns page for editing advertisement(for entering valid data)
	 * 
	 * <p>If data is valid updates old ad and returns page for editing with message 
	 * about successful editing
	 * 
	 * @param adId
	 * @param editedAd
	 * @param result
	 * @param user
	 */
	@RequestMapping(value = "/saveAdEdits",method = RequestMethod.POST)
	public ModelAndView saveAdEdits(
							@Valid @ModelAttribute("ad") Ad editedAd,
							BindingResult result,
							Principal user){
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/client/adbuilder");
			model = addMapsToAdView(model);
			return model;
		}
		ModelAndView errorView= new ModelAndView("/client/ads");
		
		try {
			Ad updatedAd = adService.updateAd(editedAd.getId(),editedAd);
			ModelAndView model = new ModelAndView("/client/adbuilder");
			model.addObject("ad", updatedAd);
			model.addObject("Editmsg", "Advertisement is edited successfully");
			model = addMapsToAdView(model);
			return model;
		} catch (NonExistedAdException e) {
			errorView.addObject("error","You can't edit non existed advertisement");
		} catch (UnacceptableActionForAcceptedAd e) {
			errorView.addObject("error","You can't edit advertisement, which has Accepted status");
		} catch (IllegalActionForAd e) {
			errorView.addObject("error","For editing advertisement, you must reject responses to it");
		}
			
		Set<Ad> ads = clientService.getAds(user.getName());
		if(!ads.isEmpty()){
			errorView.addObject("ads", ads);
		}
		return errorView;
		
	}
	
	/**
	 * Returns {@code ModelAndView} page with {@link ResponsedAd}s of this client
	 * in order from latest to earliest
	 * @param user - {@code Principal} object for retrieving client from db
	 * @return
	 */
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView responsedAds(Principal user,
									 @RequestParam(name="page",defaultValue="1",required = false) int page){
		
		Set<ResponsedAd> responsedAds = null;
		try {
			responsedAds = clientService
					.getResponsedAds(user.getName(),page,RESPONSED_ADS_ON_PAGE);
		} catch (WrongPageNumber e) {
			try {
				responsedAds = clientService
						.getResponsedAds(user.getName(),1,RESPONSED_ADS_ON_PAGE);
			} catch (WrongPageNumber unused) {}
		}
		
		long nunmberOfPages = clientService
				.getNumberOfPagesForResponsedAds(user.getName(), RESPONSED_ADS_ON_PAGE);
		ModelAndView model = new ModelAndView("/client/responses");
		model.addObject("responsedAds", responsedAds);
		model.addObject("numberOfPages",nunmberOfPages);
		return model;
	}
	
	/**
	 * If {@code ResponsedAd} with {@code responsedAdId} exists, rejects it,
	 * in any case redirects to {@link #responsedAds(Principal)}
	 */
	@RequestMapping(value = "/reject",method = RequestMethod.GET)
	public ModelAndView rejectResponsedAd(@RequestParam("radId") long responsedAdId){
		try {
			responsedAdService.reject(responsedAdId);
		} catch (NonExistedResponsedAdException e) {
			return new ModelAndView("redirect:/client/responses");
		}
		return new ModelAndView("redirect:/client/responses");
	}
	
	/*!!!! Must sends message to appropriate translator's email!!!!*/
	/**
	 * If {@code ResponsedAd} with {@code responsedAdId} exists, accepts it,
	 * in any case redirects to {@link #responsedAds(Principal)}
	 */
	@RequestMapping(value = "/accept",method = RequestMethod.GET)
	public ModelAndView acceptResponsedAd(@RequestParam("radId") long responsedAdId,
										Principal user){
		
		try {
			responsedAdService.accept(responsedAdId);
		} catch (NonExistedResponsedAdException e) {
			return new ModelAndView("redirect:/client/responses");
		}
		return new ModelAndView("redirect:/client/responses");
	}
	
	/**
	 * Adds maps of {@link Language}, {@link TranslateType}, {@link Currency}
	 * to model for rendering available variants to the client
	 * @param model - initial {@code ModelAndView} object
	 * @return {@code ModelAndView} object with added maps
	 * @see #getCurrenciesForSelect()
	 * @see #getLanguagesForSelect()
	 * @see #getTranslateTypesForSelect()
	 */
  private ModelAndView addMapsToAdView(ModelAndView model){
  	model.addObject("languages", getLanguagesForSelect());
		model.addObject("translateTypes", getTranslateTypesForSelect());
		model.addObject("currencies", getCurrenciesForSelect());
		return model;
  }
  
  
  /**
   * Returns {@code Map}, where keys - {@link Language} values,
   * objects - user-friendly names of languages
   */
  private Map<String,String> getLanguagesForSelect(){
		Language[] languages = Language.values();
		Map<String, String> languagesMap = new HashMap<String, String>();
		for(Language language:languages){
			//creating user-friendly name of language and adding to map
			String renderedLanguage = language.name();
			renderedLanguage = renderedLanguage.substring(0, 1) + 
					renderedLanguage.substring(1, renderedLanguage.length()).toLowerCase();
			languagesMap.put(language.name(), renderedLanguage);
		}
		return languagesMap;
  }
  
  
  /**
   * Returns {@code Map}, where keys - {@link TranslateType} values,
   * objects - user-friendly names of translate types
   */
  private Map<String,String> getTranslateTypesForSelect(){
		TranslateType[] types = TranslateType.values();
		Map<String, String> typesMap = new HashMap<String, String>();
		for(TranslateType type:types){
			String renderedTranslateType = type.name();
			renderedTranslateType = renderedTranslateType.substring(0, 1) + 
					renderedTranslateType.substring(1, renderedTranslateType.length()).toLowerCase();
			typesMap.put(type.name(), renderedTranslateType);
		}
		return typesMap;
	}
  
  /**
   * Returns {@code Map}, where keys - {@link Currency} values,
   * objects - user-friendly names of currencies
   */
  private Map<String,String> getCurrenciesForSelect(){
  	Currency[] currencies = Currency.values();
		Map<String,String> currenciesMap = new HashMap<>();
		for(Currency currency: currencies){
			currenciesMap.put(currency.name(),"(" +currency.name()+")");
		}
		return currenciesMap;
  }
} 