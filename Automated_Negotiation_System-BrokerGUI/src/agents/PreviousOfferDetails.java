package agents;

public class PreviousOfferDetails {
	private double lastConcessionRate;
	private double lastOfferOfSeller;
	private long buyerLastOfferAtTime;

	public PreviousOfferDetails(double lastConcessionRate, double lastOfferOfSeller, long buyerLastOfferAtTime) {
		super();
		this.lastConcessionRate = lastConcessionRate;
		this.lastOfferOfSeller = lastOfferOfSeller;
		this.buyerLastOfferAtTime = buyerLastOfferAtTime;
	}

	public double getLastConcessionRate() {
		return lastConcessionRate;
	}

	public void setLastConcessionRate(double lastConcessionRate) {
		this.lastConcessionRate = lastConcessionRate;
	}

	public double getLastOfferOfSeller() {
		return lastOfferOfSeller;
	}

	public void setLastOfferOfSeller(double lastOfferOfSeller) {
		this.lastOfferOfSeller = lastOfferOfSeller;
	}

	public long getBuyerLastOfferAtTime() {
		return buyerLastOfferAtTime;
	}

	public void setBuyerLastOfferAtTime(long buyerLastOfferAtTime) {
		this.buyerLastOfferAtTime = buyerLastOfferAtTime;
	}
		
}
