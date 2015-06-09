package com.echostar.gopher.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/***
 * 
 * @author Shekhar Bhardwaj
 *
 */
public class MoveFile {
	private static Logger log = Logger.getLogger ("com.echostar.gopher.util.MoveFile");
	public static String dirPaths;
	public static String srcPaths;
	public static String source;
	public static String destination;
	public static void main(String[] args) throws IOException {
		String dirname = dirName();
		srcPaths = System.getProperty("user.dir")+"\\test-output";
		dirPaths = System.getProperty("user.dir")+"\\PreservedResults\\"+dirname;
		log.info("GOPHER-MoveFile:"+srcPaths);
		log.info("GOPHER-MoveFile:"+dirPaths);
		createDIR(dirPaths);
		copyFolder(new File(srcPaths),new File(dirPaths ));
		source= System.getProperty("user.dir")+"\\test-output\\emailable-report.html";
		log.info("GOPHER-MoveFile:source folder for emailable report-"+source);
		destination=System.getProperty("user.dir")+"\\emailables\\"+dirname+"_emailableResults.html";
		log.info("GOPHER-MoveFile:destination folder for emailable report-"+destination);
		prepareEmailable(new File(source),new File(destination));
	}
	

	/***
	 * 
	 * @return String 
	 * @throws IOException
	 */
	private static String dirName() throws IOException{
		DateFormat dt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String d =dt.format(date).toString();
        String fname = "GOPHER_RUN_";
        String folderName = fname+d;
        String folderName1 = folderName.replaceAll("/", "_");
        String folderName2 = folderName1.replaceAll(":", "-");
        String folderName3 = folderName2.replaceAll(" ", "-");
        log.info("GOPHER-MoveFile:New destination folder name- "+folderName3);
        return folderName3;
        }
	
	/***
	 * 
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	private static void copyFolder(File src, File dest)throws IOException{
		try{
			if(src.isDirectory()){
				//if directory not exists, create it
				if(!dest.exists()){
					dest.mkdir();
					log.info("GOPHER-MoveFile:Directory copied from "+ src + "  to " + dest);
				}
				//list all the directory contents
				String files[] = src.list();
				for (String file : files) {
					//construct the src and dest file structure
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);
	    		   //recursive copy
					copyFolder(srcFile,destFile);
				}
			}else{
				//if file, then copy it
				//Use bytes stream to support all file types
				InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dest); 
				byte[] buffer = new byte[1024];
				int length;
				//copy the file content in bytes 
				while ((length = in.read(buffer)) > 0){
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();
				log.info("GOPHER-MoveFile:File copied from " + src + " to " + dest);
			} 
		}catch (Exception e){
			log.error("GOPHER-MoveFile:File not copied from " + src + " to " + dest);
			log.error("GOPHER-MoveFile: Something wrong with Source folder or it's name or destination folder name");
		}
	}


	/***
	 * 
	 * @param destDir
	 */
	private static void createDIR(String destDir){	
		File file = new File(destDir);
		if (!file.exists()) {
			if (file.mkdir()) {
				log.info("GOPHER-MoveFile:Directory is created!");
			} else {
				log.error("GOPHER-MoveFile:Failed to create directory!");
			}
		}
	}
    /***
     * 
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
	private static void prepareEmailable(File sourceFile, File destFile) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(sourceFile);
			os = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			log.info("GOPHER-MoveFile:Emailabe file prepared!");
		}catch (Exception e){
			log.error("GOPHER-MoveFile:Failed to prepare emailable file!");
		}finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}
  }

