package segua.framework.payoffs.payoff_single.normal_form_payoffs;

import segua.data_structures.Range;
import segua.framework.payoffs.payoff_single.NormalFormPayoff;
import segua.framework.payoffs.payoff_single.bba_payoffs.PointValuePayoff;

public class IntegerPayoff extends NormalFormPayoff {
	
	private int integerValue;
	
	public IntegerPayoff(int i) {
		integerValue = i;
	}
	
	public int getInteger() {
		return integerValue;
	}
	
	@Override
	public String toString() {
		return String.valueOf(integerValue);
	}

	@Override
	public double getDouble() {
		return (double)integerValue;
	}
	
	public PointValuePayoff getPointValuePayoff(Range r) {
		return new PointValuePayoff(r, integerValue);
	}
	
}
