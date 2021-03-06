package ua.translate.model;

import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SortComparator;

import ua.translate.controller.support.CommentComparatorByDate;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.model.ad.RespondedAd;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@NamedQueries({
	@NamedQuery(name =  "translatorByEmail",
				query = "from Translator translator where translator.email = :email"),
	@NamedQuery(name =  "getTranslatorsDescOrderByPubTime",
				query = "from Translator translator order by translator.publishingTime desc")
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
	
	@OneToMany(fetch = FetchType.EAGER,orphanRemoval = true,mappedBy = "translator")
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@SortComparator(value = CommentComparatorByDate.class)
	private SortedSet<Comment> comments = new TreeSet<>();
	
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
	private Set<RespondedAd> respondedAds = new LinkedHashSet<>();
	
	@Column(nullable = false)
	private LocalDateTime publishingTime;
	
	@OneToOne(optional=true, mappedBy="translator")
	private Ad ad;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "translator")
	@Fetch(FetchMode.SELECT)
	@Cascade(CascadeType.ALL)
	public Set<ArchievedAd> archievedAds = new LinkedHashSet<>();
	
	/**
	 * Sets {@link #role} to {@link UserRole#ROLE_TRANSLATOR UserRole.ROLE_TRANSLATOR}
	 * and {@link #publishingTime} to {@link LocalDateTime#now() LocalDateTime.now}
	 */
	public Translator(){
		super();
		role = UserRole.ROLE_TRANSLATOR;
		publishingTime = LocalDateTime.now();
	}
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

	public SortedSet<Comment> getComments() {
		return comments;
	}

	public void setRespondedAds(Set<RespondedAd> respondedAds) {
		this.respondedAds = respondedAds;
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

	public Set<RespondedAd> getRespondedAds() {
		return respondedAds;
	}

	public void addRespondedAd(RespondedAd respondedAd){
		respondedAds.add(respondedAd);
		respondedAd.setTranslator(this);
	}
	
	public void removeRespondedAd(RespondedAd respondedAd){
		respondedAds.remove(respondedAd);
	}
	public Ad getAd() {
		return ad;
	}
	public void setAd(Ad ad) {
		this.ad = ad;
	}
	public Set<ArchievedAd> getArchievedAds() {
		return archievedAds;
	}
	
	public void addArchievedAd(ArchievedAd archievedAd){
		archievedAds.add(archievedAd);
		archievedAd.setTranslator(this);
	}
	
	
	
}
