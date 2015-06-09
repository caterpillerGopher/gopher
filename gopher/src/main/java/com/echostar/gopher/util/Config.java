package com.echostar.gopher.util;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.deetysoft.config.ConfigProperties;
import com.deetysoft.util.HierarchyNode;

/**
 * Wrap {@link com.deetysoft.config.ConfigProperties ConfigProperties} and
 * {@link com.deetysoft.config.Config Config}
 * to do specific things for Gopher.
 * Look for an environment variable {@link #GOPHER_USER GOPHER_USER} and
 * look for a sub-folder of 'Config' with that name.
 * Read all the (non-excluded) properties and XML files under 'Config' and if the sub-directory exists,
 * read all files in that directory and merge the properties into one collection and all the XML
 * elements into one hierarchy.
 *
 * @author charles.young
 *
 */
public class Config {

	/**
	 * Name of environment variable defining a
	 * sub-directory name associated with a Gopher user.
	 */
	public static final String GOPHER_USER = "GOPHER_USER";

	protected static Config defaultConfig = null;

	protected com.deetysoft.config.Config deetyConfig = null;
	protected ConfigProperties deetyProperties = null;

	/**
	 * Create a Config singleton.
	 */
	static {
		try {
			Logger log_ = Logger.getRootLogger();
			log_.debug("Creating Config singleton.");
			defaultConfig = new Config();
			log_.debug("Config singleton created.");
			//defaultConfig.dump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Merge the properties defined in property files under 'Config' and under 'Config'/%GOPHER_USER%
	 * into a Hierarchy under a {@link com.deetysoft.util.HierarchyNode HierarchyNode}.
	 *
	 * @throws Exception
	 */
	public Config () throws Exception {
		Logger log = Logger.getLogger (getClass().getName());
		log.debug("In Config().");
		String userName = System.getenv(GOPHER_USER);
		log.debug(GOPHER_USER+" environment variable is \""+userName+"\".");
		String configDirName = System.getProperty("user.dir")+ "/src/main/config";

		File[] files = getFiles (configDirName, userName, new PropertyFileListener());
		log.debug("Property file names:");
		for (File f : files) {
			log.debug(f.getAbsolutePath());
		}
		String[] filePaths = getPaths(files);
		deetyProperties = new ConfigProperties(filePaths);

		File[] xmlFiles = getFiles (configDirName, userName, new XMLFileListener());
		log.debug("XML file names:");
		for (File f : xmlFiles) {
			log.debug(f.getAbsolutePath());
		}
		String[] xmlFilePaths = getPaths(xmlFiles);
		deetyConfig = new com.deetysoft.config.Config(xmlFilePaths,
			new String[xmlFilePaths.length]);

		log.debug("Leaving Config().");
	}

	/**
	 * Read the property files under 'filePath.'
	 *
	 * @param filePath		path to directory containing property files
	 * @throws Exception	on any exception
	 */
	public Config (String filePath) throws Exception {
		//super ();
		String[] paths = new String[1];
		paths[0] = filePath;
		//init(paths);
		deetyProperties = new ConfigProperties(paths);
	}

	/**
	 * Read the property files under 'filePaths.'
	 *
	 * @param filePaths		paths to directory containing property files
	 * @throws Exception	on any exception
	 */
	public Config (String[] filePaths) throws Exception {
		deetyProperties = new ConfigProperties(filePaths);
	}

	/**
	 * Merge the property file names under 'configDirName' and 'configDirName'/'userName
	 * into a single array of file paths.
	 *
	 * @param configDirName		name of the config dir
	 * @param userName			the sub-directory name
	 * @param filter			a FileNameFilter to ignore some files
	 * @return					a merged array of file paths
	 */
	static File[] getFiles (String configDirName, String userName,
		FilenameFilter filter) {
		
		Logger log = Logger.getRootLogger();

		File configDirFile = new File (configDirName);

		File[] files1 = configDirFile.listFiles(filter);

		File[] files2 = new File[0];

		if (userName != null) {
			String subDirName =configDirName+"/"+userName;
			File subDirFile = new File (subDirName);
			if (subDirFile.exists()) {
				files2 = subDirFile.listFiles(filter);
			} else {
				log.warn("Config subdirectory '"+subDirName+"' not found.");
			}
		}

		File[] files = mergeArrays (files1, files2);
		
		return files;
	}

	/**
	 * Merge two arrays
	 * @param a		the first array
	 * @param b		the second array
	 * @return		a merged array
	 */
	static File[] mergeArrays (File[] a, File[] b) {
		File[] v = new File[a.length+b.length];
		int i = 0;
		for (File s : a) {
			v[i++]=s;
		}
		for (File s : b) {
			v[i++]=s;
		}
		return v;
	}

	/**
	 * Convert an array of Files to an array of String file paths.
	 * @param files		an array of File
	 * @return			an array of String file paths
	 */
	static String[] getPaths(File[] files) {
		String[] v = new String[files.length];
		int i = 0;
		for (File f : files) {
			v[i++]=f.getAbsolutePath();
		}
		return v;
	}

	/**
	 * Get the Collection of nodes (if any) with the given path in the Hierarchy.
	 * @param path	a path like 'name1'/'name2'/'name3' where each name in the path is a node name
	 * @return
	 */
	public Collection<HierarchyNode> getNode(String path) {
		return deetyConfig.getData().getNodesByPath(path);
	}
	public static Collection<HierarchyNode> getNode_S(String path) {
		return defaultConfig.getNode(path);
	}

	public String getVal(String path) {
		return deetyConfig.getVal(path);
	}

	public static String getVal_S(String path) {
		return defaultConfig.getVal(path);
	}

	public String getProperty(String key) {
		return deetyProperties.getProperty(key);
	}

    public static String getProperty_S(String key) {
        return defaultConfig.getProperty(key);
    }

    public static int getPropertyAsInt_S(String key) {
        return Integer.parseInt(defaultConfig.getProperty(key));
    }

    /**
     * Get the IP address of the Selenium hub.
     * If "hubIP" is defined in a property file, return that value.
     * Otherwise, return the address of the localhost.
     *
     * @return
     */
    public static String getHubIP () throws Exception {
 
		Logger log = Logger.getLogger (Class.class);
    	String ip = null;
    	
    	try {
    		ip = getProperty_S("hubIP");
    	} catch (Exception e) {
    		log.info("Property 'hubIP' not found.");
    	}

    	if (ip == null) {
    		log.info("Trying to determine localhost IP to use as grid hub IP.");
    		InetAddress addr=InetAddress.getLocalHost();
    		ip = addr.getHostAddress();
    	}
		log.info("Using "+ip+" as Selenium grid hub address.");
		return ip;
    }

	public void dump () {
		//deetyProperties.dumpProperties();
		deetyConfig.toString();
		System.out.println (deetyConfig.getData());
	}

	public static void main (String[] args) {
		Config c = null;	
		try {
			c = new Config ();
			c.dump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Implement a FilenameFilter to exclude some files.
	 * Exclude 'log4j.properties.'
	 * @author charles.young
	 *
	 */
	class PropertyFileListener implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".properties")) {
				if (name.equalsIgnoreCase("log4j.properties")) {
					return false;
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * Implement a FilenameFilter to exclude some files.
	 * Exclude any files with 'hibernate.cfg' in the file name.
	 * @author charles.young
	 *
	 */
	class XMLFileListener implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".xml")) {
				if (name.indexOf("hibernate.cfg") > -1) {
					return false;
				}
				return true;
			}
			return false;
		}
	}
}
