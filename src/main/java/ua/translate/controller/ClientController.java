package ua.translate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import freemarker.ext.servlet.FreemarkerServlet;
import ua.translate.model.ChangeEmailBean;
import ua.translate.model.Client;
import ua.translate.model.UserStatus;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Language;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.ad.TranslateType;
import ua.translate.service.AdServiceImpl;
import ua.translate.service.ClientServiceImpl;
import ua.translate.service.EmailService;
import ua.translate.service.ResponsedAdServiceImpl;
import ua.translate.service.UserService;

/**
 * Receives all requests, related to client
 * 
 * @author Yuriy Phediv
 *
 */


@Controller
@RequestMapping("/client")
public class ClientController extends UserController{

	
	Logger logger = Logger.getLogger(ClientController.class.getName());
	
	/*@Autowired
	AuthenticationManager authenticationManager;*/
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	@Qualifier("clientService")
	private UserService<Client> clientService;
	
	@Autowired
	@Qualifier("adService")
	private AdServiceImpl adService;
	
	@Autowired
	@Qualifier("responsedAdService")
	private ResponsedAdServiceImpl responsedAdService;
	
	/**
	 * Returns {@link ModelAndView} client's profile
	 *  
	 * <p>Converts avatar to String representation,
	 * <p>adds that representation for rendering avatar,
	 * <p>adds {@link Client} for rendering client's info on page
	 * 
	 * @param user - principal, from whom we get {@code Client}
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(Principal user) throws UnsupportedEncodingException{
		Client clientFromDB = (Client)clientService.getUserByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/profile");
		model.addObject("client", clientFromDB);
		if(clientFromDB.getAvatar() != null){
			model.addObject("image", convertAvaForRendering(clientFromDB.getAvatar()));
		}
		return model;
	}
	
	/**
	 * Returns {@link ModelAndView} client's login form
	 * 
	 * <p>If client is authenticated, he is redirected to {@link #profile(Principal)}.
	 * 
	 * <p>If <tt>error</tt> exists and session has targetUrl parameter, refreshes value of targetUrl
	 * @see #isCurrentAuthenticationAnonymous()
	 * @see #getRememberMeTargetUrlFromSession(HttpServletRequest)
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginForm(
				@RequestParam(value = "error", required = false) String error,
				HttpServletRequest request){
		logger.info("error=" + error);
		if(!isCurrentAuthenticationAnonymous()){
			return new ModelAndView("redirect:/client/profile");
		}
		ModelAndView model = new ModelAndView("/client/login");
		if(error!=null){
			String targetUrl = getRememberMeTargetUrlFromSession(request);
			logger.info("target url from session = " + targetUrl);
			if(StringUtils.hasText(targetUrl)){	
				model.addObject("targetUrl", targetUrl);
				model.addObject("loginUpdate",true);
			}
		}
		return model;
	}
	
	/**
	 * Returns {@link ModelAndView} client's registration form
	 * and adds {@link Client} for binding fields on page
	 * @return
	 */
	@RequestMapping(value = "/registration",method = RequestMethod.GET)
	public ModelAndView registrationForm(){
		ModelAndView model = new ModelAndView("/client/registration");
		Client client = new Client();
		model.addObject("client", client);
		return model;
	}
	
	/**
	 * <p>Checks {@code BindingResult} on errors, if such exists returns {@link #registrationForm()}
	 * <p>Attempts to register {@code Client}, if client's email is registered in system already, 
	 * method returns registration form with message about existing of such email,
	 * otherwise returns {@link #loginForm(String, String, HttpServletRequest)} with message
	 * about successfull registration.
	 * @param client - {@link Client}, retrieved from registration form and valided by Hibernate Validator
	 */
	@RequestMapping(value = "/registrationConfirm",method = RequestMethod.POST)
	public ModelAndView registration(@Valid @ModelAttribute("client") Client client,
								BindingResult result){
		if(result.hasErrors()){
			return new ModelAndView("/client/registration");
		}
		if(clientService.isEmailUnique(client.getEmail())){
			Long clientId = clientService.registerUser(client);
			ModelAndView loginView = new ModelAndView("/client/login");
			loginView.addObject("msg", 
									"Please, watch in your email box "+ client.getEmail() +" and go to link "
									+ "for confirmation your email");
			emailService.sendConfirmationEmailMessage(client.getEmail(),clientId);
			return loginView;
		}else{
			ModelAndView registrationView = new ModelAndView("/client/registration");
			registrationView.addObject("resultRegistration", 
									"User with the same email "
									+ "is registered in system already");
			return registrationView;
		}
	}
	
	/**
	 * <p>If client doesn't confirmed email yet, 
	 * confirms his and return login page with message
	 * <p>If client have already confirmed email, returns login form
	 */
	@RequestMapping(value = "/confirmation",method = RequestMethod.GET)
	public ModelAndView registrationConfirmation(@RequestParam("uid") long clientId){
		logger.info("Request Param clientId= " + clientId);
		Client client = (Client)clientService.getUserById(clientId);
		logger.info("Get from DB: " + client.getEmail() + ", status: "+client.getStatus());
		if(client.getStatus().equals(UserStatus.NOTCONFIRMED)){
			clientService.confirmEmail(clientId);
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("msg", "You succesfully confirm your email!");
			return model;
		}else{
			ModelAndView model = new ModelAndView("/client/login");
			return model;
		}
		
	}
	/**
	 * Returns {@link ModelAndView} client's page for editing of profile if
	 * client is not authenticated via remember-me authentication
	 * <p> if is, returns login form page for re-entering email and password, 
	 * @return page for editing a user's profile
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editProfile(Principal user, HttpServletRequest request){
		Client clientFromDB = (Client)clientService.getUserByEmail(user.getName());
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			//model.addObject("client", clientFromDB);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/client/edit");
			ChangeEmailBean changeEmailBean = new ChangeEmailBean();
			model.addObject("client", clientFromDB);
			model.addObject("email", clientFromDB.getEmail());
			model.addObject("changeEmailBean",changeEmailBean);
			return model;
		}
	}
	
	/*!!!!Reduce number of strings of this method!!!!*/
	/**
	 * <p>If new email exists or some errors exist,  
	 * returns initial page(for editing user's profile)
	 * 
	 * <p>If no errors exist updates the user and returns user's profile page
	 * @param editedClient - {@code Client} object with changes made by client
	 * @param result - {@code BindingResult} object for checking errors
	 * @param user - {@code Principal} object for retrieving {@code Client} from db
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveProfileEdits(
			@Valid @ModelAttribute("client") Client editedClient,
			BindingResult clientResult,
			@Valid @ModelAttribute("changeEmailBean") ChangeEmailBean changeEmailBean,
			BindingResult changeEmailResult,
			Principal user,
			@RequestParam(value = "changeEmail",defaultValue = "false") String changeEmail,
			HttpServletRequest request) throws UnsupportedEncodingException{
		
		
		final String oldEmail = user.getName();
		if(clientResult.hasErrors()||
				changeEmailResult.hasErrors()){
			clientResult.getAllErrors().forEach(error->{
				logger.info("Client error: "+error.getDefaultMessage());
			});
			changeEmailResult.getAllErrors().forEach(error->{
				logger.info("Change email error: "+error.getDefaultMessage());
			});
			ModelAndView model = new ModelAndView("/client/edit");
			model.addObject("email",oldEmail);
			return model;
		}
		logger.info("Input parameters: \n"+
				"changeEmail:"+changeEmail+
				"\nClient: fN:"+editedClient.getFirstName()+" lN:"+editedClient.getLastName()+
				" bday:"+editedClient.getBirthday()+" \n"+
				" ChangeEmailBean: nE:"+changeEmailBean.getNewEmail()+
				" nEA:"+changeEmailBean.getNewEmailAgain()+
				" pass:"+changeEmailBean.getCurrentPassword());
		if(changeEmail.equals("true")){
			final String newEmail = changeEmailBean.getNewEmail();
			editedClient.setEmail(newEmail);
			Client client = (Client) clientService.getUserByEmail(oldEmail);
			if(!clientService.isPasswordRight(changeEmailBean.getCurrentPassword(), 
											  client.getPassword())){
				ModelAndView model = new ModelAndView("/client/edit");
				model.addObject("email",oldEmail);
				model.addObject("wrongPassword","Password doesn't match to real");
				return model;
			}
			if(clientService.isEmailChanged(oldEmail, newEmail)){
				if(clientService.isEmailUnique(newEmail)){
					Client updatedClient = (Client)clientService.editUserProfile(oldEmail, editedClient, true);
					/*UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(newEmail, updatedClient.getPassword());
					
				    // Authenticate the user
				    Authentication authentication = authenticationManager.authenticate(authRequest);
				    SecurityContext securityContext = SecurityContextHolder.getContext();
				    securityContext.setAuthentication(authentication);

				    // Create a new session and add the security context.
				    HttpSession session = request.getSession(true);
				    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);*/
					ModelAndView editedProfile = new ModelAndView("/client/profile");
					editedProfile.addObject("client", updatedClient);
					if(updatedClient.getAvatar()!=null){
						editedProfile.addObject("image", convertAvaForRendering(updatedClient.getAvatar()));
					}
					return editedProfile;
				}else{
					ModelAndView model = new ModelAndView("/client/edit");
					model.addObject("email", oldEmail);
					model.addObject("emailExists","Such email is registered in system already");
					return model;
				}
			}else{
				Client updatedClient = (Client)clientService.editUserProfile(oldEmail, editedClient, false);
				ModelAndView editedProfile = new ModelAndView("/client/profile");
				editedProfile.addObject("client", updatedClient);
				if(updatedClient.getAvatar()!=null){
					editedProfile.addObject("image", convertAvaForRendering(updatedClient.getAvatar()));
				}
				return editedProfile;
			}
		}
		
		Client updatedClient = (Client)clientService.editUserProfile(oldEmail, editedClient, false);
		ModelAndView editedProfile = new ModelAndView("/client/profile");
		editedProfile.addObject("client", updatedClient);
		if(updatedClient.getAvatar()!=null){
			editedProfile.addObject("image", convertAvaForRendering(updatedClient.getAvatar()));
		}
		return editedProfile;
	}
	
	/**
	 * Saves new avatar in DB if type is acceptable and {@code file} is not empty
	 * 
	 * @param user - {@code Principal} object for retrieving {@code Client} from db
	 * @param file - file chosen to be a avatar
	 * @return {@code ModelAndView} user's profile page if saving was successfull, otherwise page for editing profile 
	 */
	@RequestMapping(value = "/saveAvatar",method = RequestMethod.POST)
	public ModelAndView saveAvatar(Principal user, 
						@RequestParam("file") MultipartFile file){
		if(!file.isEmpty()){
			try {
				String contentType = file.getContentType();
				if(contentType.startsWith("image/")){
					byte[] avatar = file.getBytes();
					Client updatedClient = (Client)clientService.updateAvatar(user.getName(), avatar);
					ModelAndView model = new ModelAndView("/client/profile");
					model.addObject("client", updatedClient);
					model.addObject("image", convertAvaForRendering(updatedClient.getAvatar()));
					return model;
				}else{
					Client clientFromDB = (Client)clientService.getUserByEmail(user.getName());
					ModelAndView model = new ModelAndView("/client/edit");
					model.addObject("client", clientFromDB);
					model.addObject("wrongFile", "Please, choose image.");
					return model;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				/**
				 * Заглушка, поменять!!
				 */
				return new ModelAndView("/client/exception");
			}
		}else{
			ModelAndView model = new ModelAndView("/client/edit");
			Client clientFromDB = (Client)clientService.getUserByEmail(user.getName());
			model.addObject("client", clientFromDB);
			return model;
		}
		
	}
	/**
	 * Returns {@code ModelAndView} page with all client's ads
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/ads",method = RequestMethod.GET)
	public ModelAndView ads(Principal user){
		Client client= (Client)(clientService.getUserByEmail(user.getName()));
		List<Ad> ads = client.getAds();
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
	 * otherwise saves {@code Ad} in db, returns page with message about successful creating and link to new {@code Ad} in Internet
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
	public ModelAndView deleteAd(@RequestParam("adId") long adId){
		adService.deleteById(adId);
		return new ModelAndView("redirect:/client/ads");
	}
	
	/**
	 * Returns {@code ModelAndView} page for editing existed advertisement,
	 * and adds {@code Ad} object and maps of enum types for binding if such ad exists
	 * 
	 * if {@code Ad} with {@code Ad.id=adId} doesn't exist, redirects to {@link #ads(Principal)}
	 * 
	 * @param adId - id of {@code Ad} object
	 * @see ClientController#addMapsToAdView(ModelAndView)
	 */
	@RequestMapping(value = "/ads/edit", method = RequestMethod.GET)
	public ModelAndView editAd(@RequestParam("adId") long adId){
		Ad ad = adService.get(adId);
		if(ad == null){
			return new ModelAndView("redirect:/client/ads");
		}
		ModelAndView model = new ModelAndView("/client/adbuilder");
		model.addObject("ad",ad);
		model = addMapsToAdView(model);
		return model;
	}
	
	/**
	 * Checks {@code BindingResult} for errors, if such exists, 
	 * returns page for editing advertisement(for entering valid data)
	 * 
	 * <p>If data is valid updates old ad and return page for editing with message 
	 * about successful editing
	 * 
	 * @param adId
	 * @param editedAd
	 * @param result
	 * @param user
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/saveAdEdits",method = RequestMethod.POST)
	public ModelAndView saveAdEdits(
			@RequestParam("adId") long adId,
			@Valid @ModelAttribute("ad") Ad editedAd,
			BindingResult result,
			Principal user) throws UnsupportedEncodingException{
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/client/adbuilder");
			model = addMapsToAdView(model);
			editedAd.setId(adId);
			return model;
		}
		editedAd.setId(adId);
		Ad updatedAd = adService.updateAd(editedAd);
		ModelAndView model = new ModelAndView("/client/adbuilder");
		model.addObject("ad", updatedAd);
		model.addObject("Editmsg", "Advertisement is edited successfully");
		model = addMapsToAdView(model);
		return model;
	}
	
	/**
	 * Returns {@code ModelAndView} page with all {@link ResponsedAd}s of this client
	 * @param user - {@code Principal} object for retrieving client from db
	 * @return
	 */
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView responsedAds(Principal user){
		List<ResponsedAd> responsedAds = ((ClientServiceImpl)clientService).getResponsedAds(user.getName());
		ModelAndView model = new ModelAndView("/client/responses");
		model.addObject("responsedAds", responsedAds);
		return model;
	}
	
	/**
	 * If {@code ResponsedAd} with {@code responsedAdId} exists, rejects it,
	 * in any case redirects to {@link #responsedAds(Principal)}
	 */
	@RequestMapping(value = "/reject",method = RequestMethod.GET)
	public ModelAndView rejectResponsedAd(@RequestParam("radId") long responsedAdId,
										Principal user){
		ResponsedAd responsedAd = responsedAdService.get(responsedAdId);
		if(responsedAd == null){
			return new ModelAndView("redirect:/client/responses");
		}
		responsedAdService.reject(responsedAdId);
		return new ModelAndView("redirect:/client/responses");
	}
	
	/*!!!! Must sends message to appropriate translator's email!!!!*/
	/*!!!! Must rejects all ResponsedAds related to Ad!!!!*/
	/**
	 * If {@code ResponsedAd} with {@code responsedAdId} exists, accepts it,
	 * in any case redirects to {@link #responsedAds(Principal)}
	 */
	@RequestMapping(value = "/accept",method = RequestMethod.GET)
	public ModelAndView acceptResponsedAd(@RequestParam("radId") long responsedAdId,
										Principal user){
		ResponsedAd responsedAd = responsedAdService.get(responsedAdId);
		if(responsedAd == null){
			return new ModelAndView("redirect:/client/responses");
		}
		responsedAdService.accept(responsedAd);
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
