package org.dream.university.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.dream.university.model.Client;
import org.dream.university.model.User;
import org.dream.university.model.UserRole;
import org.dream.university.service.UserService;
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

@Controller
@RequestMapping("/client")
public class ClientController {

	@Autowired
	@Qualifier("clientService")
	private UserService<Client> clientService;
	
	@Autowired
	private AuthenticationTrustResolver authenticationTrustResolver;
	
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder){
		/*dataBinder.registerCustomEditor(LocalDate.class, new CustomLocalDateEditor());*/
	}
	
	/**
	 * Returns client's profile view
	 * 
	 * @param user
	 * @return client's profile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(Principal user) throws UnsupportedEncodingException{
		Client clientFromDB = clientService.getUserByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/profile");
		model.addObject("client", clientFromDB);
		if(clientFromDB.getAvatar() == null){
			System.out.println("OOPS");
			/**
			 * Поменять!!!
			 */
		}else{
			model.addObject("image", convertImageForRendering(clientFromDB.getAvatar()));
		}
		return model;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
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
	public ModelAndView getRegistrationForm(){
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
	public ModelAndView editPage(Principal user, HttpServletRequest request){
		Client clientFromDB = clientService.getUserByEmail(user.getName());
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/login");
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
	public ModelAndView saveEdits(
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
			editedProfile.addObject("image", convertImageForRendering(client.getAvatar()));
			return editedProfile;
		}
	}
	
	/**
	 * Saves new avatar in DB
	 * 
	 * @param user - {Principal.class} object with name equals to {User.class} object's login
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
					Client updatedClient = clientService.updateAvatar(user.getName(), avatar);
					ModelAndView model = new ModelAndView("/client/profile");
					model.addObject("client", updatedClient);
					model.addObject("image", convertImageForRendering(updatedClient.getAvatar()));
					return model;
				}else{
					Client clientFromDB = clientService.getUserByEmail(user.getName());
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
			Client clientFromDB = clientService.getUserByEmail(user.getName());
			model.addObject("client", clientFromDB);
			return model;
		}
		
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
	
	
}
