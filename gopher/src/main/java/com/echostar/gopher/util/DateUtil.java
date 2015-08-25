package com.echostar.gopher.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Date related utilities.
 * @author charles.young
 *
 */
public class DateUtil {

	/**
	 * Create a date string from the components :
	 *
	 * "'year'-'month'-'day of month'-'hour'-'minute'-'sec'".
	 *
	 * @param dateComponents	an array of 'year', 'month', 'day of month',
	 * 							'hour', 'minute' and 'sec'.
	 * @return					the string
	 */
	public static String getDateString (String[] dateComponents) {

		String result = dateComponents[0] + "-" +
				dateComponents[1] + "-" +
				dateComponents[2] + "-" +
				dateComponents[3] + "-" +
				dateComponents[4] + "-" +
				dateComponents[5];

		return result;
	}

	/**
	 * Decompose the current date and time into components :
	 * an array of 'year', 'month', 'day of month',
	 * 'hour', 'minute' and 'sec'.
	 * Use the default time zone.
	 * TBD - set a specific time zone.
	 * 
	 * @return	the array
	 */
	public static String[] getDateComponents () {
		String[] result = new String[6];
		TimeZone mst = TimeZone.getDefault();
		Calendar cal = new GregorianCalendar(mst);

		result[0] = Integer.toString(cal.get(Calendar.YEAR));
		result[1] = Integer.toString(cal.get(Calendar.MONTH)+1);
		result[2] = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		result[3] = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		result[4] = Integer.toString(cal.get(Calendar.MINUTE));
		result[5] = Integer.toString(cal.get(Calendar.SECOND));

		return result;
	}
}
