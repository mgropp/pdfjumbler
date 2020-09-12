package net.sourceforge.pdfjumbler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Methods to move & copy files.
 * 
 * @author Martin Gropp
 */
public class FileUtils {
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		FileInputStream inStream = new FileInputStream(sourceFile);
		try {
			FileOutputStream outStream = new FileOutputStream(destFile);
			try {
				inStream.getChannel().transferTo(0, sourceFile.length(), outStream.getChannel());
			}
			finally {
				outStream.close();
			}
		}
		finally {
			inStream.close();
		}
	}
	
	public static void moveFile(File sourceFile, File destFile) throws IOException {
		if (destFile.exists()) {
			destFile.delete();
		}
		
		// Try rename
		if (sourceFile.renameTo(destFile)) {
			return;
		}
		
		// Copy & delete
		copyFile(sourceFile, destFile);
		sourceFile.delete();
	}
}
