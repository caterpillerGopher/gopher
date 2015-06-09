package com.deetysoft.exception;

import com.deetysoft.collections.ArrayCreator;

/**
 * A property was not found.
 */
@SuppressWarnings("serial")
public class MissingPropertyException extends RuntimeException_
{
	/**
	 * The default message - "Property {0} not found in {1}.".
	 */
	public static final String DEFAULT_MESSAGE =
		"Property {0} not found in {1}.";

	/**
	 * Construct using the given message, the default key and null args.
	 * 
	 * @param	string	the message for the exception
	 */
	public MissingPropertyException (String string)
	{
		super (string);
	}

	/**
	 * Construct using {@link #DEFAULT_MESSAGE DEFAULT_MESSAGE}.
	 * The default message requires a property name and store name.
	 * Note that with this method, the message key defaults to this
	 * class name.
	 *
	 * @param	propertyName	the name of the missing property
	 * @param	storeName		the data store of concern
	 */
	public MissingPropertyException (String propertyName, String storeName)
	{
		super (DEFAULT_MESSAGE, null,
			ArrayCreator.create (propertyName, storeName));
	}

	/**
	 * Construct with a message, a key and run-time arguments.
	 *
	 * @param	msg			the message
	 * @param	key			the key
	 * @param	args		the run-time args
	 */
	public MissingPropertyException (String msg, Object key, Object[] args)
	{
		super (msg, key, args);
	}
}
