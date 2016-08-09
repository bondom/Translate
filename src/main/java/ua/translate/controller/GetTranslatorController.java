package ua.translate.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.controller.support.ControllerHelper;
import ua.translate.controller.support.TranslatorComparatorByDate;
import ua.translate.model.Translator;
import ua.translate.model.viewbean.TranslatorView;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.NonExistedTranslatorException;

@Controller
@RequestMapping("/translators")
public class GetTranslatorController {
	
	private Logger logger = LoggerFactory.getLogger(ControllerHelper.class);
	
	@Autowired
	private TranslatorService translatorService;
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getAllTranslators(){
		List<Translator> translators = translatorService.getAllTranslators();
		
		Set<TranslatorView> translatorsForRendering = new TreeSet<>(new TranslatorComparatorByDate());
		translators.forEach(translator->{
			String avatar = null;
			try {
				byte[] translatorAvatar = translator.getAvatar();
				if(translatorAvatar!=null){
					avatar = ControllerHelper.
							convertAvaForRendering(translator.getAvatar());
				}
			} catch (UnsupportedEncodingException e) {
				avatar = "";
				logger.error("Problem with converting avatar of translator:{}",e.getMessage());
			}
			String relativePublishingTime = ControllerHelper.
					getStringRelativeTime(translator.getPublishingTime());
			
			TranslatorView translatorView = new TranslatorView(
					translator, avatar, relativePublishingTime);
			
			translatorsForRendering.add(translatorView);
		});
		ModelAndView model = new ModelAndView("/translators");
		model.addObject("translatorsView", translatorsForRendering);
		return model;
	}
	
	@RequestMapping(value = "/{tId}", method = RequestMethod.GET)
	public ModelAndView translator(@PathVariable("tId") long translatorId) throws UnsupportedEncodingException{
		try {
			Translator translator = translatorService.getTranslatorById(translatorId);
			ModelAndView model = new ModelAndView("/translatorProfile");
			model.addObject("translator", translator);
			if(translator.getAvatar() != null){
				model.addObject("image", ControllerHelper.
											convertAvaForRendering(translator.getAvatar()));
			}
			return model;
		} catch (NonExistedTranslatorException e) {
			ModelAndView model = new ModelAndView("/exception/invalidTranslatorId");
			model.addObject("errorUrl",webRootPath+"/translator/"+translatorId);
			return model;
		}
	}
}
