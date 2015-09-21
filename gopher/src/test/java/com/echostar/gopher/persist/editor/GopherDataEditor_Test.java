package com.echostar.gopher.persist.editor;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.Browser;
import com.echostar.gopher.persist.BrowserEnum;
import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;

public class GopherDataEditor_Test {

	@Test
	public static void testDelete () throws Exception {
		GopherData gopherData = GopherDataFactory.getGopherData();
		gopherData.cleanDB();

		// Insert a Browser and apply an empty XML.
		// This should cause the browser to be deleted.
		Session session = gopherData.getHibernateSession();
		Transaction trans = session.beginTransaction();
		gopherData.createBrowser("blee", BrowserEnum.IEXPLORE);
		trans.commit();
		session.flush();

		Collection<Browser> browsers = gopherData.findAllBrowsers();
		Assert.assertEquals(browsers.size(), 1);

		String dtdName = "src/main/java/gopher-data.dtd";
		String fileNames = "src/test/resources/com/echostar/gopher/persist/editor/deleteTest1.xml";
		GopherDataEdit editor = new GopherDataEdit (fileNames, dtdName);
		editor.doEdit(false);

		gopherData = GopherDataFactory.getGopherData();
		browsers = gopherData.findAllBrowsers();
		Assert.assertEquals(browsers.size(), 0);
	}
}
