package ua.translate.model;

import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@NamedQueries({
	@NamedQuery(name =  "translatorByEmail",
				query = "from Translator translator where translator.email = :email"),
	@NamedQuery(name =  "allTranslators",
				query = "from Translator")
})
@Table(name = "TRANSLATOR_TEST")
@PrimaryKeyJoinColumn(name= "translator_id")
public class Translator extends User{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private double rating;
	
	@OneToMany(fetch = FetchType.LAZY,orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private List<Comment> comments = new ArrayList<>();
	
	@Column(nullable = false)
	private short numberOfExecutedAds;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = Language.class,fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	private Set<Language> languages = new LinkedHashSet<>();
	
	private String addedInfo;

	@OneToMany(fetch = FetchType.LAZY,orphanRemoval = true,mappedBy = "translator")
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<ResponsedAd> responsedAds = new LinkedHashSet<>();
	
	@Column(nullable = false)
	private LocalDateTime publishingTime;
	
	public LocalDateTime getPublishingTime() {
		return publishingTime;
	}

	public void setPublishingTime(LocalDateTime publishingTime) {
		this.publishingTime = publishingTime;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public short getNumberOfExecutedAds() {
		return numberOfExecutedAds;
	}

	public void setNumberOfExecutedAds(short numberOfExecutedAds) {
		this.numberOfExecutedAds = numberOfExecutedAds;
	}

	public String getAddedInfo() {
		return addedInfo;
	}

	public void setAddedInfo(String addedInfo) {
		this.addedInfo = addedInfo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public Set<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<Language> languages) {
		this.languages = languages;
	}

	public Set<ResponsedAd> getResponsedAds() {
		return responsedAds;
	}

	public void addResponsedAd(ResponsedAd responsedAd){
		responsedAds.add(responsedAd);
		responsedAd.setTranslator(this);
	}
	
	public void removeResponsedAd(ResponsedAd responsedAd){
		responsedAds.remove(responsedAd);
	}
	
}
