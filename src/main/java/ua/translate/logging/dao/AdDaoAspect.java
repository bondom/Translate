package ua.translate.logging.dao;


import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.Client;
import ua.translate.model.Order;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;

@Aspect
@Component
public class AdDaoAspect {

	Logger logger = LoggerFactory.getLogger(AdDaoAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() && "
			+ "execution(Long save(ua.translate.model.ad.Ad)) && args(ad)")
	public Long saving(ProceedingJoinPoint thisJoinPoint, Ad ad) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long id = 0L;
		try {
			id = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return id;
		}
		logger.debug("{}.{}: Ad  with name='{}' is successfully saved, owner(client)={}",className,methodName,ad.getName(),ad.getClient().getEmail());
		return id;
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public ua.translate.model.ad.Ad get(*)) && args(id)")
	public Ad getting(ProceedingJoinPoint thisJoinPoint, Long id) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad ad = null;
		try {
			ad = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return null;
		}
		
		if(ad!=null){
			
			Client client = ad.getClient();
			long clientId = 0;
			if(client != null){
				clientId = client.getId();
			}
			Translator translator = ad.getTranslator();
			long translatorId = 0;
			if(translator!=null){
				translatorId = translator.getId();
			}
			
			logger.debug("{}.{}(id={}):advertisement is retrieved from db, name='{}',status={},client id={},translator id={}",
					className,methodName,id,ad.getName(),ad.getStatus(),clientId,translatorId);
		}else{
			logger.error("{}.{}(id={}):ad with such id doesn't exist",
					className,methodName,id);
		}
		return ad;
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public long getNumberOfAdsByStatusAndTranslateType(..)) && "
			 + "args(adStatus, translateType)")
	public long getNumberOfAdsByStatusAndTranslateType(ProceedingJoinPoint thisJoinPoint, 
													   AdStatus adStatus,
													   TranslateType translateType) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		long numberOfAds = (long)thisJoinPoint.proceed();
		
		logger.debug("{}.{}(adStatus={},translateType={}): {} ads",
				className,methodName,adStatus.name(),translateType,numberOfAds);
		return numberOfAds;
	}
	
	@SuppressWarnings("unchecked")
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public * getAdsByTranslateTypeAndStatusAndOrder(..)) && "
			 + "args(page,numberAdsOnPage,translateType,adStatus,order)")
	public Set<Ad> getAdsByTranslateTypeAndStatusAndOrder(ProceedingJoinPoint thisJoinPoint,
									   int page,
									   int numberAdsOnPage,
									   TranslateType translateType,
									   AdStatus adStatus,
									   Order order) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		Set<Ad> ads = null;
		try{
			ads = (Set<Ad>) thisJoinPoint.proceed();
		}catch(Throwable ex){
			logger.error("{}.{}(page={},numberOfAdsOnPage={},translateType={},status={},order={}):{}",
					className,methodName,page,numberAdsOnPage,translateType,adStatus,
					order,ex.getClass());
			ex.printStackTrace();
			throw ex;
		}
		
		if(ads.size()>0){
			ads.stream().forEach(ad ->{
				logger.debug("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):"
						+ "Ad=[id = {},name='{}',cost='{}',"
						+ "initLanguage={},resultLanguage={}]"
						,className,methodName,
						page,numberAdsOnPage,adStatus,order,ad.getId(),
						ad.getName(),ad.getCost(),ad.getInitLanguage(),
						ad.getResultLanguage());
			});
		}else logger.debug("{}.{}(page={},numberOfAdsOnPage={},translateType={},status={},order={}):"
				+ " 0 ads exists",className,methodName,page,numberAdsOnPage,
				translateType,adStatus,order);
		return ads;
	}
	
}
