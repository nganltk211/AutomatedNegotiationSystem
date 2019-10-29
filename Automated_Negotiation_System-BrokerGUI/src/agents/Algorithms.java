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
	 * @param deadLine:
	 *            maximal time step
	 * @param beeta
	 *            : concession rate
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

	// Calculate offer by CONAN Strategy
	// ---------------------------------------------------------
	/**
	 * Method to calculate the next generated offer by CONAN Strategy.
	 * @param intialPrice
	 * @param reservationPrice
	 * @param concessionRate
	 * @return the next offer
	 */
	public static int getNextOffer(double intialPrice, double reservationPrice, double concessionRate) {
		return (int) (intialPrice + (reservationPrice - intialPrice) * concessionRate);
	}

	/**
	 * Method to calculate the concession rate at time t for a seller 
	 * @param time : time point t
	 * @param startTime : time when the negotiation starts
	 * @param deadline : deadline of negotiation for the buyer
	 * @param timeOneRound : time to complete one negotiation round
	 * @param lastConcessionRate : concession rate at time t-1
	 * @param lastOfferReserved : true if the buyer has reserved at least one offer. 
	 * @param ws : self weights
	 * @param enviromentFactor: the effect of the environment factor
	 * @param selfFactor : the effect of self factor
	 * @return
	 */
	public static double getConcessionRate(long time, long startTime, long deadline, long timeOneRound,
			double lastConcessionRate, boolean lastOfferReserved, double ws, double enviromentFactor,
			double selfFactor) {
		
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

		if (concessionRate < lastConcessionRate && !lastOfferReserved) {
			concessionRate = lastConcessionRate;
		}

		return concessionRate;
	}

	/**
	 * Method to calculate effect of self factor 
	 * @param ro : the number of reserved offers
	 * @param ns : the negotiation situation for all threads
	 * @param te : the effect of the passage of time on the concession rate for the buyer.
	 * @param eg : the eagerness of buyer b to obtain the resource (assume that it is always 1.0)
	 * @return the self factor
	 */
	public static double getSelfFactor(int ro, double ns, double te, double eg) {
		return ((double) 1 / (ro + 1) + ns + te + eg) / 4;
	}

	/**
	 * Method to calculate the negotiation situation
	 * @param numberOfSeller : number of sellers attending to the negotiation
	 * @param opponentFactor : sums up the scores Opponent’s response time and Opponent’s concession rate
	 * @return value of negotiation situation
	 */
	public static double getNegotiationSituation(int numberOfSeller, double opponentFactor) {
		return (opponentFactor - (2 * numberOfSeller)) / (4 * numberOfSeller);
	}

	/**
	 * Method to calculate the score of opponent’s response time
	 * @param beginTime : time point of giving an offer from the buyer b to a seller s
	 * @param responseTime : time point of giving a response to that offer from the seller to the buyer
	 * @param timeDuration : negotiation duration
	 * @return score of opponent’s response time 
	 */
	public static int getOpponentResponseTimeASellerScore(long beginTime, long responseTime, long timeDuration) {
		double oppResponseTime = (double) (beginTime - responseTime) / timeDuration;
		if (oppResponseTime > 0 && oppResponseTime <= 1 / 3) {
			return 1;
		}

		if (oppResponseTime > 1 / 3 && oppResponseTime <= 2 / 3) {
			return 2;
		}

		if (oppResponseTime > 2 / 3 && oppResponseTime <= 1) {
			return 3;
		}
		return 0;
	}

	/**
	 * Method to calculate the score for Opponent’s concession rate
	 * @param currentOffer : current offer from a seller
	 * @param lastOffer : the last offer made to b by a seller 
	 * @param rp : buyer's reservation price
	 * @param ip : buyer's initial price
	 * @return of opponent’s concession rate
	 */
	public static int getOpponentConcessionRateASellerScore(double currentOffer, double lastOffer, double rp,
			double ip) {
		double r = (lastOffer - currentOffer) / (rp - ip);
		double oppConcessionRate = 1 - r;
		if (oppConcessionRate > 0 && oppConcessionRate <= 1 / 3) {
			return 1;
		}

		if (oppConcessionRate > 1 / 3 && oppConcessionRate <= 2 / 3) {
			return 2;
		}

		if (oppConcessionRate > 2 / 3 && oppConcessionRate <= 1) {
			return 3;
		}
		return 0;
	}

	/**
	 * Method to calculate the effect of the passage of time on the concession rate for buyer b
	 * @param currentTime
	 * @param startTime
	 * @param timeDuration
	 * @return the effect of time
	 */
	public static double getEffectOfTime(long currentTime, long startTime, long timeDuration) {
		return ((double) (currentTime - startTime)) / timeDuration;
	}

	/**
	 * Method to calculate the environment factor at a given time
	 * @param se : the number of sellers that are actively negotiating with b
	 * @param numberOfCompetitorAgents : the number of active competitor agents
	 * @param numberOfBuyer : number of buyers
	 * @param numberOfSeller : number of sellers
	 * @return value of environment factor
	 */
	public static double getEnvironmentFactor(int se, int numberOfCompetitorAgents, int numberOfBuyer,
			int numberOfSeller) {
		return (((double) 1 / se) + ((double) numberOfCompetitorAgents / numberOfSeller)
				+ ((double) numberOfBuyer / numberOfSeller)) / 3;
	}

	/**
	 * Method to calculate the weight for self-factor
	 * @param effectOfTime : effect of time
	 * @param d : seller's initial price
	 * @param reservationPrice : buyer's reservation price
	 * @param selffactor : value of self-factor
	 * @return weight for self-factor 
	 */
	public static double getWeightForSelfFactor(double effectOfTime, double d, double reservationPrice,
			double selffactor) {
		double dm = (d / reservationPrice) * effectOfTime;
		if (selffactor > 0 && selffactor <= 1 / 3) {
			return dm * 0.75;
		}

		if (selffactor > 1 / 3 && selffactor <= 2 / 3) {
			return dm * 0.5;
		}

		if (selffactor > 2 / 3 && selffactor <= 1) {
			return dm * 0.25;
		}

		return 0.0;
	}

}
