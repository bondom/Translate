package ua.translate.logging.dao;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.ad.WrittenAd;
import ua.translate.model.searchbean.SearchWrittenAdBean;
import ua.translate.model.status.AdStatus;

@Aspect
@Component
public class WrittenAdDaoAspect {
	
	Logger logger = LoggerFactory.getLogger(WrittenAdDaoAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public * getWrittenAdById(..)) "
			 + "&& args(adId)")
	public WrittenAd getWrittenAdById(ProceedingJoinPoint thisJoinPoint,
								long adId)
								throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		WrittenAd writtenAd= null;
		try {
			writtenAd = (WrittenAd)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(adId={}):{}",
					className,methodName,adId,e.getClass());
			e.printStackTrace();
			throw e;
		}
		
		logger.debug("{}.{}(adId={}): WrittenAd=[name={},status={}]",
				className,methodName,adId,writtenAd.getName(),writtenAd.getStatus());
		
		return writtenAd;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public * getFilteredWrittenAdsForShowing(..)) "
			 + "&& args(page,numberAdsOnPage,searchAdBean)")
	public Set<WrittenAd> getFilteredWrittenAdsForShowing(ProceedingJoinPoint thisJoinPoint,
									int page, int numberAdsOnPage,
									SearchWrittenAdBean searchAdBean) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters: page={},numberAdsOnPage={},"
				+ "searchAdBean=[translateType={},initLanguage={},resultLanguage={},"
				+ "currency={},maxCost={},minCost={}]",
				className,methodName,page,numberAdsOnPage,searchAdBean.getTranslateType(),
				searchAdBean.getInitLanguage(),searchAdBean.getResultLanguage(),
				searchAdBean.getCurrency(),searchAdBean.getMaxCost(),searchAdBean.getMinCost());
				
		Set<WrittenAd> ads = null;
		try{
			ads = (Set<WrittenAd>) thisJoinPoint.proceed();
		}catch(Throwable ex){
			logger.error("{}.{}(page={},numberOfAdsOnPage={},searchAdBean-'see above' ):{}",
					className,methodName,page,numberAdsOnPage, ex.getClass());
			ex.printStackTrace();
			throw ex;
		}
		
		if(ads.size()>0){
			ads.stream().forEach(ad ->{
				logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchAdBean -'see above'):"
						+ "Ad=[id = {},name='{}',cost='{}',"
						+ "initLanguage={},resultLanguage={}]"
						,className,methodName,
						page,numberAdsOnPage,ad.getId(),
						ad.getName(),ad.getCost(),ad.getInitLanguage(),
						ad.getResultLanguage());
				
			});
		}else logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchAdBean-'see above'):"
				+ " 0 ads exists",className,methodName,page,numberAdsOnPage);
		
		return ads;
	
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public long getNumberOfWrittenAdsByStatusAndFilter(..)) "
			 + "&& args(adStatus,searchAdBean)")
	public Long getNumberOfWrittenAdsByStatusAndFilter(ProceedingJoinPoint thisJoinPoint,
												AdStatus adStatus, 
												SearchWrittenAdBean searchAdBean)
												throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters: ad status={},"
				+ "searchAdBean=[translateType={},initLanguage={},resultLanguage={},"
				+ "currency={},maxCost={},minCost={}]",
				className,methodName,adStatus,searchAdBean.getTranslateType(),
				searchAdBean.getInitLanguage(),searchAdBean.getResultLanguage(),
				searchAdBean.getCurrency(),searchAdBean.getMaxCost(),searchAdBean.getMinCost());
		Long numberOfAds= 0L;
		try {
			numberOfAds = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(input parameters - 'see above'):{}",
					className,methodName,e.getClass());
			e.printStackTrace();
			throw e;
		}
		
		logger.debug("{}.{}(input parameters - 'see above'): number of ads={}",
				className,methodName,numberOfAds);
		
		return numberOfAds;
	}
	
	
}
