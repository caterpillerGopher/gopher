package com.echostar.gopher.persist.editor;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.deetysoft.util.HierarchyNode;
import com.echostar.gopher.persist.Browser;
import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestNode;

/**
 * Edit Gopher Model data in a Gopher database based on differences with a set of
 * Gopher Model XML files.
 *
 * Read either a single Gopher XML file or directory or list of either separated by a ';'.
 * A path to a Gopher DTD file is required.
 * 
 * Maintain a set of generated {@link TestRun TestRuns} for {@link TestCase TestCases}.
 * The set is based on all combinations of {@link TestNode TestNodes} and supported {@link Browser Browsers}.
 * 
 * @author charles.young
 * @see	#usage	for command-line arguments to main.
 */
public class GopherDataEdit {

	private Logger log = null;

	// The interface into the Gopher database
	private GopherData gopherData;

	// The Hibernate Session
	private Session session;

	// The reporter for our edits
	private Reporter reporter;

	// A Hierarchy of Gopher data
	private GopherHierarchy	gopherHierarchy = null;

	/**
	 * Construct with a set of XML file paths and a path to a Gopher DTD file.
	 * @param fileArg		a ';' delimited set of Gopher XML files or directories containing XML.
	 * @param dtdName		a path to a Gopher DTD file
	 * @throws Exception	on any error
	 */
	GopherDataEdit (String fileArg, String dtdName) throws Exception {
		log = Logger.getLogger(getClass());
		gopherData = GopherDataFactory.getGopherData();
		session = gopherData.getHibernateSession();
		gopherHierarchy = new GopherHierarchy (fileArg, dtdName);
		reporter = new Reporter ();
	}

	/**
	 * Run the editor with arguments.
	 * 
	 * @param args	the command-line arguments
	 * @see			#usage
	 */
	public static void main (String[] args) {

		// The file argument.
		// This may be a single file or directory or list of either separated by a ';'.
		String fileArg		= null;

		// Name of data DTD file.
		String dtdFileName	= null;

		// Create a report.
		boolean doReport = true;

		// Report-only, no update.
		boolean reportOnly = true;

		for (int i=0; i < args.length; i++) {
			switch (args[i]) {
				case "-h":
				case "-help":
					System.out.println (getUsage ());
					break;
				case "-dtd":
					dtdFileName = args[++i];
					break;
				case "-ro":
					reportOnly = true;
				case "-r":
					doReport = true;
					break;
				default:
					if (args[i].charAt(0) != '-') {
						fileArg = args[i];
					}
					break;
			}
		}

		if (fileArg == null) {
			System.err.println ("The file name/dir argument is missing.");
			System.err.println ("\n"+getUsage ());
			System.exit (1);
		}

		GopherDataEdit editor = null;

		try {
			// Construct with the set of Gopher XML.
			editor = new GopherDataEdit (fileArg, dtdFileName);

			// Find all the edits for the database and make a record for use by doReport.
			// If reportOnly do not commit edits.
			editor.doEdit(reportOnly);

			if (doReport) {
				editor.doReport();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit (-1);
		}
		finally {
			if (editor != null) {
				editor.close();
			}
		}
		System.exit (0);
	}

	/**
	 * Edit the Gopher data model in a Gopher database using the given Gopher XML files or directories.
	 * Do updates, inserts and deletes based on differences between the XML and the tables.
	 *
	 * @param	fileArg		a ';' delimited list of XML files and/or directories containing Gopher data definitions
	 * @param	dtdName		the DTD file for Gopher data
	 * @throws	Exception	on any error
	 */
	public void doEdit (boolean reportOnly) throws Exception {

		Transaction trans = null;
		try {
			trans = session.beginTransaction();
			doUpdates();
			doDeletions ();
			doInsertions();
			if (!reportOnly) {
				log.debug("Committing.");
				trans.commit();
				session.flush();
			}
		} finally {
			if (trans != null && !trans.wasCommitted()) {
				trans.rollback();
			}
			session.close();
			if (!reportOnly) {
				System.out.println ("Edit done.");
			}
		}
	}

	/**
	 * Delete any elements in the database not in the Hierarchy.
	 */
	private void doDeletions () throws Exception {

		//Delete Browsers
		Collection<Browser> browsers = gopherData.findAllBrowsers();
		for (Browser browser : browsers) {
			// Search the browser nodes for match on name and type.
			String name = browser.getName();
			BrowserEnum type = browser.getType();
			log.debug("Looking for Browser named '"+name+"'");
			HierarchyNode browserNode = gopherHierarchy.findBrowserNode (name, type.getValue());

			// No match - delete
			if (browserNode==null) {
				log.debug("Deleting Browser named '"+name+"'.");
				session.delete(browser);
				// Record deletion.
				reporter.objectDeleted(browser);
			}
		}

		//Delete TestNodes
		Collection<TestNode> testNodes = gopherData.findAllTestNodes();
		for (TestNode testNode : testNodes) {
			// Search the test node nodes for match on ip and port.
			String ip = testNode.getNodeIP();
			String port = testNode.getNodePort();
			HierarchyNode testNodeNode = gopherHierarchy.findTestNodeNode (ip, port);

			// No match - delete
			if (testNodeNode==null) {
				session.delete(testNode);
				reporter.objectDeleted(testNode);
			}
		}
	}

	/**
	 * Insert any elements in the Hierarchy not in the database.
	 * @throws Exception on any error
	 */
	private void doInsertions () throws Exception {

		// Insert browsers.
		for (HierarchyNode browserNode : gopherHierarchy.browserNodes) {
			String name = (String) browserNode.getNodeByName ("name").getValue();
			String typeStr = (String) browserNode.getNodeByName ("browser-type").getValue();
			BrowserEnum type = BrowserEnum.valueOf(typeStr);
			List<Browser> browsers = gopherData.findBrowser (name, type);
			if (browsers.size() == 0) {
				// Insert
				Browser browser = gopherData.createBrowser(name, type);				
				// Record insertion.
				reporter.objectInserted(browser);
				continue;
			}
		}
	}

	/**
	 * Update any elements in the database in the Hierarchy.
	 * @throws Exception on any error
	 */
	private void doUpdates () throws Exception {

		// Update browsers.
		for (HierarchyNode browserNode : gopherHierarchy.browserNodes) {
			String name = (String) browserNode.getNodeByName ("name").getValue();
			String typeStr = (String) browserNode.getNodeByName ("browser-type").getValue();
			BrowserEnum type = BrowserEnum.valueOf(typeStr);
			List<Browser> browsers = gopherData.findBrowser (name, type);
			if (browsers.size() == 1) {
				// Update
				Browser browser = new Browser (name, type);
				session.save(browser);
				reporter.objectUpdated(browser);
				continue;
			}
		}
	}

	/**
	 * Generate the report.
	 */
	void doReport () {
		reporter.doReport ();
		System.out.println ("Report done.");
	}

	/**
	 * Close the GopherData interface and any other resources.
	 */
	protected void close () {
		if (gopherData != null) {
			gopherData.close();
		}
	}

	/**
	 * Get the usage message.
	 * @return	the message
	 */
	public static String getUsage () {

		StringBuffer usage = new StringBuffer ();

		usage.append("Usage: GopherDataEdit [\'file1;file2\' or \'dir1;dir2\' or \'file1;dir1\'] -dtd 'dtdFile'");
		usage.append("\nwhere \'file*\' is the path to a XML file containing Gopher data definitions.");
		usage.append("\nwhere \'dir*\' is the path to a directory containing Gopher data definitions files.");
		usage.append("\nwhere \'dtdFile\' is the name of an optional DTD file containing Gopher data XML schema.");
		return usage.toString();
	}
}
