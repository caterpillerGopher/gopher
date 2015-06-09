package com.deetysoft.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that can be used to retrieve the indices of tokens.
 */
public class TokenIndexRetriever
{
	/**
	 * Prevents this class from being instantiated.
	 */
	private TokenIndexRetriever() {}

	/**
	 * Get indices for all instances of a char token in a String.
	 * A token is ignored if it is preceded by the escape character
	 * '\'.
	 *
	 * @param	str		the string to search
	 * @param	token	the character to search for
	 * @return			a list of Integer indices
	 */
	public static List<Integer> getIndices (String str, char token)
	{
		List<Integer> indices = new ArrayList<Integer> ();

		int index = -1;
		while ((index = str.indexOf(token, index+1)) != -1) {
			if (index > 0 && str.charAt(index-1) == '\\') {
				continue;
			}
			indices.add(index);
		}

		return indices;
	}
}
