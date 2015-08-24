package com.echostar.gopher.persist.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.deetysoft.util.Hierarchy;
import com.deetysoft.util.HierarchyNode;
import com.echostar.gopher.persist.Browser;
import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.DataRoleEnum;
import com.echostar.gopher.persist.DataTypeEnum;
import com.echostar.gopher.persist.ElementLocator;
import com.echostar.gopher.persist.ElementLocatorType;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.PlatformEnum;
import com.echostar.gopher.persist.Suite;
import com.echostar.gopher.persist.SuiteDecorator;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestSuiteDecorator;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestData;
import com.echostar.gopher.persist.TestDataType;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestNode;
import com.echostar.gopher.persist.TestSuite;
import com.echostar.gopher.persist.TestClassDecorator;

/**
 * Ingest Gopher data from XML.
 * Ingest either a single file or directory or list of either separated by a ';'.
 * A DTD is optional but advised. Specify the DTD with -dtd 'file'.
 * If the -dtd option is not given, look for the file 'gopher-data.dtd' in the CLASSPATH.
 * 
 * Create {@link TestRun TestRuns} for {@link TestCase TestCases}
 * based on the {@link TestNode TestNodes} supported browsers.
 * 
 * @author charles.young
 *
 */
public class GopherDataIngest {

	// The file argument. This may be a single file or directory or list of either separated by a ';'.
	private static String fileName = null;

	// Name of optional data DTD file.
	private static String dtdFileName = null;

	// Create a report.
	private static boolean doReport = false;

	// Read-only, no ingest.
	private static boolean readOnly = false;

	// The Gopher data interface
	private GopherData gopherData;

	// The Hibernate Session, from Gopher
	private Session session;

	// Collections of nodes from the Gopher model XML
	Collection<HierarchyNode> suiteNodes;
	Collection<HierarchyNode> suiteDecoratorNodes;
	Collection<HierarchyNode> testSuiteDecoratorNodes;
	Collection<HierarchyNode> testSuiteNodes;
	Collection<HierarchyNode> testClassDecoratorNodes;
	Collection<HierarchyNode> testClassNodes;
	Collection<HierarchyNode> testCaseNodes;
	Collection<HierarchyNode> testDataTypeNodes;
	Collection<HierarchyNode> testDataNodes;
	Collection<HierarchyNode> elementLocatorNodes;
	Collection<HierarchyNode> testRunNodes;
	Collection<HierarchyNode> testNodeNodes;
	Collection<HierarchyNode> browserNodes;

	// A map of requested Suite id to Suite.
	private Map<String, Suite> suiteMap = new HashMap<String, Suite>();

	// A map of requested TestSuite id to TestSuite.
	private Map<String, TestSuite> testSuiteMap = new HashMap<String, TestSuite>();

	// A map of requested TestClass id to TestClass.
	private Map<String, TestClass> testClassMap = new HashMap<String, TestClass>();

	// A map of requested TestCase id to TestCase.
	private Map<String, TestCase> testCaseMap = new HashMap<String, TestCase>();

	// A map of requested TestDataType id to TestDataType.
	private Map<String, TestDataType> testDataTypeMap = new HashMap<String, TestDataType>();

	// A map of requested TestData id to TestData.
	private Map<String, TestData> testDataMap = new HashMap<String, TestData>();

	// A map of requested TestNode id to TestNode.
	private Map<String, TestNode> testNodeMap = new HashMap<String, TestNode>();
	
	// A map of requested Browser id to Browser.
	private Map<String, Browser> browserMap = new HashMap<String, Browser>();

	// A map of requested ElementLocator id to ElementLocator.
	private Map<String, ElementLocator> elementLocatorMap = new HashMap<String, ElementLocator>();

	public GopherDataIngest () {
		gopherData = GopherDataFactory.getGopherData();
		session = gopherData.getHibernateSession();
	}

	public static void main (String[] args) {

		for (int i=0; i < args.length; i++) {
			switch (args[i]) {
				case "-h":
				case "-help":
					System.out.println (getUsage ());
					break;
				case "-dtd":
					dtdFileName = args[++i];
					break;
				case "-ro":
					readOnly = true;
					doReport = true;
					break;
				case "-r":
					doReport = true;
					break;
				default:
					if (args[i].charAt(0) != '-') {
						fileName = args[i];
					}
					break;
			}
		}

		if (fileName == null) {
			System.err.println ("The file name/dir argument is missing.");
			System.err.println ("\n"+getUsage ());
			System.exit (1);
		}

		GopherDataIngest ingester = new GopherDataIngest ();
	
		try {
			ingester.ingest(fileName, dtdFileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit (-1);
		}
		finally {
			if (ingester != null) {
				ingester.close();
			}
		}
		System.exit (0);
	}

	protected void close () {
		if (session != null) {
			session.close();
		}
	}

	/**
	 * Ingest the data from the given file or directory.
	 *
	 * @param	fileArg		a ';' delimited list of XML files and/or directories containing Gopher data definitions
	 * @param	dtdName		the DTD file for Gopher data
	 * @throws	Exception	on any error
	 */
	public void ingest (String fileArg, String dtdName) throws Exception {

		if (dtdName == null) {
			URL dtdURL = getClass().getClassLoader().getResource("gopher-data.dtd");
			File dtdFile = new File(dtdURL.getFile());
			dtdName = dtdFile.getPath();
			System.out.println ("Found DTD in CLASSPATH '"+dtdName+"'.");
		}
		
		List<InputStream> fileStreams = new ArrayList<InputStream>();
		List<URL> dtds = new ArrayList<URL>();

		URL dtdURL = null;
		if (dtdName != null) {
			dtdURL = new File(dtdName).toURI().toURL();
		}

		String[] fileArgs = fileArg.split(";");

		// For every file or directory in the list
		for (int i = 0; i < fileArgs.length; i++) {
			// If the file is not a directory
			File f = new File (fileArgs[i]);
			if (!f.isDirectory()) {
				// Add the file to the list of file streams
				@SuppressWarnings("resource")
				FileInputStream xmlStream = new FileInputStream(fileArgs[i]);
				fileStreams.add (xmlStream);
				// Add the DTD if any to the list of DTDs
				dtds.add(dtdURL);
			} else {
				// For every XML file in the directory
				String[] dirFiles = f.list();
				String dirName = f.getPath();
				for (int j = 0; j < dirFiles.length; j++) {
					if (dirFiles[j].endsWith(".xml")) {					
						// Add the file to the list of file streams
						@SuppressWarnings("resource")
						FileInputStream xmlStream = new FileInputStream(dirName + "/" + dirFiles[j]);
						fileStreams.add (xmlStream);
						// Add the DTD if any to the list of DTDs
						dtds.add(dtdURL);
					}
				}
			}
		}

		HierarchyNode root = Hierarchy.buildFromXml(fileStreams, dtds, false);

		for (InputStream stream : fileStreams) {
			stream.close();
		}

		testClassNodes = root.getNodesByPath("gopher-data/test-class");
		suiteNodes = root.getNodesByPath("gopher-data/suite");
		suiteDecoratorNodes = root.getNodesByPath("gopher-data/suite-decorator");
		testSuiteDecoratorNodes = root.getNodesByPath("gopher-data/test-suite-decorator");
		testSuiteNodes = root.getNodesByPath("gopher-data/test-suite");
		testClassDecoratorNodes = root.getNodesByPath("gopher-data/test-class-decorator");
		testDataTypeNodes = root.getNodesByPath("gopher-data/test-data-type");
		testDataNodes = root.getNodesByPath("gopher-data/test-data");
		elementLocatorNodes = root.getNodesByPath("gopher-data/element-locator");
		testCaseNodes = root.getNodesByPath("gopher-data/test-case");
		testRunNodes = root.getNodesByPath("gopher-data/test-run");
		testNodeNodes = root.getNodesByPath("gopher-data/test-node");
		browserNodes = root.getNodesByPath("gopher-data/supported-browser");
		
		System.out.println ("Read "+suiteNodes.size()+" suite,\n"+
				testSuiteNodes.size()+" test-suite,\n"+
				testClassNodes.size()+" test-class,\n"+
				testCaseNodes.size()+" test-case,\n"+
				testDataTypeNodes.size()+" test-data-type,\n"+
				testDataNodes.size()+" test-data,\n"+
				elementLocatorNodes.size()+" element-locator,\n"+
				suiteDecoratorNodes.size()+" suite-decorator,\n"+
				testSuiteDecoratorNodes.size()+" test-suite-decorator,\n"+
				testClassDecoratorNodes.size()+" test-class-decorator,\n"+
				testRunNodes.size()+" test-run,\n"+
				browserNodes.size()+" supported-browser,\n"+
				testNodeNodes.size()+" test-node.");

		if (doReport) {
			createReport ();
		}

		if (readOnly) {
			return;
		}

		Transaction tran = session.beginTransaction();

		// Ingest all the test data types.
		for (HierarchyNode testDataTypeNode : testDataTypeNodes) {

			String idReq = (String) testDataTypeNode.getNodeByName ("id").getValue();
			String name = (String) testDataTypeNode.getNodeByName ("name").getValue();
			String type = (String) testDataTypeNode.getNodeByName ("type").getValue();
			String role = (String) testDataTypeNode.getNodeByName ("role").getValue();
			DataTypeEnum typeEnum = DataTypeEnum.valueOf (type);
			DataRoleEnum roleEnum = DataRoleEnum.valueOf (role);
			TestDataType testDataType = new TestDataType (name, typeEnum, roleEnum);
			session.save(testDataType);

			testDataTypeMap.put(idReq, testDataType);
		}

		// Ingest all the element locators.
		for (HierarchyNode elementLocatorNode : elementLocatorNodes) {

			String name = (String) elementLocatorNode.getNodeByName ("name").getValue();
		    String value = (String) elementLocatorNode.getNodeByName ("value").getValue();
		    String description = (String) elementLocatorNode.getNodeByName ("description").getValue();
		    String locatorString = (String) elementLocatorNode.getNodeByName ("locator-type").getValue();
		    ElementLocatorType locatorType = ElementLocatorType.valueOf(locatorString);

		    ElementLocator locator = new ElementLocator (locatorType, name, value, description);
			session.save (locator);
			String idReq = (String) elementLocatorNode.getNodeByName("id").getValue();
			elementLocatorMap.put(idReq, locator);
		}

		// Ingest all the test data.
		for (HierarchyNode testDataNode : testDataNodes) {

			String testDataTypeIdReq = (String) testDataNode.getNodeByName ("test-data-type-id").getValue();
			String value = (String) testDataNode.getNodeByName ("value").getValue();
			TestDataType testDataType = testDataTypeMap.get(testDataTypeIdReq);

			TestData testData = new TestData (testDataType, value);
			session.save (testData);
			String idReq = (String) testDataNode.getNodeByName("id").getValue();
			testDataMap.put(idReq, testData);
		}

		// Ingest all the test classes.
		for (HierarchyNode testClassNode : testClassNodes) {

			String name = (String) testClassNode.getNodeByName ("name").getValue();
			String version = (String) testClassNode.getNodeByName ("version").getValue();
			String className = (String) testClassNode.getNodeByName ("class-name").getValue();
			String desc = (String) testClassNode.getNodeByName ("description").getValue();
			String jiraIssue = (String) testClassNode.getNodeByName ("jira-issue").getValue();
			boolean runmode = testClassNode.getNodeByName ("runmode").getValueAsBoolean();

			TestClass testClass = gopherData.createTestClass (name, version, className, desc, runmode,
				jiraIssue);

			String testClassIdReq = (String) testClassNode.getNodeByName("id").getValue();
			if (testClassIdReq == null) {
				throw new Exception ("Test case node has missing id.");
			}
			testClassMap.put(testClassIdReq, testClass);

			Collection<HierarchyNode> testDataTypeIdNodes = testClassNode.getNodesByName (
				"test-data-type-id");

			List<TestDataType> testDataTypes =  getTestDataTypes (testDataTypeIdNodes);
			testClass.setTestDataTypes (testDataTypes);
			session.update (testClass);
		}

		// Ingest all the test suites.
		for (HierarchyNode testSuiteNode : testSuiteNodes) {
			String idReq = (String) testSuiteNode.getNodeByName ("id").getValue();
			String name = (String) testSuiteNode.getNodeByName ("name").getValue();
			String version = (String) testSuiteNode.getNodeByName ("version").getValue();
			String desc = (String) testSuiteNode.getNodeByName ("description").getValue();
			boolean runmode = testSuiteNode.getNodeByName ("runmode").getValueAsBoolean();
			TestSuite testSuite = gopherData.createTestSuite (name, version, desc, runmode, null);
			testSuiteMap.put(idReq, testSuite);

			Collection<HierarchyNode> testClassIdNodes = testSuiteNode.getNodesByName (
				"test-class-id");

			List<TestClass> testClasses = findTestClassesByReqId (testClassIdNodes);
			testSuite.setTestClasses (testClasses);
			session.update (testSuite);
		}

		// Ingest all the suites.
		for (HierarchyNode suiteNode : suiteNodes) {
			String idReq = (String) suiteNode.getNodeByName ("id").getValue();
			String name = (String) suiteNode.getNodeByName ("name").getValue();
			String version = (String) suiteNode.getNodeByName ("version").getValue();
			String desc = (String) suiteNode.getNodeByName ("description").getValue();
			boolean runmode = suiteNode.getNodeByName ("runmode").getValueAsBoolean();
			Suite suite = gopherData.createSuite (name, version, desc, runmode, null);
			suiteMap.put(idReq, suite);

			Collection<HierarchyNode> testSuiteIdNodes = suiteNode.getNodesByName (
				"test-suite-id");

			List<TestSuite> testSuites = findTestSuitesByReqId (testSuiteIdNodes);
			suite.setTestSuites (testSuites);
			session.update (suite);
		}

		// Ingest all the test cases.
		for (HierarchyNode testCaseNode : testCaseNodes) {

			String idReq = (String) testCaseNode.getNodeByName ("id").getValue();
			String testClassIdReq = (String) testCaseNode.getNodeByName ("test-class-id").getValue();
			String name = (String) testCaseNode.getNodeByName ("name").getValue();
			String version = (String) testCaseNode.getNodeByName ("version").getValue();
			boolean runmode = testCaseNode.getNodeByName ("runmode").getValueAsBoolean();

			TestClass testClass = testClassMap.get (testClassIdReq);
			if (testClass == null) {
				throw new Exception ("TestClass not found for requested id "+testClassIdReq+".");
			}
			TestCase testCase = gopherData.createTestCase (name, version, runmode, testClass, null);
			testCaseMap.put(idReq, testCase);

			Collection<HierarchyNode> testDataIdNodes = testCaseNode.getNodesByName (
				"test-data-id");

			List<TestData> testData = getTestData (testDataIdNodes);
			testCase.setTestData (testData);

			Collection<HierarchyNode> elementLocatorIdNodes = testCaseNode.getNodesByName (
					"element-locator-id");

			List<ElementLocator> elementLocators = getElementLocators (elementLocatorIdNodes);
			testCase.setElementLocators (elementLocators);

			session.update (testCase);
		}

		// Ingest all the support browsers.
		for (HierarchyNode browserNode : browserNodes) {

			String idReq = (String) browserNode.getNodeByName ("id").getValue();
			String name = (String) browserNode.getNodeByName ("name").getValue();
			String type = (String) browserNode.getNodeByName ("browser-type").getValue();
			BrowserEnum browserEnum = BrowserEnum.valueOf(type);

			Browser browser = gopherData.createBrowser(name, browserEnum);
			browserMap.put(idReq, browser);
		}

		// Ingest all the test nodes.
		for (HierarchyNode testNodeNode : testNodeNodes) {

			String idReq = (String) testNodeNode.getNodeByName ("id").getValue();
			String platform = (String) testNodeNode.getNodeByName ("platform").getValue();
			PlatformEnum platformEnum = PlatformEnum.valueOf(platform);
			HierarchyNode nodeIPReqNode = testNodeNode.getNodeByName ("node-ip");
			// If the node ip is null default to localhost.
			String nodeIP = null;
			if (nodeIPReqNode == null) {
		    	System.out.println("Trying to determine localhost IP to use as test node IP.");
		    	InetAddress addr=InetAddress.getLocalHost();
		    	nodeIP = addr.getHostAddress();
		    	System.out.println("Using "+nodeIP+" as test node address.");
			} else {
				nodeIP = (String) nodeIPReqNode.getValue();
			}
			String nodePort = (String) testNodeNode.getNodeByName ("node-port").getValue();
			String userName = (String) testNodeNode.getNodeByName ("user-name").getValue();
			String password = null;
			HierarchyNode passwordReq = testNodeNode.getNodeByName ("password");
			if (passwordReq != null) {
				password = (String) passwordReq.getValue();
			}
			String installDir = (String) testNodeNode.getNodeByName ("install-dir").getValue();
			String seleniumServer = (String) testNodeNode.getNodeByName ("selenium-server").getValue();

			TestNode testNode = gopherData.createTestNode(platformEnum, nodeIP, nodePort,
					userName,password,installDir,seleniumServer);

			Collection<HierarchyNode> browserIdNodes = testNodeNode.getNodesByName (
					"supported-browser-id");
			List<Browser> browsers = findBrowsersByReqId (browserIdNodes);
			testNode.setSupportedBrowsers (browsers);

			session.update (testNode);
			testNodeMap.put(idReq, testNode);
		}

		// Ingest all the test runs.
		for (HierarchyNode testRunNode : testRunNodes) {

			// The id is required.
			String testCaseIdReq = (String) testRunNode.getNodeByName ("test-case-id").getValue();

			// The test node is optional so test classes other than Selenium test classes may run.
			HierarchyNode testNodeIdReqNode = testRunNode.getNodeByName ("test-node-id");
			String testNodeIdReq = null;
			if (testNodeIdReqNode != null) {
				testNodeIdReq = (String)testNodeIdReqNode.getValue();
			}

			// The url and browser are optional.
			HierarchyNode urlNode = testRunNode.getNodeByName ("url");
			String url = null;
			if (urlNode != null) {
				url = (String)urlNode.getValue();
			}

			HierarchyNode browserNode = testRunNode.getNodeByName ("browser");
			BrowserEnum browserEnum = null;
			String browser = null;
			if (browserNode != null) {
				browser = (String) browserNode.getValue();
				browserEnum = BrowserEnum.valueOf(browser);
			}

			boolean runmode = testRunNode.getNodeByName ("runmode").getValueAsBoolean();

			TestCase testCase = testCaseMap.get(testCaseIdReq);
			TestNode testNode = testNodeMap.get(testNodeIdReq);
			TestRun testRun = gopherData.createTestRun(url, browserEnum, runmode, testCase, testNode);
			session.update (testRun);
		}

		// Ingest all the suite-decorators.
		for (HierarchyNode suiteDecoratorNode : suiteDecoratorNodes) {

			String suiteIdReq = (String) suiteDecoratorNode.getNodeByName ("suite-id").getValue();
			String name = (String) suiteDecoratorNode.getNodeByName ("name").getValue();

			// The url and browser are optional.
			HierarchyNode urlNode = suiteDecoratorNode.getNodeByName ("url");
			String url = null;
			if (urlNode != null) {
				url = (String)urlNode.getValue();
			}

			HierarchyNode browserNode = suiteDecoratorNode.getNodeByName ("browser");
			BrowserEnum browserEnum = null;
			String browser = null;
			if (browserNode != null) {
				browser = (String) browserNode.getValue();
				browserEnum = BrowserEnum.valueOf(browser);
			}

			boolean runmode = suiteDecoratorNode.getNodeByName ("runmode").getValueAsBoolean();

			Suite suite = suiteMap.get(suiteIdReq);
			SuiteDecorator suiteDecorator = gopherData.createSuiteDecorator(name, url, browserEnum, runmode, suite);
			session.update (suiteDecorator);
		}

		// Ingest all the test-suite-runs.
		for (HierarchyNode testSuiteDecoratorNode : testSuiteDecoratorNodes) {

			String suiteIdReq = (String) testSuiteDecoratorNode.getNodeByName ("suite-id").getValue();
			String testSuiteIdReq = (String) testSuiteDecoratorNode.getNodeByName ("test-suite-id").getValue();
			String url = (String) testSuiteDecoratorNode.getNodeByName ("url").getValue();
			String browser = (String) testSuiteDecoratorNode.getNodeByName ("browser").getValue();
			BrowserEnum browserEnum = BrowserEnum.valueOf(browser);
			boolean runmode = testSuiteDecoratorNode.getNodeByName ("runmode").getValueAsBoolean();

			Suite suite = suiteMap.get(suiteIdReq);
			TestSuite testSuite = testSuiteMap.get(testSuiteIdReq);
			TestSuiteDecorator testSuiteDecorator = gopherData.createTestSuiteDecorator(url, browserEnum, runmode, suite, testSuite);
			session.update (testSuiteDecorator);
		}

		// Ingest all the test-class-runs.
		for (HierarchyNode testClassDecoratorNode : testClassDecoratorNodes) {

			String testClassIdReq = (String) testClassDecoratorNode.getNodeByName ("test-class-id").getValue();
			String testSuiteIdReq = (String) testClassDecoratorNode.getNodeByName ("test-suite-id").getValue();
			HierarchyNode urlNode = testClassDecoratorNode.getNodeByName ("url");
			String url = null;
			if (urlNode != null) {
				url = (String) urlNode.getValue();
			}
			HierarchyNode browserNode = testClassDecoratorNode.getNodeByName ("browser");
			BrowserEnum browserEnum = null;
			if (browserNode != null) {
				String browser = (String) browserNode.getValue();
				browserEnum = BrowserEnum.valueOf(browser);
			}
			boolean runmode = testClassDecoratorNode.getNodeByName ("runmode").getValueAsBoolean();

			TestSuite testSuite = testSuiteMap.get(testSuiteIdReq);
			TestClass testClass = testClassMap.get(testClassIdReq);
			TestClassDecorator testClassDecorator = gopherData.createTestClassDecorator(url, browserEnum, runmode,
				testSuite, testClass);
			session.update (testClassDecorator);
		}

		createTestRuns ();

		tran.commit();
		System.out.println ("Ingest ok");
	}

	/**
	 * Create TestRuns based on all TestNodes and their supported Browsers.
	 * @throws Exception	on any error
	 */
	private void createTestRuns () throws Exception {

		List<TestCase> testCases = gopherData.findTestCases();
		List<TestNode> testNodes = gopherData.findTestNodes();

		for (TestCase testCase : testCases) {
			if (testCase.getName().equals("ShutdownGrid") ||
				testCase.getName().equals("StartupGrid")) {
				continue;
			}
			for (TestNode testNode : testNodes) {
				for (Browser browser : testNode.getSupportedBrowsers()) {
					gopherData.createTestRun(null, browser.getType(), true, testCase, testNode);
				}
			}
		}
	}

	private List<Browser> findBrowsersByReqId (Collection<HierarchyNode> browserIdNodes) {
		List<Browser> browsers = new ArrayList<Browser>();
		for (HierarchyNode node : browserIdNodes) {
			String reqId = (String) node.getValue();
			browsers.add (browserMap.get(reqId));
		}
		return browsers;
	}

	private List<TestSuite> findTestSuitesByReqId (Collection<HierarchyNode> testSuiteIdNodes) {
		List<TestSuite> testSuites = new ArrayList<TestSuite>();
		for (HierarchyNode node : testSuiteIdNodes) {
			String reqTestSuiteId = (String) node.getValue();
			testSuites.add (testSuiteMap.get(reqTestSuiteId));
		}
		return testSuites;
	}

	private HierarchyNode findTestSuiteNodeById(String testSuiteIdReq) {
		for (HierarchyNode node : testSuiteNodes) {
			String idReq = (String) node.getNodeByName ("id").getValue();
			if (testSuiteIdReq.equals(idReq)) {
				return node;
			}
		}
		return null;
	}
	
	private List<TestClass> findTestClassesByReqId (Collection<HierarchyNode> testClassIdNodes) {
		List<TestClass> testClasses = new ArrayList<TestClass>();
		for (HierarchyNode node : testClassIdNodes) {
			String reqTestClassId = (String) node.getValue();
			testClasses.add (testClassMap.get(reqTestClassId));
		}
		return testClasses;
	}

	private List<TestDataType> getTestDataTypes (Collection<HierarchyNode> testDataTypeIdNodes) throws Exception {

	    List<TestDataType> testDataTypes =  new ArrayList<TestDataType>();

		for (HierarchyNode testDataTypeIdNode : testDataTypeIdNodes) {
			String testDataTypeIdReq = (String) testDataTypeIdNode.getValue();

			testDataTypes.add(testDataTypeMap.get(testDataTypeIdReq));
		}
		return testDataTypes;
	}

	private List<TestData> getTestData (Collection<HierarchyNode> testDataIdNodes) throws Exception {

	    List<TestData> testDataSet =  new ArrayList<TestData>();
		for (HierarchyNode testDataIdNode : testDataIdNodes) {
			String testDataIdReq = (String)testDataIdNode.getValue();

			testDataSet.add(testDataMap.get(testDataIdReq));
		}
		return testDataSet;
	}

	/**
	 * Get the ElementLocators from the elementLocatorMap for the given element-locator-id elements from the
	 * model XML.
	 *
	 * @param elementLocatorIdNodes		the element-locator-id XML elements
	 * @return							the ElementLocators
	 * @throws Exception				on any error
	 */
	private List<ElementLocator> getElementLocators (Collection<HierarchyNode> elementLocatorIdNodes) throws Exception {

	    List<ElementLocator> elementLocators =  new ArrayList<ElementLocator>();
		for (HierarchyNode elementLocatorIdNode : elementLocatorIdNodes) {
			String elementLocatorIdReq = (String) elementLocatorIdNode.getValue();
			elementLocators.add(elementLocatorMap.get(elementLocatorIdReq));
		}
		return elementLocators;
	}
	
	private void createReport () throws Exception {
		createSuitesPage ();
	}

	private void createSuitesPage () throws Exception {

		File f = new File ("test-data/views/suites.html");
		FileWriter writer = new FileWriter (f);

		String header = "<html><head></head><body>\n"+
			"<table>\n";
		writer.write(header, 0, header.length());
		
		for (HierarchyNode suiteNode : suiteNodes) {
			String beginRow = "<tr>\n";
			writer.write(beginRow, 0, beginRow.length());

			String id = (String) suiteNode.getNodeByName ("id").getValue();
			String td = "<td><a href=\"suite-"+id+".html\">"+id+"</a></td>\n";
			writer.write(td, 0, td.length());

			String name = (String) suiteNode.getNodeByName ("name").getValue();
			td = "<td><a href=\"suite-"+id+".html\">"+name+"</a></td>\n";
			writer.write(td, 0, td.length());

			String version = (String) suiteNode.getNodeByName ("version").getValue();
			td = "<td>"+version+"</td>\n";
			writer.write(td, 0, td.length());

			String endRow = "</tr>\n";
			writer.write(endRow, 0, endRow.length());

			createSuitePage (suiteNode);
		}

		String trailer = "</table></body></html>";
		writer.write(trailer, 0, trailer.length());
		writer.flush();
		writer.close();
	}

	private void createSuitePage (HierarchyNode suiteNode) throws Exception {

		String id = (String) suiteNode.getNodeByName ("id").getValue();

		File f = new File ("test-data/views/suite-"+id+".html");
		FileWriter writer = new FileWriter (f);

		String suiteName = (String) suiteNode.getNodeByName ("name").getValue();

		String header = "<html><head><title>"+suiteName+"</title></head><body>\n"+
				suiteName+"<p/>\n<ul>";
		writer.write(header, 0, header.length());
		
		Collection<HierarchyNode> testSuiteIdNodes = suiteNode.getNodesByName (
			"test-suite-id");

		for (HierarchyNode testSuiteIdNode : testSuiteIdNodes) {
			String testSuiteIdReq = (String) testSuiteIdNode.getValue();
			HierarchyNode testSuiteNode = findTestSuiteNodeById(testSuiteIdReq);
			String name = (String) testSuiteNode.getNodeByName ("name").getValue();
			String li = "<li>"+name+"</li>\n";
			writer.write(li, 0, li.length());
		}

		String trailer = "</ul></body></html>";
		writer.write(trailer, 0, trailer.length());
		writer.flush();
		writer.close();
	}

	/**
	 * Get the usage message.
	 * @return	the message
	 */
	public static String getUsage () {

		StringBuffer usage = new StringBuffer ();

		usage.append("Usage: GopherDataIngest [\'file1;file2\' or \'dir1;dir2\' or \'file1;dir1\'] [-dtd 'dtdFile']");
		usage.append("\nwhere \'file*\' is the path to a XML file containing Gopher data definitions.");
		usage.append("\nwhere \'dir*\' is the path to a directory containing Gopher data definitions files.");
		usage.append("\nwhere \'dtdFile\' is the name of an optional DTD file containing Gopher data XML schema.");
		usage.append("\nIf the DTD argument is not supplied, try to find 'gopher-data.dtd' in the CLASSPATH.");
		return usage.toString();
	}
}
