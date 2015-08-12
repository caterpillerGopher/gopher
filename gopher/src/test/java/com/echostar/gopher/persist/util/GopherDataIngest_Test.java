package com.echostar.gopher.persist.util;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.ElementLocator;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;
import com.echostar.gopher.persist.TestCase;
import com.echostar.gopher.persist.TestData;

/**
 * Test the Gopher XML data ingester {@link GopherDataIngest GopherDataIngest}.
 * @author charles.young
 *
 */
public class GopherDataIngest_Test {

	final static String dtdName = "src/main/java/gopher-data.dtd";

	public GopherDataIngest_Test () {
		
	}

	@Test
	public static void testElementLocator () throws Exception {

		final String xmlPath = "src/test/resources/com/echostar/gopher/persist/util/GopherDataIngest_Test/testElementLocator.xml";

		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();
			gopherData.cleanDB();

			GopherDataIngest ingestor = new GopherDataIngest();
			ingestor.ingest(xmlPath, dtdName);

			List<TestCase> testCase1s = gopherData.findTestCasesByName("case 1");
			List<TestCase> testCase2s = gopherData.findTestCasesByName("case 2");

			Assert.assertEquals(testCase1s.size(), 1);
			Assert.assertEquals(testCase2s.size(), 1);

			TestCase testCase1 = testCase1s.get(0);
			TestCase testCase2 = testCase2s.get(0);
			List<ElementLocator> locators1 = testCase1.getElementLocators();
			List<ElementLocator> locators2 = testCase2.getElementLocators();

			Assert.assertEquals(locators1.size(), 1);
			Assert.assertEquals(locators2.size(), 2);

			ElementLocator locator1 = locators1.get(0);
			Assert.assertEquals(locator1.getName(), "locator 1");

			ElementLocator locator2 = locators2.get(0);
			ElementLocator locator3 = locators2.get(1);
			if (locator2.getName().equals("locator 1")) {
				Assert.assertEquals(locator3.getName(), "locator 2");
			} else if (locator2.getName().equals("locator 2")) {				
				Assert.assertEquals(locator3.getName(), "locator 1");
			} else {
				throw new Exception ("Locators for case 2 unexpected.");
			}
			
			List<ElementLocator> locators = gopherData.findAllElementLocators();
			Assert.assertEquals(locators.size(), 2);

		} finally {
			if (gopherData!=null) {
				gopherData.close();
			}
		}
	}

	@Test
	public static void testTestData () throws Exception {

		final String xmlPath = "src/test/resources/com/echostar/gopher/persist/util/GopherDataIngest_Test/testTestData.xml";

		GopherData gopherData = null;
		try {
			gopherData = GopherDataFactory.getGopherData();
			gopherData.cleanDB();

			GopherDataIngest ingestor = new GopherDataIngest();
			ingestor.ingest(xmlPath, dtdName);

			List<TestCase> testCase1s = gopherData.findTestCasesByName("case 1");
			List<TestCase> testCase2s = gopherData.findTestCasesByName("case 2");

			Assert.assertEquals(testCase1s.size(), 1);
			Assert.assertEquals(testCase2s.size(), 1);

			TestCase testCase1 = testCase1s.get(0);
			TestCase testCase2 = testCase2s.get(0);
			List<TestData> testDatas1 = testCase1.getTestData();
			List<TestData> testDatas2 = testCase2.getTestData();

			Assert.assertEquals(testDatas1.size(), 1);
			Assert.assertEquals(testDatas2.size(), 2);

			TestData testData1 = testDatas1.get(0);
			Assert.assertEquals(testData1.getDataValue(), "test data 1");

			TestData testData2 = testDatas2.get(0);
			TestData testData3 = testDatas2.get(1);
			if (testData2.getDataValue().equals("test data 1")) {
				Assert.assertEquals(testData3.getDataValue(), "test data 2");
			} else if (testData2.getDataValue().equals("test data 2")) {				
				Assert.assertEquals(testData3.getDataValue(), "test data 1");
			} else {
				throw new Exception ("TestData for case 2 unexpected. TestData value ='"+testData2.getDataValue()+"'.");
			}
			
			List<TestData> testDatas = gopherData.findAllTestData();
			Assert.assertEquals(testDatas.size(), 2);

		} finally {
			if (gopherData!=null) {
				gopherData.close();
			}
		}
	}
}