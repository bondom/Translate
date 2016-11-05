package ua.translate.logging.service;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.Order;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchFilterForWrittenAds;

@Aspect
@Component
public class WrittenAdServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(WrittenAdServiceAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.WrittenAd updateWrittenAdByClient(..)) && args(email,writtenAd)")
	public WrittenAd updateWrittenAdByClient(ProceedingJoinPoint thisJoinPoint,String email,WrittenAd writtenAd) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		WrittenAd savedAd= null;
		try {
			savedAd = (WrittenAd)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(email={}, WrittenAd=[id={}]):{}:{}",className,methodName,
					email,writtenAd.getId(),e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(email={},WrittenAd=[id={}]): WrittenAd is successfully updated - [ad name={}]"
				+"",
				className,methodName,email,writtenAd.getId(),savedAd.getName());

		return savedAd;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getWrittenAdsByStatusAndOrder(..)) && "
			 + "args(page,numberAdsOnPage,adStatus,order)")
	public Set<WrittenAd> getWrittenAdsByStatusAndOrder(ProceedingJoinPoint thisJoinPoint,
									   int page,
									   int numberAdsOnPage,
									   AdStatus adStatus,
									   Order order) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		Set<WrittenAd> ads = null;
		try{
			ads = (Set<WrittenAd>) thisJoinPoint.proceed();
		}catch(Throwable ex){
			logger.error("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):{}",
					className,methodName,page,numberAdsOnPage,adStatus,
					order,ex.getClass());
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
				
						Document document = ad.getDocument();
						logger.debug("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):"
								+ "Document[Ad id={},fileName={},contentType={},file={}]"
								,className,methodName,
								page,numberAdsOnPage,adStatus,order,ad.getId(),
								document.getFileName(),document.getContentType(),document.getFile().hashCode());
						ResultDocument resultDocument = ad.getResultDocument();
						if(resultDocument!=null){
							logger.debug("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):"
									+ "ResultDocument[Ad id={},fileName={},contentType={},file={}]"
									,className,methodName,
									page,numberAdsOnPage,adStatus,order,ad.getId(),
									resultDocument.getFileName(),resultDocument.getContentType(),resultDocument.getFile().hashCode());
						}
			});
		}else logger.debug("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):"
				+ " 0 ads exists",className,methodName,page,numberAdsOnPage,adStatus,order);
		return ads;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getWrittenAdsForShowingByFilter(..)) "
			 + "&& args(page,numberAdsOnPage,searchFilter,valueWithoutFilter)")
	public Set<WrittenAd> getWrittenAdsForShowingByFilter(ProceedingJoinPoint thisJoinPoint,
									int page, int numberAdsOnPage,
									SearchFilterForWrittenAds searchFilter,
									String valueWithoutFilter) throws Throwable {
	String className = thisJoinPoint.getTarget().getClass().getName();
	String methodName = thisJoinPoint.getSignature().getName();
	logger.debug("{}.{}: input parameters: page={},numberAdsOnPage={},"
			+ "searchFilter=[currency={},initLanguage={},resultLanguage={},"
			+ "maxCost={},minCost={}],valueWithoutFilter='{}'",
			className,methodName,page,numberAdsOnPage,
			searchFilter.getCurrency(),
			searchFilter.getInitLanguage(),searchFilter.getResultLanguage(),
			searchFilter.getMaxCost(), searchFilter.getMinCost(),
			valueWithoutFilter);
	Set<WrittenAd> ads = null;
	try{
		ads = (Set<WrittenAd>) thisJoinPoint.proceed();
	}catch(Throwable ex){
		logger.error("{}.{}(page={},numberOfAdsOnPage={},searchFilter-'see above' ):{}",
				className,methodName,page,numberAdsOnPage, ex.getClass());
		ex.printStackTrace();
		throw ex;
	}
	
	if(ads.size()>0){
		ads.stream().forEach(ad ->{
			logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchFilter -'see above'):"
					+ "Ad=[id = {},name='{}',cost='{}',"
					+ "initLanguage={},resultLanguage={}]"
					,className,methodName,
					page,numberAdsOnPage,ad.getId(),
					ad.getName(),ad.getCost(),ad.getInitLanguage(),
					ad.getResultLanguage());
			
					Document document = ad.getDocument();
					logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchFilter-'see above'):"
							+ "Document[Ad id={},fileName={},contentType={},file={}]"
							,className,methodName,
							page,numberAdsOnPage,ad.getId(),
							document.getFileName(),document.getContentType(),document.getFile().hashCode());
					ResultDocument resultDocument = ad.getResultDocument();
					if(resultDocument!=null){
						logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchFilter-'see above'):"
								+ "ResultDocument[Ad id={},fileName={},contentType={},file={}]"
								,className,methodName,
								page,numberAdsOnPage,ad.getId(),
								resultDocument.getFileName(),resultDocument.getContentType(),resultDocument.getFile().hashCode());
					}
		});
	}else logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchFilter-'see above'):"
			+ " 0 ads exists",className,methodName,page,numberAdsOnPage);
	
	return ads;
	
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public long getNumberOfPagesForWrittenAdsByStatus(..)) "
			 + "&& args(adStatus,numberOfAds)")
	public Long getNumberOfPagesForWrittenAdsByStatus(ProceedingJoinPoint thisJoinPoint,
													  AdStatus adStatus,
													  long numberOfAds) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long numberOfPages= 0L;
		try {
			numberOfPages = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(numberOfAdsOnPage={},status={}):{}:{}",
					className,methodName,numberOfAds,adStatus,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}(numberOfAdsOnPage={},status={}): number of pages={}",
				className,methodName,numberOfAds,adStatus,numberOfPages);

		return numberOfPages;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
		 + " execution(public long getNumberOfPagesForWrittenAdsByStatusAndFilter(..)) "
		 + "&& args(adStatus,numberOfAds,searchFilter,valueWithoutFilter)")
	public Long getNumberOfPagesForWrittenAdsByStatusAndFilter(ProceedingJoinPoint thisJoinPoint,
												AdStatus adStatus, 
												int numberOfAds, 
												SearchFilterForWrittenAds searchFilter,
												String valueWithoutFilter)
												throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters:numberOfAds={},ad status={},"
				+ "searchFilter=[currency={},initLanguage={},resultLanguage={},"
				+ "maxCost={},minCost={}],valueWithoutFilter='{}'",
				className,methodName,numberOfAds,adStatus,
				searchFilter.getCurrency(),
				searchFilter.getInitLanguage(),searchFilter.getResultLanguage(),
				searchFilter.getMaxCost(), searchFilter.getMinCost(),
				valueWithoutFilter);
		Long numberOfPages= 0L;
		try {
			numberOfPages = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}: number of pages={}",
				className,methodName,numberOfPages);
		
		return numberOfPages;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public boolean saveResultDocAndMarkAsNotChecked(..)) "
			 + "&& args(email,adId,resultDocument)")
	public boolean saveResultDocAndMarkAsNotChecked(ProceedingJoinPoint thisJoinPoint,
												String email, 
												long adId, 
												ResultDocument resultDocument)
												throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		boolean marked = false;
		try {
			marked = (boolean)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(email={},adId={},ResultDocument=["
					+ "name={},content type={}]):{}",
					className,methodName,email,adId,resultDocument.getFileName(),
					resultDocument.getContentType(),e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}(email={},adId={},ResultDocument=["
				+ "name={},content type={}]): result = {}",
				className,methodName,email,adId,resultDocument.getFileName(),
				resultDocument.getContentType(),marked);
		
		return marked;
	}
	

	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getDocumentForDownloading(..)) "
			 + "&& args(adId,userEmail)")
	public Document getDocumentForDownloading(ProceedingJoinPoint thisJoinPoint,
												long adId, 
												String userEmail) throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Document document = null;
		try {
			document = (Document)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(adId={},userEmail={}):{}",
					className,methodName,adId,userEmail,e.getClass());
			throw e;
		}
		
		logger.error("{}.{}(adId={},userEmail={}):Document=[name={},contentType={}]",
				className,methodName,adId,userEmail,document.getFileName(),document.getContentType());
		
		return document;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getResultDocumentForDownloading(..)) "
			 + "&& args(adId,userEmail)")
	public ResultDocument getResultDocumentForDownloading(ProceedingJoinPoint thisJoinPoint,
												long adId, 
												String userEmail) throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		ResultDocument document = null;
		try {
			document = (ResultDocument)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(adId={},userEmail={}):{}",
					className,methodName,adId,userEmail,e.getClass());
			throw e;
		}
		
		logger.error("{}.{}(adId={},userEmail={}):ResultDocument=[name={},contentType={}]",
				className,methodName,adId,userEmail,document.getFileName(),document.getContentType());
		
		return document;
	}
	
	
}
