package model;

import java.util.ArrayList;

public class CarList extends ArrayList<Car> {

	private static final long serialVersionUID = -5857202238478847267L;

	public CarList() {}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < this.size(); i++) {
			s = s + this.get(i) + "\n";
		}
		return s;
	}
	
}
