package com.deetysoft.exception;

import com.deetysoft.collections.ArrayCreator;

import java.text.MessageFormat;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A TestClass for
 * {@link com.deetysoft.exception.MissingPropertyException
 * com.deetysoft.exception.MissingPropertyException}.
 */
public class MissingPropertyExceptionTest
{
	/**
	 * Test the convenience constructor
	 * {@link com.deetysoft.exception.MissingPropertyException#MissingPropertyException
	 * (String, String)
	 * MissingPropertyException(String propertyName, String storeName)}.
	 * <p>
	 * We verify that the methods
	 * {@link com.deetysoft.exception.MissingPropertyException#getMessage getMessage},
	 * {@link com.deetysoft.exception.MissingPropertyException#getKey getKey} and
	 * {@link com.deetysoft.exception.MissingPropertyException#getRootMessageUnexpanded
	 * getMessageUnexpanded}
	 * work as expected after construction.
	 * 
	 * @throws	Exception	on any error
	 */
	@Test
	public static void testConvenienceConstructor () throws Exception
	{
		// Construct with the default message and key.

		MissingPropertyException mpe = new MissingPropertyException
			("a property", "a store");

		// Test getMessage.

		String expected = MessageFormat.format
			(MissingPropertyException.DEFAULT_MESSAGE,
			ArrayCreator.create ("a property", "a store"));

		Assert.assertEquals (expected, mpe.getMessage ());

		// Test getRootMessageUnexpanded.

		Assert.assertEquals (null,
			mpe.getRootMessageUnexpanded ());

		// Test getKey. It should return the class name.

		Assert.assertEquals (mpe.getClass().getName(), mpe.getKey ());
	}
}
