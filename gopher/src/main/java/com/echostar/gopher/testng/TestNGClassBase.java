package com.echostar.gopher.testng;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.echostar.gopher.exception.TestException;
import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.SuiteInstance;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestRunResult;
import com.echostar.gopher.persist.TestSuite;
import com.echostar.gopher.persist.TestSuiteInstance;
import com.echostar.gopher.util.Config;
import com.echostar.gopher.util.ExceptionUtil;

import org.apache.log4j.Logger;

/**
 * A base class for TestNG test classes.
 * In particular, we implement a generic DataProvider {@link #doDataProvider(ITestContext) doDataProvider}
 * and a generic test method {@link #wrapDoTest(TestRun, TestCase, TestClass, TestSuite, ITestContext) wrapTest} associated with that DataProvider.
 * Override {@link #doTest(TestRun, TestCase, TestClass, TestSuite) doTest} in a subclass to implement the test code.
 * 
 * @author charles.young
 *
 */
public class TestNGClassBase {

	// Map of ISuite to Long id of TestSuiteInstance
	protected static Map<ISuite, Long> testSuiteInstanceMap =
		new HashMap<ISuite, Long>();

	protected Config			config;
	// URL provided by TestNG as a Parameter
	protected String			url;
	// Browser provided by TestNG as a Parameter
	protected BrowserEnum		browser;

	protected TestNGClassBase () throws Exception {
		config = new Config();
	}

	/**
	 * Wrap the test method {@link #doTest doTest} with exception handling.
	 * Insert a {@link TestRunResult TestRunResult}.
	 * Over-riding methods must not catch exceptions or insert a result.
	 *
	 * @param testSuite			the testSuite
	 * @param testClass			the testClass
	 * @param testCase			the TestCase
	 * @param testRun			the TestRun
	 * @param context			the ITestContext
	 * @throws SkipException	if the TestSuite, TestClass, TestCase or TestRun should be skipped
	 * @throws Exception		on any error
	 */
	@Test(dataProvider="doDataProvider")
	public void wrapDoTest (TestRun testRun, TestCase testCase, TestClass testClass,
		TestSuite testSuite, ITestContext context) throws SkipException, Exception {

		Logger log = Logger.getLogger (getClass().getName());

		log.debug ("In wrapDoTest, testRun='"+testRun+"' testCase='"+testCase+
			"' testClass='"+testClass+"' testSuite='"+testSuite+"'.");

		ISuite testng_suite = context.getSuite();
		//String suiteName = testng_suite==null?null:testng_suite.getName();
		//log.trace("ISuite.suiteName="+suiteName);

		// If we should skip this test run
		if ((testSuite != null && !testSuite.getRunmode()) || !testClass.getRunmode() ||
			!testCase.getRunmode() || !testRun.getRunmode()) {
			log.info("TestClass "+testClass.getName() + " Skipped.");
			throw new SkipException("Skipped");
		}

		// Get a Hibernate Session. If this fails, this test can not continue.
		GopherData gopherData = null;
		Session hibernateSession = null;
		Transaction tran = null;
		Throwable e = null;
		try {
			gopherData = GopherDataFactory.getGopherData();
			hibernateSession = gopherData.getHibernateSession();

			// TBD - remove this work-around to prevent dirty reads and writes.
			gopherData.getHibernateSession().beginTransaction().commit();
		}
		catch (Throwable ex) {
			e = ex;
			log.error(ExceptionUtil.getStackTraceString(ex, 10000));
			// Stop the suite.
			throw ex;
		} finally {
			if (e != null && gopherData != null) {
				gopherData.close();
			}
			log.debug("Done refreshing arguments.");
		}

		Date startTime = new Date();

		try {
			// Do actual test stuff here.
			doTest (testRun, testCase, testClass, testSuite);
		} catch (AssertionError err) {
			// Preserve the exception.
			e = err;
			// Log the exception.
			//log.error(getStackTraceString(e, 10000));
			// Make sure it is in the failure map
			log.error("Adding AssertionError to verification failures : "+err.getMessage());
			ErrorUtil.addVerificationFailure(e);
		} catch (TestException te) {
			// A Gopher TestException
			e = te;
			log.error("Adding TestException to verification failures : "+te.getMessage());
			ErrorUtil.addVerificationFailure(e);
		} catch (RuntimeException rex) {
			e = rex;
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			// Stop the suite.
			throw rex;
		} catch (Exception ex) {
			e = ex;
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			// Stop the suite.
			throw ex;
		} finally {
			// Do reporting.
			boolean result = true;
			String message = "success";
			Date endTime = new Date();

			// If an exception was thrown to us
			if (e != null) {
				result = false;
				// Use the message on the exception.
				message = e.getMessage ();
			// Else if the test class registered an exception
			} else if (ErrorUtil.getVerificationFailures().size() > 0) {
				// Use the message from the first registered exception.
				message = ErrorUtil.getVerificationFailures ().get(0).getMessage();
				result = false;
			}

			try {
				// TBD - remove this work-around to prevent dirty reads and writes.
				//gopherData.getHibernateSession().beginTransaction().commit();

				// Get the TestSuiteInstance.
				TestSuiteInstance testSuiteInstance = null;
				Long testSuiteInstanceId = testSuiteInstanceMap.get(testng_suite);
				if (testSuiteInstanceId == null) {
					log.debug ("TestSuiteInstance for testng_suite '"+testng_suite+
						"' not found in map.");
				} else {
					testSuiteInstance = gopherData.findTestSuiteInstanceById(testSuiteInstanceId);

					if (testSuiteInstance == null) {
						Exception nfe = new Exception ("TestSuiteInstance with id "+testSuiteInstanceId+
								"' not found.");
						log.error(nfe);
						throw nfe;
					}
				}
				// Get the SuiteInstance if any.
				SuiteInstance suiteInstance = SuiteListenerAdaptor.getSuiteInstance(gopherData);

				// Create a TestRunResult.
				log.debug("Creating TestRunResult.");
				String user = System.getProperty("user.name");
				synchronized (this) {
					tran = hibernateSession.beginTransaction();
					//String url = determineUrl (testSuite, testRun);
					TestRunResult testRunResult = gopherData.createTestRunResult
						(result, message, startTime, endTime, user, url, testRun,
						suiteInstance, testSuiteInstance);

					// Preserve any exceptions.
					List<Throwable> exceptions = ErrorUtil.getVerificationFailures ();
					if (exceptions != null && exceptions.size() > 0) {
						for (Throwable t : exceptions) {
							gopherData.createTestException (t, 10000, testRunResult);
						}
					}
					tran.commit();
				}
				log.debug("Created TestRunResult.");
			} catch (Throwable t) {
				// Log this exception.
				log.error(ExceptionUtil.getStackTraceString(t, 10000));
			} finally {
				if (tran != null && !tran.wasCommitted()) {
					tran.rollback();
				}
			}

			if (gopherData != null) {
				log.trace("closing gopherData");
				gopherData.close();
			}
			log.debug("Leaving wrapDoTest testRun "+testRun.getId()+" testCase "+testCase.getName()+
					" testClass "+testClass.getName()+" ITestContext "+context+".");
		}
	}

	/**
	 * Override this method and do actual test.
	 *
	 * @param testSuite		the testSuite
	 * @param testClass		the testClass
	 * @param testCase		the TestCase
	 * @param testRun		the TestRun
	 * @throws Exception	on any error
	 */
	public void doTest (TestRun testRun, TestCase testCase, TestClass testClass, TestSuite testSuite) throws Exception {
		Logger log = Logger.getLogger (getClass().getName());
		log.warn ("doTest() not overridden.");
	}

	/**
	 * A TestNG DataProvider.
	 * Build the array needed by the TestClass.
	 * This method should be suitable for all production TestClass.
	 *
	 * @param context		the TestNG context
	 * @return				a two dimensional array where the number of rows is equal to the sum of
	 * 						all {@link TestCase TestCase} {@link TestRun TestRuns}. The number of columns is 3.
	 * 						The first column is our class name, the second is a TestCase, and the third column is a TestRun.
	 * @throws Exception	on any error
	 */
	@DataProvider//(parallel = true)
	public Object[][] doDataProvider (ITestContext context) throws Exception {

		String className = getClass().getName();
		Logger log = Logger.getLogger (className);
		log.debug("In "+className+"#doDataProvider ITestContext "+context+".");

		GopherData gopherData = null;
		Transaction tran = null;
		Object[][] returnValues = null;

		try {
			gopherData = GopherDataFactory.getGopherData();

			ISuite suite = context.getSuite();
			String suiteName = suite.getName();

			// TBD - remove this work-around to prevent dirty reads and writes.
			gopherData.getHibernateSession().beginTransaction().commit();

			TestSuite testSuite = gopherData.findTestSuiteByName (suiteName);

			if (testSuite == null){
				throw new Exception ("TestSuite with name '"+suiteName+"' not found.");					
			}

			// TBD - remove this work-around to prevent dirty reads and writes.
			gopherData.getHibernateSession().beginTransaction().commit();

			// Find the TestClass by our class name.
			TestClass testClass = gopherData.findTestClassByClassName(className);

			if (testClass == null){
				throw new Exception ("TestClass with member className '"+className+"' not found.");
			}

			// TBD - remove this work-around to prevent dirty reads and writes.
			gopherData.getHibernateSession().beginTransaction().commit();

			// Find the TestCase(s) for this TestClass.
			// TestCases define what we are expected to do.
			List<TestCase> testCases = gopherData.findTestCasesByTestClass(testClass.getId());

			// Count the number of TestRuns.
			int testRunCount = 0;
			for (TestCase testCase : testCases) {
			    List<TestRun> testRuns = testCase.getTestRuns();
				testRunCount += testRuns.size();
			}

			// The return array is of size testRunCount by 4.
			// It is 4 wide because we are going to add the TestSuite(if any), our class name, the TestCase and TestRun.
			returnValues = new Object[testRunCount][4];

			// For every TestCase
			int testRunNumber = 0;
			for (TestCase testCase : testCases) {
				// For every TestRun
				for (TestRun testRun : testCase.getTestRuns ()) {
					// Add the TestCase and TestRun to the array.
					returnValues[testRunNumber][0] = testRun;
					returnValues[testRunNumber][1] = testCase;
					returnValues[testRunNumber][2] = testClass;
					returnValues[testRunNumber++][3] = testSuite;
				}
			}
		} catch (Exception e) {			
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			throw e;
		} finally {
			if (tran != null && !tran.wasCommitted()) {
				log.debug("Rolling back transaction.");
				tran.rollback();
			}
			if (gopherData != null) {
				gopherData.close();
			}
			log.debug("Leaving "+className+"#doDataProvider ITestContext "+context+".");		}

	    return returnValues;
	}

	/**
	 * Create a Suite if this is the first call.
	 * Call mock() for unit tests.
	 * Create a TestSuiteInstance.
	 *
	 * @param url			the url parameter if any
	 * @param browser		the browser parameter if any
	 * @param suiteName		the Suite name parameter if any
	 * @param suiteVersion	the Suite version if any
	 * @param context		the TestNG context
	 * @throws Exception	on any error
	 */
	@Parameters({ "url","browser","suiteName","suiteVersion" })
	@BeforeSuite
	public void acceptSuiteParameters (@Optional()String url, @Optional()String browser,
		@Optional()String suiteName,@Optional()String suiteVersion,
		ITestContext context) throws Exception {

		Logger log = Logger.getLogger (getClass().getName());
		log.debug("@BeforeSuite acceptSuiteParameters(url=\""+url+
			"\" browser=\""+browser+"\").");
		log.debug("suiteName='"+suiteName+"' suiteVersion='"+suiteVersion+
			"' context="+context);

		this.url = url;
		if (browser != null) {
			this.browser = BrowserEnum.valueOf(browser);
		}

		GopherData gopherData = null;
		Transaction tran = null;

		try {
			// Let any subclass that is a unit test know that it should mock data
			// for this Suite.
			mockData(context, suiteName, suiteVersion);

			// If we are running in a Suite
			// (if the suiteName parameter was defined)
			SuiteInstance suiteInstance = null;
			if (suiteName != null) {
				// Create a SuiteInstance.
				// The adaptor will insure this only happens once per Suite.
				suiteInstance = SuiteListenerAdaptor.createSuiteInstance(suiteName,
					suiteVersion, log);
			}

			gopherData = GopherDataFactory.getGopherData();

			// Create a TestSuiteInstance for every unique ISuite.
			// Maintain a map of ISuite to TestSuiteInstance id.
			// This allows us to have multiple TestSuites with the same name
			// in a Suite.
			// If the map does not contain this ISuite
			ISuite isuite = context.getSuite();
			if (!testSuiteInstanceMap.containsKey(isuite)) {
				String testSuiteName = isuite.getName();

				// TBD - remove this work-around.
				gopherData.getHibernateSession().beginTransaction().commit();

				log.trace("Finding TestSuite named '"+testSuiteName+"'.");
				TestSuite testSuite = gopherData.findTestSuiteByName (testSuiteName);

				if (testSuite == null) {
					throw new Exception ("TestSuite with name '"+testSuiteName+
						"' not found.");
				}
				log.trace("Found TestSuite.");

				log.debug("Creating TestSuiteInstance for TestSuite named '"+testSuiteName+"'.");

				tran = gopherData.getHibernateSession().beginTransaction();

				TestSuiteInstance testSuiteInstance = gopherData.createTestSuiteInstance
					(testSuite, suiteInstance);

				tran.commit();

				Long testSuiteInstanceId = testSuiteInstance.getId();
				log.debug("Created TestSuiteInstance with id "+testSuiteInstanceId+".");

				testSuiteInstanceMap.put (isuite, testSuiteInstanceId);
				log.debug("Mapped ISuite "+isuite+" to TestSuiteInstance id "+
					testSuiteInstanceId+".");
			}
			else {
				log.debug("ISuite '"+isuite+"' in map.");
			}
		} catch (Exception e) {			
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			throw e;
		} finally {
			if (tran != null && !tran.wasCommitted()) {
				log.debug("Rolling back transaction.");
				tran.rollback();
			}
			if (gopherData != null) {
				log.debug("Closing gopherData.");
				gopherData.close();
			}
			log.debug("Leaving @BeforeSuite acceptSuiteParameters(url=\""+url+
					"\" browser=\""+browser+"\").");
		}
	}

	@Parameters({ "url","browser" })
	@BeforeMethod
	public void acceptMethodParameters (@Optional()String url, @Optional()String browser) {
		Logger log = Logger.getLogger (getClass().getName());
		log.debug("In @BeforeMethod acceptMethodParameters(url=\""+url+
				"\" browser=\""+browser+"\").");
		this.url = url;
		if (browser != null) {
			this.browser = BrowserEnum.valueOf(browser);
		}
		log.debug("Leaving @BeforeMethod acceptMethodParameters(url=\""+url+
				"\" browser=\""+browser+"\").");
	}

	/**
	 * Update the TestSuiteInstance with the end time.
	 * @param suiteName		the Suite name parameter if any
	 * @param suiteVersion	the Suite version if any
	 * @param context		the TestNG context
	 * @throws Exception	on any error
	 */
	@Parameters({"suiteName","suiteVersion"})
	@AfterSuite
	public void afterSuite (@Optional()String suiteName,@Optional()String suiteVersion,
		ITestContext context) throws Exception {

		Logger log = Logger.getLogger (getClass().getName());
		log.debug("@AfterSuite afterSuite(suiteName='"+suiteName+"' suiteVersion='"+suiteVersion+
			"' context="+context);

		GopherData gopherData = null;
		Transaction tran = null;

		try {
			gopherData = GopherDataFactory.getGopherData();

			// Maintain a map of ISuite to TestSuiteInstance id.
			// This allows us to have multiple TestSuites with the same name
			// in a Suite.
			// If the map does not contain this ISuite
			ISuite isuite = context.getSuite();
			if (!testSuiteInstanceMap.containsKey(isuite)) {
				Exception e = new Exception("ISuite not found in map.");
				throw e;
			}
			log.debug("ISuite '"+isuite+"' in map.");
			Long testSuiteInstanceId=testSuiteInstanceMap.get(isuite);
			TestSuiteInstance testSuiteInstance = gopherData.findTestSuiteInstanceById(testSuiteInstanceId);
			testSuiteInstance.setEndTime(new Date());
			Session hibernateSession=gopherData.getHibernateSession();
			tran=hibernateSession.beginTransaction();
			log.debug("Setting TestSuiteInstance end time.");
			hibernateSession.update(testSuiteInstance);
			tran.commit();
			hibernateSession.flush();
		} catch (Exception e) {			
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
			throw e;
		} finally {
			if (tran != null && !tran.wasCommitted()) {
				log.debug("Rolling back transaction.");
				tran.rollback();
			}
			if (gopherData != null) {
				log.debug("Closing gopherData.");
				gopherData.close();
			}
			log.debug("Leaving afterSuite(suiteName='"+suiteName+"' suiteVersion='"+suiteVersion+
				"' context="+context);
		}
	}

	/**
	 * A stub for tests to use for mock data.
	 * @param context	the ITestContext
	 * @param suiteName		the Suite name
	 * @param suiteVersion	the Suite version
	 * @throws Exception	on any error
	 */
	protected void mockData (ITestContext context, String suiteName, String suiteVersion) throws Exception
	{
		Logger log = Logger.getLogger (getClass().getName());
		log.warn("mockData not over-ridden");
	}

	@BeforeSuite
	public static void clearValidationExceptions () {
		ErrorUtil.clear();
	}

	/**
	 * Determine the URL given the
	 * {@link com.echostar.gopher.persist.TestSuite TestSuite} and
	 * {@link com.echostar.gopher.persist.TestRun TestRun}.
	 * @param testSuite		the TestSuite
	 * @param testRun		the TestRun
	 * @return	the URL
	 * @throws	Exception	on any error
	 */
	public String determineUrl (TestSuite testSuite, TestRun testRun) throws Exception {
		String url = testRun.getUrl();
		if (url != null) {
			return url;
		}

		// Return url as TestNGParameter.
		if (this.url != null) {
			return this.url;
		}

		throw new Exception ("Test URL not defined in SuiteDecorator, TestSuiteDecorator, TestClassDecorator or TestRun.");
	}

	/**
	 * Determine the browser given the
	 * {@link com.echostar.gopher.persist.TestSuite TestSuite} and
	 * {@link com.echostar.gopher.persist.TestRun TestRun}.
	 * @param testSuite		the TestSuite
	 * @param testRun		the TestRun
	 * @return				the browser name
	 * @throws Exception	on any error
	 */
	public BrowserEnum determineBrowser (TestSuite testSuite, TestRun testRun) throws Exception {
		BrowserEnum browser = null;
		
		if (testRun != null) {
			browser = testRun.getBrowser();
			if (browser != null) {
				return browser;
			}
		}
		
		// Return browser as TestNGParameter.
		if (this.browser != null) {
			return this.browser;
		}
		
		throw new Exception ("Test browser not defined in TestSuiteDecorator or TestRun.");
	}
}
