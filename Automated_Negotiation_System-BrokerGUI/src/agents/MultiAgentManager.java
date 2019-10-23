package agents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import model.Car;
import model.CarList;
import model.MultipleMessage;

/**
 * MultiAgent negotiation manager for storing and handling requests from many buyers for a same car. 
 */
public class MultiAgentManager {

	// list storing requested cars with his interested buyers
	private ArrayList<MultipleMessage> negotiationList = null;
	private BrokerAgent brokerAgent;

	public MultiAgentManager(BrokerAgent brokerAgent) {
		negotiationList = new ArrayList<MultipleMessage>();
		this.brokerAgent = brokerAgent;
	}

	/**
	 * Method to add a car (if not exist) or a buyer with his offer (if the car is
	 * in the list) to the list
	 * 
	 * @param car
	 * @param buyer
	 * @param firstOffer
	 */
	private void addBuyerWithCar(Car car, String buyer, double firstOffer) {
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
	 * Method to add a list of cars or a buyer with his offer (if the car is
	 * in the list) to the list
	 * 
	 * @param car
	 * @param buyer
	 * @param firstOffer
	 */
	public void addBuyer(CarList carlist, String buyer, double firstOffer) {
		for (Car car : carlist) {
			addBuyerWithCar(car, buyer, firstOffer);
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
	
	public void removeBuyerFromList(String buyerName) {
		for (Iterator<MultipleMessage> iterator = negotiationList.iterator(); iterator.hasNext();) {
			MultipleMessage message = iterator.next();
			if (!message.getBuyerList().isEmpty()) {
				for (Entry<String, Double> element : message.getBuyerList().entrySet()) {
					 if (element.getKey().equals(buyerName)) { 
						 if (brokerAgent.getCatalog().contains(message.getCar())) {
							 message.getBuyerList().remove(buyerName);
						 } else {
							 iterator.remove();
						 }				
					 }
				 }
			}
		}
	}

	public ArrayList<MultipleMessage> getNegotiationList() {
		return negotiationList;
	}

}
