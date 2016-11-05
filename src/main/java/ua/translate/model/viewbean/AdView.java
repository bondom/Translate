package ua.translate.model.viewbean;

import java.time.LocalDateTime;

import ua.translate.model.ad.Ad;

/**
 * This bean is used for rendering {@link Ad}s for all visitors of website.
 * <p>Bean contains String field {@link AdView#messageWithPublishingTime AdView.messageWithPublishingTime},
 * which contains user-friendly message when {@code Ad} was published.
 * <br>Example:"4 hours ago"
 * <p>If user, requested page with {@code Ad}s, is translator,
 * String field {@link AdView#respondingTime AdView.respondingTime} contains
 * {@link LocalDateTime} when that translator responded on that particular {@code Ad}, or
 * {@code null} if that translator didn't respond on {@code Ad}
 * 
 * 
 * @author Yuriy Phediv
 *
 */
public class AdView {
	
	private Ad ad;
	
	private String messageWithPublishingTime;

	private LocalDateTime respondingTime;
	
	public AdView(Ad ad, String messageWithPublishingTime) {
		super();
		this.ad = ad;
		this.messageWithPublishingTime = messageWithPublishingTime;
	}

	
	public AdView(Ad ad, String messageWithPublishingTime, LocalDateTime respondingTime) {
		super();
		this.ad = ad;
		this.messageWithPublishingTime = messageWithPublishingTime;
		this.respondingTime = respondingTime;
	}


	public LocalDateTime getRespondingTime() {
		return respondingTime;
	}


	public void setRespondingTime(LocalDateTime respondingTime) {
		this.respondingTime = respondingTime;
	}


	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}

	public String getMessageWithPublishingTime() {
		return messageWithPublishingTime;
	}

	public void setMessageWithPublishingTime(String messageWithPublishingTime) {
		this.messageWithPublishingTime = messageWithPublishingTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ad == null) ? 0 : ad.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdView other = (AdView) obj;
		if (ad == null) {
			if (other.ad != null)
				return false;
		} else if (!ad.equals(other.ad))
			return false;
		return true;
	}
	
	
}
