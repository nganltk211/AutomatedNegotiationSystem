package agents;

import java.util.ArrayList;
import java.util.Iterator;

import model.Car;
import model.MultipleMessage;

/**
 * MultiAgent negotiation manager for storing and handling requests from many buyers for a same car. 
 */
public class MultiAgentManager {

	// list storing requested cars with his interested buyers
	private ArrayList<MultipleMessage> negotiationList = null;

	public MultiAgentManager() {
		negotiationList = new ArrayList<MultipleMessage>();
	}

	/**
	 * Method to add a car (if not exist) or a buyer with his offer (if the car is
	 * in the list) to the list
	 * 
	 * @param car
	 * @param buyer
	 * @param firstOffer
	 */
	public void addBuyer(Car car, String buyer, double firstOffer) {
		boolean isCarinList = false;
		for (MultipleMessage m : negotiationList) {
			if (car.getCarId() == m.getCar().getCarId()) {
				// add only buyer name to multiple message list
				m.addToBuyerList(buyer, firstOffer);
				isCarinList = true;
			}
		}
		if (!isCarinList) {
			// add a new car with the buyer to the list if there is no same car in the list
			MultipleMessage buyerList = new MultipleMessage(car);
			buyerList.addToBuyerList(buyer, firstOffer);
			negotiationList.add(buyerList);
		}

	}

	/**
	 * Method to remove the car from the controlling list after negotiation
	 * @param car
	 */
	public void removeCarFromList(Car car) {
		for (Iterator<MultipleMessage> iterator = negotiationList.iterator(); iterator.hasNext();) {
			MultipleMessage message = iterator.next();
			if (!message.getBuyerList().isEmpty()) {
				if (car.getCarId() == message.getCar().getCarId()) {
					iterator.remove();
				}
			}
		}
	}

	public ArrayList<MultipleMessage> getNegotiationList() {
		return negotiationList;
	}

}
