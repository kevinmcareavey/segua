package segua.payoffs.payoff_single.bba_payoffs;

import segua.payoffs.payoff_single.BBAPayoff;
import data_structures.AdvancedSet;
import data_structures.BBA;
import data_structures.Range;

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
