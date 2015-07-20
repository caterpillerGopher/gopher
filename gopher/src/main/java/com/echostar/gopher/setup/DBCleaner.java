package com.echostar.gopher.setup;

import com.echostar.gopher.persist.GopherData;
import com.echostar.gopher.persist.GopherDataFactory;

/**
 * Clean the database.
 * @author charles.young
 *
 */
public class DBCleaner {

	/**
	 * Clean the DB.
	 * @param args command-line arguments
	 */
	public static void main (String[] args) {
		try {
			cleanDB ();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.exit(0);
	}
	
	/**
	 * Clean the DB.
	 * @throws	on any exception
	 */
	public static void cleanDB () throws Exception {
		GopherData gopherData = GopherDataFactory.getGopherData();

		System.out.println ("Cleaning database ...");
		gopherData.cleanDB();		
		System.out.println ("Done.");
	}
}
