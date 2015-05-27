package segua.payoffs.payoff_single.bba_payoffs;

import segua.payoffs.payoff_single.BBAPayoff;
import data_structures.BBA;
import data_structures.Interval;
import data_structures.Range;

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
