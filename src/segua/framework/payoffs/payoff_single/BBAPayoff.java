package segua.framework.payoffs.payoff_single;

import segua.data_structures.BBA;
import segua.data_structures.Range;
import segua.framework.payoffs.PayoffSingle;

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
