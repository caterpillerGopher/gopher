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
import com.echostar.gopher.persist.TestData;
import com.echostar.gopher.persist.TestDataType;
import com.echostar.gopher.persist.TestCase;

public class TestData_Test  extends TestClassBase {

	@Test
	public static void testCreate () {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		// Insert a TestClass with related test data types.
		Transaction tran = hibernateSession.beginTransaction();

		TestClass testClass = new TestClass("AnExampleTestClass", "a version", "com.echostar.gopher.dany.AnExampleTestClass",
				"an example TestClass", Boolean.TRUE, "a Jira issue");
		Long testClassId = (Long) hibernateSession.save(testClass);
		testClass.setId(testClassId);

		TestDataType testDataType1 = new TestDataType ("arg type 0", DataTypeEnum.STRING, DataRoleEnum.LABEL);
		TestDataType testDataType2 = new TestDataType ("arg type 1", DataTypeEnum.STRING, DataRoleEnum.LABEL);
		Long testDataType1Id = (Long) hibernateSession.save(testDataType1);
		Long testDataType2Id = (Long) hibernateSession.save(testDataType2);
		testDataType1.setId(testDataType1Id);
		testDataType2.setId(testDataType2Id);

		TestCase testCase = new TestCase ("A case name", "a version", true, testClass);
		Long testCaseId = (Long) hibernateSession.save(testCase);
		testCase.setId(testCaseId);

		TestData testData1 = new TestData (testDataType1, "a value");
		TestData testData2 = new TestData (testDataType2, "another value");
		Long testData1Id = (Long) hibernateSession.save(testData1);
		Long testData2Id = (Long) hibernateSession.save(testData2);
		testData1.setId(testData1Id);
		testData2.setId(testData2Id);
		List<TestData> testData = new ArrayList<TestData>();
		testData.add(testData1);
		testData.add(testData2);
		testCase.setTestData(testData);
		hibernateSession.update (testCase);

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
		
		// Verify the test data values were inserted and related to the test data types.
		TestData actualTestData1 = (TestData) hibernateSession.get(TestData.class, testData1Id);
		TestData actualTestData2 = (TestData) hibernateSession.get(TestData.class, testData2Id);
		Assert.assertEquals (actualTestData1, testData1);
		Assert.assertEquals (actualTestData2, testData2);
		
		Assert.assertEquals(actualTestData1.getTestDataType(), testDataType1);
		Assert.assertEquals(actualTestData2.getTestDataType(), testDataType2);
	}

	@Test
	public static void testGopherData () throws Exception {
		
		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// First create a TestClass.
		TestClass testClass = gopherData.createTestClass("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass",
			"an example TestClass", Boolean.TRUE, "a Jira issue");

		TestCase testCase = new TestCase ("A case name", "a version", true, testClass);
		Long testCaseId = (Long) hibernateSession.save(testCase);
		testCase.setId(testCaseId);

		// Relate test data types.
		TestDataType testDataType1 = gopherData.createTestDataType ("arg type 0",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);
		TestDataType testDataType2 = gopherData.createTestDataType ("arg type 1",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);

		List<TestDataType> testDataTypes = new ArrayList<TestDataType>();
		testDataTypes.add(testDataType1);
		testDataTypes.add(testDataType2);
		testClass.setTestDataTypes(testDataTypes);

		// Relate test data.
		TestData testData1 = gopherData.createTestData(testDataType1, "a value");
		TestData testData2 = gopherData.createTestData(testDataType2, "another value");

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

		// Verify the test data was related to the data types.
		Assert.assertEquals(testDataType1, testData1.getTestDataType());
		Assert.assertEquals(testDataType2, testData2.getTestDataType());
	}
}
