package org.dream.university.model;

import javax.persistence.Table;

import org.dream.university.model.ad.Language;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
	@NamedQuery(name =  "translatorByEmail",
				query = "from Translator translator where translator.email = :email")
})
@Table(name = "TRANSLATOR_TEST")
public class Translator extends User{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private double rating;
	
	@OneToMany(/*mappedBy = "translator",*/ fetch = FetchType.LAZY,orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	private List<Comment> comments;
	
	@Column(nullable = false)
	private short numberOfExecutedAds;
	
	@Column
	@Enumerated
	@ElementCollection(targetClass = Language.class)
	private List<Language> languages;
	
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
	
	
}
