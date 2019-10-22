package agents;

import model.Car;

/**
 * This class contains all methods used in the agent-AI part
 */
public class Algorithms {

	/**
	 * Method for calculating the next offer through the time-dependent tactics.
	 * 
	 * @param initialPrice
	 * @param reservPrice
	 * @param time 
	 * @param deadLine: maximal time step
	 * @param beeta : concession rate 
	 * @return next offer
	 */
	public static int offer(double initialPrice, double reservPrice, double time, double deadLine, double beeta) {
		double t = (double) (time / deadLine);
		double offer = (double) (initialPrice + (reservPrice - initialPrice) * Math.pow(t, beeta));
		return (int) Math.round(offer);
	}

	/**
	 * Method for calculating the car score of the passing car.
	 * 
	 * @param car
	 * @return car score
	 */
	public static float carScore(Car car) {
		float score = 0;

		score = (float) car.getWarranty() * 5;
		score = score + ((float) car.getCarrating() * 5);

		int manuFacYear = Integer.parseInt(car.getManufactureYear());
		float year = (float) (2019 - manuFacYear);

		// Manufacture year [ y = -0.52x + 31.25 ]
		score = score + ((-5 * year) + 25);

		float km = ((float) car.getKm() / 1000);

		if (km < 12) {
			score = score + 25;
		} else {// Mileage [ y = -0.52x + 31.25 ]
			score = score + ((-0.52f * km) + 31.25f);
		}

		return score;
	}

}
