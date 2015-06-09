package com.echostar.gopher.persist.test;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestSuite;

public class TestSuite_Test extends TestClassBase {

	@Test
	public static void testCreate () {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// Insert a test suite.
		TestSuite testSuite = new TestSuite ("A test suite", "a version", "A description", true, null);
		Long suiteId = (Long) hibernateSession.save(testSuite);
		testSuite.setId (suiteId);

		// Insert TestClasses.
		TestClass testClass1 = new TestClass("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass", "an example TestClass", Boolean.TRUE,
			"a Jira issue");
		TestClass testClass2 = new TestClass("AnotherExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnotherExampleTestClass", "another example TestClass", Boolean.TRUE,
			"a Jira issue");

		List<TestSuite> testSuites = new ArrayList<TestSuite>();
		testSuites.add (testSuite);

		testClass1.setTestSuites(testSuites);
		testClass2.setTestSuites(testSuites);

		Long testClass1Id = (Long) hibernateSession.save(testClass1);
		Long testClass2Id = (Long) hibernateSession.save(testClass2);
		testClass1.setId (testClass1Id);
		testClass2.setId (testClass2Id);

		// Relate cases to suite.
		List<TestClass> testClasses = new ArrayList<TestClass>();
		testClasses.add(testClass1);
		testClasses.add(testClass2);
		testSuite.setTestClasses(testClasses);
		hibernateSession.update(testSuite);

		tran.commit();

		// Verify the suite was inserted.
		TestSuite actualSuite = (TestSuite) hibernateSession.get(TestSuite.class, suiteId);
		Assert.assertNotNull(actualSuite);
		Assert.assertEquals (suiteId, actualSuite.getId());
		
		// Verify the TestClasses were inserted.
		List<TestClass> actualTestClasses = actualSuite.getTestClasses ();
		Assert.assertEquals(actualTestClasses.size(), 2);
	}

	@Test
	public static void testGopherData () throws Exception {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// First create TestClasses for the suite.
		TestClass testClass1 = gopherData.createTestClass("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass",
			"an example TestClass", Boolean.TRUE, "a Jira issue");
		TestClass testClass2 = gopherData.createTestClass("AnotherExampleTestClass", "another version",
			"com.echostar.gopher.dany.AnotherExampleTestClass",
			"another example TestClass", Boolean.TRUE, "another Jira issue");

		List<TestClass> testClasses = new ArrayList<TestClass>();
		testClasses.add(testClass1);
		testClasses.add(testClass2);

		// Create a test suite.
		TestSuite testSuite = gopherData.createTestSuite ("A suite name", "a version", "a description", true, testClasses);

		tran.commit();

		// Verify the suite was inserted.
		TestSuite actualSuite = (TestSuite) hibernateSession.get(TestSuite.class, testSuite.getId());
		Assert.assertNotNull(actualSuite);
		Assert.assertEquals (actualSuite, testSuite);
		
		// Verify the TestClasses were inserted.
		List<TestClass> actualTestClasses = actualSuite.getTestClasses ();
		Assert.assertEquals(actualTestClasses.size(), 2);
		Assert.assertTrue(actualTestClasses.contains(testClass1));
		Assert.assertTrue(actualTestClasses.contains(testClass2));
	}
}
