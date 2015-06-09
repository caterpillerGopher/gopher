package com.echostar.gopher.persist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * A TestClassDecorator relates a URL, browser, and runmode with a {@link TestClass TestClass}
 * in a {@link TestSuite TestSuite}.
 * 
 * If a SuiteDecorator is related to a {@link Suite Suite},
 * {@link TestSuiteDecorator TestSuiteRuns} need not be related to
 * {@link TestSuite TestSuites}.
 *
 * If a TestSuiteDecorator is related to a {@link TestSuite TestSuite},
 * {@link TestClassDecorator TestClassRuns} need not be related to
 * {@link TestClass TestClasses} and
 *
 * The values in the {@link TestRun TestRuns}
 * for the TestCase will be used if neither a TestClassDecorator, TestSuiteDecorator
 * or SuiteDecorator is defined.
 *
 * @author charles.young
 *
 */
@Entity
@Table(name="test_class_decorator")
public class TestClassDecorator {

    Long			id;
    String			url;
    BrowserEnum		browser;
    Boolean			runmode;
    TestSuite		testSuite;
    TestClass		testClass;

    public TestClassDecorator () {}
 
    /**
     * Construct with all member data.
     *
     * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		do run (true) or not (false)
     * @param testSuite		the TestSuite we are related to
     * @param testClass		the TestClass we are related to
     */
    public TestClassDecorator (String url, BrowserEnum browser, Boolean runmode,
    		TestSuite testSuite, TestClass testClass) {
    	this.url = url;
     	this.browser = browser;
    	this.runmode = runmode;
    	this.testSuite = testSuite;
    	this.testClass = testClass;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the url of the web page under test.
     * @return the url
     */
    @Column(name = "url")
    public String getUrl () {return url;}
    public void setUrl (String url) { this.url = url; }

    /**
     * Get the type of browser under test.
     * @return the browser type
     */
    @Column(name = "browser")
    @Enumerated(EnumType.STRING)
    public BrowserEnum getBrowser () {return browser;}
    public void setBrowser (BrowserEnum browser) { this.browser = browser; }

    /**
     * Get the runmode of this TestClass. If set to false, no instances will run.
     * @return	the run mode
     */
    @Column(name = "runmode")
    public Boolean getRunmode () {return runmode;}
    public void setRunmode (Boolean runmode) { this.runmode = runmode; }

    /**
     * Get the {@link TestSuite TestSuite} of this run.
     * @return the TestSuite
     */
	@OneToOne
	@JoinColumn(name = "test_suite_id")
	public TestSuite getTestSuite () {
    	return testSuite;
    }
    public void setTestSuite (TestSuite testSuite) {
    	this.testSuite = testSuite;
    }

    /**
     * Get the {@link TestClass TestClass} of this run.
     * @return the TestClass
     */
	@OneToOne
	@JoinColumn(name = "test_class_id")
    public TestClass getTestClass () {
    	return testClass;
    }
    public void setTestClass (TestClass testClass) {
    	this.testClass = testClass;
    }

    /**
	 * Override Object to show our id, and TestSuite name.
	 * @return	the String
	 */
	public String toString() {
		return getClass().getSimpleName()+"("+id+" '"+testSuite.getName()+"' "+",'"+url+"', '"+browser+"')";
	}

	/**
	 * Compare each member and if not equal, return a reason.
	 * @param	o	an Object for comparison
	 * @return	a reason or null if equal
	 */
	public String getNotEqualsReason (Object o) {

		String notEqualsReason = null;

	    if (!(o instanceof TestClassDecorator)) {
			notEqualsReason = "Object is not a TestClassDecorator.";
			return notEqualsReason;
		}
		TestClassDecorator s = (TestClassDecorator) o;
		if (!id.equals(s.getId())) {
			notEqualsReason = "Ids not equal";
			return notEqualsReason;			
		}
		if (!url.equals(s.getUrl())) {
			notEqualsReason = "Urls not equal";
			return notEqualsReason;			
		}
		if (!browser.equals(s.getBrowser())) {
			notEqualsReason = "Browsers not equal";
			return notEqualsReason;			
		}
		if (!(runmode == s.getRunmode())) {
			notEqualsReason = "Runmodes not equal";
			return notEqualsReason;			
		}
		if (!testSuite.equals(s.getTestSuite())) {
			notEqualsReason = "TestSuites not equal";
			return notEqualsReason;			
		}
		if (!testClass.equals(s.getTestClass())) {
			notEqualsReason = "TestClasses not equal";
			return notEqualsReason;			
		}
		return notEqualsReason;
	}
}