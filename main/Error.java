package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Error extends ArrayList<String> {

	/**
	 * How is this thing used?
	 * Is it used for tracking? Or used by the JVM? Who knows?
	 * Or for Serialization from a file?
	 */
	private static final long serialVersionUID = 4930926161194281896L;

	
	/**
	 * Writes all errors to a File
	 * @param file
	 * @return whether the write was successfully
	 */
	public boolean writeToDisk (File file) {
		//If something goes wrong while deleting the file...
		//Just in case...
		if(file.exists() && !file.delete()) {
			JOptionPane.showMessageDialog(null, "Error", "Error while deleting previos log files from disk", JOptionPane.ERROR_MESSAGE);
		}
		
		//If no errors were logged, nice ignore it
		if(this.size() != 0 && !this.get(0).equals("")) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			//Print it to a file using the UTF-8 Charset
			try (PrintWriter writer = new PrintWriter(file, Charset.forName("UTF-8"))) {
				for (String e : this) {
					writer.println(e);
				}
				writer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		} else {
			System.out.println("No errors occured");
		}
		return true;
	}
	
	
}
