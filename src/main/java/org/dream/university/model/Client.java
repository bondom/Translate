package org.dream.university.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.dream.university.model.ad.Ad;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
	
}
