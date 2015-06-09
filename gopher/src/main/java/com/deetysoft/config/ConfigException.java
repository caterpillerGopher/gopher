package com.deetysoft.config;

import com.deetysoft.exception.Exception_;

/**
 * An exception occurred in the config hierarchy class.
 */
@SuppressWarnings("serial")
public class ConfigException extends Exception_
{
	/**
	 * Construct with message text and cause.
	 *
	 * @param	msg		the message
	 * @param	cause	cause Throwable or null
	 */
	public ConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Construct with a message and a key.
	 *
	 * @param	msg		the message
	 * @param	key		the message key or null
	 * @param	cause	cause Throwable or null
	 */
	public ConfigException(String msg, Object key, Throwable cause) {
		super(msg, key, cause);
	}

	/**
	 * Construct with a message, a key and run-time arguments.
	 *
	 * @param	msg		the message
	 * @param	key		the message key or null
	 * @param	args	the run-time args
	 * @param	cause	cause Throwable or null
	 */
	public ConfigException (String msg, Object key, Object[] args,
			Throwable cause) {
		super(msg, key, args, cause);
	}

	/**
	 * Construct with a message, a key and a run-time argument.
	 *
	 * @param	msg		the message
	 * @param	key		the message key or null
	 * @param	arg		the run-time arg
	 * @param	cause	cause Throwable or null
	 */
	public ConfigException (String msg, Object key, Object arg,
			Throwable cause) {
		super(msg, key, arg, cause);
	}
}
