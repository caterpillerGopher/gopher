package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * A test data argument value. TestData is an actual argument supplied to a {@link TestCase TestCase}.
 * The {@link #testDataType testDataType} member describes the argument.
 * @author charles.young
 *
 */
@Entity
@Table(name="test_data")
public class TestData {

    private Long id;
    private TestDataType testDataType;
    private String dataValue;
    private List<TestCase> testCases = new ArrayList<TestCase>();

    /**
     * The default constructor.
     */
    public TestData () {
    }

    /**
     * A constructor which initializes all the members.
     * 
     * @param	testDataType	type information for this argument
     * @param	dataValue		the value of the argument
     */
    public TestData (TestDataType testDataType, String dataValue) {
    	this.testDataType = testDataType;
    	this.dataValue = dataValue;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the {@link TestDataType TestDataType} of this argument.
     * @return the TestDataType
     */
	@OneToOne
	@JoinColumn(name = "test_data_type_id")
    public TestDataType getTestDataType () {
    	return testDataType;
    }
    public void setTestDataType (TestDataType testDataType) {
    	this.testDataType = testDataType;
    }

    /**
     * Get the data value.
     * @return	the data value
     */
    @Column(name = "data_value", nullable = false)
    public String getDataValue () {return dataValue;}
    public void setDataValue (String dataValue) { this.dataValue = dataValue; }

    /**
     * Get the @{link TestCase TestCase} for this test data.
     * @return	the TestCase
     */
    @ManyToMany(mappedBy="testData")
	public List<TestCase> getTestCases() {
		return this.testCases;
	}
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	/**
	 * Add a TestCase to the Set.
	 * @param testCase	a TestCase to be added
	 */
	public void addTestCase (TestCase testCase) {
		testCases.add(testCase);
	}

	/**
	 * This is a hack to allow Locators to remain as TestData until they can be moved to ElementLocator.
	 * @return	the array expected by GopherDriverAPI
	 */
	@Transient
	public String[] getLocator() {
		String role = testDataType.getRole().getValue();
		ElementLocatorType locatorType = ElementLocatorType.valueOf(role);
		return ElementLocator.getLocator(locatorType, dataValue);
	}
}