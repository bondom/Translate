package ua.translate.controller.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.UserController;
import ua.translate.controller.editor.CommentTextEditor;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Client;
import ua.translate.model.Comment;
import ua.translate.model.Language;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.settings.Settings;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdWithStatusMessageView;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.model.viewbean.ChooseTranslateTypeBean;
import ua.translate.service.AbstractAdService;
import ua.translate.service.AdService;
import ua.translate.service.AdStatusMessageService;
import ua.translate.service.ClientService;
import ua.translate.service.CommentService;
import ua.translate.service.OralAdService;
import ua.translate.service.RespondedAdService;
import ua.translate.service.SettingsService;
import ua.translate.service.WrittenAdService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.IllegalActionForRespondedAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.TooManyAds;
import ua.translate.service.exception.TooManyRefreshings;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.exception.WrongPageNumber;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.TooEarlyPaying;
import ua.translate.service.exception.InvalidIdentifier;

/**
 * Contains handler methods for executing business tasks, related to
 * {@link Ad}s and {@link RespondedAd}s.<br> 
 * <em><b>Two main business tasks:</b></em> Rejecting and accepting<br>
 * 
 * @author Yuriy Phediv
 *
 */
/*!!!! Returning page with default used PLEDGE for confirmation!!!!*/
@Controller
@RequestMapping("/client")
public class BusinessClientController extends UserController{

	@Autowired
	private WrittenAdService writtenAdService;
	
	@Autowired
	private OralAdService oralAdService;
	
	@Autowired
	private RespondedAdService respondedAdService;
	
	@Autowired
	private SettingsService settingsService;
	
	@Autowired
	ControllerHelper controllerHelper;
	
	Logger logger = LoggerFactory.getLogger(BusinessClientController.class);
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder){
		dataBinder.registerCustomEditor(String.class, 
										"text", 
										new CommentTextEditor());
	}
	
	
	
	
	/**
	 * If {@code RespondedAd} with {@code respondedAdId} exists, rejects it,
	 * in any case redirects to {@link #respondedAds(Principal)}
	 */
	@RequestMapping(value = "/reject",method = RequestMethod.POST)
	public ModelAndView rejectRespondedAd(@RequestParam(name="id") long respondedAdId,
										  Principal user){
		try {
			respondedAdService.reject(user.getName(),respondedAdId);
		} catch (InvalidIdentifier | IllegalActionForRespondedAd e) {
			return new ModelAndView("redirect:/client/responses");
		} 
		return new ModelAndView("redirect:/client/responses");
	}
	
	/**
	 * Returns page with message about confirmation of accepting
	 * of {@link Ad}  with {@link TranslateType#WRITTEN WRITTEN}
	 * translateType
	 */
	@RequestMapping(value = "/confwritten",method = RequestMethod.GET)
	public ModelAndView confirmationForAcceptingRespondedAdRelatedToWrittenAd(@RequestParam("id") long respondedAdId,
															@RequestParam("cost") double cost,
															Principal user){
		
		Settings settings = new Settings();
		try{
			settings = settingsService.getProjectSettings();
		}catch(InvalidIdentifier e){}
		ModelAndView model = new ModelAndView("/client/acceptconfwritten");
		model.addObject("id",respondedAdId);
		model.addObject("cost",cost);
		model.addObject("pledge", settings.getInitPledgeInPercent());
		return model;
	}
	
	/**
	 * Returns page with message about confirmation of accepting
	 * of {@link Ad}  with {@link TranslateType#ORAL ORAL}
	 * translateType
	 */
	@RequestMapping(value = "/conforal",method = RequestMethod.GET)
	public ModelAndView confirmationForAcceptingRespondedAdRelatedToOralAd(@RequestParam("id") long respondedAdId,
															@RequestParam("cost") double cost,
															Principal user){
		Settings settings = new Settings();
		try{
			settings = settingsService.getProjectSettings();
		}catch(InvalidIdentifier e){}
		ModelAndView model = new ModelAndView("/client/acceptconforal");
		model.addObject("id",respondedAdId);
		model.addObject("cost",cost);
		model.addObject("pledge", settings.getInitPledgeInPercent());
		return model;
	}
	
	/**
	 * If {@code RespondedAd} with {@code respondedAdId} exists,this respondedAd
	 * hasn't ACCEPTED status,and translator, related to respondedAd, isn't busy, 
	 * accepts it
	 * <p>If  translator is busy, redirects to appropriate page with message about that,
	 * otherwise redirects to {@link #respondedAds(Principal, int)}
	 */
	@RequestMapping(value = "/accept",method = RequestMethod.POST)
	public ModelAndView acceptRespondedAd(@RequestParam("id") long respondedAdId,
										Principal user,RedirectAttributes attr){
		
		Settings settings = new Settings();
		try{
			settings = settingsService.getProjectSettings();
		}catch(InvalidIdentifier e){}
		
		try {
			RespondedAd updatedRespondedAd = 
					respondedAdService.acceptRespondedAdAndTransferPledge
											(user.getName(),respondedAdId,settings.getInitPledgeInPercent());
			Ad ad = updatedRespondedAd.getAd();
			final TranslateType translateType = ad.getTranslateType();
			if(TranslateType.WRITTEN.equals(translateType)){
				return new ModelAndView("redirect:/client/responses");
			}else if(TranslateType.ORAL.equals(translateType)){
				//redirecting to  page with translator's phone
				final Translator translator = updatedRespondedAd.getTranslator();
				attr.addFlashAttribute("translatorPhone", translator.getPhoneNumber());
				return new ModelAndView("redirect:/client/phone");
			}else{
				//unacceptable error
				logger.debug("TranslateType of updated RespondedAd via "
						+ "respondedAdService.acceptRespondedAdAndTransferPledge has"
						+ " Ad is:{}",translateType);
				return new ModelAndView("redirect:/client/responses");
			}
		} catch (InvalidIdentifier | IllegalActionForRespondedAd e) {
			return new ModelAndView("redirect:/client/responses");
		} catch (TranslatorDistraction e) {
			return new ModelAndView("redirect:/client/error/busyTranslator");
		} catch (InsufficientFunds e) {
			attr.addFlashAttribute("error", "You hasn't sufficient funds for this operation");
			return new ModelAndView("redirect:/client/responses");
		} 
	}
	
	@RequestMapping(value="/payandget",method = RequestMethod.POST)
	public ModelAndView payRestPriceAndGetTranslate(Principal user,
									@RequestParam("adId") long adId,
									RedirectAttributes attr){
		Settings settings = new Settings();
		try{
			settings = settingsService.getProjectSettings();
		}catch(InvalidIdentifier e){}
		try {
			writtenAdService.transferRestPriceAndChangeStatusAndIncrementExecutedAds(user.getName(), adId, 100-settings.getInitPledgeInPercent());
			attr.addFlashAttribute("success", "Paying was successfull");
			return new ModelAndView("redirect:/client/ads");
		} catch (InvalidIdentifier | IllegalActionForAd e) {
			return new ModelAndView("redirect:/client/ads");
		}catch(InsufficientFunds e){
			attr.addFlashAttribute("error", "You haven't sufficient funds for this operation");
			return new ModelAndView("redirect:/client/ads");
		}
	}
	
	@RequestMapping(value="/pay",method = RequestMethod.POST)
	public ModelAndView payRestPriceForOralAd(Principal user,
									@RequestParam("adId") long adId,
									RedirectAttributes attr){
		Settings settings = new Settings();
		try{
			settings = settingsService.getProjectSettings();
		}catch(InvalidIdentifier e){}
		try {
			oralAdService.transferRestPriceAndChangeStatusAndIncrementExecutedAds(user.getName(), adId, 100 - settings.getInitPledgeInPercent());
			attr.addFlashAttribute("success", "Paying was successfull");
			return new ModelAndView("redirect:/client/ads");
		} catch (InvalidIdentifier | IllegalActionForAd e) {
			return new ModelAndView("redirect:/client/ads");
		}catch(InsufficientFunds e){
			attr.addFlashAttribute("error", "You haven't sufficient funds for this operation");
			return new ModelAndView("redirect:/client/ads");
		} catch (TooEarlyPaying e) {
			attr.addFlashAttribute("error", "You can pay only after ending end time of Advertisement");
			return new ModelAndView("redirect:/client/ads");
		}
	}
	/**
	 * If {@code translatorPhone} exists in FlashAttributes, returns page with translator's phone,
	 * otherwise redirects to {@link #respondedAds(Principal, int)}
	 */
	@RequestMapping(value = "/phone",method = RequestMethod.GET)
	public ModelAndView getTranslatorsPhone(HttpServletRequest request){
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		if (inputFlashMap == null || !inputFlashMap.containsKey("translatorPhone")) {
			return new ModelAndView("redirect:/client/responses");
		}
		ModelAndView model = new ModelAndView("/client/translatorPhone");
		return model;
	}
	/**
	 * Returns page with message, that translator executes another order in that moment
	 */
	@RequestMapping(value = "/error/busyTranslator",method = RequestMethod.GET)
	public ModelAndView busyTranslator(){
		return new ModelAndView("/client/error/busyTranslator");
	}
	
	
  
 /* *//**
   * Returns {@code Map}, where keys and values - numbers from 1 to 31
   *//*
  private Map<String,String> getDays(){
	  Map<String,String> days= new HashMap<>();
	  for(int i=1;i<=31;i++){
		  days.put(i+"", i+"");
	  }
	  return days;
  }
  
  *//**
   * Returns {@code Map}, where keys - numbers from 1 to 12, values - months
   * from {@link Month#January} to {@link Month#DECEMBER} 
   *//*
  private Map<String,String> getMonth(){
	  Map<String,String> months= new HashMap<>();
	  for(int i=1;i<=12;i++){
		  months.put(i+"", Month.of(i)+"");
	  }
	  return months;
  }
  
  *//**
   * Returns {@code Map}, where keys and value - numbers from 1900 to 2016
   *//*
  private Map<String,String> getYears(){
	  Map<String,String> months= new HashMap<>();
	  for(int i=1900;i<=2016;i++){
		  months.put(i+"", i+"");
	  }
	  return months;
  }*/
  
} 