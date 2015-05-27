package segua.payoffs;

import segua.Payoff;

public class PayoffPair<T extends PayoffSingle> extends Payoff {
	
	private T negative;
	private T positive;
	
	public PayoffPair(T n, T p) {
		negative = n;
		positive = p;
	}
	
	public T getNegative() {
		return negative;
	}
	
	public T getPositive() {
		return positive;
	}
	
	@Override
	public String toString() {
		return "(" + negative.toString() + ", " + positive.toString() + ")";
	}
	
}
