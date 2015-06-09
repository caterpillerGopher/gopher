package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A suite. A Suite is a collection of TestSuite.
 * The combination of name and version must be unique.
 * @author charles.young
 *
 */
@Entity
@Table(name="suite")
public class Suite {

	private Long id;
	private String name;
	private String version;
	private String description;
	private boolean runmode;
    private List<TestSuite> testSuites = new ArrayList<TestSuite>();
    private List<TestSuiteDecorator> testSuiteDecorators = new ArrayList<TestSuiteDecorator>();
    private List<SuiteDecorator> suiteDecorators = new ArrayList<SuiteDecorator>();

    /**
     * The default constructor.
     */
    public Suite () {}

    /**
     * A constructor that initializes all the members.
     * @param name			a name for the test suite
     * @param version		a unique version for this suite
     * @param description	a description of this suite
     * @param runmode		yes (run suite) or no (do not run suite)
     * @param testSuites	a collection of TestSuite
     */
    public Suite (String name, String version, String description, boolean runmode,
    	List<TestSuite> testSuites) {
    	this.name = name;
    	this.version = version;
    	this.description = description;
    	this.runmode = runmode;
    	this.testSuites = testSuites;
    }

    /**
     * The id generated by Hibernate.
     * @return	the id
     */
    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the suite name.
     * @return	the name
     */
    @Column(name = "name", unique=false, nullable = false)
    public String getName () {return name;}
    public void setName (String name) { this.name = name; }

    /**
     * Get the version.
     * @return	the version
     */
    @Column(name = "version", unique=false, nullable = false)
    public String getVersion () {return version;}
    public void setVersion (String version) { this.version = version; }

    /**
     * Get the description of this suite.
     * @return	the description
     */
    @Column(name = "description")
    public String getDescription () {return description;}
    public void setDescription (String description) { this.description = description; }
    
    /**
     * Get the runmode of this suite. If set to false, the suite will not run.
     * @return	the run mode
     */
    @Column(name = "runmode")
    public Boolean getRunmode () {return runmode;}
    public void setRunmode (Boolean runmode) { this.runmode = runmode; }

    /**
     * Get the set of TestSuite for this Suite.
     * @return	the Set of TestSuite
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name="suite_test_suite",
    	joinColumns={@JoinColumn(name="suite_id")},
    	inverseJoinColumns={@JoinColumn(name="test_suite_id")})
    public List<TestSuite> getTestSuites () {
    	return testSuites;
    }
    public void setTestSuites (List<TestSuite> testSuites) {
    	this.testSuites = testSuites;
    }

    /**
     * Get the set of {@link TestSuiteDecorator SuiteRuns} for this suite.
     * @return the SuiteRuns
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "suite", cascade = {CascadeType.ALL})
    public List<TestSuiteDecorator> getTestSuiteDecorators () {
    	return testSuiteDecorators;
    }
    public void setTestSuiteDecorators (List<TestSuiteDecorator> testSuiteDecorators) {
    	this.testSuiteDecorators = testSuiteDecorators;
    }

    /**
     * Get the set of {@link SuiteDecorator SuiteRuns} for this suite.
     * @return the SuiteRuns
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "suite", cascade = {CascadeType.ALL})
    public List<SuiteDecorator> getSuiteDecorators () {
    	return suiteDecorators;
    }
    public void setSuiteDecorators (List<SuiteDecorator> suiteDecorators) {
    	this.suiteDecorators = suiteDecorators;
    }

    /**
     * Add a TestSuite to the list.
     * Use Hibernate Session to update the store when done adding TestSuites.
     * @param testSuite	the TestSuite to add
     */
    public void addTestSuite (TestSuite testSuite) {
    	if (testSuites == null)
    		testSuites = new ArrayList<TestSuite>();
    	testSuites.add(testSuite);
    }

    /**
     * Add a TestSuiteDecorator to the list.
     * Use Hibernate Session to update the store when done adding.
     * @param testSuiteDecorator	the TestSuiteDecorator to add
     */
    public void addTestSuiteDecorator (TestSuiteDecorator testSuiteDecorator) {
    	if (testSuiteDecorators == null)
    		testSuiteDecorators = new ArrayList<TestSuiteDecorator>();
    	testSuiteDecorators.add(testSuiteDecorator);
    }
    
    /**
     * Add a SuiteDecorator to the list.
     * Use Hibernate Session to update the store when done adding.
     * @param suiteDecorator	the SuiteDecorator to add
     */
    public void addSuiteDecorator (SuiteDecorator suiteDecorator) {
    	if (suiteDecorators == null)
    		suiteDecorators = new ArrayList<SuiteDecorator>();
    	suiteDecorators.add(suiteDecorator);
    }
    
    /**
	 * Override Object to show our id, and name.
	 * @return	the String
	 */
	public String toString() {
		return getClass().getSimpleName()+"("+id+", '"+name+"', '"+version+"')";
	}

	/**
	 * Compare each member and if not equal, return a reason.
	 * @param	o	an Object for comparison
	 * @return	a reason or null if equal
	 */
	public String getNotEqualsReason (Object o) {
		String notEqualsReason = null;

		if (!(o instanceof Suite)) {
			notEqualsReason = "Object is not a Suite";
			return notEqualsReason;
		}
		Suite s = (Suite) o;
		if (!id.equals(s.getId())) {
			notEqualsReason = "Ids not equal";
		}
		else if (!name.equals(s.getName())) {
			notEqualsReason = "Names not equal";
		}
		else if (!version.equals(s.getVersion())) {
			notEqualsReason = "Versions not equal";
		}
		else if (!(runmode == s.getRunmode())) {
			notEqualsReason = "Runmodes not equal";
		}
		else if (!description.equals(s.getDescription())) {
			notEqualsReason = "Descriptions not equal";
		}
		if (testSuites.size() != s.getTestSuites().size()) {
			notEqualsReason = "TestSuites size "+s.testSuites.size()+
				" not equal "+getTestSuites().size();
			return notEqualsReason;			
		}
		if (testSuiteDecorators.size() != s.getTestSuiteDecorators().size()) {
			notEqualsReason = "TestSuiteRuns not equal";
			return notEqualsReason;			
		}
		if (suiteDecorators.size() != s.getSuiteDecorators().size()) {
			notEqualsReason = "SuiteRuns not equal";
			return notEqualsReason;			
		}

		return notEqualsReason;
	}
}
