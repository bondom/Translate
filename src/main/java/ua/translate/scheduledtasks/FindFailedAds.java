package ua.translate.scheduledtasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.service.WrittenAdService;

@Component
public class FindFailedAds {
	
	@Autowired
	private WrittenAdService writtenAdService;
	
	private static Logger logger = LoggerFactory.getLogger(FindFailedAds.class);
	
	private AdStatus[] adStatusesForSearching = {AdStatus.ACCEPTED,AdStatus.REWORKING};
	
	/**
	 * Gets from data storage {@link WrittenAd}s, which have status, equal to one of 
	 * {@link #adStatusesForSearching}. Checks if 
	 * {@link WrittenAd#getEndDate() WrittenAd.endDate} is more than 
	 * {@code LocalDateTime.now()}. If it is, change status of this ad to 
	 * {@link AdStatus#FAILED FAILED} and updates it in data storage.
	 * <p> This method will be fired on the top of every day. 
	 */
	@Scheduled(cron="0 0 0 * * *")
	public void findWrittenAdsWithPassedDateAndMarkAsFailed(){
		Set<WrittenAd> writtenAds = writtenAdService.getAllWrittenAdsByStatuses(
								new HashSet(Arrays.asList(adStatusesForSearching)));
		Set<WrittenAd> failedAds = 
				writtenAds.stream()
						  .filter(wad -> statusIsDesired(wad.getStatus()))
						  .filter(wadForChecking -> LocalDate.now().isAfter(
								  					wadForChecking.getEndDate()))
						  .collect(Collectors.toSet());
		failedAds.forEach(failedWrittenAd ->{
			logger.debug("WrittenAd with id={} was failed by translator with email={}",
						  failedWrittenAd.getId(),failedWrittenAd.getTranslator().getEmail());
			failedWrittenAd.setStatus(AdStatus.FAILED);
			writtenAdService.updateWrittenAd(failedWrittenAd);
		});
	}
	
	/**
	 * Checks if {@code adStatus} equals to one of status from {@link #adStatusesForSearching}.
	 * If it is return true, else false
	 * @param adStatus - {@link AdStatus}
	 */
	private boolean statusIsDesired(AdStatus adStatus){
		if(AdStatus.ACCEPTED.equals(adStatus) ||
 	     AdStatus.REWORKING.equals(adStatus)){
			return true;
		}else return false;
	}
}
