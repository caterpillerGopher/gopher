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
@Table(name="suite_instance")
public class SuiteInstance {
    Long		id;
    Suite		suite;
    Date		startTime;
    Date		endTime;
    List<TestRunResult> testRunResults = new ArrayList<TestRunResult>();
    List<TestSuiteInstance> testSuiteInstances = new ArrayList<TestSuiteInstance>();
 
    public SuiteInstance () {}
    public SuiteInstance (Date startTime, Date endTime, Suite	suite)
    {
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.suite = suite;
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
	@JoinColumn(name = "suite_id")
	public Suite getSuite () {
    	return suite;
    }
    public void setSuite (Suite suite) {
    	this.suite = suite;
    }

    //, cascade = {CascadeType.ALL}
    /**
     * Get the set of {@link TestSuiteDecorator TestRunResults} for this suite.
     * @return the TestRunResults
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "suiteInstance")
    public List<TestRunResult> getTestRunResults () {
    	return testRunResults;
    }
    public void setTestRunResults (List<TestRunResult> testRunResults) {
    	this.testRunResults = testRunResults;
    }

    /**
     * Get the set of {@link TestSuiteInstance TestSuiteInstance}.
     * @return the TestSuiteInstances
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "suiteInstance")
    public List<TestSuiteInstance> getTestSuiteInstances () {
    	return testSuiteInstances;
    }

    public void setTestSuiteInstances (List<TestSuiteInstance> testSuiteInstances) {
    	this.testSuiteInstances = testSuiteInstances;
    }

    /**
     * Add a {@link TestSuiteInstance TestSuiteInstance}.
     * @param testSuiteInstance		a TestSuiteInstance
     */
    public void addTestSuiteInstance (TestSuiteInstance testSuiteInstance) {
    	testSuiteInstances.add(testSuiteInstance);
    }

    /**
     * Add a {@link TestRunResult TestRunResult} to the result set.
     * @param testRunResult		a result
     */
    public void addTestRunResult (TestRunResult testRunResult) {
    	testRunResults.add(testRunResult);
    }
}