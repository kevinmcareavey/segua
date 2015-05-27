package segua.framework.payoffs.payoff_single.bba_payoffs;

import segua.data_structures.BBA;
import segua.data_structures.Range;
import segua.framework.payoffs.payoff_single.BBAPayoff;

public class ReliabilityConsiderationPayoff extends BBAPayoff {
	
	public ReliabilityConsiderationPayoff(Range r, BBA<Integer> b) throws Exception {
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
