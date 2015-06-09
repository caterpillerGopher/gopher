package com.echostar.gopher.reports;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.SuiteInstance;
import com.echostar.gopher.persist.TestException;
import com.echostar.gopher.persist.TestNode;
import com.echostar.gopher.persist.TestRun;
import com.echostar.gopher.persist.TestRunResult;
import com.echostar.gopher.persist.TestSuiteInstance;
import com.echostar.gopher.util.Config;

public class GopherReporter {

	// Report type executive
	public final static String EXECUTIVE = "executive";

	private GopherData gopherData = null;

	public GopherReporter (GopherData gopherData) {
		this.gopherData = gopherData;
	}

	public static void main (String[] args) {

		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();
			GopherReporter reporter = new GopherReporter (gopherData);
			// Put the executive report first in the file.
			reporter.report (EXECUTIVE);
		} catch (Exception e) {
			e.printStackTrace ();
		} finally {
			if (gopherData != null ) {
				gopherData.close();
			}
			System.out.println ("Done.");
		}
		System.exit(0);
	}

	public void report (String type) throws Exception  {

		FileWriter writer = null;
		try {
			String filePath = Config.getProperty_S("GopherReporter.reportFilePath");
			File f = new File (filePath);
			System.out.println ("Writing '"+f.getPath()+"'.");
			writer = new FileWriter (f);

			report(writer, type);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void report(FileWriter writer, String type) throws Exception {
		String header = "<head>\n<title>Gopher Report</title>\n</head>\n";
		writer.write(header, 0, header.length());
		writeBody(writer, type);
	}

	/**
	 * Write the body.
	 * @param writer		an open FileWriter to write with
	 * @param type			the type of body
	 * @throws Exception	on any exception
	 */
	private void writeBody(FileWriter writer, String type) throws Exception {
		String element = "<body>\n";
		writer.write(element, 0, element.length());

		//if (type.equals(EXECUTIVE)) {
		//	writeExecutivePage (writer);
		//}
		//writeIndexPage(writer);
		//if (!type.equals(EXECUTIVE)) {
		//	writeExecutivePage (writer);
		//}

		List<SuiteInstance> suiteInstances = gopherData.findAllSuiteInstances ();
		writeSuiteInstancesView(writer, suiteInstances);
        writeSuiteInstanceViews(writer, suiteInstances);
        writeTestSuiteInstanceViews(writer, suiteInstances);
		writeTestRunResultViews(writer, suiteInstances);
		writeTestExceptionViews(writer, suiteInstances);
		
		String trailer = "\n</body>";
		writer.write(trailer, 0, trailer.length());
	}

	//private void writeIndexPage(FileWriter writer) throws Exception {
	//	
	//}

	//private void writeExecutivePage(FileWriter writer) throws Exception {
	//}

	private static void writeSuiteInstancesView(FileWriter writer,
		List<SuiteInstance> suiteInstances) throws Exception {

		String element = "</p>SuiteInstances</br>";
		writer.write(element, 0, element.length());

		element = "<Table border=\"1\">\n";
		writer.write(element, 0, element.length());

		for (SuiteInstance suiteInstance : suiteInstances) {
			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = "<a href=\"#SuiteInstance_"+suiteInstance.getId()+"\">"+
				suiteInstance.getId()+"</a>";
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = suiteInstance.getSuite().getName();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = suiteInstance.getStartTime()+"</br>\n";
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());
			long delta=suiteInstance.getEndTime().getTime()-
					suiteInstance.getStartTime().getTime();
			element = Float.toString((float)delta/1000)+" sec</br>\n";
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());
			List<TestRunResult>results=suiteInstance.getTestRunResults();
			int passed=0;
			for (TestRunResult r : results) {
				if (r.getResult()) {
					passed++;
				}
			}
			element = Float.toString((float)passed/results.size()*100)+ "% true of "+results.size()+".\n";
			writer.write(element, 0, element.length());
			element = "</td></tr>\n";
			writer.write(element, 0, element.length());
		}
		element = "</Table>\n";
		writer.write(element, 0, element.length());
	}

	private static void writeSuiteInstanceViews(FileWriter writer,
		List<SuiteInstance> suiteInstances) throws Exception {
		
		for (SuiteInstance suiteInstance : suiteInstances) {
			writeSuiteInstanceView(writer, suiteInstance);
		}
	}

	private static void writeTestRunResultViews(FileWriter writer,
		List<SuiteInstance> suiteInstances) throws Exception {
				
		for (SuiteInstance suiteInstance : suiteInstances) {
			writeTestRunResultViews(writer, suiteInstance);
		}
	}

    private static void writeTestSuiteInstanceViews(FileWriter writer,
        List<SuiteInstance> suiteInstances) throws Exception {
                    
        for (SuiteInstance suiteInstance : suiteInstances) {
            writeTestSuiteInstanceViews(writer, suiteInstance);
        }
    }

    private static void writeTestExceptionViews(FileWriter writer,
		List<SuiteInstance> suiteInstances) throws Exception {
				
		for (SuiteInstance suiteInstance : suiteInstances) {
			writeTestExceptionViews(writer, suiteInstance);
		}
	}

	private static void writeSuiteInstanceView(FileWriter writer,
		SuiteInstance suiteInstance) throws Exception {
		
		String element = "</p><a id=\"SuiteInstance_"+suiteInstance.getId()+
			"\">SuiteInstance "+suiteInstance.getId()+"</a></br>";
		writer.write(element, 0, element.length());

		element = "<Table border=\"1\">\n";
		writer.write(element, 0, element.length());

		/*element = "<tr>\n";
		writer.write(element, 0, element.length());
		element = "<td>\n";
		writer.write(element, 0, element.length());
		element = Long.toString(suiteInstance.getId());
		writer.write(element, 0, element.length());
		element = "</td>\n";
		writer.write(element, 0, element.length());
		element = "</tr>\n";*/
		writer.write(element, 0, element.length());
		element = "<tr>\n";
		writer.write(element, 0, element.length());
		element = "<td>\n";
		writer.write(element, 0, element.length());
		element = suiteInstance.getSuite().getName();
		writer.write(element, 0, element.length());
		element = "</td>\n";
		writer.write(element, 0, element.length());
		element = "</tr>\n";
		writer.write(element, 0, element.length());
		element = "<tr>\n";
		writer.write(element, 0, element.length());
		element = "<td>\n";
		writer.write(element, 0, element.length());
		List<TestRunResult>results=suiteInstance.getTestRunResults();
		int passed=0;
		for (TestRunResult r : results) {
			if (r.getResult()) {
				passed++;
			}
		}
		element = Float.toString((float)passed/results.size()*100)+ "% true of "+results.size()+".\n";
		writer.write(element, 0, element.length());
		element = "</tr>\n";
		writer.write(element, 0, element.length());
		element = "<tr>\n";
		writer.write(element, 0, element.length());
		element = "<td>\n";
		writer.write(element, 0, element.length());
		element = suiteInstance.getStartTime().toString();
		writer.write(element, 0, element.length());
		element = "</td>\n";
		writer.write(element, 0, element.length());
		element = "</tr>\n";
		writer.write(element, 0, element.length());
		element = "<tr>\n";
		writer.write(element, 0, element.length());
		element = "<td>\n";
		writer.write(element, 0, element.length());
		long delta=suiteInstance.getEndTime().getTime()-
				suiteInstance.getStartTime().getTime();
		element = Float.toString((float)delta/1000)+" sec";
		writer.write(element, 0, element.length());
		element = "</td>\n";
		writer.write(element, 0, element.length());
		element = "</tr>\n";
		writer.write(element, 0, element.length());
		element = "</Table>\n";
		writer.write(element, 0, element.length());

		element = "</p>TestRunResults</br>";
		writer.write(element, 0, element.length());

		element = "<Table border=\"1\">\n";
		writer.write(element, 0, element.length());
		
		for (TestRunResult testRunResult : suiteInstance.getTestRunResults()) {
			element = "<tr>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());

			String id = Long.toString(testRunResult.getId());
			element = "<a href=\"#TestRunResult_"+id+"\">"+id+"</a>";
			writer.write(element, 0, element.length());			

			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());

			element = Boolean.toString(testRunResult.getResult());
			writer.write(element, 0, element.length());			

			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());

			element = testRunResult.getUrl();
			writer.write(element, 0, element.length());			

			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());
			TestRun testRun = testRunResult.getTestRun();
			element = testRun.getBrowser().toString();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());
			TestNode testNode = testRun.getTestNode();
			//Assert.assertNotNull(testNode);
			//Assert.assertNotNull(testNode.getPlatform());
			String platform="null";
			if (testNode != null) {
				platform=testNode.getPlatform().toString();
			}
			element = platform;
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());

			element = testRunResult.getFailureMessage();
			writer.write(element, 0, element.length());			

			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "</tr>\n";
			writer.write(element, 0, element.length());
		}
		element = "</Table>\n";
		writer.write(element, 0, element.length());
	}

    private static void writeTestSuiteInstanceViews(FileWriter writer,
        SuiteInstance suiteInstance) throws Exception {

        List<TestSuiteInstance> testSuiteInstances = suiteInstance.getTestSuiteInstances();

        for (TestSuiteInstance testSuiteInstance : testSuiteInstances) {
            String element = "</p><a id=\"TestSuiteInstance_"+testSuiteInstance.getId()+
                    "\">TestSuiteInstance "+testSuiteInstance.getId()+"</a></br>";
            writer.write(element, 0, element.length());

            element = "<Table border=\"1\">\n";
            writer.write(element, 0, element.length());

            element = "<tr>\n";
            writer.write(element, 0, element.length());
            element = "<td>\n";
            writer.write(element, 0, element.length());
            element = testSuiteInstance.getTestSuite().getName();
            writer.write(element, 0, element.length());
            element = "</td>\n";
            writer.write(element, 0, element.length());
            element = "</tr>\n";

            element = "</Table>\n";
            writer.write(element, 0, element.length());
        }
    }

	private static void writeTestRunResultViews(FileWriter writer,
		SuiteInstance suiteInstance) throws Exception {

		for (TestRunResult testRunResult : suiteInstance.getTestRunResults()) {

			String id = Long.toString(testRunResult.getId());
			String element = "</p><a id=\"TestRunResult_"+id+"\">TestRunResult "+id+"</a></br>";
			writer.write(element, 0, element.length());

			element = "<Table border=\"1\">\n";
			writer.write(element, 0, element.length());

			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = Boolean.toString(testRunResult.getResult());
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = testRunResult.getUrl();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = testRunResult.getFailureMessage();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			long delta=testRunResult.getEndTime().getTime()-
					testRunResult.getStartTime().getTime();
			element = Float.toString((float)delta/1000)+" sec";
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "</Table>\n";
			writer.write(element, 0, element.length());

			// Write a line for every TestException.
			element = "<Table border=\"1\">\n";
			writer.write(element, 0, element.length());

			for (TestException testException : testRunResult.getTestExceptions()) {

			element = "<tr>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());
			String teid = Long.toString(testException.getId());
			element = "<a href=\"#TestException_"+teid+"\">"+id+"</a>";
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = testException.getExceptionClass();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());

			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "</Table>\n";
			writer.write(element, 0, element.length());
			}

			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "</Table>\n";
			writer.write(element, 0, element.length());
		}
	}

	private static void writeTestExceptionViews(FileWriter writer,
		SuiteInstance suiteInstance) throws Exception {

		for (TestRunResult testRunResult : suiteInstance.getTestRunResults()) {

			for (TestException testException : testRunResult.getTestExceptions()) {
			String id = Long.toString(testException.getId());
			String element = "</p><a id=\"TestException_"+id+"\">TestException "+id+"</a></br>";
			writer.write(element, 0, element.length());

			element = "<Table border=\"1\">\n";
			writer.write(element, 0, element.length());

			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = id;
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = testException.getExceptionClass();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = testException.getMessage();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "<tr>\n";
			writer.write(element, 0, element.length());
			element = "<td>\n";
			writer.write(element, 0, element.length());
			element = testException.getStacktrace();
			writer.write(element, 0, element.length());
			element = "</td>\n";
			writer.write(element, 0, element.length());
			element = "</tr>\n";
			writer.write(element, 0, element.length());
			element = "</Table>\n";
			writer.write(element, 0, element.length());
			}
		}
	}
}