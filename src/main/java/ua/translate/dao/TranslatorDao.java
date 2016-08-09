package ua.translate.dao;

import java.util.List;

import ua.translate.model.Translator;

public abstract class TranslatorDao  extends AbstractUserDao<Long, Translator>{
	
	public abstract Translator getTranslatorByEmail(String email);
	public abstract List<Translator> getAllTranslators();
}
