package ua.translate.service;

import ua.translate.model.settings.Settings;
import ua.translate.service.exception.InvalidIdentifier;

public interface SettingsService {
	
	/**
	 * Returns {@link Settings} object, never {@code null}
	 * @throws InvalidIdentifier  if none {@code Settings} 
	 * object exist in data storage
	 */
	public Settings getProjectSettings() throws InvalidIdentifier;
	
	/**
	 * Updates {@link Settings} object, existed in data storage
	 * @param settings
	 */
	public void updateSettings(Settings settings);
}
