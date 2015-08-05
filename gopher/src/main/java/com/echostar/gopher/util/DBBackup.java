package com.echostar.gopher.util;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.deetysoft.util.StreamGobbler;

/**
 * Backup the Gopher database to a file.
 *
 * A root directory defined by the
 * {@link com.echostar.gopher.util.Config Config} property
 * 'db.archive.root' is required.
 * A Config property 'MYSQL_HOME' defining the path to the
 * MySQL installation directory is required.
 *
 * DBBackup creates a directory structure below the root.
 * The structure is: 'year'/'month'/'day of month'.
 *
 * The file is written into the 'day of month' directory and has the name:
 * "gopher-db.'year'-'month'-'day of month'-'hour'-'minute'-'sec'.sql"
 *
 * If an error occurs with mysqldump, the file may be empty.
 *
 * @author greg
 *
 */
public class DBBackup {

	// Name of property defining the MySQL install dir
	public static final String MYSQL_HOME_KEY = "MYSQL_HOME";
	// Name of property defining the root directory for the backup file
	public static final String DB_ARCHIVE_ROOT = "db.archive.root";

	/**
	 * Do the backup based on the property file values.
	 * @param args
	 */
	public static void main (String[] args) {
		backup ();
	}

	/**
	 * Do the backup based on the property file values.
	 */
	public static void backup () {
		Logger log = Logger.getLogger (DBBackup.class.getName());
		String mysqlHome = Config.getProperty_S(MYSQL_HOME_KEY);
		if (mysqlHome == null) {
			log.error("Property '"+MYSQL_HOME_KEY+"' not found in Config.");
			return;
		}
		String mysqldumpPath = mysqlHome + "/bin/mysqldump";
		try {
			String[] dateComponents = getDateComponents();
			String root = Config.getProperty_S(DB_ARCHIVE_ROOT);
			String dirPath = createDirectory (root, dateComponents);
			String dateString = getDateString(dateComponents);
			String fileName = dirPath + File.separator + "gopher-db."+dateString+".sql";
			log.info("Dumping database to file '"+fileName+"'.");
			ProcessBuilder pb = new ProcessBuilder (mysqldumpPath,
				"--single-transaction", "--user=root", "gopher");
			log.info(pb.command());
			pb.redirectError();
			Process p = pb.start();
			StreamGobbler sg = new StreamGobbler(p.getInputStream(), "fred",
					fileName);
			sg.run();
			p.waitFor();
			//log.debug(sg.getStringBuffer());
			log.info("Process ended.");
		} catch (Exception e) {
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
		}
	}

	/**
	 * Create the directory structure below the root.
	 * The structure is: 'year'/'month'/'day of month'.
	 * @param root				the root directory
	 * @param dateComponents	an array of 'year', 'month', 'day of month',
	 * 							'hour', 'minute' and 'sec'.
	 * @return					the directory path created from the root and dataComponents
	 */
	protected static String createDirectory (String root, String[] dateComponents) {
		Logger log = Logger.getLogger(Class.class.getName());
		try {
			File rootDir = new File ("root");
			if (!rootDir.exists()) {
				rootDir.mkdir();
			}
		} catch (Exception e) {
			log.error("Error trying to create directory '"+root+"' : '"+e.getMessage()+"'.");
			throw e;
		}

		// Build the directory structure 'year'/'month'/'day of month'.
		StringBuffer path = new StringBuffer(root);
		for(int i=0; i< 3; i++) {
			path.append(File.separator);
			path.append(dateComponents[i]);
			try {
				File f = new File (path.toString());
				if (!f.exists()) {
					f.mkdir();
				}
			} catch (Exception e) {
				log.error("Error trying to create directory '"+root+"' : '"+e.getMessage()+"'.");
				throw e;
			}			
		}
		return path.toString();
	}

	/**
	 * Create a date string from the components :
	 *
	 * "'year'-'month'-'day of month'-'hour'-'minute'-'sec'".
	 *
	 * @param dateComponents	an array of 'year', 'month', 'day of month',
	 * 							'hour', 'minute' and 'sec'.
	 * @return					the string
	 */
	protected static String getDateString (String[] dateComponents) {

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
	protected static String[] getDateComponents () {
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