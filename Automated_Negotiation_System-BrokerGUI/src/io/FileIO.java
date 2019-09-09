package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Car;
import model.CarList;

/**
 * The class to support the file reading.
 */
public class FileIO {

	protected BufferedReader csvReader= null;
	protected FileWriter csvWriter = null;
	private String path;
	/**
	 * Method to open a text file.
	 * @param path : File path.
	 * @return true, if successful.
	 */
	
	public FileIO(String setpath){
		path = setpath;
	}
	
	private boolean openFileReader() {
		try {
			try {
				csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
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
	 * Method to open a text file writer.
	 */
	
	private void openFileWriter() {
		try {
			csvWriter = new FileWriter(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to close the text file reader.
	 */
	private void closeFileReader() {
		try {
			csvReader.close();
		} catch (IOException e) {
			System.err.println("Error when closing the text file");
		}
	}
	

	/**
	 * Method to close the text file writer.
	 */
	private void closeFileWriter() {
		try {
			csvWriter.close();
		} catch (IOException e) {
			System.err.println("Error when closing the text file");
		}
	}
	
	/**
	 * Method to read a line of the text file and return it. 
	 * @return context of the line.
	 */
	
	private String readLine() {
		String line = null;
		
		try {
			line = csvReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
	}
	
	private ArrayList<String> spliter(String row){
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(row.split(" , ")));
		return list;
	}
	
	public ArrayList<List<String>> readFileToArray() {
		openFileReader();
		ArrayList<List<String>> rows = new ArrayList<List<String>>();
		String line = null;
		int listCount = Integer.parseInt(readLine());
		
		if (listCount != 0) {
			
			while((line = readLine()) != null) {
				rows.add(spliter(line));
				System.out.println("TL : " + line);
			}
		}		
		
		closeFileReader();
		return rows;
	}
	
	private void writeLine(String line) {
		
		try {
			csvWriter.append(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void writeToFile(ArrayList<Car> carList) {
		int length = carList.size();
		String line = null;
		ArrayList<String> rows = new ArrayList<String>();
		
		System.out.println("T0");
		
		//Open file reader and read current length of the file
		openFileReader();
		
		int listCount = 0;
		line = readLine();
		if(line != null) {
			listCount = Integer.parseInt(line);
		}
		String newListCount = Integer.toString(listCount + length);
		
		//Read out all current lines in file
		if (listCount != 0) {
			System.out.println("T3");
			while((line = readLine()) != null) {
				rows.add(line);
				System.out.println("TL : " + line);
			}
		}
		
		//Add new lines to the ArrayList
		int l = 0;
		for(Car car : carList) {
			l++;
			rows.add(Integer.toString(listCount + l) + "," + car.toString());
		}
		
		//Close File Reader
		closeFileReader();
		//Open File Writer 
		openFileWriter();
		
		//Write new length to the file
		writeLine(newListCount + "\n");
		
		//Write list back to file
		for(String row : rows) {
			writeLine(row + "\n");
		}
		
		//Close File writer
		closeFileWriter();
	}
}
