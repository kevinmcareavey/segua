package segua.payoffs.payoff_single.bba_payoffs;

import segua.payoffs.payoff_single.BBAPayoff;
import data_structures.BBA;
import data_structures.Range;

public class AbsentPayoff extends BBAPayoff {
	
	public AbsentPayoff(Range r) {
		super(r);
		super.setBBA(new BBA<Integer>(r.getAdvancedSet()));
		super.getBBA().addMass(r.getAdvancedSet(), 1);
	}
	
	@Override
	public String toString() {
		return super.getRange().toString();
	}
	
	@Override
	public boolean isAbsentPayoff() {
		return true;
	}
	
}
