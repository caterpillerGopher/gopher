package com.echostar.gopher.selenium;

public class GopherDriverFactory {

	protected GopherDriverFactory () {
		
	}
	
	public static GopherDriver getGopherDriver (String browserName,
            String platform, String nodeIp, String nodePort) throws Exception {

		return new GopherDriverImpl (browserName, platform, nodeIp, nodePort);
	}
}
