package ua.translate.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;

@Entity
@NamedQueries({
	@NamedQuery(name =  "clientByEmail",
				query = "from Client client where client.email = :email")
})
@Table(name = "CLIENT_TEST")
@PrimaryKeyJoinColumn(name= "client_id")
public class Client extends User{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;
	
	@OneToMany(fetch = FetchType.EAGER,orphanRemoval = true,mappedBy = "client")
	@Cascade(CascadeType.ALL)
	public List<Ad> ads = new ArrayList<>();
	
	@OneToMany(fetch = FetchType.EAGER,orphanRemoval = true,mappedBy = "client")
	@Cascade(CascadeType.ALL)
	private List<ResponsedAd> responsedAds = new ArrayList<>();

	public Client(){}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public List<Ad> getAds() {
		return ads;
	}

	public void setAds(List<Ad> ads) {
		this.ads = ads;
	}
	
	public void addAd(Ad ad){
		ads.add(ad);
		ad.setClient(this);
	}
	
	public void removeAd(Ad ad){
		ads.remove(ad);
		ad.setClient(null);
	}

	public List<ResponsedAd> getResponsedAds() {
		return responsedAds;
	}
	
	public void addResponsedAd(ResponsedAd responsedAd){
		responsedAds.add(responsedAd);
		responsedAd.setClient(this);
	}
	
	public void removeResponsedAd(ResponsedAd responsedAd){
		responsedAds.remove(responsedAd);
		responsedAds.remove(responsedAd);
		responsedAd.setClient(null);
	}
}
