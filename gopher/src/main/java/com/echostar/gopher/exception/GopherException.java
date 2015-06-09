package com.echostar.gopher.exception;

/**
 * Gopher code threw the exception.
 * @author greg
 *
 */
@SuppressWarnings("serial")
public class GopherException extends Exception {

	public GopherException (String message) {
		super(message);
	}
	public GopherException (String message, Throwable cause) {
		super(message, cause);
	}
}
