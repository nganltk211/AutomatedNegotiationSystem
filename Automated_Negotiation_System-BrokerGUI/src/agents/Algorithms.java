package agents;

public class Algorithms {
	
	public static double offer(double initialPrice, double reservPrice, double time, double deadLine, double beeta)
	{	
		double t = (double)(time/deadLine);
		double offer = (double) (initialPrice + (reservPrice - initialPrice) * Math.pow( t, beeta));
		return offer;
	}

}
