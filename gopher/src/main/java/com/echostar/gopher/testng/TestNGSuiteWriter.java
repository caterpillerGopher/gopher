package com.echostar.gopher.testng;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.Suite;
import com.echostar.gopher.persist.SuiteDecorator;
import com.echostar.gopher.persist.TestSuiteDecorator;
import com.echostar.gopher.persist.TestClass;
import com.echostar.gopher.persist.TestSuite;
import com.echostar.gopher.persist.TestClassDecorator;

/**
 * Create the suite XML files needed by TestNG.
 * We create a file for every
 * {@link com.echostar.gopher.persist.Suite Suite} and
 * {@link com.echostar.gopher.persist.TestSuite TestSuite} in the database.
 *
 * @author charles.young
 *
 */
public class TestNGSuiteWriter {

	private GopherData gopherData;
	// Path to the dir to contain the generated suite files
	private String     suiteDirPath = "src/test/resources/suites";

	/**
	 * Create the suites directory if it does not exist and get the GopherData
	 * interface from GopherDFataFactory.
	 * 
	 * @param suiteDirPath	path to directory to contain the generated suite files
	 * @throws IOException
	 */
	public TestNGSuiteWriter (String suiteDirPath) throws IOException {
	    if (suiteDirPath != null) {
	        this.suiteDirPath = suiteDirPath;
	    }
	    File suiteDir = new File (this.suiteDirPath);
	    if (!suiteDir.exists()) {
	    	suiteDir.mkdir();
	    }
		gopherData = GopherDataFactory.getGopherData();
	}

	/**
	 * Write a TestNG suite file for every {@link com.echostar.gopher.persist.Suite Suite} in the database.
	 * @throws Exception	on any error
	 */
	public void writeSuiteFiles () throws Exception {
	    List<Suite> suites = gopherData.findAllSuites();
		for (Suite suite : suites) {
		    List<SuiteDecorator> suiteDecorators = suite.getSuiteDecorators ();
		    if (suiteDecorators.size() > 0) {
		    	for (SuiteDecorator suiteDecorator : suiteDecorators) {
		    		writeSuiteFile (suite, suiteDecorator);
		    	}
		    }
		    else {
	    		writeSuiteFile (suite, null);		    	
		    }
		}
	}
	
	/**
	 * Write a TestNG test suite file for every {@link com.echostar.gopher.persist.TestSuite TestSuite} in the database.
	 * @throws Exception	on any error
	 */
	public void writeTestSuiteFiles () throws Exception {
	    List<TestSuite> testSuites = gopherData.findAllTestSuites();
		for (TestSuite testSuite : testSuites) {
			writeTestSuiteFile (testSuite);
		}
	}
	
	/**
	 * Write a TestNG test suite file for the given test suite.
	 * The file name is "test-data/suites/" plus the {@link com.echostar.gopher.persist.TestSuite TestSuite} name
	 * plus ".xml".
	 * @param testSuite		the TestSuite
	 * @throws Exception	on any error
	 */
	public void writeTestSuiteFile (TestSuite testSuite) throws Exception {
		List<TestClass> testClasses = testSuite.getTestClasses ();

		File f = new File (suiteDirPath+"/"+testSuite.getName()+".xml");
		System.out.println ("TestSuite \""+f.getPath()+"\"");
		FileWriter writer = new FileWriter (f);

		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n\n"+
				"<suite name=\""+testSuite.getName()+"\">\n";
				//"<suite name=\""+testSuite.getName()+"\" parallel=\"tests\" thread-count=\"2\">\n";

		writer.write(header, 0, header.length());
		
		for (TestClass testClass : testClasses) {
			String listeners = "	<listeners>\n"+
					"		<listener class-name=\"com.echostar.gopher.testng.TestsListenerAdapter\"/>\n"+
					"	</listeners>\n";

			writer.write(listeners, 0, listeners.length());

			TestClassDecorator testClassDecorator = gopherData.findTestClassDecorator (testSuite, testClass);
			if (testClassDecorator != null) {
				String url = testClassDecorator.getUrl();
				if (url != null) {
					String p =
					"	<parameter name=\"url\"  value=\""+url+"\"/>\n";
					writer.write(p, 0, p.length());
				}
				BrowserEnum browser = testClassDecorator.getBrowser();
				if (browser != null) {
					String p =
					"	<parameter name=\"browser\"  value=\""+browser+"\"/>\n";
					writer.write(p, 0, p.length());
				}
			}
			String testElement =
			"	<test name=\""+testClass.getName()+"\">\n"+
		        "		<classes>\n"+
		            "			<class name=\""+testClass.getClassName()+"\"></class>\n"+
		        "		</classes>\n"+
		    "	</test>\n"; 
			writer.write(testElement, 0, testElement.length());
		}

		String trailer = "</suite>";
		writer.write(trailer, 0, trailer.length());
		writer.flush();
		writer.close();
	}

	/**
	 * Write a TestNG suite file for the given suite.
	 * The file name is "test-data/suites/" plus the {@link com.echostar.gopher.persist.Suite Suite} name
	 * plus ".xml".
	 * @param suite			the Suite
	 * @param suiteDecorator		the SuiteDecorator if any
	 * @throws Exception	on any error
	 */
	public void writeSuiteFile (Suite suite, SuiteDecorator suiteDecorator) throws Exception {

		String suiteName = suite.getName();
		String suiteVersion = suite.getVersion();

		File f = null;
		if (suiteDecorator != null) {
			f = new File (suiteDirPath+"/"+suiteName+"_"+suiteDecorator.getName()+".xml");
		} else {
			f = new File (suiteDirPath+"/"+suiteName+".xml");			
		}
		System.out.println ("Suite \""+f.getPath()+"\"");
		FileWriter writer = new FileWriter (f);

		List<TestSuite> testSuites = suite.getTestSuites();

		// Find the grid startup and shutdown.
		TestSuite startupTestSuite = null;
		TestSuite shutdownTestSuite = null;
		for (TestSuite testSuite : testSuites) {
			if (testSuite.getName().equals("StartupGrid_Suite")) {
				startupTestSuite = testSuite;
			} else if (testSuite.getName().equals("ShutdownGrid_Suite")) {
				shutdownTestSuite = testSuite;
			}
		}
		// If there is a shutdown test, remove it from the set and write it last.
		if (shutdownTestSuite != null) {
			testSuites.remove(shutdownTestSuite);
		}

		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n\n"+
				"<suite name=\""+suiteName+"\">\n\n";

		writer.write(header, 0, header.length());

		String listeners = "	<listeners>\n"+
				"		<listener class-name=\"com.echostar.gopher.testng.TestsListenerAdapter\"/>\n"+
				"	</listeners>\n";

		writer.write(listeners, 0, listeners.length());

		String suiteFilesElement = "	<suite-files>\n";
		writer.write(suiteFilesElement, 0, suiteFilesElement.length());

		// If there is a startup test, remove it from the set and write it first.
		if (startupTestSuite != null) {
			testSuites.remove(startupTestSuite);
			String testSuiteElement =
			"		<suite-file path=\""+startupTestSuite.getName()+".xml\"/>\n";
			writer.write(testSuiteElement, 0, testSuiteElement.length());
		}

		// Set url and browser to values in a SuiteDecorator, if any.
		String url = null;
		BrowserEnum browser = null;

		if (suiteDecorator != null) {
			url = suiteDecorator.getUrl();
			browser =suiteDecorator.getBrowser();
		}

		Map<Long, TestSuiteDecorator> testSuiteDecoratorMap = new HashMap<Long,TestSuiteDecorator>();

		boolean first = true;
		for (TestSuite testSuite : testSuites) {		
			List<TestSuiteDecorator> testSuiteDecorators = gopherData.findTestSuiteDecorators (suite, testSuite);
			if (testSuiteDecorators.size() > 0) {
				TestSuiteDecorator testSuiteDecorator = null;
				for (TestSuiteDecorator testSuiteDecorator_ : testSuiteDecorators) {
					if (testSuiteDecoratorMap.get(testSuiteDecorator_.getId()) != null) {
						continue;
					}
					testSuiteDecoratorMap.put(testSuiteDecorator_.getId(), testSuiteDecorator_);
					testSuiteDecorator = testSuiteDecorator_;
					break;
				}
				if (testSuiteDecorator != null) {
					url = testSuiteDecorator.getUrl();
					browser = testSuiteDecorator.getBrowser();
				}
			}

			if (first) {
				first = false;
				if (url != null) {
					String p =
							"		<parameter name=\"url\"  value=\""+url+"\"/>\n";
					writer.write(p, 0, p.length());
				}

				if (browser != null) {
					String p =
							"		<parameter name=\"browser\"  value=\""+browser+"\"/>\n";
					writer.write(p, 0, p.length());
				}
				String p =
						"		<parameter name=\"suiteName\"  value=\""+suiteName+"\"/>\n";
				writer.write(p, 0, p.length());
				p =
						"		<parameter name=\"suiteVersion\"  value=\""+suiteVersion+"\"/>\n";
				writer.write(p, 0, p.length());
			}

			String testSuiteElement =
			"		<suite-file path=\""+testSuite.getName()+".xml\"/>\n";
			writer.write(testSuiteElement, 0, testSuiteElement.length());
		}

		// If there is a shutdown test, write it.
		if (shutdownTestSuite != null) {
			String testSuiteElement =
			"		<suite-file path=\""+shutdownTestSuite.getName()+".xml\"/>\n";
			writer.write(testSuiteElement, 0, testSuiteElement.length());
		}

		String trailer = "	</suite-files>\n\n</suite>";
		writer.write(trailer, 0, trailer.length());

		writer.flush();
		writer.close();
	}

	/**
	 * Write both the suite and test suite files.
	 * @throws Exception
	 */
	public void write () throws Exception {
		System.out.println ("Generating TestNG suite files...");
		writeSuiteFiles ();
		writeTestSuiteFiles ();
		System.out.println ("Done.");
	}

	/**
	 * Write both the suite and test suite files.
	 * @param args	arg[0] - the path to the directory to contain the files
	 */
	public static void main (String[] args) {
	    String dirPath = null;
	    if (args.length > 0) {
	        dirPath = args[0];
	    }
		try {
			TestNGSuiteWriter t = new TestNGSuiteWriter (dirPath);
			t.write();
		} catch (Exception e) {
			e.printStackTrace ();
		}
		System.exit(0);
	}
}
