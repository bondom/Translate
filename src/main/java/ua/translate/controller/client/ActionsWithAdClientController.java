package ua.translate.controller.client;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.UserController;
import ua.translate.model.Language;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.settings.Settings;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdWithStatusMessageView;
import ua.translate.model.viewbean.ChooseTranslateTypeBean;
import ua.translate.service.AbstractAdService;
import ua.translate.service.AdStatusMessageService;
import ua.translate.service.ArchievedAdService;
import ua.translate.service.ClientService;
import ua.translate.service.OralAdService;
import ua.translate.service.SettingsService;
import ua.translate.service.WrittenAdService;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TooManyAds;
import ua.translate.service.exception.TooManyRefreshings;
import ua.translate.service.exception.WrongPageNumber;


/**
 * Contains handler methods for executing actions with {@link Ad},
 * such as creating, editing,saving,deleting, refreshing of
 * {@link Ad#getPublicationDateTime() Ad.publicationDateTime}
 * 
 * Contains handler methods for rendering {@link Ad}s and
 * {@link RespondedAd}s of client. 
 * @author Yuriy Phediv
 *
 */
@Controller
@RequestMapping("/client")
public class ActionsWithAdClientController extends UserController{
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private AdStatusMessageService adStatusMessageService;
	
	@Autowired
	private SettingsService settingsService;
	
	@Autowired
	private WrittenAdService writtenAdService;
	
	@Autowired
	private OralAdService oralAdService;
	
	@Autowired
	private ArchievedAdService archievedAdService;
	
	@Autowired
	@Qualifier("defaultImpl")
	private AbstractAdService adService;
	
	Logger logger = LoggerFactory.getLogger(ActionsWithAdClientController.class);
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	/**
	 * Returns {@code ModelAndView} page with all client's ads
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/ads",method = RequestMethod.GET)
	public ModelAndView ads(Principal user){
		final Set<Ad> ads = clientService.getAds(user.getName());
		ads.forEach(ad ->{
			if(AdStatus.PAYED.equals(ad.getStatus()) &&
			   ad.getTranslator()==null){
				//if translator have archieved Ad(deletes
				//relationships with it), but client must see link to
				//translator's profile 
				ArchievedAd archievedAd;
				try {
					archievedAd = archievedAdService.getArchievedAdByAdId(ad.getId());
					Translator translator = archievedAd.getTranslator();
					if(translator!=null){
						ad.setTranslator(archievedAd.getTranslator());
					}else{
						logger.error("Ad with id={} have PAYED status and translator is null,"
								+ " but ArchievedAd, related to this Ad, has translator null as well.",ad.getId());
					}
				} catch (InvalidIdentifier e) {
					logger.error("Ad with id={} have PAYED status and translator is null,"
							+ " but ArchievedAd, related to this Ad, doesn't exist in data storage.",ad.getId());
				}
					
			}
		});
		final ModelAndView model = new ModelAndView("/client/ads");
		final Set<AdWithStatusMessageView> adsWithStatusMessage = new LinkedHashSet<>();
		if(!ads.isEmpty()){
			ads.forEach(ad ->{
				AdStatus adStatus = ad.getStatus();
				TranslateType translateType = ad.getTranslateType();
				AdStatusMessage adStatusMessage  = adStatusMessageService
							.getAdStatusMessageByAdStatusAndTranslateType(adStatus, translateType);
				
				String statusMessage = adStatusMessage.getMessageForClient();
				adsWithStatusMessage.add(new AdWithStatusMessageView(ad, statusMessage));
			});
			model.addObject("adsWithStatusMessages", adsWithStatusMessage);
		}
		return model;
	}
	
	/**
	 * Returns {@code ModelAndView} page with {@link RespondedAd}s of this client
	 * in order from latest to earliest
	 * @param user - {@code Principal} object for retrieving client from db
	 * @return
	 */
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView respondedAds(Principal user,
									 @RequestParam(name="page",defaultValue="1",required = false) int page){
		
		Set<RespondedAd> respondedAds = null;
		
		Settings settings = settingsService.getProjectSettings();
		try {
			respondedAds = clientService
					.getRespondedAds(user.getName(),page,
							settings.getMaxNumberOfRespondedAdsOnOnePage());
		} catch (WrongPageNumber e) {
			try {
				respondedAds = clientService
						.getRespondedAds(user.getName(),1,
								settings.getMaxNumberOfRespondedAdsOnOnePage());
			} catch (WrongPageNumber unused) {}
		}
		
		long nunmberOfPages = clientService
				.getNumberOfPagesForRespondedAds(user.getName(), 
						settings.getMaxNumberOfRespondedAdsOnOnePage());
		ModelAndView model = new ModelAndView("/client/responses");
		model.addObject("respondedAds", respondedAds);
		model.addObject("numberOfPages",nunmberOfPages);
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
	public ModelAndView adbuilder(@RequestParam(name="page",required=false,defaultValue="1")
																				int page,
								  @ModelAttribute("cTTBean") ChooseTranslateTypeBean 
																chosenTranslateTypeBean,
								  BindingResult result,
								  Principal user,HttpServletRequest request){
		if(result.hasErrors()){
			return new ModelAndView("redirect:/client/adbuilder");
		}
		if(page==1){
			ModelAndView model = new ModelAndView("/client/adbuilderstep1");
			model.addObject("translateTypes", getTranslateTypesForSelect());
			ChooseTranslateTypeBean chooseTranslateType = new ChooseTranslateTypeBean();
			model.addObject("cTTBean",chooseTranslateType);
			return model;
		}
		
		final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap != null && inputFlashMap.containsKey("translateType") &&
	    		inputFlashMap.containsKey("ad")) {
	    	//if saving of ad failed
	    	//get translate type and return appropriate
	    	//page for editing entered data
	    	TranslateType translateType = 
	    				(TranslateType)inputFlashMap.get("translateType");
	    	if(translateType.equals(TranslateType.ORAL)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderoral");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", true);
	    		return model;
	    	}else if(translateType.equals(TranslateType.WRITTEN)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderwritten");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", true);
	    		return model;
	    	}else{
	    		//unacceptable situation
	    		logger.error("'translateType' variable exists in flash attributes,"
	    				+ " but doesn't equal to ORAL or WRITTEN");
	    	}
	    }
	    
		
		if(page==2){
			//if it is first call of method(without redirecting)
			//with instantiated chosenTranslateTypeBean
			ModelAndView model = new ModelAndView();
			model.addObject("createAd", true);
		    //adding maps for rendering variants languages or currencies
			addMapsToAdView(model);
			if(TranslateType.ORAL.equals(chosenTranslateTypeBean.getTranslateType())){
		    	OralAd ad = new OralAd();
				model.addObject("ad",ad);
			    model.setViewName("/client/adbuilderoral");
				return model;
			}else if(TranslateType.WRITTEN.equals(chosenTranslateTypeBean.getTranslateType())){
			    WrittenAd ad = new WrittenAd();
				model.addObject("ad",ad);
			    model.setViewName("/client/adbuilderwritten");
				return model;				
			}else{
				//redirecting to adbuilder without page requestparam
				return new ModelAndView("redirect:/client/adbuilder");
			}
		}
		
		return new ModelAndView("redirect:/client/adbuilder");
	}
	
	/**
	 * If some errors exist, redirects to {@link #adbuilder(int, ChooseTranslateTypeBean, BindingResult, Principal, HttpServletRequest)}
	 * otherwise saves {@code Ad} of ORAL type in db, returns {@code ModelAndView} page
	 * @param ad - {@code Ad} object with inputted parameters
	 * @param result - {@code BindingResult} for checking errors
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/saveOralAd", method = RequestMethod.POST)
	public ModelAndView saveOralAd(@Valid @ModelAttribute("ad") OralAd ad,
								BindingResult result,
								Principal user,
								RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			attr.addFlashAttribute("ad",ad);
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+ad.getTranslateType());
		}
		Long adId;
		try {
			Settings settings = settingsService.getProjectSettings();
				
			adId = adService.saveAd(ad, user.getName(),settings.getMaxNumberOfAdsForClient());
			attr.addFlashAttribute("adUrl",webRootPath+"/ads/"+adId);
			attr.addFlashAttribute("adId",adId);
			return new ModelAndView("redirect:/client/success");
		} catch (TooManyAds e) {
			attr.addFlashAttribute("error","Simultaneously you can have only 3 advertisements");
			return new ModelAndView("redirect:/client/ads");
		} catch (DuplicateAdException e) {
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			attr.addFlashAttribute("ad",ad);
			attr.addFlashAttribute("error", "This advertisement is very similar to your another one");
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+ad.getTranslateType());
		}

		
	}
	
	/**
	 * If some errors exist, redirects to {@link #adbuilder(int, ChooseTranslateTypeBean, BindingResult, Principal, HttpServletRequest)}
	 * otherwise saves {@code Ad} of WRITTEN type in db, returns {@code ModelAndView} page
	 * @param ad - {@code Ad} object with inputted parameters
	 * @param result - {@code BindingResult} for checking errors
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/saveWrittenAd", method = RequestMethod.POST)
	public ModelAndView saveWrittenAd(@Valid @ModelAttribute("ad") WrittenAd ad,
								BindingResult result,
								@RequestParam(name="multipartFile",required=false)
										MultipartFile file,
								Principal user,
								RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			attr.addFlashAttribute("ad",ad);
			attr.addFlashAttribute("multipartFile",file);
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+ad.getTranslateType());
		}
		if(!file.isEmpty()){
			if(!isfileContentTypeAllowed(file)){
				attr.addFlashAttribute("error", "Please choose file with .pdf or .doc(.docx) extension");
				attr.addFlashAttribute("translateType",ad.getTranslateType());
				attr.addFlashAttribute("ad",ad);
				return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+TranslateType.WRITTEN);
			}
			try {
				byte[] fileWithText = file.getBytes();
				ad.setDocument(
						new Document(ad,fileWithText,file.getOriginalFilename(),file.getContentType()));
			} catch (IOException e) {
				attr.addFlashAttribute("errorFile", "Some problem with your file, please repeat action");
				attr.addFlashAttribute("translateType",ad.getTranslateType());
				attr.addFlashAttribute("ad",ad);
				return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+TranslateType.WRITTEN);
			}
		}else{
			attr.addFlashAttribute("errorFile", "Please choose file");
			attr.addFlashAttribute("ad",ad);
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+TranslateType.WRITTEN);
		}
		Long adId;
		try {
			Settings settings = settingsService.getProjectSettings();
			adId = writtenAdService.saveAd(ad, user.getName(),settings.getMaxNumberOfAdsForClient());
			attr.addFlashAttribute("adUrl",webRootPath+"/ads/"+adId);
			attr.addFlashAttribute("adId",adId);
			return new ModelAndView("redirect:/client/success");
		} catch (TooManyAds e) {
			attr.addFlashAttribute("error","Simultaneously you can have only 3 advertisements");
			return new ModelAndView("redirect:/client/ads");
		} catch (DuplicateAdException e) {
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			attr.addFlashAttribute("ad",ad);
			attr.addFlashAttribute("multipartFile",file);
			attr.addFlashAttribute("error", "This advertisement is very similar to your another one");
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+ad.getTranslateType());
		}

		
	}
	
	
	
	/**
	 * Deletes {@code Ad}, if such exists and it status is not accepted, 
	 * and redirects to {@link #ads(Principal)}
	 * @param adId - id of {@code Ad} object
	 */
	@RequestMapping(value = "/ads/delete", method = RequestMethod.POST)
	public ModelAndView deleteAd(@RequestParam("adId") long adId,Principal user,
								  RedirectAttributes attr){
		ModelAndView model = new ModelAndView("redirect:/client/ads");
		
		try {
			adService.deleteById(adId);
			attr.addFlashAttribute("msg", "Adversiment successfully deleted");
		} catch (InvalidIdentifier e) {
			attr.addFlashAttribute("error", "You can't delete non existed ad");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error", "Sorry, but you can't delete advertisement, "
					+ "which has Accepted status");
		}
		
		return model;
	}
	
	/**
	 * If flash attribute {@code adUrl} and {@code adId} exist, returns page with message about successful 
	 * creation of Ad, otherwise redirects to {@link #ads(Principal)} 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public ModelAndView createdAd(HttpServletRequest request){
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("adUrl")
	    							|| !inputFlashMap.containsKey("adId")) {
	    	return new ModelAndView("redirect:/client/ads");
	    }
		return new ModelAndView("/client/createdAd");
	}
	
	/**
	 * Returns page for editing existed advertisement
	 * 
	 * <p>If Ad doesn't exist or his status doesn't allow to edit this Ad,
	 * redirects to {@link #ads(Principal)} with added flash attribute {@code error}
	 * 
	 * @param adId - id of {@code Ad} object
	 * @see ClientController#addMapsToAdView(ModelAndView)
	 */
	@RequestMapping(value = "/ads/edit", method = RequestMethod.GET)
	public ModelAndView editAd(@RequestParam("adId") long adId,Principal user,
								RedirectAttributes attr,HttpServletRequest request){
		
		ModelAndView errorView= new ModelAndView("redirect:/client/ads");
		
		final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap != null && inputFlashMap.containsKey("translateType") &&
	    		inputFlashMap.containsKey("ad")) {
	    	//if saving of edits failed or was successful
	    	//get translate type and return appropriate
	    	//page for editing
	    	TranslateType translateType = 
	    				(TranslateType)inputFlashMap.get("translateType");
	    	if(translateType.equals(TranslateType.ORAL)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderoral");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", false);
	    		return model;
	    	}else if(translateType.equals(TranslateType.WRITTEN)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderwritten");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", false);
	    		return model;
	    	}else{
	    		//unacceptable situation
	    		logger.error("'translateType' variable exists in flash attributes,"
	    				+ " but doesn't equal to ORAL or WRITTEN");
	    	}
	    }
		try {
			ModelAndView model = null;
			Ad ad = adService.getForUpdating(user.getName(),adId);
			if(ad.getTranslateType().equals(TranslateType.WRITTEN)){
				model = new ModelAndView("/client/adbuilderwritten");
			}else if(ad.getTranslateType().equals(TranslateType.ORAL)){
				model = new ModelAndView("/client/adbuilderoral");
			}else{
				//unacceptable situation
				logger.error("field 'translateType' of retrieved ad "
	    				+ " doesn't equal to ORAL or WRITTEN");
			}
			model.addObject("ad", ad);
			model.addObject("createAd", false);
			addMapsToAdView(model);
			return model;
		}  catch (InvalidIdentifier e) {
			attr.addFlashAttribute("error","You can't edit non existed advertisement");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error","You can edit advertisement, which has SHOWED status, and "
					+ "all responses to it are rejected");
		}
		return errorView;
		
	}
	
	/**
	 * Checks {@code BindingResult} for errors, if such exists, 
	 * redirects to {@link #editAd(long, Principal, RedirectAttributes, HttpServletRequest)}(for entering valid data)
	 * with added flash attributes {@code ad}, {@code result} and {@code translateType}
	 * 
	 * <p>If data is valid updates old ad and returns page for editing with message 
	 * about successful updating
	 * 
	 * If Ad doesn't exist or his status doesn't allow to edit this Ad,
	 * redirects to {@link #ads(Principal)} with added flash attribute {@code error}
	 * @param adId
	 * @param editedAd
	 * @param result
	 * @param user
	 */
	@RequestMapping(value = "/saveOralAdEdits",method = RequestMethod.POST)
	public ModelAndView saveOralAdEdits(
							@Valid @ModelAttribute("ad") OralAd editedAd,
							BindingResult result,
							Principal user,
							RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("ad",editedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
		}
		ModelAndView errorView= new ModelAndView("redirect:/client/ads");
		
		try {
			Ad updatedAd = oralAdService.updateOralAd(user.getName(),editedAd);
			attr.addFlashAttribute("Editmsg", "Advertisement is edited successfully");
			attr.addFlashAttribute("ad",updatedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+updatedAd.getId());
		} catch (InvalidIdentifier e) {
			attr.addFlashAttribute("error","You can't edit non existed advertisement");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error","You can edit advertisement, which has SHOWED status, and "
					+ "all responses to it are rejected");
		} catch (DuplicateAdException e) {
			attr.addFlashAttribute("ad",editedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			attr.addFlashAttribute("error","This advertisement is very similar to your another one");
			return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
		}
			
		return errorView;
		
	}
	/**
	 * This method is similar to {@link #saveOralAdEdits(Ad, BindingResult, Principal, RedirectAttributes)},
	 * <br>difference is that this method checks if {@code file} is empty or it is invalid file,
	 * in this case redirects to {@link #editAd(long, Principal, RedirectAttributes, HttpServletRequest)}
	 * @param editedAd
	 * @param result
	 * @param user
	 * @param attr
	 */
	@RequestMapping(value = "/saveWrittenAdEdits",method = RequestMethod.POST)
	public ModelAndView saveWrittenAdEdits(
							@Valid @ModelAttribute("ad") WrittenAd editedAd,
							BindingResult result,
							@RequestParam(name="multipartFile",required=false)
							MultipartFile file,
							Principal user,
							RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("ad",editedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
		}
		ModelAndView errorView= new ModelAndView("redirect:/client/ads");
		
		if(!file.isEmpty()){
			if(!isfileContentTypeAllowed(file)){
				attr.addFlashAttribute("error", "Please choose file with .pdf or .doc(.docx) extension");
				attr.addFlashAttribute("translateType",editedAd.getTranslateType());
				attr.addFlashAttribute("ad",editedAd);
				return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
			}
			try {
				byte[] fileWithText = file.getBytes();
				editedAd.setDocument(
						new Document(editedAd,fileWithText,file.getOriginalFilename(),file.getContentType()));
			} catch (IOException e) {
				attr.addFlashAttribute("ad",editedAd);
				attr.addFlashAttribute("translateType",editedAd.getTranslateType());
				attr.addFlashAttribute("errorFile", "Some problem with your file, please repeat action");
				return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
			}
		}
		
		//If all data is valid, attempt update WrittenAd
		try {
			Ad updatedAd = writtenAdService.updateWrittenAdByClient(user.getName(),editedAd);
			attr.addFlashAttribute("Editmsg", "Advertisement is edited successfully");
			attr.addFlashAttribute("ad",updatedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+updatedAd.getId());
		} catch (InvalidIdentifier e) {
			attr.addFlashAttribute("error","You can't edit non existed advertisement");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error","You can edit advertisement, which has SHOWED status, and "
					+ "all responses to it are rejected");
		} catch (DuplicateAdException e) {
			attr.addFlashAttribute("ad",editedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			attr.addFlashAttribute("error","This advertisement is very similar to your another one");
			return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
		}
			
		return errorView;
	}
	
	/**
	 * Refreshes {@link Ad#getPublicationDateTime()}, if such exists and it status is not accepted, 
	 * and redirects to {@link #ads(Principal)}
	 * @param adId - id of {@code Ad} object
	 */
	@RequestMapping(value = "/ads/refresh", method = RequestMethod.POST)
	public ModelAndView refreshPubDateOfAd(@RequestParam("adId") long adId,Principal user,
								  RedirectAttributes attr){
		ModelAndView model = new ModelAndView("redirect:/client/ads");
		
		Settings settings = settingsService.getProjectSettings();
		
		try {
			adService.refreshPubDate(user.getName(), adId, settings.getMinHoursBetweenRefreshings());
			attr.addFlashAttribute("msg", "Date is successfully refreshed");
		} catch (InvalidIdentifier e) {
			attr.addFlashAttribute("error", "You can't delete non existed ad");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error", "Sorry, but you can't delete advertisement, "
					+ "which has Accepted status");
		} catch (TooManyRefreshings e) {
			attr.addFlashAttribute("error", "Sorry, but you can refresh advertisement only "
					+ "one time in " + settings.getMinHoursBetweenRefreshings() + " hours");
		}
		
		return model;
	}
	
	@RequestMapping(value="/ads/clear",method = RequestMethod.POST)
	public ModelAndView deleteAdFromAssociationsWithClient(
						@RequestParam(name="adId",required = true) long adId,
						Principal user,
						RedirectAttributes attr){
		try {
			adService.deleteFromAssociationsWithClientAndArchieveAd(adId, user.getName());
			attr.addFlashAttribute("success", "Ad is successfully deleted");
			return new ModelAndView("redirect:/client/ads");
		} catch (InvalidIdentifier e) {
			return new ModelAndView("redirect:/client/ads");
		}
	}
	
	
	 /**
	  * Adds maps of {@link Language}, {@link Currency}
	  * to {@code model} for rendering available variants to the client while
	  * creating advertisement
	  * @see #getCurrenciesForSelect()
	  * @see #getLanguagesForSelect()
	  */
	  private void addMapsToAdView(ModelAndView model){
	  	model.addObject("languages", getLanguagesForSelect());
		model.addObject("currencies", getCurrenciesForSelect());
	  }
}
	  
