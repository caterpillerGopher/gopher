package com.echostar.gopher.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;

import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.DataRoleEnum;
import com.echostar.gopher.persist.DataTypeEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.PlatformEnum;
import com.echostar.gopher.persist.Suite;
import com.echostar.gopher.persist.SuiteInstance;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestData;
import com.echostar.gopher.persist.TestDataType;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestNode;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestRunResult;
import com.echostar.gopher.persist.TestSuite;
import com.echostar.gopher.persist.TestClassDecorator;
import com.echostar.gopher.persist.TestSuiteInstance;
import com.echostar.gopher.testng.ErrorUtil;
import com.echostar.gopher.testng.SuiteListenerAdaptor;
import com.echostar.gopher.testng.TestNGClassBase;
import com.echostar.gopher.util.ExceptionUtil;

/**
 * Test the {@link TestNGClassBase TestNGClassBase}.
 * @author greg
 *
 */
public class DataProvider_Test extends TestNGClassBase {

	private Suite suite;
	private TestSuite testSuite;
	private TestClassDecorator testClassDecorator;
	private TestRun testRun;
	private TestRun testRun2;
	private TestClass testClass;
	private TestCase testCase;
	private TestCase testCase2;
	private List<TestCase> testCases = new ArrayList<TestCase>();
	private List<TestRun> testRuns = new ArrayList<TestRun>();
	private List<TestNode> testNodes = new ArrayList<TestNode>();

	DataProvider_Test () throws Exception {
		super ();
	}

	/**
	 * Override super. Mock all necessary data.
	 * @throws	Exception	on error
	 */
	public void mockData (ITestContext context, String suiteName, String suiteVersion) throws Exception {

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
			testClass = gopherData.createTestClass("A class name", "a version", className,
				"Test integration of a TestClass with GopherData.", true, "a Jira issue");

			// Create test data types for the TestClass.
			TestDataType testDataType0 = gopherData.createTestDataType("data argument 0",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);
			TestDataType testDataType1 = gopherData.createTestDataType("data argument 1",
				DataTypeEnum.STRING, DataRoleEnum.LABEL);

			// Create a test case for the TestClass.
			testCase = gopherData.createTestCase("A case name", "a version", true, testClass, null);

			// Create the test data for the test case.
			TestData testData0 = gopherData.createTestData(testDataType0, "argument 0 value");
			TestData testData1 = gopherData.createTestData(testDataType1, "argument 1 value");
		
			testCase.addTestData(testData0);
			testCase.addTestData(testData1);
			hibernateSession.update(testCase);

			// Create another test case for the TestClass.
			testCase2 = gopherData.createTestCase("Another case name", "another version", true, testClass, null);

			// Create the test data for the test case.
			testData0 = gopherData.createTestData(testDataType0, "another argument 0 value");
			testData1 = gopherData.createTestData(testDataType1, "another argument 1 value");
		
			testCase2.addTestData(testData0);
			testCase2.addTestData(testData1);
			hibernateSession.update(testCase2);

			testCases.add(testCase);
			testCases.add(testCase2);

			TestNode testNode = new TestNode (PlatformEnum.WIN7, "10.79.82.141", "4444",
					"a user", "a password", "an install dir", "the selenium server");
			Long testNodeId = (Long) hibernateSession.save(testNode);
			testNode.setId(testNodeId);

			testNodes.add (testNode);

			testRun = new TestRun ("a url",BrowserEnum.FIREFOX, true, testCase, testNode);
			Long testRunId = (Long) hibernateSession.save(testRun);
			testRun.setId(testRunId);

			testRun2 = new TestRun (null, null, true, testCase2, testNode);
			Long testRun2Id = (Long) hibernateSession.save(testRun2);
			testRun2.setId(testRun2Id);

			testCase.addTestRun(testRun);
			testCase.addTestRun(testRun2);
			hibernateSession.update(testCase);

			testRuns.add (testRun);
			testRuns.add (testRun2);

			// Create a TestSuite.
			List<TestClass> testClasses = new ArrayList<TestClass>();
			testClasses.add(testClass);
			testSuite = gopherData.createTestSuite ("DataProvider_TestSuite", "version", "desc",
				true, testClasses);

			List<TestSuite> testSuites = new ArrayList<TestSuite>();
			testSuites.add(testSuite);
			testClass.setTestSuites(testSuites);
			hibernateSession.update(testClass);

			// Create a Suite.
			List<TestSuite> testSuites_ = new ArrayList<TestSuite>();
			testSuites_.add(testSuite);
			suite = gopherData.createSuite("DataProvider_Suite", "1.0", "description", true,
				testSuites_);
			testSuite.addSuite(suite);
		
			// Create a TestClassDecorator.
			testClassDecorator = gopherData.createTestClassDecorator("suite-url", BrowserEnum.FIREFOX,
				true, testSuite, testClass);

			log.debug("TestSuite has "+
					testSuite.getTestClassDecorators().size()+" TestClassRuns.");

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
	 * Do after suite validation.
	 * @param context  
	 */
	@AfterSuite
	public void afterSuite(ITestContext context) throws Exception {
		Logger log = Logger.getLogger (getClass().getName());
		log.debug("In @AfterSuite afterSuite.");
		//ISuite suite = context.getSuite();
		//String suiteName = suite.getName();
		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();
			log.debug("afterSuite getting suiteInstance.");
			SuiteInstance suiteInstance = SuiteListenerAdaptor.getSuiteInstance(gopherData);
			log.debug("afterSuite got suiteInstance "+suiteInstance+".");
			if (suiteInstance != null) {
				log.debug("afterSuite suiteInstance not null.");
				Assert.assertEquals(suiteInstance.getTestRunResults().size(), 2);
				Assert.assertEquals(suiteInstance.getTestSuiteInstances().size(), 1);
				List<TestRunResult> testRunResults = suiteInstance.getTestRunResults();
				TestRunResult trs = testRunResults.iterator().next();
				Assert.assertEquals(trs.getSuiteInstance().getId(),
						suiteInstance.getId());
				trs = testRunResults.iterator().next();
				Assert.assertEquals(trs.getSuiteInstance().getId(),
						suiteInstance.getId());
				List<TestSuiteInstance> testSuiteInstances = suiteInstance.getTestSuiteInstances();
				TestSuiteInstance testSuiteInstance = testSuiteInstances.iterator().next();
				Assert.assertNotNull(testSuiteInstance.getSuiteInstance());
				SuiteInstance si = testSuiteInstance.getSuiteInstance();
				Assert.assertEquals(si.getId(), suiteInstance.getId());
				Assert.assertNotNull(testSuiteInstance.getTestSuite());
				Assert.assertEquals(testSuiteInstance.getTestSuite().getId(),testSuite.getId());
			}
			List<TestSuiteInstance> testSuiteInstances = gopherData.findAllTestSuiteInstances();
			log.debug("afterSuite got testSuiteInstances.");
			Assert.assertEquals(testSuiteInstances.size(), 1);
			TestSuiteInstance testSuiteInstance = testSuiteInstances.iterator().next();
			List<TestRunResult> testRunResults = testSuiteInstance.getTestRunResults();
			Assert.assertEquals(testRunResults.size(), 2);

			//log.debug("Cleaning db.");
			//gopherData.cleanDB();
			//SuiteListenerAdaptor.clearSuiteInstance();
			//log.debug("Cleaned db.");
		} catch (AssertionError e) {
			log.error("SuiteInstance comparison result unexpected.");
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			ErrorUtil.addVerificationFailure(e);
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
			log.debug("Leaving @AfterSuite afterSuite.");
		}
	}

	/**
	 * Override super and do actual test.
	 *
	 * @param testSuite		the testSuite
	 * @param testClass		the testClass
	 * @param testCase		the TestCase
	 * @param testRun		the TestRun
	 * @throws Exception	on any error
	 */
	public void doTest (TestRun testRun, TestCase testCase, TestClass testClass, TestSuite testSuite) throws Exception {

		Logger log = Logger.getLogger (getClass().getName());
		log.debug("In doTest.");

		Assert.assertNotNull(testRun);
		Assert.assertNotNull(testCase);
		Assert.assertNotNull(testClass);
		Assert.assertNotNull(testSuite);

		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();

			// Validate the suite instance.
			try {
				SuiteInstance suiteInstance = SuiteListenerAdaptor.getSuiteInstance(gopherData);
				if (suiteInstance != null) {
					Assert.assertNotNull(suiteInstance, "SuiteInstance");
					Suite suite = suiteInstance.getSuite();
					Assert.assertNotNull(suite, "Suite");
					Assert.assertEquals(gopherData.findSuiteInstancesBySuite(
						suite.getId()).size(), 1, "(SuiteInstances by Suite).size");
					Assert.assertEquals(suite.getId(), this.suite.getId(), "Suite.id");
					Assert.assertEquals(suite.getName(), this.suite.getName(), "Suite.name");
					Assert.assertEquals(suite.getVersion(), this.suite.getVersion(), "Suite.version");
					Assert.assertEquals(suite.getTestSuites().size(), 1, "Suite.TestSuites");
					TestSuite ts = suite.getTestSuites().iterator().next();
					Assert.assertEquals(ts.getId(), this.testSuite.getId(), "TestSuite.id");
					Assert.assertTrue(suiteInstance.getTestSuiteInstances().size() == 1, "SuiteInstances.TestSuiteInstances.size");
				}
			} catch (AssertionError e) {
				log.error("Suite comparison result unexpected.");
				log.error(e);
				ErrorUtil.addVerificationFailure(e);
			}

			// Validate the TestSuite.
			try{
				// Refresh the expected TestSuite.
				TestSuite expectedTestSuite = gopherData.findTestSuiteById(
					this.testSuite.getId());
				Assert.assertNotNull(expectedTestSuite);
				Assert.assertEquals(testSuite.getName(), expectedTestSuite.getName());
				Assert.assertEquals(testSuite.getVersion(), expectedTestSuite.getVersion());
				Assert.assertEquals(testSuite.getTestClasses().size(), 1);
				Assert.assertEquals(testSuite.getTestClasses().size(),
					expectedTestSuite.getTestClasses().size());
				Assert.assertEquals(gopherData.findTestSuiteInstancesByTestSuite(
					testSuite.getId()).size(), 1);
				//Assert.assertEquals(testSuite, expectedTestSuite);
			}
			catch (AssertionError e) {
				log.error("TestSuite comparison result unexpected.");
				log.error(e);
				ErrorUtil.addVerificationFailure(e);
			}

			// Validate the TestClass.
			try{
				Assert.assertEquals(this.testClass.getTestSuites().size(), 1);
				Assert.assertEquals(testClass.getTestSuites().size(),1);
			} catch (AssertionError e) {
				log.error("TestClass comparison result unexpected.");
				log.error(e);
				ErrorUtil.addVerificationFailure(e);			
			}

			// Validate the TestClassRuns.
			try {
				Assert.assertTrue(this.testSuite.getTestClassDecorators().size() == 1);
				Assert.assertTrue(testSuite.getTestClassDecorators().size() == 1);
				Iterator<TestClassDecorator> iterator = testSuite.getTestClassDecorators().iterator();
				TestClassDecorator tcr = iterator.next();
				Assert.assertEquals(tcr.getId(), testClassDecorator.getId());
			} catch (AssertionError e) {
				log.error("TestClassDecorator comparison result unexpected.");
				log.error(e);
				ErrorUtil.addVerificationFailure(e);			
			}

			// Validate the TestCase.
			try {
				Assert.assertTrue(testCase.getId().equals(this.testCase.getId()) ||
					testCase.getId().equals(this.testCase2.getId()));
			} catch (AssertionError e) {
				log.error("TestCases comparison result unexpected.");
				log.error(e);
				ErrorUtil.addVerificationFailure(e);			
			}

			// Validate the TestRuns.
			try {
				Assert.assertTrue(testRun.getId().equals(this.testRun.getId()) ||
					testRun.getId().equals(this.testRun2.getId()));
				Assert.assertNotNull(testRun.getTestNode());
			} catch (AssertionError e) {
				log.error("TestRuns comparison result unexpected.");
				log.error(e);
				ErrorUtil.addVerificationFailure(e);			
			}

			// Validate the URL and browser.
			try {
				//Assert.assertTrue(testNodes.contains(testRun.getTestNode()));
				String url = determineUrl (testSuite, testRun);
				log.debug("url='"+url+"'.");
				BrowserEnum browserEnum = determineBrowser (testSuite, testRun);
				log.debug("browser='"+browser+"'.");
				if (testRun == this.testRun) {
					Assert.assertEquals(url, testRun.getUrl());
					Assert.assertEquals(browserEnum, testRun.getBrowser());
				} else {
					Assert.assertTrue(url.equals ("suite-url") || url.equals("method-url") ||
						url.equals ("a url"));
					//Assert.assertEquals(browserEnum, BrowserEnum.FIREFOX);
				}
			} catch (AssertionError e) {
				log.error("URL or browser comparison result unexpected.");
				log.error(e);
				ErrorUtil.addVerificationFailure(e);			
			}
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
			log.debug("Leaving doTest.");						
		}
	}
}