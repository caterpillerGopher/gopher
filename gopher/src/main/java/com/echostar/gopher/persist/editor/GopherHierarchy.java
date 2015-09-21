package com.echostar.gopher.persist.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.deetysoft.util.Hierarchy;
import com.deetysoft.util.HierarchyNode;
import com.echostar.gopher.persist.BrowserEnum;

public class GopherHierarchy {

	// Branches for each XML element type
	Collection<HierarchyNode> suiteNodes;
	Collection<HierarchyNode> suiteDecoratorNodes;
	Collection<HierarchyNode> testSuiteDecoratorNodes;
	Collection<HierarchyNode> testSuiteNodes;
	Collection<HierarchyNode> testClassDecoratorNodes;
	Collection<HierarchyNode> testClassNodes;
	Collection<HierarchyNode> testCaseNodes;
	Collection<HierarchyNode> testDataTypeNodes;
	Collection<HierarchyNode> testDataNodes;
	Collection<HierarchyNode> elementLocatorNodes;
	Collection<HierarchyNode> testRunNodes;
	Collection<HierarchyNode> testNodeNodes;
	Collection<HierarchyNode> browserNodes;

	/**
	 * Read all the given XML and create a Hierarchy of the merged data.
	 *
	 * @param	fileArg		a ';' delimited list of XML files and/or directories containing Gopher data definitions
	 * @param	dtdName		the DTD file for Gopher data
	 * @throws	Exception	on any error
	 */
	public GopherHierarchy (String fileArg, String dtdName) throws Exception {

		List<URL> dtds = new ArrayList<URL>();

		URL dtdURL = null;
		if (dtdName != null) {
			dtdURL = new File(dtdName).toURI().toURL();
		}

		// Build a collection of InputStreams, one per input XML file.
		List<InputStream> fileStreams = new ArrayList<InputStream>();

		String[] fileArgs = fileArg.split(";");

		// For every file or directory in the list
		for (int i = 0; i < fileArgs.length; i++) {
			// If the file is not a directory
			File f = new File (fileArgs[i]);
			if (!f.isDirectory()) {
				// Add the file to the list of file streams
				FileInputStream xmlStream = new FileInputStream(fileArgs[i]);
				fileStreams.add (xmlStream);
				// Add the DTD if any to the list of DTDs
				dtds.add(dtdURL);
			} else {
				// For every XML file in the directory
				String[] dirFiles = f.list();
				String dirName = f.getPath();
				for (int j = 0; j < dirFiles.length; j++) {
					if (dirFiles[j].endsWith(".xml")) {					
						// Add the file to the list of file streams
						FileInputStream xmlStream = new FileInputStream(dirName + "/" + dirFiles[j]);
						fileStreams.add (xmlStream);
						// Add the DTD if any to the list of DTDs
						dtds.add(dtdURL);
					}
				}
			}
		}

		HierarchyNode root = Hierarchy.buildFromXml(fileStreams, dtds, false);

		// Get the root of each branch for each element type in the model.

		testClassNodes = root.getNodesByPath("gopher-data/test-class");
		suiteNodes = root.getNodesByPath("gopher-data/suite");
		suiteDecoratorNodes = root.getNodesByPath("gopher-data/suite-decorator");
		testSuiteDecoratorNodes = root.getNodesByPath("gopher-data/test-suite-decorator");
		testSuiteNodes = root.getNodesByPath("gopher-data/test-suite");
		testClassDecoratorNodes = root.getNodesByPath("gopher-data/test-class-decorator");
		testDataTypeNodes = root.getNodesByPath("gopher-data/test-data-type");
		testDataNodes = root.getNodesByPath("gopher-data/test-data");
		elementLocatorNodes = root.getNodesByPath("gopher-data/element-locator");
		testCaseNodes = root.getNodesByPath("gopher-data/test-case");
		testRunNodes = root.getNodesByPath("gopher-data/test-run");
		testNodeNodes = root.getNodesByPath("gopher-data/test-node");
		browserNodes = root.getNodesByPath("gopher-data/supported-browser");
		
		System.out.println ("Read "+suiteNodes.size()+" suite,\n"+
				testSuiteNodes.size()+" test-suite,\n"+
				testClassNodes.size()+" test-class,\n"+
				testCaseNodes.size()+" test-case,\n"+
				testDataTypeNodes.size()+" test-data-type,\n"+
				testDataNodes.size()+" test-data,\n"+
				elementLocatorNodes.size()+" element-locator,\n"+
				suiteDecoratorNodes.size()+" suite-decorator,\n"+
				testSuiteDecoratorNodes.size()+" test-suite-decorator,\n"+
				testClassDecoratorNodes.size()+" test-class-decorator,\n"+
				testRunNodes.size()+" test-run,\n"+
				browserNodes.size()+" supported-browser,\n"+
				testNodeNodes.size()+" test-node.");
	}

	/**
	 * Find the HierarchyNode in the 'browser' branch with child nodes having the given
	 * names and values.
	 *
	 * @param browserNodes	a collection HierarchyNode representing 'browser' elements from
	 * 						Gopher XML
	 * @param name			the bowser name
	 * @param type			the browser type
	 * @return				the HierarchyNode if any
	 */
	public HierarchyNode findBrowserNode (String name, String type) {
		
		for (HierarchyNode browserNode : browserNodes) {
			String name_ = (String) browserNode.getNodeByName ("name").getValue();
			String typeStr = (String) browserNode.getNodeByName ("browser-type").getValue();
			BrowserEnum type_ = BrowserEnum.valueOf(typeStr);
			if (name.equals(name_) && type.equals(type_)) {
				// Match found
				return browserNode;
			}
		}
		return null;
	}

	/**
	 * Find the HierarchyNode in the 'test-node' branch with child nodes having the given
	 * names and values.
	 *
	 * @param browserNodes	a collection HierarchyNode representing 'browser' elements from
	 * 						Gopher XML
	 * @param ip			the ip address
	 * @param port			the port number
	 * @return				the HierarchyNode if any
	 */
	public HierarchyNode findTestNodeNode (String ip, String port) {
		
		for (HierarchyNode testNodeNode : testNodeNodes) {
			String ip_ = (String) testNodeNode.getNodeByName ("node-ip").getValue();
			String port_ = (String) testNodeNode.getNodeByName ("node-port").getValue();
			if (ip.equals(ip_) && port.equals(port_)) {
				// Match found
				return testNodeNode;
			}
		}
		return null;
	}
}
