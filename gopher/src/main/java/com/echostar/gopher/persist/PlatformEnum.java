package com.echostar.gopher.persist;

/**
 * An enumeration of valid types for platform.
 * @author charles.young
 *
 */
public enum PlatformEnum {

	 OSX10_8 ("OSX10.8"),
	 OSX10_9 ("OSX10.9"),
	 OSX10_10 ("OSX10.10"),
	 WIN8_1 ("Win8.1"),
	 WIN8 ("Win8"),
	 WIN7 ("Win7");
	
	String value;

	private PlatformEnum (String value) {
		this.value = value;
	}

	public String getValue() { return value; }

	/**
	 * Get the old value used by GopherDriverAPI.
	 * TBD change GopherDriverAPI to use this enum.
	 * @param newValue	a PlatformEnum
	 * @return			the old value
	 */
	public static String getOldValue (PlatformEnum newValue) {

		switch (newValue) {
			case OSX10_8:
				return "MAC";
			case OSX10_9:
				return "MAC";
			case OSX10_10:
				return "MAC";
			case WIN8_1:
				return "WIN8";
			case WIN8:
				return "WIN8";
			case WIN7:
				return "WINDOWS";
			default:
				return null;
		}
	}

	/**
	 * Is this enum a Windows flavor?
	 * @return	true if windows
	 */
	public boolean isWindows () {
		if (this.equals(PlatformEnum.WIN7) ||
			this.equals(PlatformEnum.WIN8) ||
			this.equals(PlatformEnum.WIN8_1)) {
			return true;
		} 
		return false;
	}
	
	/**
	 * Is this enum a Mac flavor?
	 * @return	true if mac
	 */
	public boolean isMac () {
		if (this.equals(PlatformEnum.OSX10_8) ||
			this.equals(PlatformEnum.OSX10_9) ||
			this.equals(PlatformEnum.OSX10_10)) {
			return true;
		} 
		return false;
	}
}