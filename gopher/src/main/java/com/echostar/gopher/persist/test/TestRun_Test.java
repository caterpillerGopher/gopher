package com.echostar.gopher.persist.test;

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
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestNode;
import com.echostar.gopher.persist.TestRun;

public class TestRun_Test  extends TestClassBase {

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
		
		TestNode testNode2 = new TestNode (PlatformEnum.WIN7, "10.79.82.142", "4445",
				"another user", "another password", "another install dir", "another selenium server");
		Long testNode2Id = (Long) hibernateSession.save(testNode);
		testNode2.setId(testNode2Id);
		
		TestRun testRun = new TestRun ("a url", BrowserEnum.FIREFOX, true, testCase, testNode);
		Long testRunId = (Long) hibernateSession.save(testRun);
		testRun.setId(testRunId);

		TestRun testRun2 = new TestRun ("another url", BrowserEnum.FIREFOX, true, testCase, testNode2);
		Long testRun2Id = (Long) hibernateSession.save(testRun2);
		testRun2.setId(testRun2Id);

		tran.commit();

	    Query query = hibernateSession.createQuery("FROM TestRun WHERE id=\'"+testRunId+"\'");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		Assert.assertEquals(results.size(), 1);
		TestRun actualTestRun = (TestRun) results.get(0);
		Assert.assertNotNull(actualTestRun);
		Assert.assertEquals(actualTestRun, testRun);
		Assert.assertEquals(actualTestRun.getTestCase(), testCase);
		Assert.assertEquals(actualTestRun.getTestCase().getTestClass(), testClass);
		Assert.assertEquals(actualTestRun.getTestNode(), testNode);

	    query = hibernateSession.createQuery("FROM TestRun WHERE id=\'"+testRun2Id+"\'");
		//@SuppressWarnings("rawtypes")
	    results = query.list();
		Assert.assertEquals(results.size(), 1);
		actualTestRun = (TestRun) results.get(0);
		Assert.assertNotNull(actualTestRun);
		Assert.assertEquals(actualTestRun, testRun2);
		Assert.assertEquals(actualTestRun.getTestCase(), testCase);
		Assert.assertEquals(actualTestRun.getTestCase().getTestClass(), testClass);
		Assert.assertEquals(actualTestRun.getTestNode(), testNode2);
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

		tran.commit();

		Query query = hibernateSession.createQuery("FROM TestRun WHERE id=\'"+testRun.getId()+"\'");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		TestRun actualTestRun = (TestRun) results.get(0);
		Assert.assertNotNull(actualTestRun);
		Assert.assertEquals(actualTestRun.getTestCase(), testCase);
		Assert.assertEquals(testCase.getTestClass(), testClass);
	}
}