package com.echostar.gopher.system;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;

import com.echostar.gopher.exception.LocatorException;
import com.echostar.gopher.exception.TestException;
import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestRunResult;
import com.echostar.gopher.persist.TestSuite;
import com.echostar.gopher.testng.ErrorUtil;
import com.echostar.gopher.testng.TestNGClassBase;
import com.echostar.gopher.util.ExceptionUtil;

public class LocatorException_Test extends TestNGClassBase {

	private TestRun testRun;
	private TestClass testClass;
	private TestCase testCase;
	private TestSuite testSuite;

	protected LocatorException_Test() throws Exception {
		super();
	}

	/**
	 * Override super. Mock all necessary data.
	 * @throws	Exception	on error
	 */
	protected void mockData (ITestContext context, String suiteName, String suiteVersion) throws Exception {

		Logger log = Logger.getLogger(getClass());
		log.debug("In mockData.");
		GopherData gopherData = null;
		Transaction tran = null;

		try {
			log.trace("Getting gopherData.");
			gopherData = GopherDataFactory.getGopherData();

			log.trace("Cleaning db.");
			gopherData.cleanDB ();

			Session hibernateSession = gopherData.getHibernateSession();

			log.trace("Creating transaction.");
			tran = hibernateSession.beginTransaction();

			// Create a TestClass.
			String className = this.getClass().getName();
			testClass = gopherData.createTestClass(className, "a version", className,
				"Test integration of a TestClass with GopherData.", true, "a Jira issue");

			// Create a test case for the TestClass.
			testCase = gopherData.createTestCase("A case name", "a version", true, testClass, null);

			testRun = new TestRun ("a url",BrowserEnum.FIREFOX, true, testCase, null);
			Long testRunId = (Long) hibernateSession.save(testRun);
			testRun.setId(testRunId);

			testCase.addTestRun(testRun);
			hibernateSession.update(testCase);

			// Create a TestSuite.
			List<TestClass> testClasses = new ArrayList<TestClass>();
			testClasses.add(testClass);
			testSuite = gopherData.createTestSuite ("LocatorException_TestSuite", "version", "desc",
				true, testClasses);

			List<TestSuite> testSuites = new ArrayList<TestSuite>();
			testSuites.add(testSuite);
			testClass.setTestSuites(testSuites);
			hibernateSession.update(testClass);

			// Create a Suite.
			List<TestSuite> testSuites_ = new ArrayList<TestSuite>();
			testSuites_.add(testSuite);
			gopherData.createSuite("Exception_Suite", "1.0", "description", true,
				testSuites_);

			tran.commit();
			log.trace("Transaction commited.");
		} finally {
			
			if (tran != null && !tran.wasCommitted()) {
				log.trace("Rolling back transaction.");
				tran.rollback();
			}
			if (gopherData != null) {
				gopherData.close();
				log.trace("gopherData closed.");
			}
			log.debug("Leaving mockData.");
		}
	}

	/**
	 * Do after method validation.
	 * @param context  
	 */
	@AfterMethod
	public void afterMethod(ITestContext context) throws Exception {
		Logger log = Logger.getLogger (getClass().getName());
		log.debug("In @AfterClass afterClass.");
		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();
			TestClass testClass = gopherData.findTestClassByClassName(getClass().getName());
			List<TestRunResult> testRunResults =
				gopherData.findTestRunResultsByTestClass(testClass.getId());
			Assert.assertEquals(testRunResults.size(), 1);
			TestRunResult testRunResult = testRunResults.iterator().next();
			Assert.assertEquals(testRunResult.getTestExceptions().size(), 1);
			com.echostar.gopher.persist.TestException testException =
				testRunResult.getTestExceptions().iterator().next();
			Assert.assertEquals(testException.getExceptionClass(),
				"com.echostar.gopher.exception.LocatorException");
		} catch (AssertionError e) {
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			ErrorUtil.addVerificationFailure(e);
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
			log.debug("Leaving @AfterClass afterClass.");
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
			log.info("Adding exception to verification failures.");
			LocatorException te = new LocatorException ("LocatorException_Test.");
			ErrorUtil.addVerificationFailure(te);
			//throw te;
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
			log.debug("Leaving doTest().");						
		}
	}
}
