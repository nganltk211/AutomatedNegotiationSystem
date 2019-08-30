package io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * The class to support the file reading.
 */
public class FileIO {

	protected BufferedReader fileInput = null;

	/**
	 * Method to open a text file.
	 * @param path : File path.
	 * @return true, if successful.
	 */
	public boolean openFile(String path) {
		try {
			try {
				fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			} catch (FileNotFoundException e) {
				System.err.println("File is not ready");
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Problem with encoding");
			return false;
		}
		return true;
	}

	/**
	 * Method to close the text file.
	 */
	public void closeFile() {
		try {
			fileInput.close();
		} catch (IOException e) {
			System.err.println("Error when closing the text file");
		}
	}

	/**
	 * Method to read a line of the text file and return it. 
	 * @return context of the line.
	 */
	public String readLine() {
		String context = null;
		try {
			context = fileInput.readLine();
		} catch (IOException e) {
			System.err.println("Problem when reading the text file");
		}
		return context;
	}
}
