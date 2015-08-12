package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.DataRoleEnum;
import com.echostar.gopher.persist.DataTypeEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestDataType;

/**
 * A TestClass for {@link TestClass TestClass}
 * @author charles.young
 *
 */
public class TestClass_Test extends TestClassBase {

	@Test
	public static void testCreate () {

		// Insert a TestClass.
		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();
		Transaction tran = hibernateSession.beginTransaction();
		TestClass testClass = new TestClass ("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass", "an example TestClass", Boolean.TRUE,
			"a Jira issue");
		Long testClassId = (Long) hibernateSession.save(testClass);
		testClass.setId(testClassId);
		tran.commit();

		// Verify the object was inserted.
		TestClass actualTestClass = (TestClass) hibernateSession.get(TestClass.class, testClassId);
		Assert.assertNotNull(actualTestClass);
		Assert.assertEquals (actualTestClass, testClass);
	}

	@Test
	public static void testGopherData () throws Exception {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// Create a TestClass.
		TestClass testClass = gopherData.createTestClass ("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass", "an example TestClass",
			Boolean.TRUE, "a Jira issue");

		// Relate test data types.
		List<TestDataType> dataTypes = new ArrayList<TestDataType>();
		TestDataType dataType0 = gopherData.createTestDataType ("data type 0",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);
		TestDataType dataType1 = gopherData.createTestDataType ("data type 1",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);
		dataTypes.add (dataType0);
		dataTypes.add (dataType1);
		testClass.setTestDataTypes (dataTypes);
		hibernateSession.update(testClass);

		tran.commit();

		// Verify the object was inserted.
		TestClass actualTestClass = (TestClass) hibernateSession.get(TestClass.class, testClass.getId());
		Assert.assertNotNull(actualTestClass);
		Assert.assertEquals (actualTestClass, testClass);

		// Verify the data types were related.
		List<TestDataType> actualDataTypes = actualTestClass.getTestDataTypes ();
		Assert.assertEquals(actualDataTypes.size(), 2);
		Assert.assertTrue (actualDataTypes.contains(dataType0));
		Assert.assertTrue (actualDataTypes.contains(dataType1));
	}
}