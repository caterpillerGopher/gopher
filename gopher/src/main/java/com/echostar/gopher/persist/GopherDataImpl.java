package com.echostar.gopher.persist;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import com.echostar.gopher.util.Config;

/**
 * An implementation of {@link GopherData GopherData}.
 *
 * @author charles.young
 *
 */
public class GopherDataImpl implements GopherData {

	protected static Logger			rootLog;
	protected static Configuration	hibernateConfig;
	protected static SessionFactory	hibernateSessionFactory;
	protected Session				hibernateSession;

	/**
	 * The default directory 'src/main/config' containing the Hibernate config file.
	 * @see #getHibernateConfigDir()
	 */
	public static final String DEFAULT_HIBERNATE_CONFIG_DIR = "src/main/config";

	/**
	 * The default Hibernate config file name 'hibernate.cfg.gopher.xml'.
	 * @see #getHibernateConfigFileName()
	 */
	public static final String DEFAULT_HIBERNATE_CONFIG_FILE = "hibernate.cfg.gopher.xml";

	/**
	 * Name of {@link com.echostar.gopher.util.Config Config} property defining the name of the
	 * directory containing the Hibernate config file.
	 * </p>
	 * This need not be the actual directory.
	 * It may be a higher level directory.
	 * In this case the config file name must be a path to the config file relative to the directory.
	 *
	 * @see #getHibernateConfigDir()
	 */
	public static final String HIBERNATE_CONFIG_DIR_PROP = "hibernate.config.dir";

	/**
	 * Name of {@link com.echostar.gopher.util.Config Config} property defining the name of the
	 * Hibernate config file.
	 *
	 * @see #getHibernateConfigFileName()
	 */
	public static final String HIBERNATE_CONFIG_FILE_NAME_PROP = "hibernate.config.fileName";

	static {
		rootLog = Logger.getRootLogger();

		hibernateConfig = new Configuration();

		String hibernateConfigDir = getHibernateConfigDir();

		String hibernateConfigFileName = getHibernateConfigFileName();

        hibernateConfig.configure (new File(hibernateConfigDir+"/"+hibernateConfigFileName));
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(hibernateConfig.getProperties());
		hibernateSessionFactory = hibernateConfig.buildSessionFactory(ssrb.build());
	}

	/**
	 * Get the name of the directory containing the Hibernate config file.
	 * This name plus "/" plus {@link getHibernateConfigFileName getHibernateConfigFileName}
	 * must define the complete path to the Hibernate config file.
	 * </p>
	 * The config dir may be defined with a {@link com.echostar.gopher.util.Config Config} property
	 * {@link #HIBERNATE_CONFIG_DIR_PROP HIBERNATE_CONFIG_DIR_PROP}.
	 * </p>
	 * The default directory is "src/main/config".
	 *
	 * @return	the directory name
	 */
	public static String getHibernateConfigDir () {
		
		String hibernateConfigDir = DEFAULT_HIBERNATE_CONFIG_DIR;
		try {
			hibernateConfigDir = Config.getProperty_S(HIBERNATE_CONFIG_DIR_PROP);
			rootLog.debug("Using '"+hibernateConfigDir+"' as Hibernate config directory.");
		} catch (Exception e) {
			rootLog.warn("Property '"+HIBERNATE_CONFIG_DIR_PROP+"' not found in Config.\nDefaulting to '"+hibernateConfigDir+"'.");
		}
		return hibernateConfigDir;
	}

	/**
	 * Get the name of the Hibernate config file.
	 * The {@link getHibernateConfigDir getHibernateConfigDir} plus "/" plus this name
	 * must define the complete path to the Hibernate config file.
	 * </p>
	 * The name may be defined with a {@link com.echostar.gopher.util.Config Config} property
	 * {@link #HIBERNATE_CONFIG_FILE_NAME_PROP HIBERNATE_CONFIG_FILE_NAME_PROP}.
	 * </p>
	 * The default name is "hibernate.cfg.gopher.xml".
	 *
	 * @return	the file name relative to the Hibernate config dir
	 */
	public static String getHibernateConfigFileName () {
		
		String hibernateConfigFileName = DEFAULT_HIBERNATE_CONFIG_FILE;
		try {
			hibernateConfigFileName = Config.getProperty_S(HIBERNATE_CONFIG_FILE_NAME_PROP);
			rootLog.debug("Using '"+hibernateConfigFileName+"' as Hibernate config file name.");
		} catch (Exception e) {
			rootLog.warn("Property '"+HIBERNATE_CONFIG_FILE_NAME_PROP+"' not found in Config.\nDefaulting to '"+hibernateConfigFileName+"'.");			
		}
		return hibernateConfigFileName;
	}

	public GopherDataImpl () {
		hibernateSession = hibernateSessionFactory.openSession();		
	}

	public Session getHibernateSession () {
		return hibernateSession;
	}

	/**
	 * Implement {@link GopherData#cleanDB() GopherData}.
	 */
	public void cleanDB () {

		GopherData gopherData = GopherDataFactory.getGopherData();
		Session hibernateSession = gopherData.getHibernateSession();

		Transaction tran = hibernateSession.beginTransaction();
	    Query query;
	    query = hibernateSession.createQuery("DELETE FROM TestException");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM Browser");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM ElementLocator");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestData");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestDataType");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestRunResult");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestSuiteInstance");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM SuiteInstance");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestRun");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestNode");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestClassDecorator");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestSuiteDecorator");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM SuiteDecorator");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestCase");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestClass");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM TestSuite");
	    query.executeUpdate();
	    query = hibernateSession.createQuery("DELETE FROM Suite");
	    query.executeUpdate();
	    tran.commit();
	}

	/**
	 * Implement {@link GopherData#close() GopherData}.
	 * Do not use GopherData interface after calling this method.
	 */
	public void close () {
		try {
			if (hibernateSession != null)
				hibernateSession.close ();
		} catch (Exception e) {
			Logger log = Logger.getLogger (getClass().getName());
			log.error("Error closing hibernateSession.");
			log.error(e);
		}
	}

	/**
	 * Implement {@link GopherData#createBrowser(String, BrowserEnum) GopherData}.
	 */
	public Browser createBrowser (String name, BrowserEnum type) throws Exception {
		Browser browser = new Browser (name, type);
		Long browserId = (Long) hibernateSession.save(browser);
		browser.setId(browserId);

		return browser;
	}

	/**
	 * Implement {@link GopherData#createTestClass(String, String, String, String, boolean, String) GopherData}.
	 */
	public TestClass createTestClass (
		String name,
		String version,
		String testClassName,
		String description,
		boolean runmode,
		String jiraIssue) throws Exception {

		TestClass testClass = new TestClass(name, version, testClassName, description, runmode,
			jiraIssue);
		Long testClassId = (Long) hibernateSession.save(testClass);
		testClass.setId(testClassId);

		return testClass;
	}

	/**
	 * Implement {@link GopherData#createTestData(TestDataType, String) GopherData}.
	 */
	public TestData createTestData (
		TestDataType testDataType,
		String value) throws Exception {

		TestData testData = new TestData (testDataType, value);
		Long testDataId = (Long) hibernateSession.save(testData);
		testData.setId(testDataId);

		return testData;		
	}

	/**
	 * Implement {@link GopherData#createElementLocator(ElementLocatorType, String, String, String) GopherData}.
	 */
	public ElementLocator createElementLocator (ElementLocatorType elementLocatorType,
		String name, String value, String description) throws Exception {

		ElementLocator elementLocator = new ElementLocator (elementLocatorType, name,
			value, description);
		Long id = (Long) hibernateSession.save(elementLocator);
		elementLocator.setId(id);

		return elementLocator;		
	}

	/**
	 * Implement {@link GopherData#createTestDataType(String, DataTypeEnum, DataRoleEnum) GopherData}.
	 */
	public TestDataType createTestDataType (
		String name,
		DataTypeEnum type,
		DataRoleEnum role) throws Exception {
		
		TestDataType dataType = new TestDataType (name, type, role);
		Long dataTypeId = (Long) hibernateSession.save(dataType);
		dataType.setId (dataTypeId);

		return dataType;
	}

	/**
	 * Implement {@link GopherData#createTestCase(String, String, Boolean, TestClass, List) GopherData}.
	 */
	public TestCase createTestCase (
		String name,
		String version,
		Boolean runmode,
		TestClass testClass,
		List<TestData> testData) throws Exception {

		TestCase testCase = new TestCase(name, version, runmode, testClass);
		Long testCaseId = (Long) hibernateSession.save(testCase);
		testCase.setId(testCaseId);
		
		return testCase;
	}

	/**
	 * Implement {@link GopherData#createTestException(Throwable, int, TestRunResult) GopherData}.
	 */
	public TestException createTestException (Throwable e, int maxTraceLen, TestRunResult testRunResult) throws Exception {
		
		TestException testException = new TestException (e, maxTraceLen, testRunResult);
		Long id = (Long) hibernateSession.save(testException);
		testException.setId(id);
		testRunResult.addTestException(testException);
		hibernateSession.update(testRunResult);
		return testException;
	}
	
	/**
	 * Implement {@link GopherData#createTestRun(String, BrowserEnum, Boolean, TestCase, TestNode) GopherData}.
	 */
	public TestRun createTestRun (String url, BrowserEnum browser,
		Boolean runmode, TestCase testCase, TestNode testNode) throws Exception {

		TestRun testRun = new TestRun (url, browser, runmode, testCase, testNode);
		Long testRunId = (Long) hibernateSession.save(testRun);
		testRun.setId(testRunId);
		testCase.addTestRun (testRun);
		hibernateSession.update(testCase);
		return testRun;
	}

	/**
	 * Implement {@link GopherData#createTestClassDecorator(String, BrowserEnum, Boolean, TestSuite, TestClass) GopherData}.
	 */
	public TestClassDecorator createTestClassDecorator (String url, BrowserEnum browser,
		Boolean runmode, TestSuite testSuite, TestClass testClass) throws Exception {

		TestClassDecorator testClassDecorator = new TestClassDecorator (url, browser, runmode, testSuite, testClass);
		Long testSuiteRunId = (Long) hibernateSession.save(testClassDecorator);
		testClassDecorator.setId(testSuiteRunId);
		testSuite.addTestClassDecorator (testClassDecorator);
		hibernateSession.update(testSuite);
		return testClassDecorator;
	}

	/**
	 * Implement {@link GopherData#createTestSuiteDecorator(String, BrowserEnum, Boolean, Suite, TestSuite) GopherData}.
	 */
	public TestSuiteDecorator createTestSuiteDecorator (String url, BrowserEnum browser,
			Boolean runmode, Suite suite, TestSuite testSuite) throws Exception {
		
		TestSuiteDecorator testSuiteDecorator = new TestSuiteDecorator (url, browser, runmode, suite, testSuite);
		Long suiteRunId = (Long) hibernateSession.save(testSuiteDecorator);
		testSuiteDecorator.setId(suiteRunId);
		suite.addTestSuiteDecorator (testSuiteDecorator);
		hibernateSession.update(suite);
		return testSuiteDecorator;
	}

	/**
	 * Implement {@link GopherData#createSuiteDecorator(String, String, BrowserEnum, Boolean, Suite) GopherData}.
	 */
	public SuiteDecorator createSuiteDecorator (String name, String url, BrowserEnum browser,
			Boolean runmode, Suite suite) throws Exception {
		
		SuiteDecorator suiteDecorator = new SuiteDecorator (name, url, browser, runmode, suite);
		Long suiteRunId = (Long) hibernateSession.save(suiteDecorator);
		suiteDecorator.setId(suiteRunId);
		suite.addSuiteDecorator (suiteDecorator);
		hibernateSession.update(suite);
		return suiteDecorator;
	}

	/**
	 * Implement
	 * {@link GopherData#createTestNode(PlatformEnum, String, String, String, String, String, String) GopherData}.
	 */
	public TestNode createTestNode (PlatformEnum platform, String nodeIP, String nodePort,
		String userName, String password, String installDir, String seleniumServer) throws Exception {

		TestNode testNode = new TestNode (platform, nodeIP, nodePort,
			userName, password, installDir, seleniumServer);
		Long testNodeId = (Long) hibernateSession.save(testNode);
		testNode.setId(testNodeId);
		return testNode;
	}

	/**
	 * Implement {@link GopherData#createTestRunResult(boolean, String, Date, Date, String, TestRun, SuiteInstance, TestSuiteInstance) GopherData}.
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
		TestSuiteInstance testSuiteInstance) throws Exception {

		TestRunResult runResult = new TestRunResult(result, message, startTime,
			endTime, user, url, testRun, suiteInstance, testSuiteInstance);
		Long runResultId = (Long) hibernateSession.save(runResult);
		runResult.setId (runResultId);
		if (suiteInstance != null) {
			suiteInstance.addTestRunResult(runResult);
			hibernateSession.update(suiteInstance);
		}
		if (testSuiteInstance != null) {
			testSuiteInstance.addTestRunResult(runResult);
			hibernateSession.update(testSuiteInstance);
		}

		return runResult;
	}

	/**
	 * Implement {@link GopherData#createSuite(String, String, String, boolean, List) GopherData}.
	 */
	public Suite createSuite (
		String name,
		String version,
		String description,
		boolean runmode,
		List<TestSuite> testSuites) throws Exception {
		
		// Create the suite.
		Suite suite = new Suite (name, version, description, runmode, testSuites);
		Long suiteId = (Long) hibernateSession.save(suite);
		suite.setId (suiteId);

		return suite;
	}

	public SuiteInstance createSuiteInstance (Suite suite) throws Exception {
		Date d1 = new Date();
		Date d2 = null;
		SuiteInstance suiteInstance = new SuiteInstance (d1, d2, suite);
		Long id = (Long) hibernateSession.save(suiteInstance);
		suiteInstance.setId (id);

		return suiteInstance;		
	}

	public TestSuiteInstance createTestSuiteInstance (TestSuite testSuite,
		SuiteInstance suiteInstance) throws Exception {
		Date d1 = new Date();
		Date d2 = null;
		TestSuiteInstance instance = new TestSuiteInstance (d1, d2, testSuite,
			suiteInstance);
		Long id = (Long) hibernateSession.save(instance);
		instance.setId (id);

		return instance;		
	}

	/**
	 * Implement {@link GopherData#findTestSuiteDecorators(Suite, TestSuite) GopherData}.
	 */
	public List<TestSuiteDecorator> findTestSuiteDecorators(Suite suite, TestSuite testSuite) {
		List<TestSuiteDecorator> testSuiteDecorators = new ArrayList<TestSuiteDecorator>();
		for (TestSuiteDecorator testSuiteDecorator : suite.getTestSuiteDecorators()) {
			if (testSuiteDecorator.getTestSuite().equals(testSuite)){
				testSuiteDecorators.add(testSuiteDecorator);
			}
		}
		return testSuiteDecorators;
	}

	/**
	 * Implement {@link GopherData#createTestSuite(String, String, String, boolean, List) GopherData}.
	 */
	public TestSuite createTestSuite (
		String name,
		String version,
		String description,
		boolean runmode,
		List<TestClass> testClasses) throws Exception {
		
		// Create the test suite.
		TestSuite testSuite = new TestSuite (name, version, description, runmode, testClasses);
		Long suiteId = (Long) hibernateSession.save(testSuite);
		testSuite.setId (suiteId);

		return testSuite;
	}

	/**
	 * Implement {@link GopherData#findTestNodes() GopherData}.
	 */
	@SuppressWarnings("unchecked")
	public List<TestNode> findTestNodes () throws Exception {

		Query query = hibernateSession.createQuery("FROM TestNode");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		return results;
	}

	/**
	 * Implement {@link GopherData#findTestCases() GopherData}.
	 */
	@SuppressWarnings("unchecked")
	public List<TestCase> findTestCases () throws Exception {

		Query query = hibernateSession.createQuery("FROM TestCase");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		return results;
	}

	/**
	 * Implement {@link GopherData#findTestClassByClassName(String) GopherData}.
	 */
	public TestClass findTestClassByClassName (String className) throws Exception {

		Query query = hibernateSession.createQuery("FROM TestClass WHERE className=\'"+className+"\'");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		if (results.size() == 0){
			return null;
		}
	    TestClass testClass = (TestClass) results.get(0);
		return testClass;
	}

	/**
	 * Implement {@link GopherData#findTestSuiteByName(String) GopherData}.
	 */
	public TestSuite findTestSuiteByName (String name) throws Exception {

		Query query = hibernateSession.createQuery("FROM TestSuite WHERE name='"+name+"'");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		if (results.size() == 0){
			return null;
		}
		TestSuite testSuite = (TestSuite) results.get(0);
		return testSuite;
	}

	/**
	 * Implement {@link GopherData#findSuiteByNameAndVersion(String, String) GopherData}.
	 */
	public Suite findSuiteByNameAndVersion (String name, String version) throws Exception {

		Query query = hibernateSession.createQuery
			("FROM Suite WHERE name='"+name+"' AND version='"+version+"'");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		if (results.size() == 0){
			return null;
		}
		Suite suite = (Suite) results.get(0);
		return suite;
	}

	/**
	 * Implement {@link GopherData#findTestCasesByTestClass(Long) GopherData}.
	 */
	public List<TestCase> findTestCasesByTestClass (Long testClassId) throws Exception {

		Query query = hibernateSession.createQuery("FROM TestCase WHERE test_class_id="+testClassId+"");
	    @SuppressWarnings("unchecked")
        List<TestCase> results = query.list();
		return results;
	}

	/**
	 * Implement {@link GopherData#findTestCasesByName(String) GopherData}.
	 */
	public List<TestCase> findTestCasesByName (String name) throws Exception {

		Query query = hibernateSession.createQuery("FROM TestCase WHERE name='"+name+"'");
	    @SuppressWarnings("unchecked")
        List<TestCase> results = query.list();
		return results;
	}

	/**
	 * Implement {@link GopherData#findTestRunResultsByTestRun(Long) GopherData}.
	 */
	public List<TestRunResult> findTestRunResultsByTestRun (Long testRunId) throws Exception {

		Query query = hibernateSession.createQuery("FROM TestRunResult WHERE test_run_id="+testRunId+"");
		@SuppressWarnings({ "unchecked" })
	    List<TestRunResult> results = query.list();
		return results;
	}

	/**
	 * Implement {@link GopherData#findTestSuiteInstancesByTestSuite(Long) TestSuite id}.
	 */
	public List<TestSuiteInstance> findTestSuiteInstancesByTestSuite (Long testSuiteId) throws Exception {

		Query query = hibernateSession.createQuery("FROM TestSuiteInstance WHERE test_suite_id="+testSuiteId+"");
		@SuppressWarnings({ "unchecked" })
	    List<TestSuiteInstance> results = query.list();
		return results;
	}

	/**
	 * Implement {@link GopherData#findSuiteInstancesBySuite(Long) Suite id}.
	 */
	public List<SuiteInstance> findSuiteInstancesBySuite (Long suiteId) throws Exception {

		Query query = hibernateSession.createQuery("FROM SuiteInstance WHERE suite_id="+suiteId+"");
		@SuppressWarnings({ "unchecked" })
	    List<SuiteInstance> results = query.list();
		return results;
	}

	/**
	 * Implement {@link GopherData#findTestRunResultsByTestClass(Long) GopherData}.
	 */
	public List<TestRunResult> findTestRunResultsByTestClass (Long testClassId) throws Exception {

		List<TestCase> testCases = findTestCasesByTestClass (testClassId);
		List<TestRunResult> testRunResults = new ArrayList<TestRunResult>();

		for (TestCase testCase : testCases) {
			List<TestRun> testRuns = testCase.getTestRuns ();
			for (TestRun testRun : testRuns) {
				testRunResults.addAll(findTestRunResultsByTestRun (testRun.getId()));
			}
		}
		return testRunResults;
	}

	/**
	 * Implement {@link GopherData#findTestDataTypeById(Long) GopherData}.
	 */
	public TestDataType findTestDataTypeById (Long id) throws Exception {

		Query query = hibernateSession.createQuery("FROM TestDataType WHERE id="+id+"");
		@SuppressWarnings("rawtypes")
	    List results = query.list();
		if (results.size() == 0){
			return null;
		}
		TestDataType testDataType = (TestDataType) results.get(0);
		return testDataType;
		
	}

	/**
	 * Implement {@link GopherData#findAllSuites() GopherData}.
	 */
    public List<Suite> findAllSuites () {
		
		Query query = hibernateSession.createQuery("FROM Suite");
	    @SuppressWarnings("unchecked")
	    List<Suite> results = query.list();
		return results;
	}

	public SuiteInstance findSuiteInstanceById(Long id) throws Exception {
		Query query = hibernateSession.createQuery(
				"FROM SuiteInstance WHERE id="+id);
		@SuppressWarnings("rawtypes")
		List results = query.list();
		if (results.size() == 0){
			return null;
		}
		SuiteInstance suiteInstance = (SuiteInstance) results.get(0);
		return suiteInstance;
	}

	public TestSuiteInstance findTestSuiteInstanceById(Long id) throws Exception {
		Query query = hibernateSession.createQuery(
				"FROM TestSuiteInstance WHERE id="+id);
		@SuppressWarnings("rawtypes")
		List results = query.list();
		if (results.size() == 0){
			return null;
		}
		TestSuiteInstance testSuiteInstance = (TestSuiteInstance) results.get(0);
		return testSuiteInstance;
	}

	/**
	 * Implement {@link GopherData#findAllTestSuites() GopherData}.
	 */
    public List<TestSuite> findAllTestSuites () {
		
		Query query = hibernateSession.createQuery("FROM TestSuite");
		@SuppressWarnings("unchecked")
	    List<TestSuite> results = query.list();
		return results;
	}

	public List<TestClass> findAllTestClasses() throws Exception {
		Query query = hibernateSession.createQuery("FROM TestClass");
		@SuppressWarnings({ "unchecked" })
	    List<TestClass> results = query.list();
		return results;				
	}

	public List<TestSuiteInstance> findAllTestSuiteInstances() throws Exception {
		Query query = hibernateSession.createQuery("FROM TestSuiteInstance");
		@SuppressWarnings({ "unchecked" })
	    List<TestSuiteInstance> results = query.list();
		return results;		
	}

	public List<ElementLocator> findAllElementLocators() throws Exception {
		Query query = hibernateSession.createQuery("FROM ElementLocator");
		@SuppressWarnings({ "unchecked" })
	    List<ElementLocator> results = query.list();
		return results;		
	}

	public List<SuiteInstance> findAllSuiteInstances() throws Exception {
		Query query = hibernateSession.createQuery("FROM SuiteInstance");
		@SuppressWarnings({ "unchecked" })
	    List<SuiteInstance> results = query.list();
		return results;		
	}

	public TestSuite findTestSuiteById(Long id) throws Exception {
		Query query = hibernateSession.createQuery(
				"FROM TestSuite WHERE id='"+id+"'");
		@SuppressWarnings("rawtypes")
		List results = query.list();
		if (results.size() == 0){
			return null;
		}
		TestSuite testSuite = (TestSuite) results.get(0);
		return testSuite;
	}

	/**
	 * Implement {@link GopherData#findTestSuiteDecorator(Suite, TestSuite) GopherData}.
	 */
	public TestSuiteDecorator findTestSuiteDecorator (Suite suite, TestSuite testSuite) {
		for (TestSuiteDecorator testSuiteDecorator : suite.getTestSuiteDecorators()) {
			if (testSuiteDecorator.getTestSuite().equals(testSuite)){
				return testSuiteDecorator;
			}
		}
		return null;
	}

	/**
	 * Implement {@link GopherData#findTestClassDecorator(TestSuite, TestClass) GopherData}.
	 */
	public TestClassDecorator findTestClassDecorator (TestSuite testSuite, TestClass testClass) {
		
		for (TestClassDecorator testClassDecorator : testSuite.getTestClassDecorators()) {
			if (testClassDecorator.getTestClass().equals(testClass)){
				return testClassDecorator;
			}
		}
		return null;
	}
}