package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * A browser. A browser has a name, a type and a relation to TestNodes.
 * @author charles.young
 *
 */
@Entity
@Table(name="browser")
public class Browser {

    private Long		id;
    private String		name;
    private BrowserEnum	type;
    private List<TestNode>	testNodes = new ArrayList<TestNode>();

    /**
     * The default constructor.
     */
    public Browser () {
    }

    /**
     * Construct with all member values.
     * @param name	the browser name
     * @param type	the browser type
     */
    public Browser (String name, BrowserEnum type) {
    	this.name = name;
    	this.type = type;
    }

    /**
     * Get the generated id.
     * @return	the id
     */
    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the name.
     * @return	the name
     */
    @Column(name = "name", unique=true, nullable = false)
    public String getName () {return name;}
    public void setName (String name) { this.name = name; }

    /**
     * Get the browser type.
     * @return	the type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", unique=true, nullable = false)
    public BrowserEnum getType () {return type;}
    public void setType (BrowserEnum type) { this.type = type; }

    /**
     * Get the set of {@link TestNode TestNodes} using this Browser.
     * @return the TestNodes
     */
    @ManyToMany(mappedBy="supportedBrowsers")
    public List<TestNode> getTestNodes () {
    	return testNodes;
    }
    public void setTestNodes (List<TestNode> testNodes) {
    	this.testNodes = testNodes;
    }
}
