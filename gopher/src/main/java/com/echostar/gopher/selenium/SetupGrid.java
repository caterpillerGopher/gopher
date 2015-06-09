package com.echostar.gopher.selenium;

import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.PlatformEnum;
import com.echostar.gopher.persist.TestNode;
import com.echostar.gopher.util.Config;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * Start or stop the Selenium webdriver nodes from a windows machine.
 * 
 * @author Sathish DS
 * @author charles.young	enhancements from the original
 *
 */
public class SetupGrid {

	private static Logger log = Logger.getLogger (SetupGrid.class.getName());
	
	public static void main(String[] args) {

		// Set status to success.
		int status = 0;

		try {
			if(args.length<1){
				log.error("Argument startGRID or stopGRID required.");
				log.info(usage());
			} else if(args[0].equalsIgnoreCase("startGRID")){
				log.info("Starting up GRID.");
				setupSeleniumGrid();
			} else if(args[0].equalsIgnoreCase("stopGRID")){
				log.info("Shutting down GRID.");
				shutdownSeleniumGrid();
			}else {
				log.error("Argument '"+args[0]+"' is an unrecognized.");
				log.info(usage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = -1;
		}
		log.info("Done");
		System.exit(status);
	}

	/**
	 * Get the usage message.
	 * @return
	 */
	public static String usage () {
		return "Usage: "+SetupGrid.class.getName()+" [startGRID,stopGrid].";
	}

	/**
	 * 
	 * @param psExePath					?
	 * @param hubIP						the hub ip
	 * @param hubPort					the hub port
	 * @param browserName				the browser name
	 * @param seleniumJarLocation		the Selenium jar location
	 * @param seleniumDriverLocation	the Selenium driver location
	 * @param nodeIP					the node ip
	 * @param nodePort					the node port
	 * @param nodeUsername				the user account name on the node
	 * @param nodePassword				the user password
	 * @throws Exception				on any error
	 */
	public static void startNode_Windows(String psExePath, String hubIP,
		String hubPort, String browserName, String seleniumJarLocation,
		String seleniumDriverLocation, String nodeIP, String nodePort,
		String nodeUsername, String nodePassword) throws Exception {

		log.info("Starting node on windows machine with IP : "+ nodeIP);
		Process p;
		int returnvalue = 0;
		String command = "";
		String[] browserNames = browserName.split("::");
		String browser_Name = "";
		for (int i = 0; i < browserNames.length; i++) {
			if ((browserNames[i]).length() != 0) {
				browser_Name = browser_Name + " -browser browserName="
						+ browserNames[i];
			}
		}
		command = "java -Dwebdriver.ie.driver=" + seleniumDriverLocation
				+ "\\IEDriverServer.exe " + "-Dwebdriver.chrome.driver="
				+ seleniumDriverLocation + "\\chromedriver.exe " + " -jar "
				+ seleniumJarLocation + " -role node -hub http://" + hubIP
				+ ":" + hubPort + "/grid/register -port " + nodePort + " "
				+ browser_Name;
		String command_full = "";
		if (hubIP.equals(nodeIP)) {
			command_full = command;
		} else {
			command_full = System.getProperty("user.dir") + "//" + psExePath
					+ " \\\\" + nodeIP + " -u " + nodeUsername + " -p "
					+ nodePassword + " -i " + command;
		}
		log.debug(command_full);
		try {
			p = Runtime.getRuntime().exec(command_full);
			Thread.sleep(30000);
			try {
				returnvalue = p.exitValue();
			} catch (Exception e) {
				returnvalue = 0;
			}
		} catch (Exception e) {
			log.error("Error executing command to start the windows node "+
				nodeIP);
			log.error(e.getMessage());
			throw e;
		}

		getNodeStatus(nodeIP, nodePort, returnvalue);
	}

	/**
	 * Clear the browser cache on the given Windows node.
	 * @param nodeIP		the node ip
	 * @param nodePort		the node port
	 * @param nodeUserName	the user account on the node
	 * @param nodePassword	the account password
	 * @param seleniumClearCacheScriptPath	the path to the clear cache script
	 * @throws Exception	on any error
	 */
	public static void clearBrowserCache_Windows(String nodeIP, String nodePort,
		String nodeUserName, String nodePassword, String seleniumClearCacheScriptPath)
		throws Exception {

		log.info("Clearing cache on windows machine with IP : "+ nodeIP);

		String hubIP = Config.getHubIP();
		String psExePath = Config.getProperty_S("hubPowerShellLocation");

		String command_IE = seleniumClearCacheScriptPath
			+ "\\Clear_IEBrowserCache_Windows.bat";
		String command_Chrome = seleniumClearCacheScriptPath
			+ "\\Clear_ChromeBrowserCache_Windows.bat";
		String command_Firfox = seleniumClearCacheScriptPath
			+ "\\Clear_FirefoxBrowserCache_Windows.bat";
		String command_IEfull = "";
		String command_Chromefull = "";
		String command_Firefoxfull = "";

		if (hubIP.equals(nodeIP)) {
			command_IEfull = command_IE;
			command_Chromefull = command_Chrome;
			command_Firefoxfull = command_Firfox;
		} else {
			command_IEfull = System.getProperty("user.dir") + "//"
				+ psExePath + " \\\\" + nodeIP + " -u " + nodeUserName
				+ " -p " + nodePassword + " -i " + command_IE;
			command_Chromefull = System.getProperty("user.dir") + "//"					+ psExePath + " \\\\" + nodeIP + " -u " + nodeUserName
				+ " -p " + nodePassword + " -i " + command_Chrome;
			command_Firefoxfull = System.getProperty("user.dir") + "//"
				+ psExePath + " \\\\" + nodeIP + " -u " + nodeUserName
				+ " -p " + nodePassword + " -i " + command_Firfox;
		}

		try {
			log.debug("Executing \""+command_IEfull+"\".");
			Runtime.getRuntime().exec(command_IEfull);
			Thread.sleep(20000);
			log.info("IE cache is cleared");
			log.debug("Executing \""+command_Chromefull+"\".");
			Runtime.getRuntime().exec(command_Chromefull);
			Thread.sleep(10000);
			log.info("Chrome cache is cleared");
			log.debug("Executing \""+command_Firefoxfull+"\".");
			Runtime.getRuntime().exec(command_Firefoxfull);
			Thread.sleep(10000);
			log.info("Firefox cache is cleared");
		} catch (Exception e) {
			log.error("Error occoured while executing command to clear cache.");
			log.error(e.getMessage());
			throw e;
		} 
	}

	/**
	 * Clear the browser cache on a Mac.
	 * @param nodeIP		the node ip
	 * @param nodePort		the node port
	 * @param nodeUserName	the user account on the node
	 * @param nodePassword	the account password
	 * @param seleniumClearCacheScriptPath	the path to the clear cache script
	 * @throws Exception	on any error
	 */
	public static void clearBrowserCache_Mac(String nodeIP, String nodePort,
		String nodeUserName, String nodePassword, String seleniumClearCacheScriptPath)
		throws Exception {

		log.info("Clearing cache on mac machine with IP : "+ nodeIP);
		int exitStatus = 0;
		Session session = null;
		String command = "";

		String command_Safari = "sh " + seleniumClearCacheScriptPath
			+ "/Clear_SafariBrowserCache_Mac.sh " + nodeUserName;
		String command_Chrome = "sh " + seleniumClearCacheScriptPath
			+ "/Clear_ChromeBrowserCache_Mac.sh " + nodeUserName;
		String command_Firfox = "sh " + seleniumClearCacheScriptPath
			+ "/Clear_FirefoxBrowserCache_Mac.sh " + nodeUserName;

		try {
			// command =
			// "java -Dwebdriver.firefox.profile=default -Dwebdriver.chrome.driver="+seleniumDriverLocation+"//chromedriver"+" -jar "+seleniumJarLocation+" -role node -hub http://"+hubIP+":"+hubPort+"/grid/register -port "+nodePort+" "+browser_Name;//+" -browserName="+browserName;
			// log.info(command);
			JSch jsch = new JSch();
			session = jsch.getSession(nodeUserName, nodeIP, 22);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setPassword(nodePassword);
			session.setConfig(config);
			session.connect();
			boolean isConnected = session.isConnected();

			if (isConnected) {
				log.info("SSH to node is successful.");
				for (int j = 0; j < 3; j++) {
					ChannelExec ChannelExec_1 = (ChannelExec) session
						.openChannel("exec");
					if (j == 0) {
						command = command_Safari;
					} else if (j == 1) {
						command = command_Chrome;
					} else if (j == 2) {
						command = command_Firfox;
					}
					ChannelExec_1.setCommand(command);
					ChannelExec_1.connect();
					Thread.sleep(2000);
					exitStatus = ChannelExec_1.getExitStatus();
					ChannelExec_1.disconnect();
					if (exitStatus < 0) {
						log.warn("Cache clear returned "+exitStatus+" < 0.");
					} else if (exitStatus > 0) {
						log.error("Cache clear returned "+exitStatus+" > 0.");
					} else {
						log.info("Cache clear returned 0 - ok.");
					}
					Thread.sleep(3000);
					if (j == 0) {
						log.info("Safari cache is cleared");
					} else if (j == 1) {
						log.info("Chrome cache is cleared");
					} else if (j == 2) {
						log.info("Firefox cache is cleared");
					}
				}
				session.disconnect();
			} else {
				log.error("Could not connect to the node \""+nodeIP+"\".");
			}
		} catch (Exception e) {
			log.error("Error occoured while clearing the cache.");
			log.error(e.getMessage());
			throw e;
		}
	}

	public static void clearBrowserCache(PlatformEnum osName, String nodeIP, String nodePort,
		String nodeUserName, String nodePassword, String seleniumClearCacheScriptPath) throws Exception {

		if (osName.isWindows()) {
			clearBrowserCache_Windows(nodeIP, nodePort,nodeUserName,nodePassword,seleniumClearCacheScriptPath);
		} else if (osName.isMac()) {
			clearBrowserCache_Mac(nodeIP, nodePort,nodeUserName,nodePassword,seleniumClearCacheScriptPath);
		} else {
			log.error("Unsupported OS \""+osName+"\".");
		}
	}

	/**
	 * Start the Selenium hub on the localhost.
	 * @param seleniumJarLocation	the path to the Selenium jar file
	 * @return						true if started
	 * @throws Exception			on any error
	 */
	public static boolean startHub(String seleniumJarLocation) throws Exception {
		log.info("Starting hub on localhost");
		int returnvalue = 0;
		Process p;
		String command = "";
		command = "java -jar " + System.getProperty("user.dir") + "//"
				+ seleniumJarLocation + " -role hub";// +">%temp%\\sel.txt %temp%\\err.txt 2<&1";
		log.info(command);
		try {
			p = Runtime.getRuntime().exec(command);
			Thread.sleep(10000);
			try {
				returnvalue = p.exitValue();
			} catch (Exception e) {
				returnvalue = 0;
			}
		} catch (Exception e) {
			log.error("Error occoured while starting the Hub server.");
			log.error(e.getMessage());
			throw e;
		}
		return getHubStatus(returnvalue);
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to start selenium webdriver node on
	// mac machine
	// Argument type :None
	// Variables and type : String hubIP,String hubPort,String
	// browserName,String seleniumJarLocation,String
	// seleniumDriverLocation,String nodeIP,String nodePort,String
	// nodeUsername,String nodePassword
	// Created Date : 07/10/2014
	// Modified Date :
	// Modified By :
	// ****************************************
	public static void startNode_Mac(String hubIP, String hubPort,
		String browserName, String seleniumJarLocation,
		String seleniumDriverLocation, String nodeIP, String nodePort,
		String nodeUsername, String nodePassword) throws Exception {

		int exitStatus = 0;
		log.info("Starting node on mac machine with IP : "+ nodeIP);
		try {
			Session session = null;
			String command = "";
			String[] browserNames = browserName.split("::");
			String browser_Name = "";
			for (int i = 0; i < browserNames.length; i++) {
				if ((browserNames[i]).length() != 0) {
					browser_Name = browser_Name + " -browser browserName="
							+ browserNames[i];
				}
			}

			command = "java -Dwebdriver.firefox.profile=default -Dwebdriver.chrome.driver="
					+ seleniumDriverLocation
					+ "//chromedriver"
					+ " -jar "
					+ seleniumJarLocation
					+ " -role node -hub http://"
					+ hubIP
					+ ":"
					+ hubPort
					+ "/grid/register -port "
					+ nodePort
					+ " "
					+ browser_Name;// +" -browserName="+browserName;
			log.debug(command);
			JSch jsch = new JSch();
			session = jsch.getSession(nodeUsername, nodeIP, 22);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setPassword(nodePassword);
			session.setConfig(config);
			session.connect();
			boolean isConnected = session.isConnected();

			if (isConnected) {
				log.info("SSH to node is successful");
				ChannelExec ChannelExec_1 = (ChannelExec) session
						.openChannel("exec");
				ChannelExec_1.setCommand(command);
				ChannelExec_1.connect();
				Thread.sleep(2000);
				exitStatus = ChannelExec_1.getExitStatus();
				ChannelExec_1.disconnect();
				if (exitStatus < 0) {
					log.warn("Cache clear returned "+exitStatus+" < 0.");
				} else if (exitStatus > 0) {
					log.error("Cache clear returned "+exitStatus+" > 0.");
				} else {
					log.info("Cache clear returned 0 - ok.");
				}
				Thread.sleep(10000);
				session.disconnect();
			} else {
				log.error("Could not connect to the node");
			}
		} catch (Exception e) {
			log.error("Following error occoured while connecting to the node");
			log.error(e.getMessage());
			throw e;
		}
		getNodeStatus(nodeIP, nodePort, 0);
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to shutdown the hub
	// Argument type :None
	// Variables and type :None
	// Created Date : 07/16/2014
	// Modified Date :
	// Modified By :
	// ****************************************
	public static void shutdownHub() throws Exception {

		String hubIP = Config.getHubIP();
		String hubPort = Config.getProperty_S("hubPort").trim();
		String request = "http://" + hubIP + ":" + hubPort
				+ "/lifecycle-manager?action=shutdown";
		log.info("Shutting down the hub on machine with IP : "+ hubIP);
		if ((sendUrlRequest(request)).equals("")) {
			log.info("Shutdown the hub successfully on machine with IP : "+ hubIP);
		} else {
			log.info("Could not(IP is not reachable) Shutdown the hub on machine with IP : "+ hubIP);
		}
		Thread.sleep(5000);
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to shutdown the given node
	// Argument type :String nodeIP,String nodePort
	// Variables and type :None
	// Created Date : 07/16/2014
	// Modified Date :
	// Modified By :
	// ****************************************
	public static void shutdownNode(String nodeIP, String nodePort)
		throws Exception {

		try {
			log.info("Shutting down the node on machine with IP : "+ nodeIP);
			String request = "http://" + nodeIP + ":" + nodePort
					+ "/selenium-server/driver/?cmd=shutDownSeleniumServer";
			log.debug ("Shutdown request "+request);
			String response = sendUrlRequest(request);
			log.debug ("Shutdown response "+response);
			if (response.contains("OK") == true) {
				log.info("Shutdown the node successfully on machine with IP : "+ nodeIP);
			} else {
				log.error("Could not(IP is not reachable) Shutdown the node on machine with IP : "+ nodeIP);
			}
			Thread.sleep(5000);
		} catch (Exception e) {
			log.debug("Caught exception: '"+e.getMessage()+"' continuing.");
		}
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to get the node running status
	// Argument type :String nodeIP,String nodePort
	// Variables and type :String nodeIP,String nodePort
	// Created Date : 07/16/2014
	// Modified Date :31/10/2014
	// Modified By :Sathish DS
	// ****************************************
	public static boolean getNodeStatus(String nodeIP, String nodePort,
		int returnvalue) throws Exception {

		String hubIP = Config.getHubIP();
		String hubPort = Config.getProperty_S("hubPort").trim();
		String request = "http://" + hubIP + ":" + hubPort
			+ "/grid/api/proxy?id=" + "http://" + nodeIP + ":" + nodePort;
		log.info("Retriving the node status on machine with IP : "+ nodeIP);
		
		if (returnvalue == 0) {
			log.info("Start node command is executed successfully on machine with IP : "
				+ nodeIP);
			Thread.sleep(5000);
			if((sendUrlRequest(request)).contains("Cannot find proxy") == true) {
				log.error("Node is not started on machine with IP : "
					+ nodeIP+ " Please check the node IP address on hub machine UI");
				return false;
			}
			log.info("Node is running on machine successfully with IP : "
				+ nodeIP);	
			return true;
		
		}
		log.error("Node command is failed on machine with IP : "
			+ nodeIP);
		log.error("Node is not started on machine with IP : "
			+ nodeIP+" Please check the node command");
		return false;				
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to get the hub running status
	// Argument type :String nodeIP,String nodePort
	// Variables and type :
	// Created Date : 08/16/2014
	// Modified Date :31/10/2014
	// Modified By :Sathish DS
	// ****************************************
	public static boolean getHubStatus(int returnvalue) throws Exception {

		String hubIP = Config.getHubIP();
		String hubPort = Config.getProperty_S("hubPort").trim();
		String request = "http://" + hubIP + ":" + hubPort + "/grid/console";
		log.info("Retriving the hub status on machine with IP : "+ hubIP);
		if(returnvalue == 0) {
			log.info("Hub command is executed successfully on machine with IP : "
				+ hubIP);
			if(((sendUrlRequest(request)) == "error")) {
				log.error("Hub is not running on machine with IP : "
					+ hubIP+ " Please check the IP address of the hub machine and the given IP address in config.properties is same");
				return false;
			}
			log.info("Hub is running on machine successfully with IP : "
				+ hubIP);	
			return true;		
		}
		log.error("Hub command is failed on machine with IP : "+ hubIP);
		log.error("Hub is not running on machine with IP : "+ hubIP+" Please check the hub command");
		return false;		
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to get the grid running status
	// Argument type :String nodeIP,String nodePort
	// Variables and type :
	// Created Date : 08/16/2014
	// Modified Date :
	// Modified By :
	// ****************************************
	public static boolean getGridStatus(String nodeIP, String nodePort)
		throws Exception {
		return (getHubStatus(0) && getNodeStatus(nodeIP, nodePort, 0));
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to send the http request to the
	// server
	// Argument type :String nodeIP,String nodePort
	// Variables and type :String urlString
	// Created Date : 07/16/2014
	// Modified Date :
	// Modified By :
	// ****************************************
	public static String sendUrlRequest(String urlString) throws Exception {
		boolean flag = true;
		String response = "Error";
		while (flag == true) {
			BufferedReader reader = null;
			try {
				URL url = new URL(urlString);
				reader = new BufferedReader(new InputStreamReader(
						url.openStream()));
				StringBuffer buffer = new StringBuffer();
				int read;
				char[] chars = new char[1024];
				while ((read = reader.read(chars)) != -1)
					buffer.append(chars, 0, read);
				flag = false;
				response = buffer.toString();
			} catch (Exception e) {
				response = "error"; // This is only for hub
				flag = false;
				log.error("Following exception occured: "+e.getMessage()+"'.");
				throw e;
			} finally {
				if (reader != null)
					reader.close();
			}
		}
		return response;
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to setup the selenium grid
	// Argument type :None
	// Variables and type :
	// Created Date : 07/15/2014
	// Modified Date :
	// Modified By :
	// ****************************************

	public static boolean setupSeleniumGrid() throws Exception {

		log.info("Starting grid setup.");
		String hubIP = Config.getHubIP();
		String hubPort = Config.getProperty_S("hubPort");
		String hubSeleniumServerLocation =
				Config.getProperty_S("hubSeleniumJarLocation");
		String hubPowerShellLocation = 
				Config.getProperty_S("hubPowerShellLocation");
		String browserNames = "";
		GopherData gopherData = null;
		try {
			if (startHub(hubSeleniumServerLocation)) {
				gopherData = GopherDataFactory.getGopherData();
				List<TestNode> testNodes = gopherData.findTestNodes();
				for (TestNode testNode : testNodes) {
					String nodeIP = testNode.getNodeIP();
					String nodePort = testNode.getNodePort();
					PlatformEnum osName = testNode.getPlatform();
					String nodeUserName = testNode.getUserName();
					String nodePassword = testNode.getPassword();
					if (nodePassword == null) {
						// Get the password from the user.
						nodePassword = getPassword (nodeIP);
					}
					String installDir = testNode.getInstallDir();
					String seleniumServer = testNode.getSeleniumServer();
					String seleniumClearCacheScriptPath = installDir;
					String seleniumServerPath = installDir+"//"+seleniumServer;
					String seleniumDriverPath = installDir;

					log.debug ("installDir \""+installDir+"\"");
					log.debug ("seleniumServer \""+seleniumServer+"\"");
					log.debug ("seleniumClearCacheScriptPath \""+seleniumClearCacheScriptPath+"\"");
					log.debug ("seleniumDriverPath \""+seleniumDriverPath+"\"");

					if (osName.isWindows()) {
						startNode_Windows(hubPowerShellLocation, hubIP,
							hubPort, browserNames,
							seleniumServerPath, seleniumDriverPath,
							nodeIP, nodePort, nodeUserName,
							nodePassword);
						clearBrowserCache(osName, nodeIP, nodePort,nodeUserName,nodePassword,seleniumClearCacheScriptPath);
					} else if (osName.isMac()) {
						startNode_Mac(hubIP, hubPort, browserNames,
							seleniumServerPath, seleniumDriverPath,
							nodeIP, nodePort, nodeUserName,
							nodePassword);
						clearBrowserCache(osName, nodeIP, nodePort,nodeUserName,nodePassword,seleniumClearCacheScriptPath);
					} else {
						log.error("Unsupported OS while starting the node");
					}
				}
				return true;
			}
			log.error("The Hub is not started.");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
		}
	}

	// ****************************************
	// Method :
	// Author : Sathish DS
	// Definition : This method will be used to shutdown the selenium grid
	// Argument type :None
	// Variables and type :
	// Created Date : 07/15/2014
	// Modified Date :
	// Modified By :
	// ****************************************

	public static void shutdownSeleniumGrid() throws Exception {

		log.info("Shutting down the grid.");
		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();
			List<TestNode> testNodes = gopherData.findTestNodes();
			for (TestNode testNode : testNodes) {
				String nodeIP = testNode.getNodeIP();
				String nodePort = testNode.getNodePort();
				PlatformEnum osName = testNode.getPlatform();
				String nodeUserName = testNode.getUserName();
				String nodePassword = testNode.getPassword();
				if (nodePassword == null) {
					// Get the password from the user.
					nodePassword = getPassword (nodeIP);
				}
				String installDir = testNode.getInstallDir();
				String seleniumServer = testNode.getSeleniumServer();
				String seleniumClearCacheScriptPath = installDir;
				log.debug ("installDir \""+installDir+"\"");
				log.debug ("seleniumServer \""+seleniumServer+"\"");
				log.debug ("seleniumClearCacheScriptPath \""+seleniumClearCacheScriptPath+"\"");
				log.debug("nodeIP "+nodeIP+" nodePort "+nodePort+" osName "+osName);

				shutdownNode(nodeIP, nodePort);
				clearBrowserCache(osName, nodeIP, nodePort,
					nodeUserName,nodePassword,seleniumClearCacheScriptPath);
			}
			shutdownHub();
		} catch (Exception e) {
			log.error("Error trying to shut down grid.");
			log.error(e.getMessage());
			throw e;
		} finally {
			if (gopherData != null) {
				gopherData.close();
			}
		}
		log.info("Shut down complete.");
	}

	/**
	 * Get the password from the user.
	 * Read from System.in.
	 * @return the password
	 */
	private static String getPassword (String testNodeIP) {
		String password = null;

		// If a Console is available (we are outside an IDE)
		Console console = System.console();
		
		if (console != null) {
			// Use Console, this will not echo the password.
			char[] password_ = console.readPassword("Enter password for test node "+testNodeIP+": ");  
			password = new String (password_);
		} else {
			// We are probably in an IDE, let the password echo.
			Scanner sc = null;
			try {
				sc = new Scanner(System.in);
				System.out.print("Enter password for test node "+testNodeIP+": ");
				while(sc.hasNextLine()) {
					password = sc.nextLine();
					break;
				}
			} finally {
				if (sc != null) {
					sc.close();
				}
			}
		}

		return password;
	}
}