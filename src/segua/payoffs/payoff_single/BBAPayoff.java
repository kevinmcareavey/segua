package segua.payoffs.payoff_single;

import segua.payoffs.PayoffSingle;
import data_structures.BBA;
import data_structures.Range;

public abstract class BBAPayoff extends PayoffSingle {
	
	private Range range;
	private BBA<Integer> bba;
	
	public BBAPayoff(Range r) {
		range = r;
	}
	
	public Range getRange() {
		return range;
	}
	
	public BBA<Integer> getBBA() {
		return bba;
	}
	
	public void setBBA(BBA<Integer> b) {
		bba = b;
	}
	
	public boolean isAbsentPayoff() {
		if(bba.getMass(range.getAdvancedSet()) >= 1.0) {
			return true;
		}
		if(bba.getMasses().size() == 1) {
			if(bba.getMasses().containsKey(range.getAdvancedSet())) {
				return true;
			}
		}
		return false;
	}
	
}
