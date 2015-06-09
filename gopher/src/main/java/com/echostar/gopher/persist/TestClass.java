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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * A TestClass is associated with a TestNG test class given by {@link #getClassName className}.
 * A TestClass may belong to {@link TestSuite TestSuites} and may have
 * arguments described by {@link TestDataType TestDataType}.
 * A TestClass may be turned on (run it) or off (do not run.)
 * The combination of name and version must be unique.
 *
 * @author charles.young
 *
 */
@Entity
@Table(name="test_class")
public class TestClass {
	private Long id;
	private String name;
	private String version;
	private String className;
	private String description;
	private Boolean runmode;
	private String jiraIssue;
    private Date installTime;
	private List<TestSuite> testSuites		= new ArrayList<TestSuite>();
	private List<TestDataType> testDataTypes	= new ArrayList<TestDataType>();

    /**
     * The default constructor.
     */
    public TestClass () {}

    /**
     * A constructor that initializes the members. InstallTime is set by this constructor.
     *
     * @param name			a name or label for this TestClass
     * @param version		the TestClass version
     * @param className		the full class name for this TestClass
     * @param description	a description of this class
     * @param runmode		run this class or not
     * @param jiraIssue		the Jira issue name
     */
    public TestClass (String name, String version, String className, String description,
    	Boolean runmode, String jiraIssue) {
    	this.name = name;
    	this.version = version;
    	this.className = className;
    	this.description = description;
    	this.runmode = runmode;
    	this.jiraIssue = jiraIssue;
    	this.installTime = new Date();
    }

    /**
     * The primary key which is auto generated.
     * @return the id
     */
    @Id
    @GeneratedValue
    public Long getId() { return id; }

    /**
     * This method should only used by Hibernate to set the generated id.
     * @param id	the generated id
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Get the name of the TestNG test class.
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
     * Get the TestNG java class name.
     * @return	the class name
     */
    @Column(name = "class_name", unique=true, nullable = false)
    public String getClassName () {return className;}
    public void setClassName (String className) { this.className = className; }
   
    /**
     * Get the description of this TestClass.
     * @return	the description
     */
    @Column(name = "description")
    public String getDescription () {return description;}
    public void setDescription (String description) { this.description = description; }
    
    /**
     * Get the runmode of this TestClass. If set to false, no instances will run.
     * @return	the run mode
     */
    @Column(name = "runmode")
    public Boolean getRunmode () {return runmode;}
    public void setRunmode (Boolean runmode) { this.runmode = runmode; }

    /**
     * Get the Jira issue of the TestNG test class.
     * @return	the issue
     */
    @Column(name = "jira_issue", unique=false, nullable = false)
    public String getJiraIssue () {return jiraIssue;}
    public void setJiraIssue (String jiraIssue) { this.jiraIssue = jiraIssue; }

    /**
     * Get the install time.
     * @return  the install time
     */
    @Column(name = "install_time")
    public Date getInstallTime () {return installTime;}
    public void setInstallTime (Date installTime) { this.installTime = installTime; }

    /**
     * Get the {@link TestSuite test suites} having this TestClass.
     * @return the test suites
     */
    @ManyToMany(mappedBy="testClasses", fetch = FetchType.EAGER)
    public List<TestSuite> getTestSuites () {
    	return testSuites;
    }
    public void setTestSuites (List<TestSuite> testSuites) {
    	this.testSuites = testSuites;
    }

    /**
     * Get the set of {@link TestDataType test data types} for this class.
     * @return the test data types
     */
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name="test_class_test_data_type",
    	joinColumns={@JoinColumn(name="test_class_id")},
    	inverseJoinColumns={@JoinColumn(name="test_data_type_id")})
    public List<TestDataType> getTestDataTypes () {
    	return testDataTypes;
    }
    public void setTestDataTypes (List<TestDataType> testDataTypes) {
    	this.testDataTypes = testDataTypes;
    }

    /**
	 * Override Object to show our id, and name.
	 * @return	the String
	 */
	public String toString() {
		return getClass().getSimpleName()+"("+id+", '"+name+"', '"+version+"', Jira='"+jiraIssue+"')";
	}

	/**
	 * Compare each member and if not equal, return a reason.
	 * @param	o	an Object for comparison
	 * @return	a reason or null if equal
	 */
	public String getNotEqualsReason (Object o) {

		String notEqualsReason = null;

		if (!(o instanceof TestClass)) {
			notEqualsReason = "Object is not a TestClass";
			return notEqualsReason;
		}
		TestClass s = (TestClass) o;
		if (!id.equals(s.getId())) {
			notEqualsReason = "Ids not equal";
			return notEqualsReason;			
		}
		if (!name.equals(s.getName())) {
			notEqualsReason = "Names not equal";
			return notEqualsReason;			
		}
		if (!version.equals(s.getVersion())) {
			notEqualsReason = "Versions not equal";
			return notEqualsReason;			
		}
		if (!className.equals(s.getClassName())) {
			notEqualsReason = "ClassNames not equal";
			return notEqualsReason;			
		}
		if (!jiraIssue.equals(s.getJiraIssue())) {
			notEqualsReason = "JiraIssues not equal";
			return notEqualsReason;			
		}
		if (!(runmode == s.getRunmode())) {
			notEqualsReason = "Runmodes not equal";
			return notEqualsReason;			
		}
		if (!description.equals(s.getDescription())) {
			notEqualsReason = "Descriptions not equal";
			return notEqualsReason;			
		}
		return notEqualsReason;
	}
}
