package org.dream.university.model;

import javax.persistence.Table;

import org.dream.university.model.ad.Language;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@NamedQueries({
	@NamedQuery(name =  "translatorByEmail",
				query = "from Translator translator where translator.email = :email")
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
	
	@OneToMany(fetch = FetchType.EAGER,orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	private List<Comment> comments = new ArrayList<>();
	
	@Column(nullable = false)
	private short numberOfExecutedAds;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = Language.class,fetch = FetchType.EAGER)
	private List<Language> languages = new ArrayList<>();
	
	private String addedInfo;

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

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}
	
	
	
}
