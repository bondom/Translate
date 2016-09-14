package ua.translate.test.service;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.translate.dao.AdDao;
import ua.translate.dao.ClientDao;
import ua.translate.model.Client;
import ua.translate.model.ad.Ad;
import ua.translate.model.status.AdStatus;
import ua.translate.service.exception.IllegalActionForAcceptedAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.NonExistedRespondedAdException;
import ua.translate.service.exception.TooManyAds;
import ua.translate.service.impl.AdServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {
	
	@Mock private ClientDao clientDao;
	@Mock private AdDao adDao;
	
	private Client client;
	private Ad ad;
	
	@InjectMocks
	private AdServiceImpl adService;
	Logger logger = LoggerFactory.getLogger(RespondedAdServiceTest.class);
	
	@Before
	public void init(){
		client = new Client();
		client.setEmail("test@mail.ru");
		ad = new Ad();
		client.addAd(ad);
	}
	
	@Test
	public void testGetAdById() throws NonExistedAdException{
		long adId = 3L;
		logger.debug("-----------------testGetAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return Ad",adId);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Calling AdService.get({})",
				adId);
		adService.get(adId);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(adDao).get(adId);
	}
	
	@Test(expected = NonExistedAdException.class)
	public void testGetAdByInvalidId() throws NonExistedAdException{
		long nonExistedAdId = 2L;
		logger.debug("-----------------testGetAdByInvalidId------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",nonExistedAdId);
		when(adDao.get(nonExistedAdId)).thenReturn(null);
		try{
			logger.debug("Calling AdService.get({}) should"
					+ " throw NonExistedAdException",
					nonExistedAdId);
			adService.get(nonExistedAdId);
		}catch(NonExistedAdException ex){
			logger.debug("NonExistedAdException has been thrown");
			throw ex;
		}
	}
	
	@Test
	public void testDeleteAdById() throws NonExistedAdException, 
											IllegalActionForAcceptedAd{
		long adId = 3L;
		ad.setStatus(AdStatus.SHOWED);
		logger.debug("-----------------testDeleteAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return SHOWED Ad",adId);
		when(adDao.get(adId)).thenReturn(ad);
		logger.debug("Calling AdService.deleteById({})",
				adId);
		adService.deleteById(adId);
		logger.debug("Verifying respondedAdDao.get({}) is called",adId); 
		verify(adDao).get(adId);
	}
	
	@Test(expected = NonExistedAdException.class)
	public void testDeleteNotExistedAdById() throws NonExistedAdException, 
											IllegalActionForAcceptedAd{
		long nonExistedAdId = 3L;
		logger.debug("-----------------testDeleteNotExistedAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return null",nonExistedAdId);
		when(adDao.get(nonExistedAdId)).thenReturn(null);
		
		try{
			logger.debug("Calling AdService.deleteById({}) should throw"
					+ " NonExistedAdException",nonExistedAdId);
			adService.deleteById(nonExistedAdId);
		}catch(NonExistedAdException ex){
			logger.debug("NonExistedAdException has been thrown");
			verify(adDao).get(nonExistedAdId);
			logger.debug("Verified AdDao.get({}) is called",nonExistedAdId); 
			throw ex;
		}
	}
	
	@Test(expected = IllegalActionForAcceptedAd.class)
	public void testDeleteAcceptedAdById() throws NonExistedAdException, 
											IllegalActionForAcceptedAd{
		long adId = 3L;
		ad.setStatus(AdStatus.ACCEPTED);
		logger.debug("-----------------testDeleteAcceptedAdById------------");
		logger.debug("Stubbing AdDao.get({}) "
				+ "to return ACCEPTED Ad",adId);
		when(adDao.get(adId)).thenReturn(ad);
		
		try{
			logger.debug("Calling AdService.deleteById({}) should throw"
					+ " IllegalActionForAcceptedAd",adId);
			adService.deleteById(adId);
		}catch(IllegalActionForAcceptedAd ex){
			logger.debug("IllegalActionForAcceptedAd has been thrown");
			verify(adDao).get(adId);
			logger.debug("Verified AdDao.get({}) is called",adId); 
			throw ex;
		}
	}
	
	@Test
	public void testSaveAd() throws TooManyAds{
		long generatedAdId = 3L;
		long maxNumberOfAds = 3;
		String email = client.getEmail();
		ad.setStatus(AdStatus.SHOWED);
		client.addAd(new Ad());
		
		logger.debug("-----------------testSaveAd------------");
		logger.debug("Stubbing ClientDao.getClientByEmail({}) "
				+ "to return Client, who has {} ads, it is  less then {} ",
				email,client.getAds().size(),maxNumberOfAds);
		when(clientDao.getClientByEmail(email)).thenReturn(client);
		logger.debug("Stubbing AdDao.save(Ad with SHOWED status) "
				+ "to return genetared id of saved ad={}",generatedAdId);
		when(adDao.save(ad)).thenReturn(generatedAdId);
		logger.debug("Calling AdService.saveAd(Ad with SHOWED status,{},{})",
				email,maxNumberOfAds);
		adService.saveAd(ad, email, maxNumberOfAds);
		logger.debug("Verifying ClientDao.getClientByEmail({}) is called",email); 
		verify(clientDao).getClientByEmail(email);
		logger.debug("Verifying AdDao.save(Ad with SHOWED status) is called"); 
		verify(adDao).save(ad);
		
		
		
	}
	
}
