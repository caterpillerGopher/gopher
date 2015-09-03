package com.echostar.gopher.persist;

import java.util.Date;
import java.util.List;
import org.hibernate.Session;

/**
 * An interface into GOPHER persistent storage.
 * GOPHER persistent objects persist through Hibernate.
 * 
 * This interface is provided by {@link GopherDataFactory GopherDataFactory}.
 *
 * Created objects will have an assigned id.
 * Users must insure that create/update method calls run in a transaction and that
 * transaction is committed.
 * Find methods need not run in a transaction.
 * Nested transaction are not supported.
 * Users may wish to make several method calls before committing
 * a transaction.
 *
 * @author charles.young
 */
public interface GopherData {

	/**
	 * Get the Hibernate Session.
	 * @return	the Session
	 */
	public Session getHibernateSession ();

	/**
	 * Clean the DB in a new transaction.
	 * @throws Exception	on any error
	 */
	public void cleanDB () throws Exception;

	/**
	 * Close the Hibernate Session.
	 */
	public void close ();

	/**
	 * Create a Browser object.
	 * @param name			the name of the browser
	 * @param type			the type of the browser {@link BrowserEnum BrowserEnum}
	 * @return				the Browser
	 * @throws Exception	on any error
	 */
	public Browser createBrowser (String name, BrowserEnum type) throws Exception;

	/**
	 * Create a {@link TestClass TestClass}.
	 * The combination of name and version must be unique.
	 *
	 * @param name			the name of the TestClass
	 * @param version		the version of the TestClass
	 * @param testClass		the simple class name of the TestNG Java class
	 * @param description	a description of the TestClass
	 * @param runmode		true (success) or false (failure)
	 * @param jiraIssue		the Jira issue number
	 * @return				the TestClass
	 * @throws Exception	on error
	 */
	public TestClass createTestClass (
		String name,
		String version,
		String testClass,
		String description,
		boolean runmode,
		String jiraIssue) throws Exception;

	/**
	 * Create {@link TestData TestData} arguments for the {@link TestCase TestCase}.
	 *
	 * @param testDataType	the {@link TestDataType TestDataType}
	 * @param value			the value. It must be convertible to the type defined by {@link TestDataType TestDataType}.
	 * @return				a {@link TestData TestData} object
	 * @throws Exception	on error
	 */
	public TestData createTestData (
		TestDataType testDataType,
		String value) throws Exception;

	/**
	 * Create a {@link TestDataType TestDataType}.
	 *
	 * @param name			a name for this data type
	 * @param type			a {@link DataTypeEnum DataTypeEnum}
	 * @param role			a {@link DataRoleEnum DataRoleEnum} 
	 * @return				a {@link TestDataType TestDataType}
	 * @throws Exception	on error
	 */
	public TestDataType createTestDataType (
		String			name,
		DataTypeEnum	type,
		DataRoleEnum	role) throws Exception;

	/**
	 * Create a {@link ElementLocator ElementLocator}.
	 *
	 * @param elementLocatorType	the {@link ElementLocatorType ElementLocatorType}
	 * @param name			a name for this locator
	 * @param value			a value for this locator
	 * @param description	a description for this locator
	 * @return				a {@link ElementLocator ElementLocator}
	 * @throws Exception	on error
	 */
	public ElementLocator createElementLocator (
		ElementLocatorType	elementLocatorType,
		String			name,
		String			value,
		String			description) throws Exception;

	/**
	 * Create a {@link TestException TestException}.
	 *
	 * @param e				the Throwable
	 * @param maxTraceLen	the maximum storage length of the stacktrace string
	 * @param testRunResult	the TestRunResult having this TestException
	 * @return				a {@link TestException TestException} object
	 * @throws Exception	on error
	 */
	public TestException createTestException (Throwable e, int maxTraceLen, TestRunResult testRunResult) throws Exception;

	/**
	 * Create a {@link TestCase TestCase}. Test cases relate run-time data
	 * arguments for a TestClass.
	 * The combination of name and version must be unique.
	 * @param name			the case name
	 * @param version		the version of the TestCase
	 * @param runmode		run (true) or not (false)
	 * @param testClass		the {@link TestClass TestClass}
	 * @param testData		the {@link TestData TestData}
	 * @return				a {@link TestCase TestCase}
	 * @throws Exception	on error
	 */
	public TestCase createTestCase (
		String name,
		String version,
		Boolean runmode,
		TestClass testClass,
		List<TestData> testData) throws Exception;

	/**
	 * Create a test run.
	 *
	 * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		run case (true) or not (false)
	 * @param testCase		the test case we are running
	 * @param testNode		the test node to run on
	 * @return				the test run
	 * @throws Exception	on any error
	 */
	public TestRun createTestRun (String url, BrowserEnum browser,
			Boolean runmode, TestCase testCase, TestNode testNode) throws Exception;

	/**
	 * Create a SuiteDecorator. Relate a url, browser name, and runmode
	 * to a {@link Suite Suite}.
	 *
	 * @param name			the name of the SuiteDecorator
	 * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		run case (true) or not (false)
	 * @param suite			the Suite we are running
	 * @return				the SuiteDecorator
	 * @throws Exception	on any error
	 */
	public SuiteDecorator createSuiteDecorator (String name, String url, BrowserEnum browser,
			Boolean runmode, Suite suite) throws Exception;
	
	/**
	 * Create a TestSuiteDecorator. Relate a url, browser name, and runmode
	 * to a {@link TestSuite TestSuite} in a {@link Suite Suite}.
	 *
	 * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		run case (true) or not (false)
	 * @param suite			the Suite we are running
	 * @param testSuite		the test suite we are relating
	 * @return				the TestSuiteDecorator
	 * @throws Exception	on any error
	 */
	public TestSuiteDecorator createTestSuiteDecorator (String url, BrowserEnum browser,
			Boolean runmode, Suite suite, TestSuite testSuite) throws Exception;

	/**
	 * Create a TestClassDecorator. Relate a url, browser name, and runmode
	 * to a {@link TestClass TestClass} in a {@link TestSuite TestSuite}.
	 *
	 * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		run case (true) or not (false)
	 * @param testSuite		the test suite we are relating
	 * @param testClass		the test class we are relating
	 * @return				the TestClassDecorator
	 * @throws Exception	on any error
	 */
	public TestClassDecorator createTestClassDecorator (String url, BrowserEnum browser,
			Boolean runmode, TestSuite testSuite, TestClass testClass) throws Exception;

	/**
	 * Create a test node.
	 * @param platform		the node operating system
	 * @param nodeIP		the node IP	
	 * @param nodePort		the node port
	 * @param userName		the user name
	 * @param password		the account password
	 * @param installDir	the directory for the files we install
	 * @param seleniumServer	the name of the Selenium server file
	 * @return				a TestNode
	 * @throws Exception	on any error
	 */
	public TestNode createTestNode (PlatformEnum platform, String nodeIP, String nodePort,
			String userName, String password, String installDir, String seleniumServer) throws Exception;

	/**
	 * Create a {@link TestRunResult TestRunResult} for a {@link TestCase TestCase}
	 * 
	 * @param result		true (success) or false (failure}
	 * @param message		a message describing a failure
	 * @param startTime		the test start time
	 * @param endTime		the test end time
	 * @param user			the name of the user who ran the TestClass
	 * @param url           the url used in the test
	 * @param testRun		the {@link TestRun TestRun}
	 * @param suiteInstance	the SuiteInstance if any
	 * @param testSuiteInstance	the TestSuiteInstance if any
	 * @return				a {@link TestRunResult TestRunResult}	
	 * @throws Exception	on error
	 */
	public TestRunResult createTestRunResult (
		boolean result,
		String message,
		Date startTime,
		Date endTime,
		String user,
		String url,
		TestRun testRun,
		SuiteInstance suiteInstance,
		TestSuiteInstance testSuiteInstance) throws Exception;

	/**
	 * Create a {@link Suite Suite}.
	 * The combination of name and version must be unique.
	 * 
	 * @param name			the name of the suite
	 * @param version		the version of the suite
	 * @param description	a description of this suite
	 * @param runmode		true (run) or false (skip, do not run)
	 * @param testSuites	the {@link TestSuite TestSuites} of this suite
	 * @return				a {@link Suite Suite}
	 * @throws Exception	on error
	 */
	public Suite createSuite (
		String name,
		String version,
		String description,
		boolean runmode,
		List<TestSuite> testSuites) throws Exception;

	/**
	 * Create a SuiteInstance.
	 * @param suite			the Suite of this instance
	 * @return				the SuiteInstance
	 * @throws Exception	on any error
	 */
	public SuiteInstance createSuiteInstance (Suite suite) throws Exception;

	/**
	 * Create a TestSuiteInstance.
	 * @param testSuite		the TestSuite of this instance
	 * @param suiteInstance	the SuiteInstance
	 * @return				the TestSuiteInstance
	 * @throws Exception	on any error
	 */
	public TestSuiteInstance createTestSuiteInstance (TestSuite testSuite,
		SuiteInstance suiteInstance) throws Exception;	

	/**
	 * Create a {@link TestSuite TestSuite}.
	 * The combination of name and version must be unique.
	 * 
	 * @param name			the name of the test suite
	 * @param version		the version of the TestSuite
	 * @param description	a description of this test suite
	 * @param runmode		true (run) or false (skip, do not run)
	 * @param testClasses	the {@link TestClass TestClasses} of this suite
	 * @return				a {@link TestSuite TestSuite}
	 * @throws Exception	on error
	 */
	public TestSuite createTestSuite (
		String name,
		String version,
		String description,
		boolean runmode,
		List<TestClass> testClasses) throws Exception;

	/**
	 * Find all the TestNodes.
	 * @return	the TestNodes
	 * @throws Exception	on error
	 */
	public List<TestNode> findTestNodes () throws Exception;

	/**
	 * Find all the TestCases.
	 * @return	the TestCases
	 * @throws Exception	on error
	 */
	public List<TestCase> findTestCases () throws Exception;

	/**
	 * Find a {@link TestSuite TestSuite} by its unique name.
	 * 
	 * @param name			the TestSuite name
	 * @return				a {@link TestSuite TestSuite}
	 * @throws Exception	on error
	 */
	public TestSuite findTestSuiteByName (String name) throws Exception;

	/**
	 * Find a Suite by name and version.
	 * @param name			the Suite name
	 * @param version		the Suite version
	 * @return				the Suite if any
	 * @throws Exception	on any error
	 */
	public Suite findSuiteByNameAndVersion (String name, String version) throws Exception;

	/**
	 * Find the latest SuiteInstance.
	 *
	 * @return				the latest SuiteInstance
	 * @throws Exception	on any error
	 */
	public SuiteInstance findLatestSuiteInstance () throws Exception;

	/**
	 * Find a {@link TestClass TestClass} by its unique test class name.
	 * 
	 * @param name			the TestNG Java class name
	 * @return				a {@link TestClass TestClass}
	 * @throws Exception	on error
	 */
	public TestClass findTestClassByClassName (String name) throws Exception;

	/**
	 * Find {@link TestCase TestCases} by {@link TestClass TestClass} id.
	 * 
	 * @param testClassId	the TestClass id
	 * @return				a List of {@link TestCase TestCase}
	 * @throws Exception	on error
	 */
	public List<TestCase> findTestCasesByTestClass (Long testClassId) throws Exception;

	/**
	 * Find {@link TestCase TestCases} by name.
	 * 
	 * @param name			the TestCase name
	 * @return				a List of {@link TestCase TestCase}
	 * @throws Exception	on error
	 */
	public List<TestCase> findTestCasesByName (String name) throws Exception;

	/**
	 * Find {@link TestRunResult TestRunResults} by {@link TestClass TestClass} id.
	 * 
	 * @param testClassId	the TestClass id
	 * @return				a List of {@link TestRunResult TestRunResult}
	 * @throws Exception	on any error
	 */
	public List<TestRunResult> findTestRunResultsByTestClass (Long testClassId) throws Exception;

	public TestRunResult findTestRunResultBySuiteInstance (Long suiteInstanceId) throws Exception;

	public List<TestRunResult> findTestRunResultsByTestRun (Long testRunId) throws Exception;

	/**
	 * Find a {@link TestDataType TestDataType} by id.
	 * 
	 * @param id			a {@link TestDataType TestDataType} id
	 * @return				a {@link TestDataType TestDataType}
	 * @throws Exception	on any error
	 */
	public TestDataType findTestDataTypeById (Long id) throws Exception;

	/**
	 * Find all {@link Suite Suites}.
	 * @return	a {@link Suite Suite}
	 */
	public List<Suite> findAllSuites ();

	public SuiteInstance findSuiteInstanceById(Long id) throws Exception;
	public List<SuiteInstance> findSuiteInstancesBySuite (Long testSuiteId) throws Exception;
	public List<TestClass> findAllTestClasses() throws Exception;
	public List<TestData> findAllTestData() throws Exception;
	public List<ElementLocator> findAllElementLocators() throws Exception;
	public List<SuiteInstance> findAllSuiteInstances() throws Exception;
	public List<TestSuiteInstance> findAllTestSuiteInstances() throws Exception;
	public TestSuiteInstance findTestSuiteInstanceById(Long id) throws Exception;
	public List<TestSuiteInstance> findTestSuiteInstancesByTestSuite (Long testSuiteId) throws Exception;
	public TestSuite findTestSuiteById(Long id) throws Exception;

	/**
	 * Find all {@link TestSuite TestSuites}.
	 * @return	a {@link TestSuite TestSuite}
	 */
	public List<TestSuite> findAllTestSuites ();

	/**
	 * Find the TestSuiteRuns if any for the given Suite and TestSuite.
	 * @param suite			the Suite
	 * @param testSuite		the TestSuite
	 * @return				the SuiteRuns
	 */
	public List<TestSuiteDecorator> findTestSuiteDecorators (Suite suite, TestSuite testSuite);

	/**
	 * Find the TestSuiteDecorator if any for the given Suite and TestSuite.
	 * @param suite			the Suite
	 * @param testSuite		the TestSuite
	 * @return				the TestSuiteDecorator if any
	 */
	public TestSuiteDecorator findTestSuiteDecorator (Suite suite, TestSuite testSuite);

	/**
	 * Find the TestClassDecorator if any for the given TestSuite and TestClass.
	 * @param testSuite		the TestSuite
	 * @param testClass		the TestClass
	 * @return				the TestClassDecorator if any
	 */
	public TestClassDecorator findTestClassDecorator (TestSuite testSuite, TestClass testClass);
}
