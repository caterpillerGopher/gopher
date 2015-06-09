package com.echostar.gopher.persist;

/**
 * Enumerate possible values for {@link TestDataType#setRole(DataRoleEnum) TestDataType} role.
 * @author charles.young
 *
 */
public enum DataRoleEnum {

	/**
	 * A label
	 */
	LABEL ("LABEL"),
	// A temporary hack. Move locator data to ElementLocator.
	LOCATOR_XPATH ("XPATH"),
	/**
	 * An href
	 */
	HREF ("HREF");
	
	String value;

	private DataRoleEnum (String value) {
		this.value = value;
	}

	public String getValue() { return value; }
}
