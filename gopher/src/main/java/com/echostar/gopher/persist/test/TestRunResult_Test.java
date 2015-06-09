package com.echostar.gopher.persist.test;

import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.PlatformEnum;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestNode;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestRunResult;

public class TestRunResult_Test  extends TestClassBase {

	@Test
	public static void testCreate () throws Exception {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		TestClass testClass = new TestClass("AnExampleTestClass", "a version",
				"com.echostar.gopher.dany.AnExampleTestClass",
				"an example TestClass", Boolean.TRUE, "a Jira issue");
		Long testClassId = (Long) hibernateSession.save(testClass);
		testClass.setId (testClassId);

		TestCase testCase = new TestCase ("A case name", "a version", true, testClass);
		Long testCaseId = (Long) hibernateSession.save(testCase);
		testCase.setId (testCaseId);

		TestNode testNode = new TestNode (PlatformEnum.WIN7, "10.79.82.141", "4444",
			"a user", "a password", "an install dir", "the selenium server");
		Long testNodeId = (Long) hibernateSession.save(testNode);
		testNode.setId(testNodeId);

		TestRun testRun = new TestRun ("a url", BrowserEnum.FIREFOX, true, testCase, testNode);
		Long testRunId = (Long) hibernateSession.save(testRun);
		testRun.setId(testRunId);

		TestRunResult testRunResult = new TestRunResult(true, "a message", new Date(), new Date(),
			"a user name", "a url", testRun, null, null);
		Long testRunResultId = (Long) hibernateSession.save(testRunResult);
		testRunResult.setId (testRunResultId);

		TestRunResult testRunResult2 = new TestRunResult(true, "another message", new Date(), new Date(),
			"a user name", "a url", testRun, null, null);
		Long testRunResult2Id = (Long) hibernateSession.save(testRunResult2);
		testRunResult2.setId (testRunResult2Id);

		hibernateSession.update(testRun);

		tran.commit();

		Query query = hibernateSession.createQuery("FROM TestRun WHERE id=\'"+testRunId+"\'");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		TestRun actualTestRun = (TestRun) results.get(0);
		Assert.assertNotNull(actualTestRun);
		Assert.assertEquals(actualTestRun, testRun);
		Assert.assertEquals(testRun.getTestNode(), testNode);
		Assert.assertEquals(testRun.getTestCase(), testCase);
		List<TestRunResult> actualRunResults =
			gopherData.findTestRunResultsByTestRun (actualTestRun.getId());
		Assert.assertTrue(actualRunResults.contains(testRunResult));
		Assert.assertTrue(actualRunResults.contains(testRunResult2));
	}

	@Test
	public static void testGopherData () throws Exception {
		
		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();

		TestClass testClass = gopherData.createTestClass("AnExampleTestClass", "a version",
			"com.echostar.gopher.dany.AnExampleTestClass",
			"an example TestClass", Boolean.TRUE, "a Jira issue");

		TestCase testCase = gopherData.createTestCase ("A case name", "a version", true, testClass, null);

		TestNode testNode = gopherData.createTestNode (PlatformEnum.WIN7, "10.79.82.141", "4444",
			"a user", "a password", "an install dir", "the selenium server");

		TestRun testRun = gopherData.createTestRun("a url", BrowserEnum.CHROME, true, testCase, testNode);
		
		TestRunResult testRunResult = gopherData.createTestRunResult(true, "a message", new Date(), new Date(),
			"a user name", "a url", testRun, null, null);
		TestRunResult testRunResult2 = gopherData.createTestRunResult(true, "another message", new Date(), new Date(),
			"a user name", "a url", testRun, null, null);

		tran.commit();

		// Verify the TestRunResults were inserted.
		List<TestRunResult> testRunResults = gopherData.findTestRunResultsByTestClass(testClass.getId());
		Assert.assertEquals(testRunResults.size(), 2);

		Query query = hibernateSession.createQuery("FROM TestRun WHERE id=\'"+testRun.getId()+"\'");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		TestRun actualTestRun = (TestRun) results.get(0);
		Assert.assertNotNull(actualTestRun);
		Assert.assertEquals(actualTestRun, testRun);
		Assert.assertEquals(testRun.getTestNode(), testNode);
		Assert.assertEquals(testRun.getTestCase(), testCase);
		List<TestRunResult> actualRunResults =
			gopherData.findTestRunResultsByTestRun(actualTestRun.getId());
		Assert.assertTrue(actualRunResults.contains(testRunResult));
		Assert.assertTrue(actualRunResults.contains(testRunResult2));
	}
}