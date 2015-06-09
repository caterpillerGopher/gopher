package com.deetysoft.file;

import com.deetysoft.util.StringReplacer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

/**
 * A class that represents a file in the classpath.
 */
public class ClasspathFile {

	private String relativePath;

	/**
	 * Creates an instance of this class from a path that is relative
	 * to the classpath.
	 *
	 * @param relativePath a path that is relative to the classpath. 
	 * The path may point to file that is in a jar file or a file that is
	 * unpackaged on the local file system.
	 */
	public ClasspathFile(String relativePath) {
		this.relativePath = StringReplacer.replaceChar(
				relativePath, '\\', '/');
	}

	/**
	 * Returns the absolute path for this file.  
	 *
	 * @return the absolute path for this file.  Note that this method 
	 * cannot be called on a classpath file that is in a jar file.
	 * @throws	FileNotFoundException	if the file is not found
	 */
	public String getAbsolutePath() throws FileNotFoundException 
	{
		Class<?> class_ = ClasspathFile.class;
		ClassLoader cl = class_.getClassLoader ();
		URL url = cl.getResource(relativePath);

		if (url == null) {
			throw new FileNotFoundException(
					"Could not get URL of: " + relativePath);
		}

		return url.getPath();
	}

	/**
	 * Get an InputStream for this file.
	 *
	 * @return	an InputStream to this file
	 * @throws	FileNotFoundException	if the file is not found
	 * @throws	IOException				on error reading the file
	 */
	public InputStream getInputStream()
		throws FileNotFoundException, IOException
	{
		InputStream is = getUrl().openStream();

		if (is == null) {
			throw new FileNotFoundException(
					"Could not find file: " + relativePath);
		}

		return is;
	}

	/**
	 * Returns a URL to this file.
	 *
	 * @return	a URL to this file
	 * @throws	FileNotFoundException	if the file is not found
	 */
	public URL getUrl() throws FileNotFoundException 
	{
		Class<?> class_ = ClasspathFile.class;
		ClassLoader cl = class_.getClassLoader ();
		URL url = cl.getResource(relativePath);

		if (url == null) {
			throw new FileNotFoundException(
					"Could not find file: " + relativePath);
		}

		return url;
	}
}
