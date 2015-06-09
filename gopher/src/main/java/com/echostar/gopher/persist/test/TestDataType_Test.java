package com.echostar.gopher.persist.test;

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

public class TestDataType_Test extends TestClassBase {

	@Test
	public static void testCreate () throws Exception {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		// Insert a TestClass with related test data types.
		Transaction tran = hibernateSession.beginTransaction();

		TestClass testClass = new TestClass("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass", "an example TestClass", Boolean.TRUE,
			"a Jira issue");
		Long testClassId = (Long) hibernateSession.save(testClass);
		testClass.setId(testClassId);

		TestDataType testDataType1 = new TestDataType ("arg type 0", DataTypeEnum.STRING, DataRoleEnum.LABEL);
		TestDataType testDataType2 = new TestDataType ("arg type 1", DataTypeEnum.STRING, DataRoleEnum.LABEL);
		Long testDataType1Id = (Long) hibernateSession.save(testDataType1);
		Long testDataType2Id = (Long) hibernateSession.save(testDataType2);
		testDataType1.setId (testDataType1Id);
		testDataType2.setId (testDataType2Id);

		List<TestDataType> testDataTypes = new ArrayList<TestDataType>();
		testDataTypes.add(testDataType1);
		testDataTypes.add(testDataType2);
		
		testClass.setTestDataTypes (testDataTypes);
		hibernateSession.update(testClass);

		tran.commit();

		// Verify the TestClass was inserted.
		TestClass actualTestClass = (TestClass) hibernateSession.get(TestClass.class, testClassId);
		Assert.assertNotNull(actualTestClass);
		Assert.assertEquals (testClassId, actualTestClass.getId());
		
		// Verify the test data types were related.
		List<TestDataType> actualTestDataTypes = actualTestClass.getTestDataTypes ();
		Assert.assertEquals(actualTestDataTypes.size(), 2);
		Assert.assertEquals (actualTestDataTypes.contains(testDataType1), true);
		Assert.assertEquals (actualTestDataTypes.contains(testDataType2), true);
	}

	@Test
	public static void testGopherData () throws Exception {
		
		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// First create TestClasses for the suite.
		TestClass testClass = gopherData.createTestClass("AnExampleTestClass", "a version", "com.echostar.gopher.dany.AnExampleTestClass",
				"an example TestClass", Boolean.TRUE, "a Jira issue");

		TestDataType testDataType1 = gopherData.createTestDataType ("arg type 0",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);
		TestDataType testDataType2 = gopherData.createTestDataType ("arg type 1",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);

		List<TestDataType> testDataTypes = new ArrayList<TestDataType>();
		testDataTypes.add(testDataType1);
		testDataTypes.add(testDataType2);
		testClass.setTestDataTypes(testDataTypes);

		tran.commit();

		// Verify the TestClass was inserted.
		TestClass actualTestClass = (TestClass) hibernateSession.get(TestClass.class, testClass.getId());
		Assert.assertNotNull(actualTestClass);
		Assert.assertEquals (actualTestClass.getId(), testClass.getId());
		
		// Verify the test data types were related.
		List<TestDataType> actualTestDataTypes = actualTestClass.getTestDataTypes ();
		Assert.assertEquals(actualTestDataTypes.size(), 2);
		Assert.assertEquals (actualTestDataTypes.contains(testDataType1), true);
		Assert.assertEquals (actualTestDataTypes.contains(testDataType2), true);
	}
}
