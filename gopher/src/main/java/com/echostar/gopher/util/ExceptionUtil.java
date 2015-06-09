package com.echostar.gopher.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

	/**
	 * Get the Throwable's stacktrace string.
	 * Limit the length of the string to maxLen or the length of the string,
	 * whichever is shorter.
	 *
	 * @param t			the Throwable
	 * @param maxLen	the maximum length of the returned string
	 * @return			the stacktrace string
	 */
	public static String getStackTraceString (Throwable t, int maxLen) {
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		StringBuffer sb = sw.getBuffer();
		int len = sb.toString().length();
		int max = maxLen;
		if (len < maxLen) {
			max = len;
		}
		String s = sb.substring(0, max);
		
		return s;
	}
}
