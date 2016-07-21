package ua.translate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.model.Client;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Language;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.ad.TranslateType;
import ua.translate.service.AdServiceImpl;
import ua.translate.service.ClientServiceImpl;
import ua.translate.service.UserService;

@Controller
@RequestMapping("/client")
public class ClientController {

	@Autowired
	@Qualifier("clientService")
	private UserService<Client> clientService;
	
	@Autowired
	@Qualifier("adService")
	private AdServiceImpl adService;
	
	@Autowired
	private AuthenticationTrustResolver authenticationTrustResolver;
	
	/**
	 * Returns client's profile view
	 * 
	 * @param user
	 * @return client's profile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(Principal user) throws UnsupportedEncodingException{
		Client clientFromDB = (Client)clientService.getUserByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/profile");
		model.addObject("client", clientFromDB);
		if(clientFromDB.getAvatar() != null){
			model.addObject("image", convertImageForRendering(clientFromDB.getAvatar()));
		}
		return model;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginForm(
				@RequestParam(value = "error", required = false) String error,
				@RequestParam(value = "logout", required = false) String logout, 
				HttpServletRequest request){
		if(!isCurrentAuthenticationAnonymous()){
			return new ModelAndView("redirect:/client/profile");
		}
		if(error!=null){
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("error","Invalid username or pass");
		
			//if login error, add targetUrl again
			String targetUrl = getRememberMeTargetUrlFromSession(request);
			System.out.println(targetUrl);
			if(StringUtils.hasText(targetUrl)){
				model.addObject("targetUrl", targetUrl);
				model.addObject("loginUpdate",true);
			}
			return model;
		}
		
		if(logout!=null){
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("logout", "You have been logged out successfully");
			return model;
		}
		return new ModelAndView("/client/login");
	}
	
	@RequestMapping(value = "/registration",method = RequestMethod.GET)
	public ModelAndView registrationForm(){
		ModelAndView model = new ModelAndView("/client/registration");
		Client client = new Client();
		model.addObject("client", client);
		return model;
	}
	
	@RequestMapping(value = "/registrationConfirm",method = RequestMethod.POST)
	public ModelAndView registration(@Valid @ModelAttribute("client") Client client,
								BindingResult result){
		if(result.hasErrors()){
			return new ModelAndView("/client/registration");
		}
		if(clientService.registerUser(client)){
			ModelAndView loginView = new ModelAndView("/client/login");
			loginView.addObject("resultRegistration", 
									"Success registration!");
			return loginView;
		}else{
			//Registration failed
			ModelAndView registrationView = new ModelAndView("/client/registration");
			registrationView.addObject("resultRegistration", 
									"User with the same login or email "
									+ "is registered in system already");
			return registrationView;
		}
	}
	/**
	 * @param user
	 * @return page for editing a user's profile
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editProfile(Principal user, HttpServletRequest request){
		Client clientFromDB = (Client)clientService.getUserByEmail(user.getName());
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			model.addObject("client", clientFromDB);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/client/edit");
			model.addObject("client", clientFromDB);
			return model;
		}
	}
	
	/**
	 * Checks if new email exist and
	 * if some errors exist returns initial page(for editing user's profile)
	 * 
	 * If no errors exist updates the user and returns user's profile page
	 * @param newUser - {User.class} object with changes made by user
	 * @param result
	 * @param user - {Principal.class} object with name equals to {User.class} object's login
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveProfileEdits(
			@Valid @ModelAttribute("client") Client editedClient,
			BindingResult result,
			Principal user) throws UnsupportedEncodingException{
		if(result.hasErrors()){
			return new ModelAndView("/client/edit");
		}
		Client client = clientService.editUserProfile(user.getName(), editedClient);
		if(client == null){
			ModelAndView model = new ModelAndView("/client/edit");
			model.addObject("emailExists","Such email is registered in system already");
			return model;
		}else{
			ModelAndView editedProfile = new ModelAndView("/client/profile");
			editedProfile.addObject("client", client);
			if(client.getAvatar()!=null){
				editedProfile.addObject("image", convertImageForRendering(client.getAvatar()));
			}
			return editedProfile;
		}
	}
	
	/**
	 * Saves new avatar in DB
	 * 
	 * @param user - {Principal.class} object with name equals to {User.class} object's email
	 * @param file - file chosen to be a avatar
	 * @return user's profile page or page for editing profile if no file is chosen
	 * or file is not image
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
					model.addObject("image", convertImageForRendering(updatedClient.getAvatar()));
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
	
	@RequestMapping(value = "/adbuilder",method = RequestMethod.GET)
	public ModelAndView adbuilder(Principal user){
		ModelAndView model = new ModelAndView("/client/adbuilder");
		
		Ad ad = new Ad();
		model.addObject("ad",ad);
		model.addObject("createAd", true);
		ModelAndView fullModel = addMapsToAdView(model);
		return fullModel;
	}
	
	@RequestMapping(value = "/saveAd", method = RequestMethod.POST)
	public ModelAndView saveAd(@Valid @ModelAttribute("ad") Ad ad,
								BindingResult result,
								Principal user){
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/client/adbuilder");
			model = addMapsToAdView(model);
			return model;
		}
		//Client client= (Client)(clientService.getUserByEmail(user.getName()));
		//Ad savedAd = ((ClientServiceImpl)clientService).saveAd(user.getName(), ad);
		Long adId = adService.saveAd(ad, user.getName());
		ModelAndView model = new ModelAndView("/client/createdAd");
		model.addObject("adId", adId);
		return model;
	}
	
	@RequestMapping(value = "/ads/delete", method = RequestMethod.GET)
	public ModelAndView deleteAd(@RequestParam("adId") long adId){
		adService.deleteById(adId);
		return new ModelAndView("redirect:/client/ads");
	}
	
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
	
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView responsedAds(Principal user){
		List<ResponsedAd> responsedAds = ((ClientServiceImpl)clientService).getResponsedAds(user.getName());
		ModelAndView model = new ModelAndView("/client/responses");
		model.addObject("responsedAds", responsedAds);
		return model;
	}
	
	private String convertImageForRendering(byte[] image) throws UnsupportedEncodingException{
		byte[] encodeBase64 = Base64.encodeBase64(image); 
		String base64Encoded = new String(encodeBase64,"UTF-8");
		return base64Encoded;
	}
	
	
	private boolean isRememberMeAuthenticated() {
		Authentication authentication = 
                    SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}
		return RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
	}
	
	private void setRememberMeTargetUrlToSession(User user, HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session!=null){
			UserRole role= user.getRole();
			if(role.equals(UserRole.ROLE_CLIENT)){
				session.setAttribute("targetUrl", "/client/edit");
			}	
		}
	}
	
	private String getRememberMeTargetUrlFromSession(HttpServletRequest request){
		String targetUrl = "";
		HttpSession session = request.getSession(false);
		if(session!=null){
			targetUrl = session.getAttribute("targetUrl")==null?""
                             :session.getAttribute("targetUrl").toString();
			System.out.println("targetUrl = " + targetUrl);
		}
		return targetUrl;
	}
	
	/**
     * This method returns true if users is already authenticated [logged-in], else false.
     */
    private boolean isCurrentAuthenticationAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationTrustResolver.isAnonymous(authentication);
    }
	
    private ModelAndView addMapsToAdView(ModelAndView model){
    	model.addObject("languages", getLanguagesForSelect());
		model.addObject("translateTypes", getTranslateTypesForSelect());
		model.addObject("currencies", getCurrenciesForSelect());
		return model;
    }
    private Map<String,String> getLanguagesForSelect(){
		Language[] languages = Language.values();
		Map<String, String> languagesMap = new HashMap<String, String>();
		for(Language language:languages){
			languagesMap.put(language.name(), language.name() + " language");
		}
		return languagesMap;
    }
    private Map<String,String> getTranslateTypesForSelect(){
		TranslateType[] types = TranslateType.values();
		Map<String, String> typesMap = new HashMap<String, String>();
		for(TranslateType type:types){
			typesMap.put(type.name(), type.name() + " type");
		}
		return typesMap;
	}
    
    private Map<String,String> getCurrenciesForSelect(){
    	Currency[] currencies = Currency.values();
		Map<String,String> currenciesMap = new HashMap<>();
		for(Currency currency: currencies){
			currenciesMap.put(currency.name(),"(" +currency.name()+")");
		}
		return currenciesMap;
    }
} 
