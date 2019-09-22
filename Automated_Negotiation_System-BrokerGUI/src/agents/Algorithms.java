package agents;

public class Algorithms {
	
	public double offer(float initialPrice, float reservPrice, float time, float deadLine, float beeta)
	{	
		double t = (double)(time/deadLine);
		double offer = (double) (initialPrice + (reservPrice - initialPrice) * Math.pow( t, beeta));
		
		return offer;
	}
	
}
