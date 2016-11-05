package ua.translate.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.SettingsDao;
import ua.translate.model.settings.Settings;
import ua.translate.service.SettingsService;
import ua.translate.service.exception.InvalidIdentifier;

@Service
@Transactional
public class SettingsServiceImpl implements SettingsService{

	@Autowired
	private SettingsDao settingsDao;
	
	Logger logger = LoggerFactory.getLogger(SettingsServiceImpl.class);
	
	@Override
	public Settings getProjectSettings(){
		Settings settings = settingsDao.get(1);
		if(settings==null){
			logger.warn("Settings doesn't exist in data storage");
		}
		return new Settings();
	}

	@Override
	public void updateSettings(Settings settings) {
		settings.setId(1);
		settingsDao.update(settings);
	}

}
