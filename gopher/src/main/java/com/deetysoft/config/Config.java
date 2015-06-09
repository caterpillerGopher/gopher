package com.deetysoft.config;

import com.deetysoft.collections.ArrayCreator;
import com.deetysoft.exception.MissingPropertyException;
import com.deetysoft.exception.StringFormatException;
import com.deetysoft.file.ClasspathFile;
import com.deetysoft.util.Hierarchy;
import com.deetysoft.util.HierarchyNode;
import com.deetysoft.util.StringReplacer;
import com.deetysoft.util.TokenIndexRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A configuration data utility.
 * <p>
 * Config reads hierarchical data from one or more XML files.
 * It merges data from multiple files to form a single set.
 * Config uses a {@link com.deetysoft.util.HierarchyNode HierarchyNode}
 * to store the data so some of it's concepts are reflected here.
 * In particular, we use the HierarchyNode notion of a path to an element.
 * See {@link com.deetysoft.util.HierarchyNode#getValue(String) getValue}
 * for details.
 * We use {@link com.deetysoft.util.Hierarchy Hierarchy} to read and merge
 * the XML files into a single HierarchyNode.
 * <p>
 * An element value may declare that it needs another element value
 * substituted into it (value expansion.)
 * The element to be substituted is defined by a begining and ending '%'
 * surrounding a element name.
 * The path for the element to be substituted must be unique in the set
 * of elements.
 * For example:
 * <p>
 * {@code &lte1&gt&lte2&gtblee&lt/e2&gt&lt/e1&gt}
 * <br>
 * {@code &lte4&gte2 value is %e1/e2%&lt/e4&gt}.
 * <p>
 * In this case, e4's value after substitution is "e2 value is blee."
 * Substitutions may be nested to any level.
 * If the name of the element to be substituted begins with 'env.',
 * Config looks for a system property to substitute.
 * See {@link #ENV_PREFIX ENV_PREFIX} for details.
 * <p>
 * The above features make Config well suited for application use
 * in managing configuration data.
 * <p>
 * We create a singleton for use throughout the application.
 * It is created during static initialization using the default constructor
 * {@link #Config Config()}.
 *
 * @see com.deetysoft.util.Hierarchy
 * @see com.deetysoft.util.HierarchyNode
 */
public class Config
{
	/**
	 * A Collection of config hierarchy listeners.
	 */
	private Collection<ConfigListener> listeners;

	/**
	 * The class defining config XML files and their DTDs -
	 * "com.deetysoft.config.ConfigFiles".
	 * It is not part of the package but may be defined by the
	 * application as an alternative to using the system property.
	 * ConfigFiles must define methods
	 * <pre>
	 * public static String[] getFileNames ();
	 * public static String[] getDTDs ();
	 * </pre>
	 * to provide the file names and DTDs.
	 */
	public static final String	CONFIG_FILES_CLASS	=
		"com.deetysoft.config.ConfigFiles";

	/**
	 * The default xml file - "config.xml".
	 */
	public static final String	DEFAULT_FILE_NAME	= "config.xml";

	/**
	 * The prefix '.env' for substitution strings denotes a system property.
	 * For example: home=%env.HOME% sets the property named home
	 * to have the value of the HOME system property.
	 */
	public static final String	ENV_PREFIX			= "env.";

	/**
	 * The system property for defining the xml file list -
	 * "config.files".
	 * For example, to use the -D option on the java command line:
	 * <br>
	 * <pre>-Dconfig.files=myData.xml;otherData.xml</pre>
	 * File names are separated by a ';'.
	 */
	public static final String	FILES_PROPERTY		= "config.files";

	/**
	 * The system property for defining the DTDs file list -
	 * "config.dtds".
	 * For example, to use the -D option on the java command line:
	 * <br>
	 * <pre>-Dconfig.dtds=myData.dtd;null</pre>
	 * <br>
	 * You must specify "null" if the corresponding file does not use a DTD.
	 * File names are separated by a ';'.
	 */
	public static final String	DTDS_PROPERTY		= "config.dtds";

	/**
	 * The token used to delimit a substitution -'%'.
	 */
	public static final char	SUBSTITUTION_TOKEN	= '%';

	private static String[]		defaultFiles		= {DEFAULT_FILE_NAME};
	private static String[]		defaultDTDs			= {"null"};

	private static Config		instance;

	// Artificial root of our data, named "root"
	private HierarchyNode		data;

	/**
	 * Initialize the singleton.
	 * <p>
	 * Look for xml files and their DTDs using the following rules:
	 * <p>
	 * 1. If the system property {@link #FILES_PROPERTY FILES_PROPERTY}
	 * exists, assume it specifies an xml file list.
	 * In this case, the system property {@link #DTDS_PROPERTY DTDS_PROPERTY}
	 * must also be defined.
	 * <p>
	 * 2. Try to instantiate by name the class
	 * {@link #CONFIG_FILES_CLASS CONFIG_FILES_CLASS} and ask it for the
	 * xml files and DTDs.
	 * <p>
	 * 3. If {@link #FILES_PROPERTY FILES_PROPERTY} is not defined and
	 * {@link #CONFIG_FILES_CLASS CONFIG_FILES_CLASS} does not exist then try
	 * to read the default file {@link #DEFAULT_FILE_NAME DEFAULT_FILE_NAME}.
	 * <p>
	 * If none of the above conditions is satisfied then an IOException is
	 * thrown for the default file not found.
	 * <p>
	 * A file name may include a complete path.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * <p>
	 * Files are read and merged in the specified order.
	 *
	 * @throws ConfigException if the class cannot be initialized 
	 */
	public static void init_S () throws ConfigException
	{
		try {
			// If the system property defining the file names exists

			String fileNames = System.getProperty (FILES_PROPERTY);

			if (fileNames != null)
			{
				String dtdNames = System.getProperty (DTDS_PROPERTY);

				if (dtdNames == null)
				{
					throw new MissingPropertyException (DTDS_PROPERTY,
							"system properties");
				}

				// Use the file and dtd names.

				Vector<String> v = new Vector<String> ();

				StringTokenizer tokenizer = new StringTokenizer (fileNames,";");

				while (tokenizer.hasMoreTokens ())
				{String name = tokenizer.nextToken ();
					v.addElement (name);
				}

				String[] fileList = new String [v.size()];

				fileList = v.toArray (fileList);

				v.clear ();

				tokenizer = new StringTokenizer (dtdNames,";");

				while (tokenizer.hasMoreTokens ())
				{String name = tokenizer.nextToken ();
					v.addElement (name);
				}

				String[] dtdList = new String [v.size()];

				dtdList = v.toArray (dtdList);

				init_S (fileList, dtdList);
			}
			else
			{
				String[] fileNames_ = null;
				String[] dtdNames = null;

				Class<?> class_ = Class.forName (CONFIG_FILES_CLASS);
				Method method = class_.getMethod ("getFileNames", (Class[])null);
				fileNames_ = (String[]) method.invoke (null, (Object[])null);
				method = class_.getMethod ("getDTDnames", (Class[])null);
				dtdNames = (String[]) method.invoke (null, (Object[])null);

				if (fileNames_ != null)
				{
					init_S (fileNames_, dtdNames);
				}
				else
				{
					// Use the default file list.
					init_S (defaultFiles, defaultDTDs);
				}
			}

			// Notify config hierarchy listeners.
			for (Iterator<ConfigListener> i = instance.listeners.iterator(); i.hasNext(); ) {
				ConfigListener chl = i.next();
				chl.initializeConfigValues();
			}
		}
		catch (Exception e) {
			String errorMsg = "An exception occured while initializing " +
				"Config.";

			throw new ConfigException(errorMsg, e);
		}
	}

	/**
	 * Construct using arrays of XML and DTD file names.
	 * A file name may include a complete path.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Files are read and merged in the array order.
	 *
	 * @param	fileNames		the XML file names
	 * @param	dtdNames		the DTD file names
	 * @throws	ConfigException	on error
	 */
	public  Config (String[] fileNames, String[] dtdNames) 
		throws ConfigException
	{
		listeners = new ArrayList<ConfigListener>();
		init (fileNames, dtdNames);
	}

	/** 
	 * Convert the given file name into an InputStream.
	 * This method will first treat fileName as an absolute path, then
	 * as a path relative to the working directory, then as a path 
	 * relative to the classpath.
	 *
	 * @param fileName the file name to ge an InputStream to
	 * @return an InputStream to the file
	 * @throws FileNotFoundException if file not found
	 */
	private static InputStream getInputStream (String fileName) 
		throws FileNotFoundException, IOException
	{
		File file = new File(fileName);

		if (file.exists()) {
			return new FileInputStream(file);
		}

		file = new File(System.getProperty("user.dir"), fileName);

		if (file.exists()) {
			return new FileInputStream(file);
		}

		return new ClasspathFile(fileName).getInputStream();
	}

	/**
	 * Get the hierarchical elements for the given path, expanding if necessary.
	 *
	 * @param	path	the path for the element
	 * @return			a collection of matching elements
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if a system property found
	 * @exception		com.deetysoft.exception.StringFormatException
	 *					if the path string is invalid
	 */
	public Collection<HierarchyNode>	getElements (String path)
		throws MissingPropertyException, StringFormatException
	{
		return data.getNodesByPath (path);
	}

	/**
	 * Static version of {@link #getElements getElements}.
	 * 
	 * @param	path		the path to the element
	 * @return				a Collection of HierarchyNode
	 * @throws	MissingPropertyException	if the element is not found
	 * @throws	StringFormatException		if the argument is not a valid path
	 */
	public static Collection<HierarchyNode>	getElements_S (String path)
		throws MissingPropertyException, StringFormatException
	{
		return instance.getElements (path);
	}

	/**
	 * Return the static instance.
	 * @return	the Config instance
	 */
	public static Config getInstance ()
	{
		return instance;
	}

	/**
	 * Get the value of the element with the given path, expanding if necessary.
	 *
	 * @param	path	the path for the element
	 * @return			the value from the matching element
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if no unique match is found or if a system property
	 *					required for expansion is not found
	 */
	public String getVal (String path) 
		throws MissingPropertyException
	{
		String value = getValUnexpanded (path);

		List<Integer> tokenIndices = TokenIndexRetriever.getIndices (value,
				SUBSTITUTION_TOKEN);

		// If the tokens are not paired
		if (tokenIndices.size() % 2 != 0)
		{
			throw new StringFormatException
				("The element value \""+value+"\" has unmatched \""+
				SUBSTITUTION_TOKEN+"\".",
			"Config.STRING_FORMAT_EXCEPTION",
			ArrayCreator.create (value));
		}

		for (int i = tokenIndices.size()-2; i >= 0; i -= 2)
		{
			int startIndex = tokenIndices.get(i);
			int endIndex = tokenIndices.get(i+1);

			String insertName = value.substring
				(startIndex+1, endIndex);

			String insertValue = null;

			if (insertName.indexOf (ENV_PREFIX) == 0)
			{
				String envName = insertName.substring
					(ENV_PREFIX.length(), insertName.length());

				insertValue = System.getProperty (envName);
			}
			else
			{
				insertValue = getVal (insertName);
			}

			if (insertValue == null)
			{
				throw new MissingPropertyException
					(insertName, Config.class.getName ());
			}

			value = StringReplacer.substitute (value, insertValue,
					startIndex, endIndex);
		}

		return value;
	}

	/**
	 * Get the value of the element with the given path, expanding references
	 * to other values or system properties as necessary.
	 * <br>
	 * Do run-time substitution of args into the value.
	 * See {@link java.text.MessageFormat#format MessageFormat} for info
	 * on how to define the value and args.
	 * Briefly - defines values like this:
	 * <br>
	   {@code <my-element>The day is {0} and month is {1}</my-element>}.
	   <br>
	 * Then call getVal with path="my-element" and args = {"Monday", "April"}
	 * to get a return value of "The day is Monday and the month is April."
	 *
	 * @param	path	the path for the element
	 * @param	args	the run-time args
	 * @return			the expanded value
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if no unique match is found or if a system property
	 *					required for expansion is not found
	 * @exception		com.deetysoft.exception.StringFormatException
	 *					if the path is invalid or the value has unmatched
	 *					substitution delimiters
	 * @see				java.text.MessageFormat
	 * @see				#getValUnexpanded
	 */
	public String	getVal (String path, String[] args)
		throws MissingPropertyException, StringFormatException
	{
		String temp = getVal (path);

		return MessageFormat.format (temp, (Object[])args);
	}

	/**
	 * Get the value of the element with the given path, but do not expand
	 * the value.
	 *
	 * @param	path	the path for the element
	 * @return			the value from the matching element
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if no unique match is found or if a system property
	 *					required for expansion is not found
	 */
	public String	getValUnexpanded (String path) 
		throws MissingPropertyException
	{
		String result = (String)data.getValue (path);

		if (result == null)
			throw new MissingPropertyException
				(path, Config.class.getName ());

		return result;
	}

	/**
	 * Like {@link #getVal(String) getVal} except that null is returned
	 * on missing property.
	 * 
	 * @param	path	get the value of the element with this path
	 * @return			the elements value
	 */
	public String getVal_ (String path)
	{
		try
		{
			return getVal (path);
		}
		catch (MissingPropertyException e)
		{
			return null;
		}
	}

	/**
	 * Static version of {@link #getVal(String) getVal}.
	 * @param	path	get the value of the element with this path
	 * @return			the elements value
	 * @throws	MissingPropertyException	if the element is not found
	 * @throws	StringFormatException		if the argument is not a valid path
	 */
	public static String get (String path)
		throws MissingPropertyException, StringFormatException
	{
		return instance.getVal (path);
	}

	/**
	 * Static version of {@link #getVal(String, String[]) getVal}.
	 * @param	path	get the value of the element with this path
	 * @param	args	the argument values for substitution
	 * @return			the elements value
	 * @throws	MissingPropertyException	if the element is not found
	 * @throws	StringFormatException		if the argument is not a valid path
	 */
	public static String get (String path, String[] args)
		throws MissingPropertyException, StringFormatException
	{
		return instance.getVal (path, args);
	}

	/**
	 * Static version of {@link #getValUnexpanded getValUnexpanded}.
	 * @param	path	path to the desired element
	 * @throws	MissingPropertyException	if the element is not found
	 * @return	the value
	 */
	public static String	getUnexpanded (String path)
		throws MissingPropertyException
	{
		return instance.getValUnexpanded (path);
	}

	/**
	 * Get the root HierarchyNode for the data set.
	 * @return		the root node
	 */
	public HierarchyNode getData ()
	{
		return data;
	}

	/**
	 * Static version of {@link #getData getData}.
	 * @return	a HierarchyNode representing the root of the config data
	 */
	public static HierarchyNode getData_S ()
	{
		return instance.getData();
	}

	/**
	 * Initialize using arrays of xml and dtd file names.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Elements are read and merged in the array order.
	 *
	 * @param		fileNames			the xml file names
	 * @param		dtdNames			the dtd file names
	 * @exception	ConfigException		on error
	 */
	public void	init (String[] fileNames, String[] dtdNames) 
		throws ConfigException
	{
		try {
			// Create new list to contain the file names converted to input
			// streams.
			List<InputStream> xmlStreams = new ArrayList<InputStream>();
			List<URL> dtdUrls = new ArrayList<URL>();

			// For every (xml,dtd) pair
			for (int i = 0; i < fileNames.length; i++) {
				// Resolve file names into input streams.
				InputStream xmlStream = getInputStream(fileNames[i]);

				// Skip files of size 0.
				/*
				   File file = new File (fileName);
				   if (file.length () == 0)
				   continue;
				   */

				xmlStreams.add(xmlStream);

				if (dtdNames[i] != null) {
					if (!dtdNames[i].equals ("null"))
						dtdUrls.add(new ClasspathFile(dtdNames[i]).getUrl());
					else
						dtdUrls.add(null);
				}
				else
				{
					dtdUrls.add (null);
				}
				xmlStream.close();
			}

			data = Hierarchy.buildFromXml(xmlStreams, dtdUrls, true);
		}
		catch (Exception e) {
			String errorMsg = "Could not initialize Config.";
			throw new ConfigException(errorMsg, e);
		}
	}

	/**
	 * Static version of {@link #init(String[],String[]) init}.
	 * @param	fileNames	names of XML files to merge
	 * @param	dtdNames	DTDs for the files. Use null if DTD not used for that file.
	 * @throws	ConfigException	on error
	 */
	public static void	init_S (String[] fileNames, String[] dtdNames) 
		throws ConfigException
	{
		try {
			setInstance (new Config (fileNames, dtdNames));
		}
		catch (Exception e) {
			String errorMsg = "Could not initialize Config.";
			throw new ConfigException(errorMsg, e);
		}
	}

	/**
	 * Have the static members been initialized?
	 * @return	true or false
	 */
	public static boolean isInitialized ()
	{
		return instance == null ? false : true;
	}

	/**
	 * Set the singleton.
	 *
	 * @param	instance_	the new instance
	 */
	public static void setInstance (Config instance_)
	{
		if (instance != null)
			instance_.listeners = instance.listeners;
		instance = instance_;
	}

	/**
	 * Add a listener for config hierarchy events.
	 * @param	chl		a ConfigListener
	 */
	public static void addConfigListener(ConfigListener chl)
	{
		instance.listeners.add(chl);
	}

	/**
	 * Remove a config hierarchy listener.
	 * @param	chl		a ConfigListener
	 */
	public static void removeConfigListener(ConfigListener chl)
	{
		instance.listeners.remove(chl);
	}

	public String toString () {
		return data.toString();
	}
}
