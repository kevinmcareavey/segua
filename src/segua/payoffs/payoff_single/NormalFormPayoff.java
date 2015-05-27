package segua.payoffs.payoff_single;

import segua.payoffs.PayoffSingle;

public abstract class NormalFormPayoff extends PayoffSingle {
	
	public abstract double getDouble();
	
	@Override
	public boolean isAbsentPayoff() {
		return false;
	}
	
}
