package segua.framework.payoffs.payoff_single.bba_payoffs;

import segua.data_structures.AdvancedSet;
import segua.data_structures.BBA;
import segua.data_structures.Range;
import segua.framework.payoffs.payoff_single.BBAPayoff;

public class PointValuePayoff extends BBAPayoff {
	
	private int pointValue;
	
	public PointValuePayoff(Range r, int i) {
		super(r);
		super.setBBA(new BBA<Integer>(r.getAdvancedSet()));
		pointValue = i;
		super.getBBA().addMass(new AdvancedSet<Integer>(i), 1);
	}
	
	public int getPointValue() {
		return pointValue;
	}
	
	@Override
	public String toString() {
		return String.valueOf(pointValue);
	}
	
}
