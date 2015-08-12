package com.echostar.gopher.persist;

import org.junit.BeforeClass;
import org.testng.annotations.Parameters;

import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;

/**
 * A base class for persistence unit test classes.
 * This base is not for production test classes.
 * 
 * @author charles.young
 *
 */
public class TestClassBase {

	// Specific test case name (if any) supplied as parameter by TestNG through suite xml.
	protected String testCaseName;

	@Parameters({ "test-case-name"})
	@BeforeClass
	public void beforeTest(String testCaseName) {
	  this.testCaseName = testCaseName;
	}

	/**
	 * Do before test method actions - call {@link #mockData() mockData}.
	 * @throws Exception	on any error
	 */
	//@BeforeMethod
	//public void beforeMethod () throws Exception {
		//cleanDB ();
	//	mockData ();
	//}

	/**
	 * Do after test method actions - call {@link #cleanDB() cleanDB}.
	 * @throws Exception	on any error
	 */
	//@AfterMethod
	//public void afterMethod () throws Exception {		
	//	cleanDB ();
	//}

	/**
	 * Clean the DB. If the TestClasses needs to do something else, override this method.
	 * @throws Exception	on any error
	 */
	public static void cleanDB () throws Exception {

		GopherData gopherData = GopherDataFactory.getGopherData();
		gopherData.cleanDB();
	}

	/**
	 * Mock the data as needed for the test.
	 * Subclasses should over-ride this method.
	 * @throws Exception	on any error
	 */
	//public void mockData () throws Exception {
	//	Logger log = Logger.getLogger(getClass());
	//	log.warn("mockData called, possibly not overridden.");
	//}
}