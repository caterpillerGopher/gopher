package com.echostar.gopher.persist;

/**
 * A factory for the {@link GopherData GopherData} interface.
 *
 * @author charles.young
 *
 */
public class GopherDataFactory {

	protected GopherDataFactory () {}

	/**
	 * Get a {@link GopherData GopherData} interface.
	 * Users should get one interface per thread.
	 * @return	a {@link GopherData GopherData} interface
	 */
	public static GopherData getGopherData () {
		return new GopherDataImpl();
	}
}
