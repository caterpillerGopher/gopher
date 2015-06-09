package com.echostar.gopher.persist;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * A test node specifies a Selenium node by IP, port and operating system (platform.)
 * We also advertise the browsers this node supports.
 * @author charles.young
 *
 */
@Entity
@Table(name="test_node")
public class TestNode {

    private Long			id;
    private PlatformEnum	platform;
    private String			nodeIP;
    private String			nodePort;
    private String			userName;
    private String			password;
    private String			installDir;
    private String			seleniumServer;
    private List<Browser>	supportedBrowsers = new ArrayList<Browser>();

    public TestNode () {}
 
    /**
     * Construct with all member data.
     *
	 * @param platform		the platform running the browser under test
	 * @param nodeIP		the IP address of the target Selenium node
	 * @param nodePort		the port number of the target Selenium node
	 * @param userName		the account name to access the host
	 * @param password		the account password encrypted
	 * @param installDir	the directory for our files
	 * @param seleniumServer	the Selenium server file
     */
    public TestNode (PlatformEnum platform, String nodeIP, String nodePort,
    	String userName, String password, String installDir,
    	String seleniumServer) {
     	this.platform = platform;
    	this.nodeIP = nodeIP;
    	this.nodePort = nodePort;
       	this.userName = userName;
       	this.password = password;
       	this.installDir = installDir;
       	this.seleniumServer = seleniumServer;
    }

    @Id
    @GeneratedValue
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Get the platform running the browser under test.
     * @return the platform
     */
    @Column(name = "platform")
    @Enumerated(EnumType.STRING)
    public PlatformEnum getPlatform () {return platform;}
    public void setPlatform (PlatformEnum platform) { this.platform = platform; }

    /**
     * Get the Selenium node IP address.
     * @return the address
     */
    @Column(name = "node_ip")
    public String getNodeIP () {return nodeIP;}
    public void setNodeIP (String nodeIP) { this.nodeIP = nodeIP; }

    /**
     * Get the Selenium node port number.
     * @return the port number
     */
    @Column(name = "node_port")
    public String getNodePort () {return nodePort;}
    public void setNodePort (String nodePort) { this.nodePort = nodePort; }

    /**
     * Get the account user name for node host access.
     * @return the name
     */
    @Column(name = "user_name")
    public String getUserName () {return userName;}
    public void setUserName (String userName) { this.userName = userName; }

    /**
     * Get the account user password for node host access.
     * @return the name
     */
    @Column(name = "user_password")
    public String getPassword () {return password;}
    public void setPassword (String password) { this.password = password; }

    /**
     * Get the directory path for our files.
     * @return the directory path
     */
    @Column(name = "install_dir")
    public String getInstallDir () {return installDir;}
    public void setInstallDir (String installDir) { this.installDir = installDir; }
    /**
     * Get the Selenium server file name.
     * @return the file name
     */
    @Column(name = "selenium_server")
    public String getSeleniumServer () {return seleniumServer;}
    public void setSeleniumServer (String seleniumServer)
    { this.seleniumServer = seleniumServer; }

    /**
     * Get the set of supported {@link Browser Browsers} for this TestNode.
     * @return the Browsers
     */
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name="test_node_browser",
    	joinColumns={@JoinColumn(name="test_node_id")},
    	inverseJoinColumns={@JoinColumn(name="browser_id")})
    public List<Browser> getSupportedBrowsers () {
    	return supportedBrowsers;
    }
    public void setSupportedBrowsers (List<Browser> supportedBrowsers) {
    	this.supportedBrowsers = supportedBrowsers;
    }
    
    /**
     * Add a Browser to the list of supported Browsers.
     * Use Hibernate Session to update the store when done adding TestSuites.
     * @param browser	the Browser to add
     */
    public void addBrowser (Browser browser) {
    	if (supportedBrowsers == null)
    		supportedBrowsers = new ArrayList<Browser>();
    	supportedBrowsers.add(browser);
    }
    
    /**
	 * Override Object to show our id, and TestClass name.
	 * @return	the String
	 */
	public String toString() {
		return getClass().getSimpleName() + " (" + id + ", '" + platform + "', '" + nodeIP + "', " + nodePort + ")";
	}
}