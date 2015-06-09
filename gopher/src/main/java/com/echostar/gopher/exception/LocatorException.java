package com.echostar.gopher.exception;

/**
 * An attempt to locate an HTML element failed.
 * @author greg
 *
 */
@SuppressWarnings("serial")
public class LocatorException extends TestException {

	public LocatorException (String msg) {
		super (msg);
	}
	public LocatorException (String msg, Throwable t) {
		super (msg, t);
	}
}
