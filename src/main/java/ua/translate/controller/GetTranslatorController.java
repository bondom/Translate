package ua.translate.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.support.ControllerHelper;
import ua.translate.controller.support.TranslatorComparatorByDate;
import ua.translate.model.Comment;
import ua.translate.model.Translator;
import ua.translate.model.viewbean.TranslatorView;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.NonExistedTranslatorException;
import ua.translate.service.exception.WrongPageNumber;

@Controller
@RequestMapping("/translators")
public class GetTranslatorController {
	
	private static final int TRANSLATORS_ON_PAGE=3;
	
	@Autowired
	ControllerHelper controllerHelper;
	
	private Logger logger = LoggerFactory.getLogger(GetTranslatorController.class);
	
	@Autowired
	private TranslatorService translatorService;
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView translators(@RequestParam(name="page",
														required = false,
														defaultValue="1") int page){
		Set<Translator> translators = null;
		try {
			translators = translatorService.getTranslators(page, TRANSLATORS_ON_PAGE);
		} catch (WrongPageNumber e1) {
			try {
				translators = translatorService.getTranslators(1, TRANSLATORS_ON_PAGE);
			} catch (WrongPageNumber unused) {}
		}
		
		Set<TranslatorView> translatorsForRendering = new LinkedHashSet<>();
		translators.forEach(translator->{
			String avatar = null;
			try {
				avatar = controllerHelper.
							getAvaForRendering(translator.getAvatar());
			} catch (UnsupportedEncodingException e) {
				avatar = "";
				logger.error("Problem with converting avatar of translator:{}",e.getMessage());
			}
			String relativePublishingTime = controllerHelper.
					getStringRelativeTime(translator.getPublishingTime());
			
			TranslatorView translatorView = new TranslatorView(
					translator, avatar, relativePublishingTime);
			
			translatorsForRendering.add(translatorView);
		});
		
		long numberOfPages= translatorService.
				getNumberOfPagesForTranslators(TRANSLATORS_ON_PAGE);
		ModelAndView model = new ModelAndView("/translators");
		model.addObject("translatorsView", translatorsForRendering);
		model.addObject("numberOfPages",numberOfPages);
		return model;
	}
	
	@RequestMapping(value = "/{tId}", method = RequestMethod.GET)
	public ModelAndView translator(HttpServletRequest request,
								   @PathVariable("tId") long translatorId) throws UnsupportedEncodingException{
		try {
			Translator translator = translatorService.getTranslatorById(translatorId);
			ModelAndView model = new ModelAndView("/translatorProfile");
			model.addObject("translator", translator);
			model.addObject("image", controllerHelper.
									getAvaForRendering(translator.getAvatar()));
			Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		    if (inputFlashMap == null || !inputFlashMap.containsKey("comment")) {
				Comment comment = new Comment();
				comment.setTranslator(translator);
				model.addObject("comment",comment);
		    }
			
			return model;
		} catch (NonExistedTranslatorException e) {
			ModelAndView model = new ModelAndView("/exception/invalidTranslatorId");
			model.addObject("errorUrl",webRootPath+"/translator/"+translatorId);
			return model;
		}
	}
}
