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
 * A SuiteDecorator associates a URL, browser, and runmode
 * with a Suite.
 *
 * If a SuiteDecorator is related to a {@link Suite Suite},
 * {@link TestSuiteDecorator TestSuiteRuns} need not be related to
 * {@link TestSuite TestSuites} and
 * {@link TestClassDecorator TestClassRuns} need not be related to
 * {@link TestClass TestClasses}.
 *
 * The values in the SuiteDecorator will be used if neither a TestSuiteDecorator or a TestClassDecorator
 * is related and if the URL or browser is undefined on a TestRun.
 *
 * @author charles.young
 *
 */
@Entity
@Table(name="suite_decorator")
public class SuiteDecorator {

    Long			id;
    String			name;
    String			url;
    BrowserEnum		browser;
    Boolean			runmode;
    Suite			suite;

    public SuiteDecorator () {}
 
    /**
     * Construct with all member data.
     *
     * @param name			the name of SuiteDecorator
     * @param url			the url of the web page under test
	 * @param browser		the name of the browser under test
	 * @param runmode		do run (true) or not (false)
     * @param suite			the suite we are related to
     */
    public SuiteDecorator (String name, String url, BrowserEnum browser, Boolean runmode,
    	Suite suite) {
       	this.name = name;
       	this.url = url;
     	this.browser = browser;
    	this.runmode = runmode;
    	this.suite = suite;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the name.
     * @return	the name
     */
    @Column(name = "name", unique=false, nullable = false)
    public String getName () {return name;}
    public void setName (String name) { this.name = name; }

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
	 * Override Object to show our id, and TestSuite name.
	 * @return	the String
	 */
	public String toString() {
		return getClass().getSimpleName()+"("+id+" '"+name+"'  suite='"+suite.getName()+"' "+",'"+url+"', '"+browser+"')";
	}
}