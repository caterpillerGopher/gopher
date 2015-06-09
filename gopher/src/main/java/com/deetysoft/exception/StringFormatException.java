package com.deetysoft.exception;

import com.deetysoft.collections.ArrayCreator;

/**
 * A string has invalid format.
 */
@SuppressWarnings("serial")
public class StringFormatException extends RuntimeException_
{
	/**
	 * The default message - "String \"{0}\" has invalid format.".
	 */
	public static final String DEFAULT_MESSAGE =
		"String \"{0}\" has invalid format.";

	/**
	 * Construct using the given message.
	 * 
	 * @param	string	the message
	 */
	public StringFormatException (String string)
	{
		super (string);
	}

	/**
	 * Construct with a message, a key and run-time arguments.
	 *
	 * @param	msg			the message
	 * @param	key			the key
	 * @param	args		the run-time args
	 */
	public StringFormatException (String msg, Object key, Object[] args)
	{
		super (msg, key, args);
	}

	/**
	 * Construct a StringFormatException using the
	 * {@link #DEFAULT_MESSAGE DEFAULT_MESSAGE},
	 * the default key and the given argument for the default message.
	 *
	 * @param	string	the value of the invalid string
	 * @return			the exception
	 */
	public static StringFormatException create (String string)
	{
		return new StringFormatException (DEFAULT_MESSAGE, null,
			ArrayCreator.create (string));
	}
}
