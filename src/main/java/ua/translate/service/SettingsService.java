package ua.translate.service;

import ua.translate.model.settings.Settings;

public interface SettingsService {
	
	/**
	 * Attempts to get and return from data storage {@link Settings} object,
	 * if such object doesn't exist, create new one(with constructor without args) 
	 * and return it.
	 */
	public Settings getProjectSettings();
	
	/**
	 * Updates {@link Settings} object, existed in data storage
	 * @param settings
	 */
	public void updateSettings(Settings settings);
}
