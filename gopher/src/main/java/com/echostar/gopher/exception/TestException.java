package com.echostar.gopher.exception;

/**
 * A test class threw the exception.
 * @author greg
 *
 */
@SuppressWarnings("serial")
public class TestException extends GopherException {

	public TestException (String message) {
		super(message);
	}
	public TestException (String message, Throwable cause) {
		super(message, cause);
	}
}

