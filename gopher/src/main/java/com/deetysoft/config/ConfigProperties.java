package com.deetysoft.config;

import com.deetysoft.collections.ArrayCreator;
import com.deetysoft.exception.MissingPropertyException;
import com.deetysoft.exception.StringFormatException;
import com.deetysoft.util.StringReplacer;
import com.deetysoft.util.TokenIndexRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A configuration properties utility.
 * ConfigProperties reads properties from one or more
 * {@link java.util.Properties property} files.
 * It merges properties from multiple files to form a single set.
 * <p>
 * A property value may declare that it needs another property value
 * substituted into it (value expansion.)
 * The property to be substituted is defined by a begining and ending '%'
 * surrounding a property name.
 * For example:
 * <p>
 * p1=blee
 * <br>
 * p2=p1 value is %p1%.
 * <p>
 * In this case, p2's value after substitution is "p1 value is blee."
 * Substitutions may be nested to any level.
 * If the name of the property to be substituted begins with 'env.',
 * ConfigProperties looks for a system property to substitute.
 * See {@link #ENV_PREFIX ENV_PREFIX} for details.
 * <p>
 * We create a singleton for use throughout the application.
 * It is created during static initialization using the default constructor
 * {@link #ConfigProperties ConfigProperties()}.
 *
 * @see java.util.Properties
 */
public class ConfigProperties
{
	/**
     * The class defining config file names - "com.deetysoft.config.ConfigFiles".
     */
	public static final String	CONFIG_FILES_CLASS	=
		"com.deetysoft.config.ConfigPropertiesFiles";

	/**
	 * The default properties file - "config.properties".
	 */
	public static final String	DEFAULT_FILE_NAME	= "config.properties";

	/**
	 * The prefix '.env' for substitution strings denotes a system property.
	 * For example: home=%env.HOME% sets the property named home
	 * to have the value of the HOME system property.
	 */
	public static final String	ENV_PREFIX			= "env.";

	/**
	 * The system property for defining the file list - "config.files".
	 */
	public static final String	FILES_PROPERTY		= 
		"config-properties.files";

	/**
	 * The token used to delimit a substitution -'%'.
	 */
	public static final char	SUBSTITUTION_TOKEN	= '%';

	private static String[]		defaultList			= {DEFAULT_FILE_NAME};

	private static ConfigProperties		instance;

	private HashMap<String, String>		properties = new HashMap<String, String> ();

	/**
	 * Create our instance.
	 */
	static
	{
		try
		{
			instance = new ConfigProperties ();
			Iterator<String> properties = instance.getProperties ();
			if (!properties.hasNext ())
			{
				//System.out.println
				//	("ConfigProperties singleton has no properties defined.");
			}
		}
		catch (Exception e)
		{
			System.err.println ("Failed to create singleton.");
			e.printStackTrace ();
		}
	}

	/**
	 * The default constructor.
	 * It is used in static initialization to create the singleton.
	 * <p>
	 * Look for property files using the following rules:
	 * <p>
	 * 1. If the system property "config-properties.files" exists, assume 
	 * it specifies a property file list.
	 * File names are separated by a ';'.
	 * For example when using the -D option to start the vm:
	 * <p>
	 * -DConfig-properties.files=f1.properties;f2.properties
	 * <p>
	 * 2. Try to instantiate by name the class
	 * "com.deetysoft.config.ConfigPropertiesFiles."
	 * It is not part of the package but may be defined by the
	 * application as an alternative to using the system
	 * property config-properties.files. 
	 * ConfigPropertiesFiles must define a method
	 * <pre>
	 * public static String[] getFileNames ()
	 * </pre>
	 * to provide the file name list.
	 * <p>
	 * 3. If neither config-properties.files or ConfigPropertiesFiles is 
	 * defined then try to load
	 * the default properties file {@link #DEFAULT_FILE_NAME DEFAULT_FILE_NAME}.
	 * <p>
	 * A file name may include a complete path.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Properties are read and merged in the specified order.
	 *
	 * @exception	IOException			from io package
	 * @exception	SecurityException	from security manager on accessing
	 *									system property
	 */
	protected ConfigProperties () throws IOException, SecurityException
	{
		// If the system property defining the file names exists

		String fileNames = System.getProperty (FILES_PROPERTY);

		if (fileNames != null)
		{
			// Use the file names.

			Vector<String> v = new Vector<String> ();

			StringTokenizer tokenizer = new StringTokenizer (fileNames,";");

			while (tokenizer.hasMoreTokens ())
			{
				String fileName = tokenizer.nextToken ();
				v.addElement (fileName);
			}

			String[] list = new String [v.size()];

			list = v.toArray (list);

			init (list);
		}
		else
		{
			String[] names = null;

			// Try to instantiate the config files class.
			try
			{
				Class<?> class_ = Class.forName (CONFIG_FILES_CLASS);

				Method method = class_.getMethod ("getFileNames", (Class[])null);

				names = (String[]) method.invoke (null, (Object[])null);
			}
			catch (Exception e)
			{
				// Ok, names will be null.
			}

			if (names != null)
			{
				init (names);
			}
			else
			{
				// Use the default file list.

				init (defaultList);
			}
		}
	}

	/**
	 * Construct using an array of property file names.
	 * A file name may include a complete path.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Properties are read and merged in the array order.
	 *
	 * @param		fileNames		the property file names
	 * @exception	IOException		from io package
	 */
	public  ConfigProperties (String[] fileNames) throws IOException
	{
		init (fileNames);
	}

	/**
	 * Dump the properties using System.out.
	 */
	public void dumpProperties ()
	{
		Iterator<String> iter = properties.keySet ().iterator ();

		while (iter.hasNext())
		{
			String key = iter.next();
			System.out.println (key+" "+properties.get (key));
		}
	}

	/**
	 * Static version of {@link #dumpProperties dumpProperties}.
	 */
	public static void dumpProperties_S ()
	{
		instance.dumpProperties ();
	}

	/**
	 * Get the value for the given property, expanding if necessary.
	 * Use {@link #getPropertyUnexpanded getPropertyUnexpanded}
	 * to get the unexpanded value.
	 *
	 * @param	name	the property name
	 * @return			the value
	 * @see				#getPropertyUnexpanded
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		com.deetysoft.exception.StringFormatException
	 *					if property value has unmatched substitution
	 *					delimeters
	 */
	public String	getProperty (String name)
		throws MissingPropertyException, StringFormatException
	{
		String value = properties.get (name);

		if (value == null)
		{
			throw new MissingPropertyException
				(name, "Config");
		}

		List<Integer> tokenIndices = TokenIndexRetriever.getIndices (value,
				SUBSTITUTION_TOKEN);

		// If the tokens are not paired
		if (tokenIndices.size() % 2 != 0)
		{
			throw new StringFormatException
				("The property value \""+value+"\" has unmatched \""+
				 SUBSTITUTION_TOKEN+"\".",
				 "ConfigProperties.STRING_FORMAT_EXCEPTION",
				 ArrayCreator.create (value));
		}

		for (int i = tokenIndices.size()-2; i >= 0; i -= 2)
		{
			int startIndex = tokenIndices.get(i);
			int endIndex = tokenIndices.get(i+1);

			String propertyName = value.substring
				(startIndex+1, endIndex);

			String propertyValue = null;

			if (propertyName.indexOf (ENV_PREFIX) == 0)
			{
				String envName = propertyName.substring
					(ENV_PREFIX.length(),propertyName.length());

				propertyValue = System.getProperty (envName);
			}
			else
			{
				propertyValue = getProperty (propertyName);
			}

			if (propertyValue == null)
			{
				throw new MissingPropertyException
					(propertyName, "Config");
			}

			value = StringReplacer.substitute (value, propertyValue,
					startIndex, endIndex);
		}

		return value;
	}

	/**
	 * Get the value for the given property, expanding references to other
	 * properties if necessary.
	 * <br>
	 * Do run-time substitution of args into the property value.
	 * See {@link java.text.MessageFormat#format MessageFormat} for info
	 * on how to define the property value and args.
	 * Briefly - defines property values like this:
	 * <br>
	 * MyClass.MY_PROPERTY=The day is {0} and month is {1}.
	 * <br>
	 * Then call getProperty with args = {"monday", "April"} to get
	 * a return value of "the day is monday and the month is april."
	 *
	 * @param	name	the property name
	 * @param	args	the run-time args
	 * @return			the value
	 * @see				java.text.MessageFormat
	 * @see				#getPropertyUnexpanded
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		com.deetysoft.exception.StringFormatException
	 *					if property value has unmatched substitution
	 *					delimeters
	 */
	public String	getProperty (String name, String[] args)
		throws MissingPropertyException, StringFormatException
	{
		String temp = getProperty (name);

		return MessageFormat.format (temp, (Object[])args);
	}

	/**
	 * Static version of {@link #getProperty(String) getProperty}.
	 * @param	name	the name of the property
	 * @return			the property value
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		com.deetysoft.exception.StringFormatException
	 *					if property value has unmatched substitution
	 *					delimeters
	 */
	public static String getProperty_S (String name)
		throws MissingPropertyException, StringFormatException

	{
		return instance.getProperty (name);
	}

	/**
	 * Static version of {@link #getProperty(String, String[]) getProperty}.
	 * @param	name	the property name
	 * @param	args	the run-time args
	 * @return			the value
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		com.deetysoft.exception.StringFormatException
	 *					if property value has unmatched substitution
	 *					delimeters
	 */
	public static String getProperty_S (String name, String[] args)
		throws MissingPropertyException, StringFormatException

	{
		return instance.getProperty (name, args);
	}

	/**
	 * Get the value for the given property without expanding.
	 *
	 * @param	name	the property name
	 * @return			the value or null
	 * @exception		com.deetysoft.exception.MissingPropertyException
	 *					if property or any nested property not found
	 */
	public String	getPropertyUnexpanded (String name)
		throws MissingPropertyException
	{
		String value = properties.get (name);

		if (value == null)
		{
			throw new MissingPropertyException
				("Missing property \""+name+"\".",
				 "ConfigProperties.MISSING_PROPERTY_EXCEPTION",
				 ArrayCreator.create (name));
		}
		return value;
	}

	/**
	 * Static version of {@link #getPropertyUnexpanded getPropertyUnexpanded}.
	 * @param	name	the property name
	 * @return			the unexpanded property value
	 */
	public static String	getPropertyUnexpanded_S (String name)
		throws MissingPropertyException
	{
		return instance.getPropertyUnexpanded (name);
	}

	/**
	 * Get an iterator over the set of property names.
	 * @return		an iterator over strings
	 */
	public Iterator<String> getProperties ()
	{
		return properties.keySet().iterator();
	}

	/**
	 * Static version of {@link #getProperties getProperties}.
	 * @return		an iterator over strings
	 */
	public static Iterator<String> getProperties_S ()
	{
		return instance.getProperties();
	}

	/**
	 * Initialize using an array of property file names.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Properties are read and merged in the array order.
	 *
	 * @param		fileNames		the property file names
	 * @exception	IOException		from io package
	 */
	public void init (String[] fileNames) throws IOException
	{
		Properties p = new Properties ();

		for (int i = 0; i < fileNames.length; i++)
		{
			String fileName = fileNames [i];

			File file = new File (fileName);

			InputStream stream = null;

			if (file.getParent () == null)
			{
				// See if it is in the working dir.

				boolean exists = false;

				// Allow for security exception.
				try
				{
					exists = file.exists ();
				}
				catch (Exception e)
				{
					System.out.println ("Property file "+file.getName()+" not found, searching CLASSPATH.");
				}

				if (exists)
				{
					stream = new FileInputStream (file);
				}
				else
				{
					// Search the CLASSPATH for the file.

					Class<?> class_ = getClass ();
					ClassLoader cl = class_.getClassLoader ();

					if (cl.getResource (fileName) != null)
					{
						stream = cl.getResourceAsStream (fileName);
					}
					else
						continue;
				}
			}
			else // There is a complete path.
			{
				stream = new FileInputStream (file);
			}

			// Merge the properties.
			p.load (stream);
			//properties.putAll (p);
			Enumeration<?> enumer = p.propertyNames();
			while (enumer.hasMoreElements()) {
				String name = (String) enumer.nextElement();
				properties.put (name, p.getProperty(name));
			}
			stream.close();
		}
	}

	/**
	 * Set the singleton.
	 *
	 * @param	instance_	the new instance
	 */
	public static void setInstance (ConfigProperties instance_)
	{
		instance = instance_;
	}
}
