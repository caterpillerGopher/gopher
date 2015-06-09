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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.deetysoft.exception.MissingPropertyException;

/**
 * A test case. A test case relates {@link TestData TestData} arguments with a TestClass producing a runnable case.
 * A TestCase may have multiple runs using different argument values.
 * The combination of name and version must be unique.
 * 
 * @author charles.young
 *
 */
@Entity
@Table(name="test_case")
public class TestCase {

    private Long			id;
    private String			name;
    private String			version;
	private Boolean			runmode;
    private TestClass		testClass;
    private List<ElementLocator>	elementLocators = new ArrayList<ElementLocator>();
    private List<TestData>	testData = new ArrayList<TestData>();
    private List<TestRun>	testRuns = new ArrayList<TestRun>();

    /**
     * The default constructor.
     */
    public TestCase () {
    	
    }

    /**
     * Construct and initialize all the members.
     * @param name		a name for this case
     * @param version	a unique version for this case
     * @param runmode	run this case or not
     * @param testClass	the TestClass
     */
    public TestCase (String name, String version, Boolean runmode, TestClass testClass) {
    	this.name = name;
    	this.version = version;
    	this.runmode = runmode;
    	this.testClass = testClass;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the case name.
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
     * Get the runmode of this test case. If set to false, it will not run.
     * @return	the run mode
     */
    @Column(name = "runmode")
    public Boolean getRunmode () {return runmode;}
    public void setRunmode (Boolean runmode) { this.runmode = runmode; }

    /**
     * Get the set of {@link TestData TestData} for this test case.
     * @return the test data
     */
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name="test_case_test_data",
    	joinColumns={@JoinColumn(name="test_case_id")},
    	inverseJoinColumns={@JoinColumn(name="test_data_id")})
    public List<TestData> getTestData () {
    	return testData;
    }
    public void setTestData (List<TestData> testData) {
    	this.testData = testData;
    }

    /**
     * Get the set of {@link ElementLocator ElementLocator} for this test case.
     * @return the ElementLocators
     */
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name="test_case_element_locator",
    	joinColumns={@JoinColumn(name="test_case_id")},
    	inverseJoinColumns={@JoinColumn(name="element_locator_id")})
    public List<ElementLocator> getElementLocators () {
    	return elementLocators;
    }
    public void setElementLocators (List<ElementLocator> elementLocators) {
    	this.elementLocators = elementLocators;
    }
    
    /**
     * Add a test data item to our collection of test data.
     * Use Hibernate Session to update this run after done adding items.
     *  
     * @param td	test data to add
     */
    public void addTestData (TestData td) {
    	if (testData == null)
    		testData = new ArrayList<TestData>();
    	testData.add (td);
    }

    /**
     * Get the TestClass for this test case.
     * @return	the TestClass
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "test_class_id", nullable = false)
	public TestClass getTestClass() {
		return this.testClass;
	}
	public void setTestClass(TestClass testClass) {
		this.testClass = testClass;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "testCase")
	public List<TestRun> getTestRuns() {
		return testRuns;
	}
	public void setTestRuns(List<TestRun> testRuns) {
		this.testRuns = testRuns;
	}

	/**
	 * Add a TestRun to the set of TestRuns.
	 * @param testRun the test run to add
	 */
	public void addTestRun (TestRun testRun) {
		testRuns.add(testRun);
	}

	/**
	 * Get the TestData for this case with the given name.
	 * @param name	the TestData name
	 * @return		the TestData
	 * @throws MissingPropertyException		if no TestData is found in this TestCase with the given name
	 */
	public TestData getTestData (String name) throws MissingPropertyException {
	    List<TestData> testData = getTestData ();
		for (TestData data : testData) {
			TestDataType dataType = data.getTestDataType ();
			if (dataType.getDataName().equals(name)) {
				return data;
			}
		}
		throw new MissingPropertyException (name, "TestCase with id "+getId()+" and name "+testClass.getName()+".");
	}

	/**
	 * Get the value of the TestData for this case with the given name.
	 * @param name	the TestData name
	 * @return		the TestData value
	 * @throws MissingPropertyException		if no TestData is found in this TestCase with the given name
	 */
	public String getDataValue (String name) throws MissingPropertyException {
	    List<TestData> testData = getTestData ();
		for (TestData data : testData) {
			TestDataType dataType = data.getTestDataType ();
			if (dataType.getDataName().equals(name)) {
				return data.getDataValue();
			}
		}
		throw new MissingPropertyException (name, "TestCase with id "+getId()+" and name "+testClass.getName()+".");
	}

	/**
	 * Get the value of the ElementLocator with the given name used by this TestCase.
	 *
	 * @param name	the ElementLocator name
	 * @return		the ElementLocator value
	 * @throws		MissingPropertyException		if no ElementLocator is found in this TestCase with the given name
	 */
	public String getElementLocatorValue (String name) throws MissingPropertyException {
		ElementLocator elementLocator = findElementLocatorByName (name);
		return elementLocator.getValue();
	}

	/**
	 * Find the ElementLocator with the given name related to this TestCase.
	 *
	 * @param name	name of the ElementLocator
	 * @return		the ElementLocator if any
	 * @throws		MissingPropertyException	if the locator is not found
	 */
	public ElementLocator findElementLocatorByName (String name) throws MissingPropertyException {
	    List<ElementLocator> elementLocator = getElementLocators ();
		for (ElementLocator data : elementLocator) {
			if (data.getName().equals(name)) {
				return data;
			}
		}
		throw new MissingPropertyException ("ElementLocator with name '"+name+"' not found.");
	}
	
	/**
	 * Provide backward compatibility by creating the old-style locator array from the ElementLocator
	 * with the given name.
	 * @param name	the ElementLocator name
	 * @return		the old-style locator array
	 */
	@Transient
	public String[] getLocatorArray(String name) throws MissingPropertyException {
		ElementLocator elementLocator = findElementLocatorByName (name);

		ElementLocatorType locatorType = elementLocator.getLocatorType();
		String value = elementLocator.getValue();
		return ElementLocator.getLocator(locatorType, value);
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

	    if (!(o instanceof TestCase)) {
			notEqualsReason = "Object is not a TestCase";
			return notEqualsReason;
		}
		TestCase s = (TestCase) o;
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
		if (!(runmode == s.getRunmode())) {
			notEqualsReason = "Runmodes not equal";
			return notEqualsReason;			
		}
		if (!testClass.equals(s.getTestClass())) {
			notEqualsReason = "TestClasses not equal";
			return notEqualsReason;			
		}
		return notEqualsReason;
	}
}