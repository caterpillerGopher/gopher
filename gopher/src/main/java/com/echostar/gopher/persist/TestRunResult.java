package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.echostar.gopher.util.Config;

/**
 * A test run result.
 * @author charles.young
 *
 */
@Entity
@Table(name="test_run_result")
public class TestRunResult {

    Long		id;
    Boolean		result;
    String		failureMessage;
    Date		startTime;
    Date		endTime;
    String		user;
    String      url;
    SuiteInstance		suiteInstance;
    TestSuiteInstance	testSuiteInstance;
    TestRun				testRun;
    List<TestException>	exceptions = new ArrayList<TestException>();

    public TestRunResult () {}

    /**
     * Construct with all member data.
     *
     * @param result			true (success) or false (failure)
     * @param failureMessage	a message describing the failure
     * @param startTime			the start time of the run
     * @param endTime			the end time of the run
     * @param user				the name of the user running the TestClass
     * @param url               the url used in the test
     * @param testRun			our TestRun
     * @param suiteInstance		our SuiteInstance
     * @param testSuiteInstance our TestSuiteInstance
     */
    public TestRunResult (Boolean result, String failureMessage, Date startTime, Date endTime, String user,
    	String url, TestRun testRun, SuiteInstance suiteInstance, TestSuiteInstance testSuiteInstance) {

    	this.result = result;

    	int maxMessageLen = Integer.parseInt(Config.getProperty_S ("Tables.test_run_result.failure_message.maxLen"));

    	if (failureMessage != null) {
 
    		if (failureMessage.length() > maxMessageLen) {
     			this.failureMessage = failureMessage.substring(0, maxMessageLen);    		
     		} else {
    			this.failureMessage = failureMessage;
    		}
    	} else {
    		this.failureMessage = "";
    	}
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.user = user;
    	this.url = url;
    	this.testRun = testRun;
    	this.testSuiteInstance = testSuiteInstance;
    	this.suiteInstance = suiteInstance;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the result of this run.
     * @return TRUE or FALSE for success or failure
     */
    @Column(name = "result")
    public Boolean getResult () {return result;}
    public void setResult (Boolean result) { this.result = result; }
    
    /**
     * Get the failure message for this run (if any.)
     * @return	the failure message
     */
    @Column(name = "failure_message")
    public String getFailureMessage () {return failureMessage;}

    public void setFailureMessage (String failureMessage) {
    	int maxMessageLen = Integer.parseInt(Config.getProperty_S("Tables.test_run_result.failure_message.maxLen"));
    	if (failureMessage != null) {
    		if (failureMessage.length() > maxMessageLen) {
    			this.failureMessage = failureMessage.substring(0, maxMessageLen);    		
    		} else {
    			this.failureMessage = failureMessage;
    		}
    	} else {
    		this.failureMessage = "";
    	}
    }

    /**
     * Get the run start time.
     * @return	the start time
     */
    @Column(name = "start_time")
    public Date getStartTime () {return startTime;}
    public void setStartTime (Date startTime) { this.startTime = startTime; }

    /**
     * Get the run end time.
     * @return	the end time
     */
    @Column(name = "end_time")
    public Date getEndTime () {return endTime;}
    public void setEndTime (Date endTime) { this.endTime = endTime; }

    /**
     * Get the name of the user who initiated the run.
     * @return the user name
     */
    @Column(name = "user")
    public String getUser () {return user;}
    public void setUser (String user) { this.user = user; }

    /**
     * Get the url used in the test.
     * @return the url
     */
    @Column(name = "url")
    public String getUrl () {return url;}
    public void setUrl (String url) { this.url = url; }

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "suite_instance_id")
	public SuiteInstance getSuiteInstance () {
    	return suiteInstance;
    }
    public void setSuiteInstance (SuiteInstance suiteInstance) {
    	this.suiteInstance = suiteInstance;
    }

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "test_suite_instance_id")
	public TestSuiteInstance getTestSuiteInstance () {
    	return testSuiteInstance;
    }
    public void setTestSuiteInstance (TestSuiteInstance testSuiteInstance) {
    	this.testSuiteInstance = testSuiteInstance;
    }

    /**
     * Get the {@link TestRun TestRun} of this run result.
     * @return the TestRun
     */
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinColumn(name = "test_run_id", nullable = false)
	public TestRun getTestRun() {
		return this.testRun;
	}
	public void setTestRun(TestRun testRun) {
		this.testRun = testRun;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "testRunResult")
	public List<TestException> getTestExceptions() {
		return exceptions;
	}
	public void setTestExceptions(List<TestException> exceptions) {
		this.exceptions = exceptions;
	}

    /**
     * Add a TestException to the list.
     * Use Hibernate Session to update the store when done adding.
     *
     * @param testException	the TestException to add
     */
    public void addTestException (TestException testException) {
    	if (exceptions == null)
    		exceptions = new ArrayList<TestException>();
    	exceptions.add(testException);
    }
}
