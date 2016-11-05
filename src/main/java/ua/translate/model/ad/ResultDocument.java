package ua.translate.model.ad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "RESULT_DOCUMENT_TEST")
public class ResultDocument extends Document{
	
	@Column(name = "MESSAGE_FOR_DOWNLOADER")
	private String messageForDownloader;

	
	public ResultDocument(){}
	public ResultDocument(byte[] file, String fileName, String contentType) {
		super(file, fileName, contentType);
	}
	
	public ResultDocument(byte[] file, String fileName, String contentType,String messageForDownloader) {
		super(file, fileName, contentType);
		this.messageForDownloader = messageForDownloader;
	}
	
	public ResultDocument(Ad ad, byte[] file, String fileName, String contentType) {
		super(ad, file, fileName, contentType);
	}
	
	public String getMessageForDownloader() {
		return messageForDownloader;
	}
	public void setMessageForDownloader(String messageForDownloader) {
		this.messageForDownloader = messageForDownloader;
	}
}
