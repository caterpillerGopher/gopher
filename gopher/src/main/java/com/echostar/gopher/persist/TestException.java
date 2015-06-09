package com.echostar.gopher.persist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.echostar.gopher.util.Config;
import com.echostar.gopher.util.ExceptionUtil;

@Entity
@Table(name="test_exception")
public class TestException {

    private Long id;
    private String message;
    private String exceptionClass;
    private String stacktrace;
    private TestRunResult testRunResult;

    public TestException () {}

    /**
     * A constructor which initializes all the members.
     *
     * @param	stacktrace		the exception stacktrace
     * @param	message			the exception message
     * @param	exceptionClass	the exception class
     * @param	testRunResult	the TestRunResult having this exception
     */
    public TestException (String exceptionClass, String message, String stacktrace, TestRunResult testRunResult) {
       	this.stacktrace = stacktrace;
       	this.exceptionClass = exceptionClass;
    	int maxMessageLen = Integer.parseInt(Config.getProperty_S("Tables.test_exception.message.maxLen")) -1;
    	if (message.length() > maxMessageLen) {
        	this.message = message.substring(0, maxMessageLen);    		
    	} else {
    		this.message = message;
    	}
       	this.testRunResult = testRunResult;
    }

    public TestException (Throwable e, int maxTraceLen, TestRunResult testRunResult) {
    	this.exceptionClass = e.getClass().getName();
    	String msg = e.getMessage();
    	if (msg != null) {
    		int maxMessageLen = Integer.parseInt(Config.getProperty_S("Tables.test_exception.message.maxLen"));
    		if (msg.length() > maxMessageLen) {
    			this.message = msg.substring(0, maxMessageLen);    		
    		} else {
    			this.message = msg;
    		}
    	} else {
    		this.message = "";
    	}
    	this.stacktrace = ExceptionUtil.getStackTraceString(e, maxTraceLen);
       	this.testRunResult = testRunResult;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the stacktrace.
     * @return	the stacktrace
     */
    @Column(name = "stacktrace", nullable = false)
    public String getStacktrace () {return stacktrace;}
    public void setStacktrace (String stacktrace)
    { this.stacktrace = stacktrace; }

    /**
     * Get the exception message.
     * @return	the message
     */
    @Column(name = "message")
    public String getMessage () {return message;}
    public void setMessage (String message) {
    	int maxMessageLen = Integer.parseInt(Config.getProperty_S("Tables.test_exception.message.maxLen"));
    	if (message.length() > maxMessageLen) {
        	this.message = message.substring(0, maxMessageLen);    		
    	} else {
    		this.message = message;
    	}
    }

    /**
     * Get the exception class.
     * @return	the class
     */
    @Column(name = "exception_class", nullable = false)
    public String getExceptionClass () {return exceptionClass;}
    public void setExceptionClass (String exceptionClass)
    { this.exceptionClass = exceptionClass; }

    /**
     * Get the {@link TestRunResult TestRunResult} having this TestException.
     * @return the TestRunResult
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "test_run_result_id")
	public TestRunResult getTestRunResult () {
    	return testRunResult;
    }
    public void setTestRunResult (TestRunResult testRunResult) {
    	this.testRunResult = testRunResult;
    }
}