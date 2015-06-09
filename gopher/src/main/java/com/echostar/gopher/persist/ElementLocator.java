package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * A page element locator. 
 * @author charles.young
 *
 */
@Entity
@Table(name="element_locator")
public class ElementLocator {

    private Long id;
    private String description;
    private String name;
    private String value;
    private ElementLocatorType locatorType;
    private List<TestCase> testCases = new ArrayList<TestCase>();

    /**
     * The default constructor.
     */
    public ElementLocator () {
    }

    /**
     * A constructor which initializes all the members.
     * 
     * @param	locatorType		the locator type
     * @param	name			the name of the locator
     * @param	value			the value of the locator
     * @param	description		the description of the locator
     */
    public ElementLocator (ElementLocatorType locatorType, String name,
    	String value, String description) {
       	this.locatorType = locatorType;
       	this.name = name;
       	this.value = value;
    	this.description = description;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the description.
     * @return	the description
     */
    @Column(name = "description", nullable = false)
    public String getDescription () {return description;}
    public void setDescription (String description) { this.description = description; }

    /**
     * Get the name.
     * @return	the name
     */
    @Column(name = "name", nullable = false)
    public String getName () {return name;}
    public void setName (String name) { this.name = name; }
    
    /**
     * Get the locator type.
     * @return	the locator type
     */
    @Column(name = "locator_type", nullable = false)
    public ElementLocatorType getLocatorType () {return locatorType;}
    public void setLocatorType (ElementLocatorType locatorType) { this.locatorType = locatorType; }

    /**
     * Get the value.
     * @return	the value
     */
    @Column(name = "value", nullable = false)
    public String getValue () {return value;}
    public void setValue (String value) { this.value = value; }

    /**
     * Get the @{link TestCase TestCases} for this ElementLocator.
     * @return	the TestCase
     */
    @ManyToMany(mappedBy="elementLocators")
	public List<TestCase> getTestCases() {
		return this.testCases;
	}
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	/**
	 * Create the locator array used by {@link com.echostar.gopher.selenium.GopherDriver GopherDriverAPI}.
	 * The first value is returned by {@link ElementLocatorType#getValue ElementLocatorType.getValue()}.
	 * The second value is the value of this ElementLocator.
	 *
	 * @return	an array of String of length 2.
	 * @throws Exception	on any error
	 */
	@Transient
	public String[] getLocator () throws Exception {
		String[] result = new String[2];

		result[0] = locatorType.getValue();
		result[1] = value;
		
		return result;
	}

	public static String[] getLocator(ElementLocatorType locatorType, String value) {
		String[] result = new String[2];
		
		result[0] = locatorType.getValue();
		result[1] = value;

		return result;
	}

	/**
	 * Add a TestCase to the Set.
	 * @param testCase	a TestCase to be added
	 */
	public void addTestCase (TestCase testCase) {
		testCases.add(testCase);
	}
}