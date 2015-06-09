package com.deetysoft.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * A Thread that can be used to read an input stream until there is no more
 * data to read.
 */
public class StreamGobbler extends Thread {
	private static Logger	log = Logger.getLogger(
			StreamGobbler.class.getName());

	private InputStream is;
	private String name;
	private StringBuffer buffer;
	private FileWriter writer = null;

	/**
	 * Constructor.
	 *
	 * @param is the input stream to read from
	 * @param name a name for this stream gobbler
	 */
	public StreamGobbler(InputStream is, String name,
		String outputFileName) throws IOException{
		this.is = is;
		this.name = name;
		this.buffer = new StringBuffer();
		this.writer = new FileWriter(outputFileName);
	}

	public StringBuffer getStringBuffer() {
		return buffer;
	}

	/**
	 * Overrides {@link Thread#run}.
	 */
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				//buffer.append(line);
				//buffer.append("\n");
				writer.append(line);
				writer.append("\n");
			}
			is.close();
		} 
		catch (Exception e) {
			log.error("The " + name + " StreamGobbler " +
					"threw an exception.", e);
		}
	}
}