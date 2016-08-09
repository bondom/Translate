package ua.translate.model.viewbean;

import ua.translate.model.Translator;

public class TranslatorView {
	
	private Translator translator;
	
	private String avatar;
	
	private String messageWithPublishingTime;

	
	public TranslatorView(Translator translator, String avatar, String messageWithPublishingTime) {
		this.translator = translator;
		this.avatar = avatar;
		this.messageWithPublishingTime = messageWithPublishingTime;
	}

	public Translator getTranslator() {
		return translator;
	}


	public void setTranslator(Translator translator) {
		this.translator = translator;
	}


	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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
		result = prime * result + ((translator == null) ? 0 : translator.hashCode());
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
		TranslatorView other = (TranslatorView) obj;
		if (translator == null) {
			if (other.translator != null)
				return false;
		} else if (!translator.equals(other.translator))
			return false;
		return true;
	}

	
	
	
}
