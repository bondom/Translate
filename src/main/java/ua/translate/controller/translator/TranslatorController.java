package ua.translate.controller.translator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.UserController;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Translator;
import ua.translate.model.Language;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.settings.Settings;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdWithStatusMessageView;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.service.AdService;
import ua.translate.service.AdStatusMessageService;
import ua.translate.service.DocumentService;
import ua.translate.service.SettingsService;
import ua.translate.service.TranslatorService;
import ua.translate.service.WrittenAdService;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.NumberExceedsException;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.exception.WrongPageNumber;

@Controller
@RequestMapping("/translator")
public class TranslatorController extends UserController{

	@Autowired
	private TranslatorService translatorService;
	
	@Autowired
	private WrittenAdService adService;
	
	@Autowired
	private AdStatusMessageService adStatusMessageService;
	
	@Autowired
	private SettingsService settingsService;
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	Logger logger = LoggerFactory.getLogger(TranslatorController.class);
	
	/**
	 * Returns initial(welcome) page for translators
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index(){
		return new ModelAndView("/translator/index");
	}
	
	
	
	/**
	 * Attempts to save new RespondedAd for translator,
	 * <br>if Ad with id={@code adId} doesn't exist, or translator has too much ads,
	 * or he has ACCEPTED ad -- redirects to appropriate page with 
	 * adding appropriate flash attributes 
	 * @param adId
	 * @param user
	 * @param attr
	 */
	@RequestMapping(value="/response",method = RequestMethod.POST)
	public ModelAndView response(@RequestParam(name="id",required = false) long adId,
								Principal user,RedirectAttributes attr){
		Settings settings = settingsService.getProjectSettings();
		try {
			translatorService.createAndSaveRespondedAd(
					user.getName(),adId,settings.getMaxNumberOfSendedRespondedAdsForTranslator());
			return new ModelAndView("redirect:/translator/successResponding");
		} catch (InvalidIdentifier e) {
			attr.addFlashAttribute("errorUrl", webRootPath+"/ads/"+adId);
			return new ModelAndView("redirect:/exception/invalidAdId");
		} catch (NumberExceedsException e) {
			return new ModelAndView("redirect:/translator/error/exceeding");
		} catch (TranslatorDistraction e) {
			attr.addFlashAttribute("error","First you must end this translate!");
			return new ModelAndView("redirect:/translator/currentOrder");
		} catch (IllegalActionForAd e) {
			return new ModelAndView("redirect:/adsw");
		}
	}
	
	


	/**
	 * Returns page with message about successfull sending response to the client 
	 */
	@RequestMapping(value="/successResponding",method=RequestMethod.GET)
	public ModelAndView successResponding(){
		return new ModelAndView("/translator/successResponding");
	}
	
	
	/**
	 * Returns {@code ModelAndView} page with {@link RespondedAd}s of this translator
	 * in order from latest to earliest
	 * @param user - {@code Principal} object for retrieving translator from db
	 * @param page - number of page
	 */
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView respondedAds(Principal user,
									 @RequestParam(name="page",defaultValue="1",required = false) int page){
		
		Settings settings = settingsService.getProjectSettings();
		final int respondedAdsOnPage = settings.getMaxNumberOfRespondedAdsOnOnePage();
		Set<RespondedAd> respondedAds = null;
		try {
			respondedAds = translatorService
					.getRespondedAds(user.getName(),page,respondedAdsOnPage);
		} catch (WrongPageNumber e) {
			try {
				respondedAds = translatorService
						.getRespondedAds(user.getName(),1,respondedAdsOnPage);
			} catch (WrongPageNumber unused) {}
		}
		
		long nunmberOfPages = translatorService
				.getNumberOfPagesForRespondedAds(user.getName(), respondedAdsOnPage);
		ModelAndView model = new ModelAndView("/translator/responses");
		model.addObject("respondedAds", respondedAds);
		model.addObject("numberOfPages",nunmberOfPages);
		return model;
	}
		
	
	/**
	 * Marks {@link WrittenAd} with {@code adId} as NOTCHECKED.
	 * Redirects to  {@link #currentOrder(Principal)} 
	 * @param user - {@code Principal} object for retrieving translator from db
	 */
	@RequestMapping(value = "/finish",method = RequestMethod.POST)
	public ModelAndView finish(Principal user,
							   @RequestParam(name="id",required = true) long adId,
							   @RequestParam(name="multipartFile",required=false)
							   MultipartFile file,
							   RedirectAttributes attr){
		if(file.isEmpty()){
			attr.addFlashAttribute("error", "Please choose file");
			return new ModelAndView("redirect:/translator/currentOrder");
		}
		
		if(!isfileContentTypeAllowed(file)){
			attr.addFlashAttribute("error", "Please choose file with .pdf or .doc(.docx) extension");
			return new ModelAndView("redirect:/translator/currentOrder");
		}
		
		try {
			byte[] fileWithText = file.getBytes();
			ResultDocument resultDocument = 
					new ResultDocument(fileWithText,file.getOriginalFilename(),file.getContentType());
			try {
				adService.saveResultDocAndMarkAsNotChecked(user.getName(), adId,resultDocument);
			} catch (InvalidIdentifier | IllegalActionForAd e) {}
			return new ModelAndView("redirect:/translator/currentOrder");
		} catch (IOException e) {
			attr.addFlashAttribute("error", "Some problem with your file, please repeat action");
			return new ModelAndView("redirect:/translator/currentOrder");
		}
		
	}
	
	/**
	 * Returns page with message, that some id of ad doesn't exist
	 * 
	 * <p><b>NOTE:</b>While redirecting to this method in flash attributes must exists
	 * flash attribute with name={@code errorUrl}, otherwise this method redirects to
	 * translator's profile
	 * 	
	 */
	@RequestMapping(value="/error/invalidAdId",method=RequestMethod.GET)
	public ModelAndView invalidAdId(HttpServletRequest request){
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("errorUrl")) {
	    	return new ModelAndView("redirect:/translator/profile");
	    }
	    ModelAndView model = new ModelAndView("/exception/invalidAdId");
		return model;
	}
	
	/**
	 * Returns {@code ModelAndView} page with {@link Ad}, which is linked to 
	 * {@link Translator} {@code translator} with 
	 * email={@link Principal#getName() user.name}, and message, which depends
	 * on status of {@code Ad}
	 * @param user - {@code Principal} object for retrieving translator from db
	 */
	@RequestMapping(value = "/currentOrder",method = RequestMethod.GET)
	public ModelAndView currentOrder(Principal user){

		final ModelAndView model = new ModelAndView("/translator/currentOrder");
		Translator translator = translatorService
								.getTranslatorByEmail(user.getName());
		Ad currentOrder = translator.getAd();
		if(currentOrder==null){
			return model;
		}
		AdWithStatusMessageView adWithStatusMessageView = 
				getAdWithStatusMessageViewForAd(currentOrder);
		model.addObject("adWithStatusMessage",adWithStatusMessageView);
		return model;
	}
	
	/**
	 * Returns page with message about having maximum number of sended ads 
	 */
	@RequestMapping(value="/error/exceeding",method=RequestMethod.GET)
	public ModelAndView exceeding(){
		return new ModelAndView("/translator/error/exceeding");
	}
	
	@RequestMapping(value="/currentOrder/clear",method = RequestMethod.POST)
	public ModelAndView deleteAdFromAssociationsWithTranslator(
						@RequestParam(name="adId",required = true) long adId,
						Principal user,
						RedirectAttributes attr){
		try {
			adService.deleteFromAssociationsWithTranslatorAndArchieveAd(adId, user.getName());
			attr.addFlashAttribute("success", "Clearing is successfully executed");
			return new ModelAndView("redirect:/translator/currentOrder");
		} catch (InvalidIdentifier e) {
			return new ModelAndView("redirect:/translator/currentOrder");
		}
	}
	
	/**
	 * Returns {@link AdWithStatusMessageView} object based on {@link Ad} {@code ad} 
	 */
	private AdWithStatusMessageView getAdWithStatusMessageViewForAd(Ad ad){
		AdStatus adStatus = ad.getStatus();
		TranslateType translateType = ad.getTranslateType();
		AdStatusMessage adStatusMessage = 
				adStatusMessageService
				.getAdStatusMessageByAdStatusAndTranslateType(adStatus, translateType);
		AdWithStatusMessageView adWithStatusMessageView = 
				new AdWithStatusMessageView(ad, adStatusMessage.getMessageForTranslator());
		return adWithStatusMessageView;
	}
	
	
}