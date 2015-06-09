package com.echostar.gopher.persist;

/**
 * Enumerate possible values for an element locator type.
 * @author charles.young
 *
 */
public enum ElementLocatorType {

	/**
	 * A name locator
	 */
	NAME ("NAME"),
	/**
	 * An id locator
	 */
	ID ("ID"),
	/**
	 * A XPATH locator
	 */
	XPATH ("XPATH"),
	/**
	 * A link locator
	 */
	LINK ("LINK"),
	/**
	 * A partial link locator
	 */
	PARTIAL_LINK ("PARTIAL_LINK"),
	/**
	 * A class name locator
	 */
	CLASSNAME ("CLASSNAME"),
	/**
	 * A tag locator
	 */
	TAGNAME ("TAGNAME");
	
	String value;

	private ElementLocatorType (String value) {
		this.value = value;
	}

	public String getValue() { return value; }
}