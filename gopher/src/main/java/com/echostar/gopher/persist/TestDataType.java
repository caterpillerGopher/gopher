package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * A test data type. This is type information for a TestData argument.
 * Type information includes a name or label for the argument, the type proper (int, string etc.) and the position in the argument
 * list.
 * @author charles.young
 *
 */
@Entity
@Table(name="test_data_type")
public class TestDataType {

	private Long		id;
	private String		dataName;
	private String		dataType;
	private DataRoleEnum	role;
	private List<TestClass>	testClasses =  new ArrayList<TestClass>();

    /**
     * The default constructor.
     */
    public TestDataType () {
    }

    /**
     * A constructor that initializes all non-relational members.
     *
     * @param dataName	a name or label for the data
     * @param dataType	the type proper
     * @param role		the role of the data (tbd make this an enum)
     */
    public TestDataType (String dataName, DataTypeEnum dataType, DataRoleEnum role) {
    	this.dataName = dataName;
    	this.dataType = dataType.getValue();
    	this.role = role;
    }
    
    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    /**
     * Get the data name.
     * @return the data name
     */
    @Column(name = "data_name", unique=true, nullable = false)
    public String getDataName () {return dataName;}
    public void setDataName (String dataName) { this.dataName = dataName; }
    
    /**
     * Get the data type.
     * @return	the data type
     */
    @Column(name = "data_type", unique=false, nullable = false)
    public String getDataType () {return dataType;}
    /**
     * Set the data type. Use {@link DataTypeEnum DataTypeEnum.getValue()}
     * to make sure the value is valid.
     * @param dataType	a {@link DataTypeEnum DataTypeEnum} value
     */
    public void setDataType (String dataType) { this.dataType = dataType; }

    /**
     * Get the data role.
     * @return	the data role
     */
    @Column(name = "role", unique=false, nullable = false)
    public DataRoleEnum getRole () {return role;}
    /**
     * Set the data role. Use {@link DataRoleEnum DataRoleEnum.getValue()}
     * to make sure the value is valid.
     * @param role	a {@link DataRoleEnum DataRoleEnum} value
     */
    public void setRole (DataRoleEnum role) { this.role = role; }

    /**
     * Get the TestClass for this test data.
     * @return	the TestClass
     */
    @ManyToMany(mappedBy="testDataTypes")
	public List<TestClass> getTestClasses() {
		return testClasses;
	}
	public void setTestClasses(List<TestClass> testClasses) {
		this.testClasses = testClasses;
	}

	/**
	 * Add a TestClass to the Set.
	 * @param testClass	a TestClass to be added
	 */
	public void addTestClass (TestClass testClass) {
		testClasses.add(testClass);
	}
}
