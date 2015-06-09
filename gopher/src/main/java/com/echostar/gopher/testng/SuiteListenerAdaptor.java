package com.echostar.gopher.testng;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.Suite;
import com.echostar.gopher.persist.SuiteInstance;
import com.echostar.gopher.util.ExceptionUtil;

/**
 * Listen to Suite level events from TestNG.
 * @author greg
 *
 */
public class SuiteListenerAdaptor implements ISuiteListener {

	private Logger log = Logger.getLogger (getClass().getName());

	// Our SuiteInstance if any. Currently, the only way to know if we are running
	// in a Suite (as opposed to a TestSuite) is to set a parameter 'suiteName' in the suite XML file.
	// ISuiteListener is invoked after all the test methods have finished,
	// making it impossible to know the suite before test methods.
	// Hence, the need for a work-around.
	// Parallel suites are not supported.
	private static Long	suiteInstanceId = null;

	public static SuiteInstance createSuiteInstance(String suiteName, String suiteVersion,
		Logger log) throws Exception {

		Assert.assertNotNull(suiteName);
		Assert.assertNotNull(suiteVersion);

		GopherData gopherData = null;
		Transaction tran = null;
		SuiteInstance suiteInstance = null;
		try {
			gopherData =GopherDataFactory.getGopherData();
			if (suiteInstanceId != null) {
				log.trace("Getting SuiteInstance.");
				return getSuiteInstance(gopherData);
			}
            log.trace("Finding Suite with name '"+suiteName+"'.");
			Suite suite = gopherData.findSuiteByNameAndVersion (suiteName, suiteVersion);
	        log.trace("Found Suite.");
			log.trace("Creating SuiteInstance for Suite '"+suite.getName()+"'.");
			Session hibernateSession = gopherData.getHibernateSession();
			tran = hibernateSession.beginTransaction();
			suiteInstance = gopherData.createSuiteInstance(suite);
			suiteInstanceId = suiteInstance.getId();
			tran.commit();
			log.trace("Created SuiteInstance for Suite '"+suite.getName()+"'.");
		} finally {
			if (tran != null && !tran.wasCommitted()) {
				tran.rollback();
			}
			if (gopherData != null) {
				gopherData.close();
			}
		}
		return suiteInstance;
	}

	public static SuiteInstance getSuiteInstance(GopherData gopherData)
		throws Exception {

		if (suiteInstanceId == null) {
			return null;
		}

		SuiteInstance suiteInstance = gopherData.findSuiteInstanceById (suiteInstanceId);
		return suiteInstance;
	}

	public static void clearSuiteInstance () {
		suiteInstanceId = null;
	}
	public void onStart(ISuite suite) {
		log.info ("Suite start "+suite.getName());
	}

	/**
	 * Determine when the Suite (as opposed to TestSuite)
	 * has ended and update the end time.
	 */
	public void onFinish(ISuite isuite) {
		log.info ("Suite onFinish ISuite name '"+isuite.getName()+"'.");

		GopherData gopherData = null;
		Transaction tran = null;

		try {
			gopherData = GopherDataFactory.getGopherData();

			SuiteInstance suiteInstance = gopherData.findSuiteInstanceById(suiteInstanceId);

			if (suiteInstance != null && isuite.getName().equals(suiteInstance.getSuite().getName())) {
				suiteInstance.setEndTime(new Date());
				Session hibernateSession=gopherData.getHibernateSession();
				tran=hibernateSession.beginTransaction();
				log.debug("Setting SuiteInstance end time.");
				hibernateSession.update(suiteInstance);
				tran.commit();
				hibernateSession.flush();
			}
		} catch (Exception e) {			
			log.error(ExceptionUtil.getStackTraceString(e, 10000));
		} finally {
			if (tran != null && !tran.wasCommitted()) {
				log.debug("Rolling back transaction.");
				tran.rollback();
			}
			if (gopherData != null) {
				log.debug("Closing gopherData.");
				gopherData.close();
			}
			log.debug("Leaving onFinish ISuite name'"+isuite.getName()+"'.");
		}
	}
}
