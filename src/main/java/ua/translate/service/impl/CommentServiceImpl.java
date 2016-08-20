package ua.translate.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ClientDao;
import ua.translate.dao.CommentDao;
import ua.translate.dao.TranslatorDao;
import ua.translate.model.Client;
import ua.translate.model.Comment;
import ua.translate.model.Translator;
import ua.translate.service.CommentService;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDao commentDao;
	
	@Autowired
	private ClientDao clientDao;
	

	@Autowired
	private TranslatorDao translatorDao;
	
	@Override
	public long save(Comment comment,String clientEmail,long translatorId) {
		comment.setCreatingDate(LocalDateTime.now());
		Client client = clientDao.getClientByEmail(clientEmail);
		comment.setClientName(client.getFirstName() + " " + client.getLastName());
		Translator translator = translatorDao.get(translatorId);
		comment.setTranslator(translator);
		
		return commentDao.save(comment);
	}

}
