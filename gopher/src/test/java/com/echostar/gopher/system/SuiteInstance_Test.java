package com.echostar.gopher.system;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;

import com.echostar.gopher.exception.TestException;
import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.PlatformEnum;
import com.echostar.gopher.persist.SuiteInstance;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestNode;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestSuite;
import com.echostar.gopher.persist.TestSuiteInstance;
import com.echostar.gopher.testng.ErrorUtil;
import com.echostar.gopher.testng.TestNGClassBase;
import com.echostar.gopher.util.ExceptionUtil;

public class SuiteInstance_Test extends TestNGClassBase {

	protected SuiteInstance_Test() throws Exception {
		super();
	}

	/**
	 * Override super. Mock all necessary data.
	 * @throws	Exception	on error
	 */
	protected void mockData (ITestContext context, String suiteName, String suiteVersion) throws Exception {

		Logger log = Logger.getLogger(getClass());
		log.debug("In mockData ITestContext "+context+".");
		GopherData gopherData = null;
		Transaction tran = null;
		String testSuiteName = null;
		ISuite isuite = context.getSuite();
		if (isuite != null) {
			testSuiteName = isuite.getName();
		}

		try {
			log.trace("Getting gopherData.");
			gopherData = GopherDataFactory.getGopherData();

			Session hibernateSession = gopherData.getHibernateSession();

			log.trace("Beginning transaction - mock data.");
			tran = hibernateSession.beginTransaction();

			// Create a TestClass.
			String className = this.getClass().getName();
			log.debug("Creating TestClass "+className+".");
			TestClass testClass = gopherData.createTestClass(className, "a version", className,
				"Test integration of a TestClass with GopherData.", true, "a Jira issue");

			// Create a test case for the TestClass.
			TestCase testCase = gopherData.createTestCase("A case name", "a version", true, testClass, null);

			// Create a TestNode for the TestRuns.
			TestNode testNode = gopherData.createTestNode(PlatformEnum.WIN7, "666", "1", "fred", "wilma", "", "");

			for (int i=0; i < 10; i++) {
			TestRun testRun = new TestRun ("a url",BrowserEnum.FIREFOX, true, testCase, testNode);
			Long testRunId = (Long) hibernateSession.save(testRun);
			testRun.setId(testRunId);

			testCase.addTestRun(testRun);
			}
			hibernateSession.update(testCase);

			// Create a TestSuite.
			List<TestClass> testClasses = new ArrayList<TestClass>();
			testClasses.add(testClass);
			if (testSuiteName == null) {
				log.warn("testSuiteName is null, using 'stub'.");
				testSuiteName = "stub";
			}
			TestSuite testSuite = gopherData.createTestSuite (testSuiteName, "version", "desc",
				true, testClasses);

			List<TestSuite> testSuites = new ArrayList<TestSuite>();
			testSuites.add(testSuite);
			testClass.setTestSuites(testSuites);
			hibernateSession.update(testClass);

			List<TestSuite> testSuites_ = new ArrayList<TestSuite>();
			testSuites_.add(testSuite);
			if (suiteName == null) {
				log.warn("suiteName is null, using 'stub'.");
				suiteName = "stub";
			}
			gopherData.createSuite(suiteName, "1.0", "description", true,
				testSuites_);

			tran.commit();
			log.trace("Transaction commited - mock data.");
		} finally {
			
			if (tran != null && !tran.wasCommitted()) {
				log.trace("Rolling back transaction.");
				tran.rollback();
			}
			if (gopherData != null) {
				gopherData.close();
				log.trace("gopherData closed.");
			}
			log.debug("Leaving mockData ITestContext "+context+".");
		}
	}

	@AfterMethod
	public void afterMethod(ITestContext context) throws Exception {
		Logger log = Logger.getLogger (getClass().getName());
		log.debug("In @AfterMethod afterMethod ITestContext "+context+".");
		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();

			// TBD - remove this work-around.
			gopherData.getHibernateSession().beginTransaction().commit();

			List<TestSuiteInstance> testSuiteInstances = gopherData.findAllTestSuiteInstances();
			Assert.assertTrue(testSuiteInstances.size() > 0);
			TestSuiteInstance testSuiteInstance = testSuiteInstances.get(0);
			SuiteInstance suiteInstance = testSuiteInstance.getSuiteInstance();
			if (suiteInstance != null) {
				Assert.assertTrue(suiteInstance.getTestSuiteInstances().contains(testSuiteInstance));				
			}
		} catch (AssertionError e) {
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			ErrorUtil.addVerificationFailure(e);
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
			log.debug("Leaving @AfterMethod afterMethod.");
		}
	}

	/**
	 * Override super and do actual test.
	 * Throw a {@link TestException TestException}
	 * so we may validate later the handling of this exception.
	 *
	 * @param testSuite		the testSuite
	 * @param testClass		the testClass
	 * @param testCase		the TestCase
	 * @param testRun		the TestRun
	 * @throws Exception	on any error
	 */
	public void doTest (TestRun testRun, TestCase testCase, TestClass testClass, TestSuite testSuite) throws Exception {

		Logger log = Logger.getLogger (getClass().getName());
		log.debug("In doTest().");

		Assert.assertNotNull(testRun);
		Assert.assertNotNull(testCase);
		Assert.assertNotNull(testClass);
		Assert.assertNotNull(testSuite);

		GopherData gopherData = null;
		try {
			//gopherData = GopherDataFactory.getGopherData();
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
			log.debug("Leaving doTest().");						
		}
	}
}
