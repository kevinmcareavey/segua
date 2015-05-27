package segua.framework.payoffs.payoff_single;

import segua.framework.payoffs.PayoffSingle;

public abstract class NormalFormPayoff extends PayoffSingle {
	
	public abstract double getDouble();
	
	@Override
	public boolean isAbsentPayoff() {
		return false;
	}
	
}
