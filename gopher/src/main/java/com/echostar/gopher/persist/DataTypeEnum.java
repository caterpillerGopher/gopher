package com.echostar.gopher.persist;

/**
 * An enumeration of valid types for {@link TestDataType TestDataType}
 * @author charles.young
 *
 */
public enum DataTypeEnum {

	STRING ("STRING"),
	INT ("INT"),
	DECIMAL ("DECIMAL"),
	DATE ("DATE");
	
	String value;

	private DataTypeEnum (String value) {
		this.value = value;
	}

	public String getValue() { return value; }
}
