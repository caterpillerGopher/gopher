package com.echostar.gopher.exception;

/**
 * A comparison failed.
 * @author greg
 *
 */
@SuppressWarnings("serial")
public class ComparisonException extends TestException {

	public ComparisonException (String msg) {
		super (msg);
	}
	public ComparisonException (String msg, Throwable cause) {
		super (msg, cause);
	}
}
