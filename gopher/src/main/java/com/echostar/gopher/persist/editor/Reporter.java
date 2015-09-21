package com.echostar.gopher.persist.editor;

public class Reporter {

	/**
	 * Generate the report.
	 */
	void doReport () {
	}

	/**
	 * Record that a Gopher persistent object was deleted.
	 * TBD - define a parent class for all Gopher POJOs.
	 * @param pojo	a Gopher Hibernate POJO
	 */
	void objectDeleted(Object pojo) {
		
	}

	/**
	 * Record that a Gopher persistent object was inserted.
	 * TBD - define a parent class for all Gopher POJOs.
	 * @param pojo	a Gopher Hibernate POJO
	 */
	void objectInserted(Object pojo) {
		
	}

	/**
	 * Record that a Gopher persistent object was updated.
	 * TBD - define a parent class for all Gopher POJOs.
	 * @param pojo	a Gopher Hibernate POJO
	 */
	void objectUpdated(Object pojo) {
		
	}
}
