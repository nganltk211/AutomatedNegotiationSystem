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

	// CONAN Strategy ---------------------------------------------------------
	public static int getNextOffer(int ip, int rp, double concessionRate) {
		return (int) (ip + (rp - ip) * concessionRate);
	}
	
	public static double getConcessionRate(long time, long startTime, long deadline, long timeOneRound, double lastConcessionRate, boolean lastOfferReserved, double ws, double enviromentFactor, double selfFactor) {
		double concessionRate = 0.0;
		if (time == startTime) {
			concessionRate = 0.0;
		} else if (time == deadline - timeOneRound) {
			concessionRate = 0.99;
		} else if (lastOfferReserved) {
			concessionRate = lastConcessionRate * 0.9;
		} else {
			double we = 1 - ws;
			concessionRate = we * enviromentFactor + ws * selfFactor;
		}
		
		if (concessionRate < lastConcessionRate) {
			concessionRate = lastConcessionRate;
		}
		
		return concessionRate;
	}
	
	public static double getSelfFactor(int ro, double ns, double te, double eg) {
		return (1/(ro + 1) + ns + te + eg) / 4;
	}
	
	public static double getNegotiationSituation(int numberOfSeller, double opponentFactor) {
		return (opponentFactor - (2*numberOfSeller))/ (4 * numberOfSeller);
	}
	
	public static double getOpponentResponseTimeASeller(long beginTime, long responseTime, long timeDuration) {
		return (beginTime - responseTime) / timeDuration;
	}
	
	public static double getOpponentConcessionRateASeller(int currentOffer, int lastOffer, int rp, int ip) {
		double r = (currentOffer - lastOffer) / (rp - ip);
		return 1 - r;
	}
	
	public static double getEffectOfTime(long currentTime, long startTime, long timeDuration) {
		return (currentTime - startTime) / timeDuration;
	}
	
	public static double getEnvironmentFactor(int se, int c, int numberOfBuyer, int numberOfSeller) {
		return ((1/se) + c + (numberOfBuyer/numberOfSeller)) / 3;
	}
	
	public static double getWeightForSelfFactor(double effectOfTime, int sellerInitialPrice, int rp, double selffactor) {
		double dm = (sellerInitialPrice/rp) * effectOfTime;
		if (selffactor > 0 && selffactor <= 1/3) {
			return dm * 0.75;
		}
		
		if (selffactor > 1/3 && selffactor <= 2/3) {
			return dm * 0.5;
		}
		
		if (selffactor > 2/3 && selffactor <= 1) {
			return dm * 0.25;
		}
		
		return 0.0;
	}
	
}
