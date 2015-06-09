package com.echostar.gopher.persist;

/**
 * An enumeration of valid browsers.
 * @author charles.young
 *
 */
public enum BrowserEnum {

	FIREFOX ("firefox"),
	CHROME ("chrome"),
	IEXPLORE ("iexplore"),
	SAFARI ("safari");
	
	String value;

	private BrowserEnum (String value) {
		this.value = value;
	}

	public String getValue() { return value; }
}