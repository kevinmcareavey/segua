package segua.payoffs.payoff_single.normal_form_payoffs;

import segua.payoffs.payoff_single.NormalFormPayoff;

public class DoublePayoff extends NormalFormPayoff {
	
	private double doubleValue;
	
	public DoublePayoff(double d) {
		doubleValue = d;
	}
	
	@Override
	public String toString() {
		return String.valueOf(doubleValue);
	}

	@Override
	public double getDouble() {
		return doubleValue;
	}
	
}
