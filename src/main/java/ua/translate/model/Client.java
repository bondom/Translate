package ua.translate.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import ua.translate.model.UserEntity.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.model.ad.RespondedAd;

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
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "client")
	@Fetch(FetchMode.SELECT)
	@Cascade(CascadeType.ALL)
	public Set<Ad> ads = new LinkedHashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "client")
	@Fetch(FetchMode.SELECT)
	@Cascade(CascadeType.ALL)
	public Set<ArchievedAd> archievedAds = new LinkedHashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "client")
	@Fetch(FetchMode.SELECT)
	@Cascade(CascadeType.ALL)
	private Set<RespondedAd> respondedAds = new LinkedHashSet<>();


	/**
	 * Sets {@link #role} to {@link UserRole#ROLE_CLIENT UserRole.ROLE_CLIENT}
	 */
	public Client(){
		super();
		role = UserRole.ROLE_CLIENT;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public Set<Ad> getAds() {
		return ads;
	}

	public void addAd(Ad ad){
		ads.add(ad);
		ad.setClient(this);
	}
	
	public void removeAd(Ad ad){
		ads.remove(ad);
	}

	public Set<RespondedAd> getRespondedAds() {
		return respondedAds;
	}

	public void addRespondedAd(RespondedAd respondedAd){
		respondedAds.add(respondedAd);
		respondedAd.setClient(this);
	}
	
	public void removeRespondedAd(RespondedAd respondedAd){
		respondedAds.remove(respondedAd);
	}

	public Set<ArchievedAd> getArchievedAds() {
		return archievedAds;
	}
	
	public void addArchievedAd(ArchievedAd archievedAd){
		archievedAds.add(archievedAd);
		archievedAd.setClient(this);
	}
}
