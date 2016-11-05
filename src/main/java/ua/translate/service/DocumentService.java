package ua.translate.service;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.ResultDocument;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.InvalidIdentifier;

public interface DocumentService {
	
	/**
	 * Gets {@link Document} from data storage by its ad id
	 * <p>Invoke this method if user attempts to download {@link Ad#getDocument() Ad.document}, 
	 * related to ad with id={@code adId}<br>
	 * <p>File can be downloaded only by translators or owner of advertisement, 
	 * to which the file is related. If another user attempts to get file, exception is thrown
	 * @param adId - ad id
	 * @param userEmail - email of authenticated user, who attempts to download file, related to ad
	 * @return {@code Document} object, never {@code null}  
	 * @throws InvalidIdentifier if {@code Ad} with such id doesn't exist
	 * @throws DownloadFileAccessDenied if user with email={@code userEmail} is not translator and
	 * is not owner of this ad
	 * @see #getForShowing(long)
	 * @see #getForUpdating(long)
	 */
	public abstract Document getDocumentForDownloading(long adId,String userEmail) throws InvalidIdentifier,DownloadFileAccessDenied;
	
	/**
	 * Gets {@link ResultDocument} {@code resultDocument} from data storage by its {@link Ad} ad id
	 * <p>Invoke this method if user attempts to download {@link Ad#getResultDocument() Ad.resultDocument}
	 * related to {@link Ad} {@code ad} with id={@code adId}<br>
	 * <p>File <b>can be downloaded</b> only by translator, who translated this text, by admin,
	 * and by owner of advertisement(if status of {@code ad} is PAYED).
	 *  If another user attempts to get file, exception is thrown
	 * @param adId - ad id
	 * @param userEmail - email of authenticated user, who attempts to download file, related to ad
	 * @return {@code Document} object, never {@code null}  
	 * @throws InvalidIdentifier if {@code Ad} with such id doesn't exist
	 * @throws DownloadFileAccessDenied if user with email={@code userEmail} hasn't access
	 * for downloading
	 * @see #getForShowing(long)
	 * @see #getForUpdating(long)
	 */
	public abstract ResultDocument getResultDocumentForDownloading(long adId,String userEmail) throws InvalidIdentifier,DownloadFileAccessDenied;
}
