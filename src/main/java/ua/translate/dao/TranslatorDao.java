package ua.translate.dao;

import ua.translate.model.Translator;

public abstract class TranslatorDao  extends AbstractUserDao<Long, Translator>{
	public abstract Translator getTranslatorByEmail(String email);
}
