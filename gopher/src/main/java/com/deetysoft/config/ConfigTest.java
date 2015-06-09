package com.deetysoft.config;

import com.deetysoft.exception.MissingPropertyException;
import com.deetysoft.util.HierarchyNode;

import java.util.Collection;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * A TestClass for {@link com.deetysoft.config.Config
 * com.deetysoft.config.Config}.
 *
 */
public class ConfigTest
{
	/**
	 * {@link com.deetysoft.config.ConfigProperties ConfigProperties} property defining the XML files
	 * for our tests. The value is "ConfigTest.FILE_NAMES".
	 */
	public static final String	FILES_PROPERTY =
								"config-test.file-names";

	/**
	 * {@link com.deetysoft.config.ConfigProperties ConfigProperties} property defining the DTD files
	 * for our tests. The value is "ConfigTest.DTD_NAMES".
	 */
	public static final String	DTDS_PROPERTY =
								"config-test.dtd-names";

	/**
	 * Set the system properties
	 * {@link com.deetysoft.config.Config#FILES_PROPERTY
	 * Config.FILES_PROPERTY} and
	 * {@link com.deetysoft.config.Config#DTDS_PROPERTY 
	 * Config.DTDS_PROPERTY}
	 * to the values of
	 * {@link #FILES_PROPERTY FILES_PROPERTY} and
	 * {@link #DTDS_PROPERTY DTDS_PROPERTY}.
	 * Call
	 * {@link com.deetysoft.config.Config#init_S() Config.init_S}
	 * to re-initialize for our tests.
	 * @throws	Exception	on error
	 */
	@BeforeClass
	public static void testInit_S () throws Exception
	{
		System.setProperty (Config.FILES_PROPERTY,
			ConfigProperties.getProperty_S (FILES_PROPERTY));

		System.setProperty (Config.DTDS_PROPERTY,
			ConfigProperties.getProperty_S (DTDS_PROPERTY));

		Config.init_S ();
	}

	/**
	 * Test missing property.
	 * @throws	Exception	on error
	 */
	@Test
	public static void testMissingProperty () throws Exception
	{
		try
		{
			String unknownName = "config-test/a-missing-property";

			Config.get (unknownName);

			throw new Exception
				("Property \""+unknownName+"\" should not exist.");
		}
		catch (Exception e)
		{
			Assert.assertTrue(e instanceof MissingPropertyException);
		}
	}

	/**
	 * Test the {@link com.deetysoft.config.Config#get get}
	 * method with value substitution.
	 * @throws	Exception	on error
	 */
	@Test
	public static void testPropertySubstitution () throws Exception
	{
		// Test property value substitution.

		String format = Config.get
			("config-test/sub-test1-format");
		String value = Config.get
			("config-test/sub-test1-value");

		if (!format.equals (value))
		{
			throw new Exception
				("Format string \""+format+"\" not equal to \""+value+"\".");
		}
	}

	/**
	 * Test the {@link com.deetysoft.config.Config#get get} method
	 * with run-time argument substitution.
	 * @throws	Exception	on error
	 */
	@Test
	public static void testArgumentSubstitution () throws Exception
	{
		String[] args = {Config.get
			("config-test/monday"),
			Config.get
				("config-test/april")};

		String format = Config.get
			("config-test/arg-test1-format", args);

		String value = Config.get
			("config-test/arg-test1-value");

		if (!format.equals (value))
		{
			throw new Exception
				("Format string \""+format+"\" not equal to \""+value+"\".");
		}
	}

	/**
	 * Test the {@link com.deetysoft.config.Config#get get}
	 * method with system property substitution.
	 * @throws	Exception	on error
	 */
	@Test
	public static void testSystemPropertySubstitution () throws Exception
	{
		System.setProperty (Config.get
			("config-test/env-name"),
			Config.get
				("config-test/env-value"));

		String format = Config.get
			("config-test/env-test1-format");
		String value = Config.get
			("config-test/env-test1-value");

		if (!format.equals (value))
		{
			throw new Exception
				("Format string \""+format+"\" not equal to \""+value+"\".");
		}
	}

	/**
	 * Test the {@link com.deetysoft.config.Config#getElements_S
	 * getElements_S}.
	 * @throws	Exception	on error
	 */
	public static void testGetElements_S () throws Exception
	{
		Collection<HierarchyNode> elements = Config.getElements_S
			("config-test/elements/element");

		Assert.assertEquals (3, elements.size ());
	}
}
