package ua.translate.model.viewbean;

import javax.validation.constraints.NotNull;

import ua.translate.model.status.AdStatus;

public class SearchAdByStatus {
	
	@NotNull
	private AdStatus adStatus;

	public AdStatus getAdStatus() {
		return adStatus;
	}

	public void setAdStatus(AdStatus adStatus) {
		this.adStatus = adStatus;
	}
	
	
}
