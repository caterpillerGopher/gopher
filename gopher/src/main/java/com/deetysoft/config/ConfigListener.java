package com.deetysoft.config;

/**
 * A listener interface for config hierarchy events.
 */
public interface ConfigListener
{
	/**
	 * Receive notification that Config has been initialized.
	 */
	public void initializeConfigValues();
}
