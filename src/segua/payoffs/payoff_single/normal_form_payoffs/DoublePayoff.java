package segua.payoffs.payoff_single.normal_form_payoffs;

import segua.payoffs.payoff_single.NormalFormPayoff;
import utilities.Utilities;

public class DoublePayoff extends NormalFormPayoff {
	
	private double doubleValue;
	
	public DoublePayoff(double d) {
		doubleValue = d;
	}
	
	@Override
	public String toString() {
		return Utilities.format(doubleValue);
	}

	@Override
	public double getDouble() {
		return doubleValue;
	}
	
}
