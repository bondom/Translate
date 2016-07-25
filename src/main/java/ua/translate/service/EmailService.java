package ua.translate.service;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*!!!!Replace with AOP!!!!*/
@Service
@Transactional
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	/*!!!!Link for confirmation is not link!!!!*/
	public void sendConfirmationEmailMessage(String email,Long clientId){

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mimeMessage,false,"UTF-8");
			String htmlMsg = "<html><body><p>Please, go to <a href='localhost:8080/university/client/confirmation?uid="+clientId+"'>languages.ru/confirmation?uid="+clientId+"</a> for confirmation "
					+ "your registration</body></html> ";
			helper.setTo(email);
			helper.setSubject("Email confirmation");
			helper.setFrom("admin@languages.ru");
			helper.setText(htmlMsg, true);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
