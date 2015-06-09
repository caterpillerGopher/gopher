package com.echostar.gopher.persist.test;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestSuite;
import com.echostar.gopher.persist.TestClassDecorator;

public class TestSuiteRun_Test extends TestClassBase {

	@Test
	public static void testCreate () throws Exception {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		// Insert a test suite.
		TestSuite testSuite = new TestSuite ("A test suite", "a version", "A description", true, null);
		Long suiteId = (Long) hibernateSession.save(testSuite);
		testSuite.setId (suiteId);

		//Set<TestSuite> testSuites = new HashSet<TestSuite>();
		//testSuites.add(testSuite);
		//Suite suite = gopherData.createSuite("a suite", "a version", "a description", true, testSuites);

		// Insert a TestClass.
		TestClass testClass = gopherData.createTestClass ("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass", "an example TestClass",
			Boolean.TRUE, "a Jira issue");

		// Insert a TestClassDecorator.
		TestClassDecorator testClassDecorator = new TestClassDecorator("a URL", BrowserEnum.FIREFOX, true,
			testSuite, testClass);

		testSuite.addTestClassDecorator(testClassDecorator);

		hibernateSession.update(testSuite);

		tran.commit();

		// Verify the suite was inserted.
		TestSuite actualSuite = (TestSuite) hibernateSession.get(TestSuite.class, suiteId);
		Assert.assertNotNull(actualSuite);
		Assert.assertEquals (suiteId, actualSuite.getId());
		
		// Verify the TestClassDecorator was related.
		Assert.assertEquals(actualSuite.getTestClassDecorators ().size(), 1);
		TestClassDecorator actualSuiteRun = actualSuite.getTestClassDecorators ().iterator().next();
		Assert.assertEquals(actualSuiteRun, testClassDecorator);
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

		// Create a test suite.
		List<TestClass> testClasses = new ArrayList<TestClass>();
		testClasses.add(testClass);
		TestSuite testSuite = gopherData.createTestSuite ("A suite name", "a version", "a description", true, testClasses);

		// Relate TestSuiteRuns for the suite.
		TestClassDecorator testClassDecorator = gopherData.createTestClassDecorator("a URL", BrowserEnum.FIREFOX,
			true, testSuite, testClass);
		
		tran.commit();

		// Verify the suite was inserted.
		TestSuite actualSuite = (TestSuite) hibernateSession.get(TestSuite.class, testSuite.getId());
		Assert.assertNotNull(actualSuite);
		Assert.assertEquals (actualSuite, testSuite);
		
		// Verify the TestSuiteDecorator was related.
		Assert.assertEquals(actualSuite.getTestClassDecorators ().size(), 1);
		TestClassDecorator actualSuiteRun = actualSuite.getTestClassDecorators ().iterator().next();
		Assert.assertEquals(actualSuiteRun, testClassDecorator);
	}
}