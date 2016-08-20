package ua.translate.service;

import ua.translate.model.Comment;

public interface CommentService {

	/**
	 * Sets Creation date, sets clientName and translator, then saves {@code comment} in data storage
	 * @param clientEmail - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param translatorId - id of translator
	 * @return generated identifier of {@code comment}
	 */
	public long save(Comment comment,String clientEmail,long translatorId);
}
