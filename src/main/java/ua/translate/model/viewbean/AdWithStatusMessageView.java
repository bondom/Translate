package ua.translate.model.viewbean;

import ua.translate.model.UserEntity.UserRole;
import ua.translate.model.ad.Ad;

/**
 * This bean is used for rendering {@link Ad} or {@link Ad}s, which belong(s)
 * to {@link Client} or {@link Translator}
 * 
 * <p>{@link AdWithStatusMessageView#statusMessage AdWithStatusMessageView.statusMessage}
 * contains message, which depends on {@link UserRole} and {@link Ad#getStatus() Ad.status}
 * 
 * @author Yuriy Phediv
 *
 */
public class AdWithStatusMessageView {
	
	private Ad ad;
	
	private String statusMessage;

	public AdWithStatusMessageView(Ad ad, String statusMessage) {
		super();
		this.ad = ad;
		this.statusMessage = statusMessage;
	}

	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
}
