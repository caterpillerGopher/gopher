package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="test_suite_instance")
public class TestSuiteInstance {
    Long		id;
    Date		startTime;
    Date		endTime;
    TestSuite	testSuite;
    SuiteInstance suiteInstance;
    List<TestRunResult> testRunResults = new ArrayList<TestRunResult>();

    public TestSuiteInstance () {}
    public TestSuiteInstance (Date startTime, Date endTime,
    	TestSuite	testSuite, SuiteInstance suiteInstance) {
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.testSuite = testSuite;
    	this.suiteInstance = suiteInstance;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    @OneToOne
	@JoinColumn(name = "test_suite_id")
	public TestSuite getTestSuite () {
    	return testSuite;
    }
    public void setTestSuite (TestSuite testSuite) {
    	this.testSuite = testSuite;
    }

    @OneToOne
	@JoinColumn(name = "suite_instance_id")
	public SuiteInstance getSuiteInstance () {
    	return suiteInstance;
    }
    public void setSuiteInstance (SuiteInstance suiteInstance) {
    	this.suiteInstance = suiteInstance;
    }

    //, cascade = {CascadeType.ALL}
    /**
     * Get the set of {@link TestSuiteDecorator TestRunResults} for this suite.
     * @return the TestRunResults
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "testSuiteInstance")
    public List<TestRunResult> getTestRunResults () {
    	return testRunResults;
    }
    public void setTestRunResults (List<TestRunResult> testRunResults) {
    	this.testRunResults = testRunResults;
    }
    public void addTestRunResult (TestRunResult testRunResult) {
    	testRunResults.add(testRunResult);
    }
}