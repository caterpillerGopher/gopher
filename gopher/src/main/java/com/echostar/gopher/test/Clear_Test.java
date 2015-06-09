package com.echostar.gopher.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;

public class Clear_Test  {

	protected Clear_Test() throws Exception {
	}

	/**
	 * Override super. Mock all necessary data.
	 * @throws	Exception	on error
	 */
	@Test
	public void clearDB () throws Exception {

		Logger log = Logger.getLogger(getClass());
		log.debug("In clearDB.");
		GopherData gopherData = null;

		try {
			log.trace("Getting gopherData.");
			gopherData = GopherDataFactory.getGopherData();

			log.trace("Cleaning db.");
			gopherData.cleanDB ();
		} finally {
			if (gopherData != null) {
				gopherData.close();
				log.trace("gopherData closed.");
			}
			log.debug("Leaving clearDB.");
		}
	}
}
