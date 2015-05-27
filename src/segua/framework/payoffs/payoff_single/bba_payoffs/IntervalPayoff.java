package segua.framework.payoffs.payoff_single.bba_payoffs;

import segua.data_structures.BBA;
import segua.data_structures.Interval;
import segua.data_structures.Range;
import segua.framework.payoffs.payoff_single.BBAPayoff;

public class IntervalPayoff extends BBAPayoff {
	
	private Interval interval;
	
	public IntervalPayoff(Range r, Interval i) {
		super(r);
		super.setBBA(new BBA<Integer>(r.getAdvancedSet()));
		interval = i;
		super.getBBA().addMass(i.getAdvancedSet(), 1);
	}
	
	public Interval getInterval() {
		return interval;
	}
	
	@Override
	public String toString() {
		return interval.toString();
	}
	
}
