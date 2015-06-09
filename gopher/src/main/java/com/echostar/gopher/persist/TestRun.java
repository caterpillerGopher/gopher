package com.echostar.gopher.persist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A TestRun associates a URL, browser, runmode and TestNode, with a TestCase.
 * Each run should relate a TestRunResult.
 * @author charles.young
 *
 */
@Entity
@Table(name="test_run")
public class TestRun {

    Long			id;
    String			url;
    BrowserEnum		browser;
    Boolean			runmode;
    TestCase		testCase;
    TestNode		testNode;

    public TestRun () {}

    /**
     * Construct with all member data.
     *
     * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		do run (true) or not (false)
     * @param testCase		the test case we are related to
     * @param testNode		the test node we will run on
     */
    public TestRun (String url, BrowserEnum browser, Boolean runmode, TestCase testCase,
    	TestNode testNode) {
    	this.url = url;
     	this.browser = browser;
    	this.runmode = runmode;
    	this.testCase = testCase;
    	this.testNode = testNode;
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
     * Get the runmode of this TestRun. If set to false, no instances will run.
     * @return	the run mode
     */
    @Column(name = "runmode")
    public Boolean getRunmode () {return runmode;}
    public void setRunmode (Boolean runmode) { this.runmode = runmode; }

    /**
     * Get the {@link TestCase TestCase} of this run.
     * @return the TestCase
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "test_case_id", nullable = false)
    public TestCase getTestCase () {
    	return testCase;
    }
    public void setTestCase (TestCase testCase) {
    	this.testCase = testCase;
    }

    /**
     * Get the {@link TestNode TestNode} of this run.
     * @return the TestNode
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "test_node_id")
    public TestNode getTestNode () {
    	return testNode;
    }
    public void setTestNode (TestNode testNode) {
    	this.testNode = testNode;
    }

	/**
	 * Override Object to show our id, and TestClass name.
	 * @return	the String
	 */
	public String toString() {
		return getClass().getSimpleName()+" ("+id+",'"+url+"', '"+browser+"')"+", "+testNode;
	}

	/**
	 * Compare each member and if not equal, return a reason.
	 * @param	o	an Object for comparison
	 * @return	a reason or null if equal
	 */
	public String getNotEqualsReason (Object o) {

		String notEqualsReason = null;

		if (!(o instanceof TestRun)) {
			notEqualsReason = "Object is not a TestRun";
			return notEqualsReason;
		}
		TestRun s = (TestRun) o;
		if (!id.equals(s.getId())) {
			notEqualsReason = "Ids not equal";
			return notEqualsReason;			
		}

		return notEqualsReason;
	}
}