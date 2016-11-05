package ua.translate.service.aspect;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.UserEntity;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.EmailStatus;
import ua.translate.service.AbstractAdService;
import ua.translate.service.ClientService;
import ua.translate.service.RespondedAdService;
import ua.translate.service.TranslatorService;

@Aspect
@Component
public class MailSenderAspect {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private Configuration configuration;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private TranslatorService translatorService;
	
	@Autowired
	@Qualifier("defaultImpl")
	private AbstractAdService adService;
	
	@Autowired
	private RespondedAdService responsedAdService;
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	Logger logger = LoggerFactory.getLogger(MailSenderAspect .class);
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public String saveConfirmationUrl(..)) && args(email)")
	public String sendConfirmationUrl(ProceedingJoinPoint thisJoinPoint,
									  String email) throws Throwable{
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		String url = "";
		try {
			url = (String)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.info("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		UserEntity user = clientService.getUserByEmail(email);
		String namespace="";
		if(user instanceof Client){
			namespace="/client";
		}else namespace="/translator";
			
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setTo(email);
		helper.setFrom("alpachinos47@gmail.com");
		
		Map<String, Object> model = new HashMap<>();
		String renderedUrl = webRootPath+namespace+"/confirmation?ecu="+url;
		model.put("url",renderedUrl);
		model.put("user", user);
		
		Template template = configuration.getTemplate("confirmationEmail.ftl");
		String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		
   	    helper.setText(text, true);
		
   	    Runnable sending = () -> {
   	    	javaMailSender.send(mimeMessage);
   	    	logger.debug("{}.{}:Sending letter to email={} is ended",className,methodName,email);
   	    };
   	    Thread thread = new Thread(sending);
   	    thread.start();
   	       	    
   	    logger.debug("{}.{}:Sending letter to email={} is begined",className,methodName,email);
   	    
		return url;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public long saveResponsedAd(..)) && args(translatorEmail,adId)")
	public long sendResponseToClient(ProceedingJoinPoint thisJoinPoint,
									 String translatorEmail,
									 long adId) throws Throwable{
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		long idOfResponsedAd=0L;
		try {
			idOfResponsedAd = (long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.info("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		Ad ad = adService.get(adId);
		Client ownerOfAd  = ad.getClient();
		String clientEmail = ownerOfAd.getEmail();

		if(ownerOfAd.getEmailStatus().equals(EmailStatus.NOTCONFIRMED)){
			logger.debug("{}.{}:client email={} is not confirmed, letter with response won't be sended",
					className,methodName,clientEmail);
			return idOfResponsedAd;
		}

		Translator responder = translatorService.getTranslatorByEmail(translatorEmail);
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
		helper.setTo(clientEmail);
		helper.setFrom("alpachinos47@gmail.com");
		
		Map<String, Object> model = new HashMap<>();
		model.put("ad",ad);
		model.put("client", ownerOfAd);
		model.put("translator", responder);
		model.put("webRootPath", webRootPath);
		model.put("radId", idOfResponsedAd);
		
		Template template = configuration.getTemplate("mailAboutResponse.ftl");
		String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		
  	    helper.setText(text, true);
		
  	    Runnable sending = () -> {
  	    	javaMailSender.send(mimeMessage);
  	    	logger.debug("{}.{}:Sending letter to email={} is ended",className,methodName,clientEmail);
  	    };
  	    Thread thread = new Thread(sending);
  	    thread.start();
  	       	    
  	    logger.debug("{}.{}:Sending letter to email={} is begined",className,methodName,clientEmail);
  	    return idOfResponsedAd;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void accept(..)) && args(radId)")
	public void acceptingOfResponse(ProceedingJoinPoint thisJoinPoint,
									 long radId) throws Throwable{
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.info("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		RespondedAd responsedAd = responsedAdService.get(radId);
		Translator translator = responsedAd.getTranslator();
		String translatorEmail = translator.getEmail();
		
		if(translator.getEmailStatus().equals(EmailStatus.NOTCONFIRMED)){
			logger.debug("{}.{}:translator email={} is not confirmed, letter with response won't be sended",
					className,methodName,translatorEmail);
			return;
		}

		Ad ad = responsedAd.getAd();
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
		helper.setTo(translatorEmail);
		helper.setFrom("alpachinos47@gmail.com");
		
		Map<String, Object> model = new HashMap<>();
		model.put("ad",ad);
		model.put("translator", translator);
		model.put("webRootPath", webRootPath);
		
		Template template = configuration.getTemplate("acceptingAd.ftl");
		String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		
 	    helper.setText(text, true);
		
 	    Runnable sending = () -> {
 	    	javaMailSender.send(mimeMessage);
 	    	logger.debug("{}.{}:Sending letter to email={} is ended",className,methodName,translatorEmail);
 	    };
 	    Thread thread = new Thread(sending);
 	    thread.start();
 	       	    
 	    logger.debug("{}.{}:Sending letter to email={} is begined",className,methodName,translatorEmail);
	}
	
}
