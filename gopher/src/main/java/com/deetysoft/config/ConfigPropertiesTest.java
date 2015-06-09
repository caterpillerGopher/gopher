package com.deetysoft.config;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.deetysoft.exception.MissingPropertyException;

/**
 * A TestClass for {@link com.deetysoft.config.ConfigProperties com.deetysoft.config.ConfigProperties}.
 *
 */
public class ConfigPropertiesTest
{
	/**
	 * Test missing property.
	 * @throws	Exception	on error
	 */
	@Test
	public static void testMissingProperty () throws Exception
	{
		try
		{
			String unknownName = "a missing property";

			ConfigProperties.getProperty_S (unknownName);

			throw new Exception
				("Property \""+unknownName+"\" should not exist.");
		}
		catch (Exception e)
		{
			Assert.assertTrue(e instanceof MissingPropertyException);
		}
	}

	/**
	 * Test the getProperties method with property value substitution.
	 * @throws	Exception	on error
	 */
	@Test
	public static void testPropertySubstitution () throws Exception
	{
		// Test property value substitution.

		String format = ConfigProperties.getProperty_S ("config-properties-test.sub-test1-format");
		String value = ConfigProperties.getProperty_S ("config-properties-test.sub-test1-value");

		if (!format.equals (value))
		{
			throw new Exception
				("Format string \""+format+"\" not equal to \""+value+"\".");
		}
	}


	/**
	 * Test the getProperties method with run-time argument substitution.
	 * @throws	Exception	on error
	 */
	@Test
	public static void testArgumentSubstitution () throws Exception
	{
		String[] args = {ConfigProperties.getProperty_S ("config-properties-test.monday"),
						ConfigProperties.getProperty_S ("config-properties-test.april")};

		String format = ConfigProperties.getProperty_S ("config-properties-test.arg-test1-format",
				args);

		String value = ConfigProperties.getProperty_S ("config-properties-test.arg-test1-value");

		if (!format.equals (value))
		{
			throw new Exception
				("Format string \""+format+"\" not equal to \""+value+"\".");
		}
	}

	/**
	 * Test the getProperties method with environment variable substitution.
	 * @throws	Exception	on error
	 */
	public static void testEnvSubstitution () throws Exception
	{
		System.setProperty (ConfigProperties.getProperty_S ("config-properties-test.env-name"),
				ConfigProperties.getProperty_S ("config-properties-test.env-value"));

		String format = ConfigProperties.getProperty_S ("config-properties-test.env-test1-format");
		String value = ConfigProperties.getProperty_S ("config-properties-test.env-test1-value");

		if (!format.equals (value))
		{
			throw new Exception
				("Format string \""+format+"\" not equal to \""+value+"\".");
		}
	}
}
