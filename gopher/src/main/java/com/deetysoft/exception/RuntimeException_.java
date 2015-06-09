package com.deetysoft.exception;

import com.deetysoft.collections.ArrayCreator;

import java.text.MessageFormat;

/**
 * An {@link java.lang.RuntimeException RuntimeException} subclass to
 * distinguish 'our' exceptions from Java and third-party exceptions.
 * <p>
 * We add two properties to support
 * localization - a message key and run-time arguments.
 * The key can be used to store an identifier for the message.
 * It could be a resource name
 * or database index, for example.
 * If a key is not specified, getClass().getName() is used.
 * <p>
 * The run-time arguments can be used to insert run-time values into
 * the message text, for example dates or user names.
 * See {@link java.text.MessageFormat java.text.MessageFormat}.
 *
 * @see		java.lang.RuntimeException
 * @see		java.text.MessageFormat
 */
@SuppressWarnings("serial")
public abstract class RuntimeException_ extends RuntimeException
{
	private	Object		key;
	private	Object[]	args;

	/**
	 * Construct with message text only.
	 * @param	msg			the message
	 */
	public RuntimeException_ (String msg)
	{
		this (msg, null, (Object) null);
	}

	/**
	 * Construct with message text and a cause.
	 * @param	msg			the message
	 * @param	cause		the cause
	 */
	public RuntimeException_ (String msg, Throwable cause)
	{
		this (msg, null, (Object) null, cause);
	}

	/**
	 * Construct with a message and a key.
	 * @param	msg			the message
	 * @param	key			the key
	 */
	public RuntimeException_ (String msg, Object key)
	{
		this (msg, key, (Object) null);
	}

	/**
	 * Construct with a message, a key, and a cause.
	 * @param	msg			the message
	 * @param	key			the key
	 * @param	cause		the cause
	 */
	public RuntimeException_ (String msg, Object key, Throwable cause)
	{
		this (msg, key, (Object) null, cause);
	}

	/**
	 * Construct with a message, a key and run-time arguments.
	 * @param	msg			the message
	 * @param	key			the key
	 * @param	args		the run-time args
	 */
	public RuntimeException_ (String msg, Object key, Object[] args)
	{
		super (msg);

		if (key == null)
			this.key = getClass().getName ();
		else
       		this.key = key;

       	this.args = args;
	}

	/**
	 * Construct with a message, a key, run-time arguments, and a cause.
	 * @param	msg			the message
	 * @param	key			the key
	 * @param	args		the run-time args
	 * @param	cause		the cause
	 */
	public RuntimeException_ (String msg, Object key, Object[] args,
			Throwable cause)
	{
		super (msg, cause);

		if (key == null)
			this.key = getClass().getName ();
		else
       		this.key = key;

       	this.args = args;
	}

	/**
	 * Construct with a message, a key, and a run-time argument.
	 * @param	msg			the message
	 * @param	key			the key
	 * @param	arg			the run-time arg
	 */
	public RuntimeException_ (String msg, Object key, Object arg)
	{
		this (msg, key, ArrayCreator.create (arg));
	}

	/**
	 * Construct with a message, a key, a run-time argument, and a cause.
	 * @param	msg			the message
	 * @param	key			the key
	 * @param	arg			the run-time arg
	 * @param	cause		the cause
	 */
	public RuntimeException_ (String msg, Object key, Object arg,
			Throwable cause)
	{
		this (msg, key, ArrayCreator.create (arg), cause);
	}

	/**
	 * Get the message key if any.
	 * @return	Object	the key
	 */
	public Object	getKey ()
	{
		return key;
	}

	/**
	 * Get the message arguments if any.
	 * @return	Object[]	the args
	 */
	public final Object[]	getArgs ()
	{
		return args;
	}

	/*
	 * Returns the message of this exception followed by the
	 * messages of all cause exceptions.
	 */
	public String getAllMessages() {
		StringBuffer sb = new StringBuffer();

		sb.append(getMessage());

		Throwable cause = getCause();

		while (cause != null) {
			sb.append("\n\n");
			sb.append(cause.getMessage());
			cause = cause.getCause();
		}

		return sb.toString();
	}

	/**
	 * If t is an RuntimeException_ return t.getAllMessages otherwise return
	 * t.getMessage().
	 * 
	 * @param	t	the Throwable from which to extract messages
	 * @return		the concatenated messages
	 * @see #getAllMessages
	 */
	public static String getAllMessages (Throwable t)
	{
		// TBD - this suggests use of an interface or introspection
		// to determine if there is a 'getAllMessages.'
		if (t instanceof Exception_)
			return ((Exception_)t).getAllMessages();
		if (t instanceof RuntimeException_)
			return ((RuntimeException_)t).getAllMessages();
		return t.getMessage();
	}

	/**
	 * Override super to insert run-time args (if specified) into
	 * super.getMessage().
	 * 
	 * @return		return the message with expanded run-time arguments
	 */
	public String getMessage ()
	{
		String msg = super.getMessage();

		if (msg != null && args != null)
		{
			msg = MessageFormat.format (msg, args);
		}

		return msg;
	}

	/**
	 * Get the root cause.
	 * 
	 * @return	the root cause Throwable
	 */
	public Throwable getRootCause ()
	{
		Throwable cause = getCause();
		Throwable rootCause = null;

		while (cause != null)  {
			rootCause = cause;
			cause = cause.getCause();
		}
		return rootCause;
	}

	/**
	 * Get the message from the initial cause.
	 * If cause is null, return getMessage().
	 * 
	 * @return	the message from the initial cause
	 */
	public String getRootMessage()
	{
		Throwable root = getRootCause ();

		if (root == null)
			return null;

		return root.getMessage();
	}

	/**
	 * Get the root message string without any run-time insertions.
	 * 
	 * @return		the root cause message without run-time arg substitution
	 */
	public String getRootMessageUnexpanded ()
	{
		Throwable root = getRootCause ();

		if (root == null)
			return null;

		if (root instanceof Exception_)
			return ((Exception_)root).getRootMessageUnexpanded ();

		return root.getMessage();
	}
}
