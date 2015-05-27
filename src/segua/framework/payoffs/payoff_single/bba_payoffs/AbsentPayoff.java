package segua.framework.payoffs.payoff_single.bba_payoffs;

import segua.data_structures.BBA;
import segua.data_structures.Range;
import segua.framework.payoffs.payoff_single.BBAPayoff;

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
