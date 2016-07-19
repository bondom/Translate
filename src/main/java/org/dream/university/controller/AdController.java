package org.dream.university.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dream.university.model.ad.Ad;
import org.dream.university.service.AdServiceImpl;
import org.dream.university.support.AdComparatorByDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/ads")
public class AdController {
	
	@Autowired
	AdServiceImpl adService;
	
	@RequestMapping(value = "/{adId}", method = RequestMethod.GET)
	public ModelAndView ad(@PathVariable("adId") long adId){
		Ad ad = adService.get(adId);
		if(ad == null){
			return new ModelAndView("/exception/404");
		}else{
			ModelAndView model = new ModelAndView("/showedAd");
			LocalDate creationDate = ad.getCreationDateTime().toLocalDate();
			
			model.addObject("ad", ad);
			/**
			 * «аменить на дату публикации и возможность скрывать и показывать объ€влени€
			 * (смена статуса)
			 */
			model.addObject("creationDate",creationDate);
			return model;
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView allAds(){
		List<Ad> ads = adService.getAllAds();
		
		Collections.sort(ads, new AdComparatorByDate());
		Collections.reverse(ads);
		
		Map<String,Ad> adsWithTimeInfo = new LinkedHashMap<>();
		ads.forEach(ad ->{
			adsWithTimeInfo.put(getTimeInfo(ad),ad);
		});
		
		ModelAndView model = new ModelAndView("/adsForAll");
		model.addObject("adsTime", adsWithTimeInfo);
		return model;
	}
	
	private String getTimeInfo(Ad ad){
		LocalDateTime creationTime = ad.getCreationDateTime();
		LocalDateTime now = LocalDateTime.now();
		Integer existsInMonths = now.getMonth().getValue() - creationTime.getMonth().getValue();
		if(existsInMonths>0){
			if(existsInMonths==1){
				return existsInMonths + " month ago";
			} else return existsInMonths + " months ago";
		}
		
		Integer existsInDays = now.getDayOfMonth()- creationTime.getDayOfMonth();
		if(existsInDays>0){
			System.out.println(creationTime + " " + existsInDays + " days");
			if(existsInDays==1){
				return existsInDays + " day ago";
			} else return existsInDays + " days ago";
		}
		
		Integer existsInHours= now.getHour()- creationTime.getHour();
		if(existsInHours>0){
			System.out.println(creationTime + " " + existsInHours + " hours");
			if(existsInHours==1){
				return existsInHours + " hour ago";
			}else return existsInHours + " hours ago";
		}
		
		Integer existsInMinutes= now.getMinute()- creationTime.getMinute();
		if(existsInMinutes>0){
			System.out.println(creationTime + " " + existsInMinutes + " minutes");
			if(existsInMinutes==1){
				return existsInMinutes + " minute ago";
			}else return existsInMinutes+ " minutes ago";
		}
		
		Integer existsInSeconds = now.getSecond()- creationTime.getSecond();
		return existsInSeconds+ " seconds ago";
	}
}
