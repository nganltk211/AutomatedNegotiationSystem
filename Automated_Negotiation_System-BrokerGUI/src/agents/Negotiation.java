package agents;

public class Negotiation {
	
	private double Offervalue;
	public double offer(float initialPrice, float reservationPrice, float steps, float totalSteps, float beetaValue)
	{	
		double negetivePrice = reservationPrice - initialPrice;
		double currentstepvalue = Math.sqrt((steps/totalSteps)*beetaValue);
		return setOffervalue(initialPrice + negetivePrice * currentstepvalue);
	}
	public double getOffervalue() {
		return Offervalue;
	}
	public double setOffervalue(double offervalue) {
		Offervalue = offervalue;
		return offervalue;
	}

}
