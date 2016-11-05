package ua.translate.model.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Represents global settings of project
 * 
 * @author Yuriy Phediv
 *
 */
@Entity
@Table(name = "PROJECT_SETTINGS")
public class Settings {
	
	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	private int id;
	
	@Min(1)
	@NotNull
	@Column(name="MAX_N_CLIENT_ADS")
	private int maxNumberOfAdsForClient;

	@Min(1)
	@NotNull
	@Column(name="MAX_N_TRNSLTR_SENDED_RADS")
	private int maxNumberOfSendedRespondedAdsForTranslator;
	
	@Min(1)
	@NotNull
	@Column(name="MIN_HOURS_REFRESHINGS")
	private int minHoursBetweenRefreshings;
	
	@Min(1)
	@Max(100)
	@Column(name="PLEDGE")
	private int initPledgeInPercent;
	
	@Min(1)
	@NotNull
	@Column(name="MAX_N_NOTCHECKED_ADS_PAGE")
	private int maxNumberNotCheckedAdsOnOnePage;
	
	@Min(1)
	@NotNull
	@Column(name="MAX_N_TRNSLTRS__PAGE")
	private int maxNumberTranslatorsOnOnePage;
	
	@Min(1)
	@NotNull
	@Column(name="MAX_N_RADS_PAGE")
	private int maxNumberOfRespondedAdsOnOnePage;
	
	@Min(1)
	@NotNull
	@Column(name="MAX_N_ADS_PAGE")
	private int maxNumberOfAdsOnOnePage;
	
	public int getMaxNumberOfAdsForClient() {
		return maxNumberOfAdsForClient;
	}

	public void setMaxNumberOfAdsForClient(int maxNumberOfAdsForClient) {
		this.maxNumberOfAdsForClient = maxNumberOfAdsForClient;
	}

	public int getMinHoursBetweenRefreshings() {
		return minHoursBetweenRefreshings;
	}

	public void setMinHoursBetweenRefreshings(int minHoursBetweenRefreshings) {
		this.minHoursBetweenRefreshings = minHoursBetweenRefreshings;
	}

	public int getMaxNumberOfSendedRespondedAdsForTranslator() {
		return maxNumberOfSendedRespondedAdsForTranslator;
	}

	public void setMaxNumberOfSendedRespondedAdsForTranslator(int maxNumberOfSendedRespondedAdsForTranslator) {
		this.maxNumberOfSendedRespondedAdsForTranslator = maxNumberOfSendedRespondedAdsForTranslator;
	}

	public int getInitPledgeInPercent() {
		return initPledgeInPercent;
	}

	public void setInitPledgeInPercent(int initPledgeInPercent) {
		this.initPledgeInPercent = initPledgeInPercent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMaxNumberNotCheckedAdsOnOnePage() {
		return maxNumberNotCheckedAdsOnOnePage;
	}

	public void setMaxNumberNotCheckedAdsOnOnePage(int maxNumberNotCheckedAdsOnOnePage) {
		this.maxNumberNotCheckedAdsOnOnePage = maxNumberNotCheckedAdsOnOnePage;
	}

	public int getMaxNumberTranslatorsOnOnePage() {
		return maxNumberTranslatorsOnOnePage;
	}

	public void setMaxNumberTranslatorsOnOnePage(int maxNumberTranslatorsOnOnePage) {
		this.maxNumberTranslatorsOnOnePage = maxNumberTranslatorsOnOnePage;
	}

	public int getMaxNumberOfRespondedAdsOnOnePage() {
		return maxNumberOfRespondedAdsOnOnePage;
	}

	public void setMaxNumberOfRespondedAdsOnOnePage(int maxNumberOfRespondedAdsOnOnePage) {
		this.maxNumberOfRespondedAdsOnOnePage = maxNumberOfRespondedAdsOnOnePage;
	}

	public int getMaxNumberOfAdsOnOnePage() {
		return maxNumberOfAdsOnOnePage;
	}

	public void setMaxNumberOfAdsOnOnePage(int maxNumberOfAdsOnOnePage) {
		this.maxNumberOfAdsOnOnePage = maxNumberOfAdsOnOnePage;
	}

	
	
	
	
	
	
}
