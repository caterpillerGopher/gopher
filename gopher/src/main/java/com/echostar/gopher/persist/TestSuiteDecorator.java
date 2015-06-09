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
 * A TestSuiteDecorator associates a URL, browser, and runmode
 * with a TestSuite in a Suite.
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
@Table(name="test_suite_decorator")
public class TestSuiteDecorator {

    Long			id;
    String			url;
    BrowserEnum		browser;
    Boolean			runmode;
    Suite			suite;
    TestSuite		testSuite;

    public TestSuiteDecorator () {}
 
    /**
     * Construct with all member data.
     *
     * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		do run (true) or not (false)
     * @param suite			the Suite we are related to
     * @param testSuite		the TestSuite we are related to
     */
    public TestSuiteDecorator (String url, BrowserEnum browser, Boolean runmode,
    	Suite suite, TestSuite testSuite) {
    	this.url = url;
     	this.browser = browser;
    	this.runmode = runmode;
    	this.suite = suite;
    	this.testSuite = testSuite;
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
     * Get the runmode. If set to false, the suite will not run.
     * @return	the run mode
     */
    @Column(name = "runmode")
    public Boolean getRunmode () {return runmode;}
    public void setRunmode (Boolean runmode) { this.runmode = runmode; }

    /**
     * Get the {@link Suite Suite} of this run.
     * @return the Suite
     */
	@OneToOne
	@JoinColumn(name = "suite_id")
	public Suite getSuite () {
    	return suite;
    }
    public void setSuite (Suite suite) {
    	this.suite = suite;
    }
    
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
	 * Override Object to show our id, and TestSuite name.
	 * @return	the String
	 */
	public String toString() {
		return getClass().getSimpleName()+"("+id+" '"+suite.getName()+"' "+",'"+url+"', '"+browser+"')";
	}
}