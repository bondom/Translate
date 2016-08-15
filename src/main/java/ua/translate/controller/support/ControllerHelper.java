package ua.translate.controller.support;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ControllerHelper {
	
	private static Logger logger = LoggerFactory.getLogger(ControllerHelper.class);
	
	/**
	 * Converts and returns user's avatar from byte[] representation to String representation,<br>
	 * if {@code ava == null} returns String representation of default avatar
	 *
	 * @throws UnsupportedEncodingException
	 */
	public String getAvaForRendering(byte[] ava) throws UnsupportedEncodingException{
		if(ava != null){
			byte[] encodeBase64 = Base64.encodeBase64(ava);
			String base64Encoded = new String(encodeBase64,"UTF-8");
			return base64Encoded;
		}
		
		//If user doesn't have avatar, return default avatar
		StringBuilder result = new StringBuilder("");

		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file;
		try {
			file = new File(classLoader.getResource("/defaultAva.txt").toURI());
			try (Scanner scanner = new Scanner(file)) {
				
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					result.append(line).append("\n");
				}
				scanner.close();
			} catch (IOException e) {
				logger.info("Problem with reading default avatar: {}",e.getMessage());
			}
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		return result.toString();
		
	}
	
	/*!!!!Need recoding!!!!*/
	/**
	 * Returns user-friendly string representation of period of time between {@code dateTime}
	 * and {@code LocalDateTime.now()} close to a larger unit of measurement of time.
	 * 
	 * <p>{@code dateTime} must be less than {@code LocalDateTime.now()}, if this condition is not executed
	 * method returns empty string
	 * <p>Example #1:
	 * <pre>	
	 * 	LocalDateTime dateTime = LocalDateTime.of(2016,Month.APRIL,15,14,30) //2016.04.15 14:30:00
	 * 	LocalDateTime.now()// 2016.07.20 13:13:13
	 * 	String relativeTime = getStringRelativeTime(dateTime);// 3 months ago
	 * </pre>
	 * 
	 * <p>Example #2:
	 * <pre>
	 *  	LocalDateTime dateTime = LocalDateTime.of(2016,Month.JULY,15,14,30) //2016.07.15 14:30:00
	 * 	LocalDateTime.now()// 2016.07.20 13:13:13
	 * 	String relativeTime = getStringRelativeTime(dateTime);// 5 days ago
	 * </pre>
	 * 
	 * @return user-friendly string representation of period of time between {@code dateTime}
	 * and {@code LocalDateTime.now()}
	 */
	public String getStringRelativeTime(LocalDateTime dateTime){
		LocalDateTime now = LocalDateTime.now();
		
		if(dateTime.compareTo(now)>0){
			logger.error("dateTime is more than LocalDateTime.now()");
			return "";
		}
		
		Integer existsInYears = now.getYear()- dateTime.getYear();
		if(existsInYears>0){
			logger.debug("{} month(s) ago",existsInYears);
			if(existsInYears==1){
				return "1 year ago";
			} else return existsInYears + " years ago";
		}
		
		Integer existsInMonths = now.getMonth().getValue() - dateTime.getMonth().getValue();
		if(existsInMonths>0){
			logger.debug("{} month(s) ago",existsInMonths);
			if(existsInMonths==1){
				return "1 month ago";
			} else return existsInMonths + " months ago";
		}
		
		Integer existsInDays = now.getDayOfMonth()- dateTime.getDayOfMonth();
		if(existsInDays>0){
			logger.debug("{} day(s) ago",existsInDays);
			if(existsInDays==1){
				return "1 day ago";
			} else return existsInDays + " days ago";
		}
		
		Integer existsInHours= now.getHour()- dateTime.getHour();
		if(existsInHours>0){
			logger.debug("{} hour(s) ago",existsInHours);
			if(existsInHours==1){
				return "1 hour ago";
			}else return existsInHours + " hours ago";
		}
		
		Integer existsInMinutes= now.getMinute()- dateTime.getMinute();
		if(existsInMinutes>0){
			logger.debug("{} minutes(s) ago",existsInMinutes);
			if(existsInMinutes==1){
				return "1 minute ago";
			}else return existsInMinutes+ " minutes ago";
		}
		
		Integer existsInSeconds = now.getSecond()- dateTime.getSecond();
		logger.debug("{} second(s) ago",existsInSeconds);
		if(existsInSeconds == 1){
			return "1 second ago";
		}
		return existsInSeconds+ " seconds ago";
	}
	
}
