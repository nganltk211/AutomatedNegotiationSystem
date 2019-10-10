package io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Car;
import model.CarList;

/**
 * The class to support the file reading.
 */
public class JsonIO {

	protected BufferedReader jsonReader = null;
	protected FileWriter jsonWriter = null;
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

	/**
	 * Method to clear a file
	 */
	public void clearFile() {
		openFileWriter();
		try {
			jsonWriter.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeFileWriter();
	}
	
	/**
	 * Method to open a file for reading purpose
	 * @return true if opening a file is successful
	 */
	public boolean openFileReader() {
		try {
			try {
				jsonReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
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
	public void openFileWriter() {
		try {
			jsonWriter = new FileWriter(path);
		} catch (IOException e) {
			System.err.println("Error when opening the text writer");
		}
	}

	/**
	 * Method to close the text file reader.
	 */
	public void closeFileReader() {
		try {
			jsonReader.close();
		} catch (IOException e) {
			System.err.println("Error when closing the text file");
		}
	}

	/**
	 * Method to close the text file writer.
	 */
	public void closeFileWriter() {
		try {
			jsonWriter.close();
		} catch (IOException e) {
			System.err.println("Error when closing the text file");
		}
	}

	/**
	 * Method to read a line of the text file and return it.
	 * 
	 * @return context of the line.
	 */
	public String readLine() {
		String line = null;

		try {
			line = jsonReader.readLine();
		} catch (IOException e) {
			System.err.println("Error when reading the text file");
		}
		return line;
	}

	/**
	 * Method to append a line in a file
	 * @param line
	 */
	public void writeLine(String line) {

		try {
			jsonWriter.append(line);
		} catch (IOException e) {
			System.err.println("Error when appending a line in the text file");
		}
	}

	/**
	 * Method to convert a car list in json-format to a CarList Object
	 * @param carList : car list in json-format
	 * @return an object CarList
	 */
	private CarList jsonToClass(String carList) {
		CarList list = null;
			try {
				list = oJsonMapper.readValue(carList, CarList.class);
			} catch (IOException e) {
				System.err.println("Error when converting json to CarList object");
			}
		return list;
	}

	/**
	 * Method to convert an object CarList in json-format
	 * @param list : list of cars
	 * @return car list in json-format
	 */
	private String classToJson(CarList list) {
		String json = null;
		try {
			json = oJsonMapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			System.err.println("Error when converting an object to json-format");
		}
		return json;
	}

	/**
	 * Method to read a file
	 * @return an object CarList
	 */
	public CarList readFile() {
		String line = null;
		CarList list = null;
		openFileReader();
		try {
			line = jsonReader.readLine();
			if (line != null) {
				list = jsonToClass(line);
			}
		} catch (IOException e) {
			System.err.println("Error when reading a file");
		}
		closeFileReader();
		return list;
	}

	/**
	 * Method to update a file
	 * @param carList car list needed to be written in the file
	 */
	public void writeToFile(String carList) {
		openFileReader();
		int listCount = 0;
		CarList list = null;
		CarList tempList = null;
		String line = readLine();
		String json = null;

		// Check whether the file is empty, if not convert json to a CarList Object
		if (line != null) {
			list = jsonToClass(line);
			listCount = list.size();
		} else { // If it is empty change carId and add it to Car List
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
		// Translate Car List into json
		json = classToJson(list);

		openFileWriter();
		writeLine(json); // Write to file
		closeFileWriter();
	}
}
