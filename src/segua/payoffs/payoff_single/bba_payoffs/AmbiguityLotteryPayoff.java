package segua.payoffs.payoff_single.bba_payoffs;

import segua.payoffs.payoff_single.BBAPayoff;
import data_structures.BBA;
import data_structures.Range;

public class AmbiguityLotteryPayoff extends BBAPayoff {
	
	public AmbiguityLotteryPayoff(Range r, BBA<Integer> b) throws Exception {
		super(r);
		
		if(!b.isValid()) {
			throw new Exception("invalid BBA");
		}
		
		super.setBBA(b);
	}
	
	@Override
	public String toString() {
		return super.getBBA().toString();
	}
	
}
