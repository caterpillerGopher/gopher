package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.DataRoleEnum;
import com.echostar.gopher.persist.DataTypeEnum;
import com.echostar.gopher.persist.ElementLocator;
import com.echostar.gopher.persist.ElementLocatorType;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestData;
import com.echostar.gopher.persist.TestDataType;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestSuite;

/**
 * A TestClass for {@link TestCase TestCase}
 * @author charles.young
 *
 */
public class TestCase_Test extends TestClassBase {

	@Test
	public static void testCreate () {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// Insert a test suite.
		TestSuite testSuite = new TestSuite ("A test suite", "a version", "A description", true, null);
		Long suiteId = (Long) hibernateSession.save(testSuite);
		testSuite.setId (suiteId);

		// Insert a TestClass.
		TestClass testClass = new TestClass("AnExampleTestClass", "a version", "com.echostar.gopher.dany.AnExampleTestClass",
				"an example TestClass", Boolean.TRUE, "a Jira issue");

		// Relate classes to suites.
		List<TestSuite> testSuites = new ArrayList<TestSuite>();
		testSuites.add (testSuite);
		testClass.setTestSuites(testSuites);
		Long testClassId = (Long) hibernateSession.save(testClass);
		testClass.setId (testClassId);

		List<TestClass> testClasses = new ArrayList<TestClass>();
		testClasses.add(testClass);
		testSuite.setTestClasses(testClasses);
		hibernateSession.update(testSuite);

		// Relate test data types to TestClass.
		TestDataType testDataType1 = new TestDataType ("arg type 0", DataTypeEnum.STRING, DataRoleEnum.LABEL);
		TestDataType testDataType2 = new TestDataType ("arg type 1", DataTypeEnum.STRING, DataRoleEnum.LABEL);
		testDataType1.addTestClass(testClass);
		testDataType2.addTestClass(testClass);
		Long testDataType1Id = (Long) hibernateSession.save(testDataType1);
		Long testDataType2Id = (Long) hibernateSession.save(testDataType2);
		testDataType1.setId(testDataType1Id);
		testDataType2.setId(testDataType2Id);
		List<TestDataType> testDataTypes = new ArrayList<TestDataType>();
		testDataTypes.add(testDataType1);
		testDataTypes.add(testDataType2);
		testClass.setTestDataTypes (testDataTypes);
		hibernateSession.update(testClass);

		// Insert a test case.
		TestCase testCase = new TestCase("A case name", "a version", true, testClass);
		Long testCaseId = (Long) hibernateSession.save(testCase);
		testCase.setId(testCaseId);

		// Insert test data.
		TestData testData1 = new TestData (testDataType1, "a value");
		TestData testData2 = new TestData (testDataType2, "another value");
		testData1.addTestCase(testCase);
		testData2.addTestCase(testCase);
		Long testData1Id = (Long) hibernateSession.save(testData1);
		Long testData2Id = (Long) hibernateSession.save(testData2);
		testData1.setId(testData1Id);
		testData2.setId(testData2Id);
		List<TestData> testData = new ArrayList<TestData>();
		testData.add(testData1);
		testData.add(testData2);
		testCase.setTestData (testData);
		hibernateSession.update(testCase);

		// Insert element locators.
		ElementLocator locator1 = new ElementLocator (ElementLocatorType.ID, "locator 1", "a value", "a locator by id");
		ElementLocator locator2 = new ElementLocator (ElementLocatorType.XPATH, "locator 2", "another value", "a locator by xpath");
		locator1.addTestCase(testCase);
		locator2.addTestCase(testCase);
		Long locator1Id = (Long) hibernateSession.save(locator1);
		Long locator2Id = (Long) hibernateSession.save(locator2);
		locator1.setId(locator1Id);
		locator2.setId(locator2Id);
		List<ElementLocator> locators = new ArrayList<ElementLocator>();
		locators.add(locator1);
		locators.add(locator2);
		testCase.setElementLocators (locators);
		hibernateSession.update(testCase);

		tran.commit();

		// Verify the test suite was inserted.
		TestSuite actualTestSuite = (TestSuite) hibernateSession.get(TestSuite.class, testSuite.getId());
		Assert.assertEquals(actualTestSuite, testSuite);

		// Verify the TestClass was related.
		List<TestClass> actualTestClasses = actualTestSuite.getTestClasses();
		Assert.assertEquals(actualTestClasses.size(), 1);
		TestClass actualTestClass = actualTestClasses.iterator().next();
		Assert.assertEquals (actualTestClass, testClass);

		// Verify the test data types were related to the TestClass.
		List<TestDataType> actualTestDataTypes = actualTestClass.getTestDataTypes ();
		Assert.assertEquals (actualTestDataTypes.contains(testDataType1), true);
		Assert.assertEquals (actualTestDataTypes.contains(testDataType2), true);
		Iterator<TestDataType> iter = actualTestDataTypes.iterator();
		TestDataType actualTestDataType_1 = iter.next();
		TestDataType actualTestDataType_2 = iter.next();
		Assert.assertTrue(actualTestDataType_1.getTestClasses().contains(testClass));
		Assert.assertTrue(actualTestDataType_2.getTestClasses().contains(testClass));

		// Verify the test case was inserted.
		TestCase actualTestCase = (TestCase) hibernateSession.get(TestCase.class, testCaseId);
		Assert.assertNotNull(actualTestCase);
		Assert.assertEquals (testCaseId, actualTestCase.getId());

		// Verify the test data was related.
		List<TestData> actualTestData = actualTestCase.getTestData();
		Assert.assertTrue(actualTestData.contains(testData1));
		Assert.assertTrue(actualTestData.contains(testData2));
		Iterator<TestData> testDataIter = actualTestData.iterator();
		TestData actualTestData_1 = testDataIter.next();
		TestData actualTestData_2 = testDataIter.next();
		Assert.assertTrue(actualTestData_1.getTestCases().contains(testCase));
		Assert.assertTrue(actualTestData_2.getTestCases().contains(testCase));

		// Verify the element locators were related.
		List<ElementLocator> actualLocators = actualTestCase.getElementLocators();
		Assert.assertTrue(actualLocators.contains(locator1));
		Assert.assertTrue(actualLocators.contains(locator2));
		Iterator<ElementLocator> locatorIter = actualLocators.iterator();
		ElementLocator actualLocator_1 = locatorIter.next();
		ElementLocator actualLocator_2 = locatorIter.next();
		Assert.assertTrue(actualLocator_1.getTestCases().contains(testCase));
		Assert.assertTrue(actualLocator_2.getTestCases().contains(testCase));

		// Verify the TestClass was related.
		Assert.assertEquals(actualTestCase.getTestClass(), testClass);
	}

	@Test
	public static void testGopherData () throws Exception {
		
		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// Create a test suite.
		TestSuite testSuite = gopherData.createTestSuite("a test suite", "a version", "a description", true, null);
		
		// Create a TestClass.
		TestClass testClass = gopherData.createTestClass("a TestClass", "a version",
			"com.echostar.gopher.dany.ATestClass", "an example TestClass", true,
			"a Jira issue");
		testSuite.addTestClass (testClass);
		hibernateSession.update(testSuite);

		// Create test data types for the TestClass.
		TestDataType testDataType0 = gopherData.createTestDataType("data type 0",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);
		TestDataType testDataType1 = gopherData.createTestDataType("data type 1",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);

		List<TestDataType> testDataTypes = new ArrayList<TestDataType>();
		testDataTypes.add(testDataType0);
		testDataTypes.add(testDataType1);
		testClass.setTestDataTypes(testDataTypes);

		// Create the test case for the TestClass.
		TestCase testCase = gopherData.createTestCase("A case name", "a version", true, testClass, null);

		// Create the test data for the test case.
		TestData testData0 = gopherData.createTestData(testDataType0, "a data 0 value");
		TestData testData1 = gopherData.createTestData(testDataType1, "a data 1 value");
		
		testCase.addTestData(testData0);
		testCase.addTestData(testData1);
		hibernateSession.update(testCase);

		tran.commit();

		// Verify the test suite was inserted.
		TestSuite actualTestSuite = (TestSuite) hibernateSession.get(TestSuite.class, testSuite.getId());
		Assert.assertEquals(actualTestSuite, testSuite);

		// Verify the TestClass was related.
		List<TestClass> actualTestClasses = actualTestSuite.getTestClasses();
		Assert.assertEquals(actualTestClasses.size(), 1);
		TestClass actualTestClass = actualTestClasses.iterator().next();
		Assert.assertEquals (actualTestClass, testClass);

		// Verify the test data types were related to the TestClass.
		List<TestDataType> actualTestDataTypes = actualTestClass.getTestDataTypes ();
		Assert.assertEquals (actualTestDataTypes.contains(testDataType0), true);
		Assert.assertEquals (actualTestDataTypes.contains(testDataType1), true);

		// Verify the test case was inserted.
		TestCase actualTestCase = (TestCase) hibernateSession.get(TestCase.class, testCase.getId());
		Assert.assertNotNull(actualTestCase);
		Assert.assertEquals (testCase.getId(), actualTestCase.getId());

		// Verify the test data was related.
		List<TestData> actualTestData = actualTestCase.getTestData();
		Assert.assertTrue(actualTestData.contains(testData0));
		Assert.assertTrue(actualTestData.contains(testData1));

		// Verify the TestClass was related.
		Assert.assertEquals(actualTestCase.getTestClass(), testClass);
	}
}
