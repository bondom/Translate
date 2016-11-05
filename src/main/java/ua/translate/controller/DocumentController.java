package ua.translate.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.WrittenAd;
import ua.translate.service.DocumentService;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.InvalidIdentifier;

/**
 * Contains handler methods for downloading {@link Document}s, related to {@link WrittenAd}
 * 
 * @author Yuriy Phediv
 *
 */
@Controller
@RequestMapping
public class DocumentController {
	
	@Autowired
	private DocumentService documentService;
	
	Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	/**
	 * Creates output stream with {@link Ad#getDocument() Ad.document} of Ad object with id={@code adId}
	 * <br>If {@code ad} with {@code adId} doesn't exist, or user don't have access to download it,
	 * returns 404 error page
	 * @param adId
	 * @param response
	 * @param user
	 */
	@RequestMapping("/download/{adId}")
	public ModelAndView downloadDocument(@PathVariable("adId")
			Long adId, HttpServletResponse response,Principal user) {
		if(user==null){
			return new ModelAndView("/exception/404");
		}
		Document textFile;
		try {
			textFile = documentService.getDocumentForDownloading(adId, user.getName());
			try {
				response.setHeader("Content-Disposition", "inline;filename=\"" +textFile.getFileName()+ "\"");
				OutputStream out = response.getOutputStream();
				response.setContentType(textFile.getContentType());
				out.write(textFile.getFile());
				out.flush();
				out.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (InvalidIdentifier eq) {
			logger.debug("User want to load file of ad, with id={}, which doesn't exist",adId);
			return new ModelAndView("/exception/404");
		} catch(DownloadFileAccessDenied  e1){
			logger.debug("User with email = {} hasn't access to load file of ad",user.getName());
			return new ModelAndView("/exception/404");
		}
		
		return null;
	}
	
	/**
	 * Creates output stream with {@link Ad#getResultDocument() AdresultDocument} of Ad object with id={@code adId}
	 * <br>If {@code ad} with {@code adId} doesn't exist, or user don't have access to download it,
	 * returns 404 error page
	 * @param adId
	 * @param response
	 * @param user
	 */
	@RequestMapping("/downloadr/{adId}")
	public ModelAndView downloadResultDocument(@PathVariable("adId")
			Long adId, HttpServletResponse response,Principal user) {
		if(user==null){
			return new ModelAndView("/exception/404");
		}
		Document textFile;
		try {
			textFile = documentService.getResultDocumentForDownloading(adId, user.getName());
			try {
				response.setHeader("Content-Disposition", "inline;filename=\"" +textFile.getFileName()+ "\"");
				OutputStream out = response.getOutputStream();
				response.setContentType(textFile.getContentType());
				out.write(textFile.getFile());
				out.flush();
				out.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (InvalidIdentifier eq) {
			logger.debug("User want to load file of ad, with id={}, which doesn't exist",adId);
			return new ModelAndView("/exception/404");
		} catch(DownloadFileAccessDenied  e1){
			logger.debug("User with email = {} hasn't access to load file of ad",user.getName());
			return new ModelAndView("/exception/404");
		}
		
		return null;
	}
}
