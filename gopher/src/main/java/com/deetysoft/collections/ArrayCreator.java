package com.deetysoft.collections;

/**
 * A class for creating arrays from individual elements.
 */
public class ArrayCreator
{
	/**
	 * Create an Object Array of length 1.
	 *
	 * @param	obj	the single element
	 * @return			the array
	 */
	public static Object[] create (Object obj)
	{
		Object[] objs = new Object [1];
		objs [0] = obj;
		return objs;
	}

	/**
	 * Create an String Array of length 1.
	 *
	 * @param	str	the single element
	 * @return			the array
	 */
	public static String[] create (String str)
	{
		String[] objs = new String [1];
		objs [0] = str;
		return objs;
	}

	/**
	 * Create an Object Array of length 2.
	 *
	 * @param	obj1	the first element
	 * @param	obj2	the second element
	 * @return			the array
	 */
	public static Object[] create (Object obj1, Object obj2)
	{
		Object[] objs = new Object [2];
		objs [0] = obj1;
		objs [1] = obj2;
		return objs;
	}

	/**
	 * Create an Object Array of length 3.
	 *
	 * @param	obj1	the first element
	 * @param	obj2	the second element
	 * @param	obj3	the third element
	 * @return			the array
	 */
	public static Object[] create (Object obj1, Object obj2,
												Object obj3)
	{
		Object[] objs = new Object [3];
		objs [0] = obj1;
		objs [1] = obj2;
		objs [2] = obj3;
		return objs;
	}

	/**
	 * Create an Object Array of length 4.
	 *
	 * @param	obj1	the first element
	 * @param	obj2	the second element
	 * @param	obj3	the third element
	 * @param	obj4	the fourth element
	 * @return			the array
	 */
	public static Object[] create (Object obj1, Object obj2,
												Object obj3, Object obj4)
	{
		Object[] objs = new Object [4];
		objs [0] = obj1;
		objs [1] = obj2;
		objs [2] = obj3;
		objs [3] = obj4;
		return objs;
	}

	/**
	 * Create an Object Array of length 5.
	 *
	 * @param	obj1	the first element
	 * @param	obj2	the second element
	 * @param	obj3	the third element
	 * @param	obj4	the fourth element
	 * @param	obj5	the fifth element
	 * @return			the array
	 */
	public static Object[] create (Object obj1, Object obj2,
	Object obj3, Object obj4, Object obj5)
	{
		Object[] objs = new Object [5];
		objs [0] = obj1;
		objs [1] = obj2;
		objs [2] = obj3;
		objs [3] = obj4;
		objs [4] = obj5;
		return objs;
	}

	/**
	 * Create an Object Array of length 6.
	 *
	 * @param	obj1	the first element
	 * @param	obj2	the second element
	 * @param	obj3	the third element
	 * @param	obj4	the fourth element
	 * @param	obj5	the fifth element
	 * @param	obj6	the sixth element
	 * @return			the array
	 */
	public static Object[] create (Object obj1, Object obj2,
	Object obj3, Object obj4, Object obj5, Object obj6)
	{
		Object[] objs = new Object [6];
		objs [0] = obj1;
		objs [1] = obj2;
		objs [2] = obj3;
		objs [3] = obj4;
		objs [4] = obj5;
		objs [5] = obj6;
		return objs;
	}

	/**
	 * Copy bytes from one array to another.
	 * @param from	byte array to copy from
	 * @param count	number of bytes to copy
	 * @return		the new byte array
	 */
	public static byte[] copy (byte[] from, int count)
	{
		byte[] to = new byte[count];

		for (int i=0; i < count; i++)
		{
			to[i] = from[i];
		}
		return to;
	}
}
