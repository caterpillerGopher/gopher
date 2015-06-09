package com.deetysoft.util;

/**
 * Provide a null-safe equals method.
 */
public class Equals {

	/**
	 * A static class.
	 */
	private Equals () {}

	/**
	 * Equals handling null objects.
	 * @param	o1	the first object for comparison
	 * @param	o2	the second object for comparison
	 * @return		true or false
	 */
	public static boolean equals (Object o1, Object o2)
	{
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	/**
	 * Equals handling floating point values.
	 * @param	d1	the first object for comparison
	 * @param	d2	the second object for comparison
	 * @param	epsilon	an acceptable delta if they are not equal
	 * @return		true or false
	 */
	public static boolean equals(double d1, double d2, double epsilon)
	{
		return Math.abs(d1 - d2) <= epsilon;
	}

	/**
	 * Return true if both args are null or both args are not null.
	 * @param	o1	the first object for comparison
	 * @param	o2	the second object for comparison
	 * @return		true or false
	 */
	public static boolean equalNullness(Object o1, Object o2)
	{
		return o1 == null ? o2 == null : o2 != null;
	}

	/**
	 * Equals handling for strings where the strings may have different
	 * case and either or both of the strings may be null.
	 * @param	s1	the first object for comparison
	 * @param	s2	the second object for comparison
	 * @return		true or false
	 */
	public static boolean equalsIgnoreCase(String s1, String s2)
	{
		return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
	}
}
