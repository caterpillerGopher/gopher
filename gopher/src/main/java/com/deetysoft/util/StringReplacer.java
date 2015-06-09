package com.deetysoft.util;

/**
 * A class that can be used to replace characters in strings.
 */
public class StringReplacer
{
	/**
	 * Prevents this class from being instantiated.
	 */
	private StringReplacer() {}

	/**
	 * Given String <code>templateString</code>, returns an identical String 
	 * with all instances of character <code>oldChar</code> replaced with 
	 * character <code>newChar</code>.
	 *
	 * @param templateString the String to use as a base for the replacements
	 * @param oldChar the character to replace
	 * @param newChar the character to replace <code>oldChar</code> with
	 * @return a String that is identical to <code>templateString</code> with 
	 * all instances of character <code>oldChar</code> replaced with character 
	 * <code>newChar</code>.
	 * @see #replaceString
	 * @see #replaceStringBuffer
	 */
	public static String replaceChar(String templateString, char oldChar, 
			char newChar) {
		StringBuffer stringBuffer = new StringBuffer(templateString);

		int index = -1;

		while ((index = templateString.indexOf(oldChar, index + 1)) != -1) {
			stringBuffer.setCharAt(index, newChar);
		}

		return stringBuffer.toString();
	}

	/**
	 * Given String <code>templateString</code>, returns an identical String 
	 * with all instances of String <code>oldString</code> replaced with 
	 * String <code>newString</code>.
	 *
	 * @param templateString the String to use as a base for the replacements
	 * @param oldString the String to replace
	 * @param newString the String to replace <code>oldString</code> with
	 * @return a String that is identical to <code>templateString</code> with 
	 * all instances of String <code>oldString</code> replaced with String 
	 * <code>newString</code>.
	 * @see #replaceChar
	 * @see #replaceStringBuffer
	 */
	public static String replaceString(String templateString, String oldString, 
			String newString)
	{
		StringBuilder stringBuffer = new StringBuilder(templateString);
		replaceStringBuffer(stringBuffer, oldString, newString);
		return stringBuffer.toString();
	}

	/**
	 * Given StringBuffer <code>templateStringBuffer</code>, replaces
	 * all instances of String <code>oldString</code> with 
	 * String <code>newString</code>.
	 *
	 * @param templateStringBuffer the StringBuffer to perform the replacements
	 * on
	 * @param oldString the String to replace
	 * @param newString the String to replace <code>oldString</code> with
	 * @see #replaceChar
	 * @see #replaceString
	 */
	public static void replaceStringBuffer(StringBuilder templateStringBuffer, 
			String oldString, String newString)
	{
		int oldStringLength = oldString.length();
		int index = templateStringBuffer.length();

		while ((index = templateStringBuffer.lastIndexOf(oldString, index)) != -1) {
			templateStringBuffer.replace(index, index + oldStringLength,
					newString);
			index -= oldStringLength;
		}
	}

	/**
	 * A version of
	 * {@link #replaceStringBuffer(StringBuilder, String, String)
	 * replaceStringBuffer}
	 * where the value to substitute is an int.
	 * @param	templateStringBuffer	tbd
	 * @param	oldString				tbd
	 * @param	value					the value to substitute
	 */
	public static void replaceStringBuffer(StringBuilder templateStringBuffer, 
			String oldString, int value)
	{
		replaceStringBuffer (templateStringBuffer, oldString,
				Integer.toString (value));
	}

	/**
	 * Given a String, a start index and a ending index, substitute
	 * another string between those indices to produce a new String.
	 *
	 * @param	templateStr		base string
	 * @param	insertStr		string to insert
	 * @param	startIndex		position in templateStr to start substitution
	 * @param	endIndex		position in templateStr to end substitution
	 * @return					the new string
	 */
	public static String	substitute (String templateStr,
			String insertStr, int startIndex, int endIndex)
	{
		StringBuilder sb = new StringBuilder(templateStr);
		sb.replace(startIndex, endIndex+1, insertStr);
		return sb.toString();
	}
}
