package com.deetysoft.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * Define configuration property files for
 * {@link com.deetysoft.config.Config Config}.
 * <p>
 * If this class exists in the classpath, Config may
 * (according to it's rules)
 * instantiate this class to get the file list.
 * <p>
 * This class is not a standard component of the package.
 * It is defined according to Config's specifications by developers
 * of a specific application.
 * <p>
 * We use {@link com.deetysoft.config.ConfigProperties ConfigProperties} with properties
 * {@link #FILES_PROP FILES_PROP} and {@link #DTDS_PROP DTDS_PROP}
 * to get the lists of XML files and their DTDs.
 * <p>
 * Config prefers to use system properties which define the XML
 * and DTD file lists.
 * The only way to define these system properties for the Sips web client
 * would be to modify the Tomcat startup script.
 * Sips web client uses this class to init Config rather than
 * modifying the web server script.
 */
public class ConfigFiles
{
	/**
	 * {@link com.deetysoft.config.ConfigProperties ConfigProperties} property name we use to get
	 * the list of XML files. 
	 */
	public static final String FILES_PROP = "sips.config.files";

	/**
	 * {@link com.deetysoft.config.ConfigProperties ConfigProperties} property name we use to get
	 * the list of DTD files.
	 */
	public static final String DTDS_PROP = "sips.config.dtds";

	/**
	 * The config files.
	 */
	public static String[] fileNames;

	/**
	 * The config dtd files.
	 */
	public static String[] dtdNames;

	static {
		String fileNames_ = ConfigProperties.getProperty_S(FILES_PROP);	
		String dtdNames_ = ConfigProperties.getProperty_S(DTDS_PROP);	

		Collection<String> fileNameCollection = new ArrayList<String>();
		Collection<String> dtdNameCollection = new ArrayList<String>();

		for (StringTokenizer st = new StringTokenizer(fileNames_, ","); 
				st.hasMoreTokens(); ) {
			fileNameCollection.add(st.nextToken());
		}

		for (StringTokenizer st = new StringTokenizer(dtdNames_, ","); 
				st.hasMoreTokens(); ) {
			dtdNameCollection.add(st.nextToken());
		}

		fileNames = fileNameCollection.toArray(new String [] {});
		dtdNames = dtdNameCollection.toArray(new String [] {});
	}

	/**
	 * This method is required.
	 * @return the file name list
	 */
	public static String[] getFileNames ()
	{
		return fileNames;
	}

	/**
	 * This method is required.
	 * @return the DTD name list
	 */
	public static String[] getDTDnames ()
	{
		return dtdNames;
	}
}
