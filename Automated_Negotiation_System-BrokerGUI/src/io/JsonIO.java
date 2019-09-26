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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Car;
import model.CarList;

/**
 * The class to support the file reading.
 */
public class JsonIO {

	protected BufferedReader csvReader = null;
	protected FileWriter csvWriter = null;
	private String path;
	private ObjectMapper oJsonMapper = new ObjectMapper();

	/**
	 * Method to open a text file.
	 * 
	 * @param path
	 *            : File path.
	 * @return true, if successful.
	 */
	public JsonIO(String setpath) {
		path = setpath;
	}

	public void clearFile() {
		openFileWriter();
		try {
			csvWriter.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeFileWriter();
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
	 * 
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

	private void writeLine(String line) {

		try {
			csvWriter.append(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private CarList jsonToClass(String carList) {
		CarList list = null;
		try {
			list = oJsonMapper.readValue(carList, CarList.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private String classToJson(CarList list) {
		String json = null;
		try {
			json = oJsonMapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public CarList readFile() {
		String line = null;
		CarList list = null;
		openFileReader();
		try {
			line = csvReader.readLine();
			if (line != null) {
				list = jsonToClass(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeFileReader();
		return list;
	}

	public void writeToFile(String carList) {

		openFileReader();
		int listCount = 0;
		CarList list = null;
		CarList tempList = null;
		String line = readLine();
		String json = null;

		// Check is the file empty or not if not read json to car class
		if (line != null) {
			list = jsonToClass(line);
			listCount = list.size();
		} else { // If it is empty change CarId and add it to Car List
			list = jsonToClass(carList);
			int i = 0;
			for (Car car : list) {
				car.setCarId(i);
				i++;
			}
		}
		// Close File reader
		closeFileReader();
		// If the file is not empty add new cars to Car List
		if (line != null) {
			tempList = jsonToClass(carList);

			int i = listCount;
			for (Car car : tempList) {
				car.setCarId(i);
				i++;
				list.add(car);
			}
		}

		// Translate Car Listin to json
		json = classToJson(list);

		openFileWriter();
		// Write to file
		writeLine(json);
		closeFileWriter();

	}
}
