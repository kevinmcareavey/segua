package segua.framework.player_pairs;

import segua.framework.PlayerPair;
import segua.framework.payoffs.PayoffPair;
import segua.framework.payoffs.PayoffSingle;

public class PlayerPairPayoffPair<T extends PayoffSingle> extends PlayerPair {
	
	private PayoffPair<T> defender;
	private PayoffPair<T> attacker;
	
	public PlayerPairPayoffPair(PayoffPair<T> d, PayoffPair<T> a) {
		defender = d;
		attacker = a;
	}
	
	public PayoffPair<T> getDefender() {
		return defender;
	}
	
	public PayoffPair<T> getAttacker() {
		return attacker;
	}
	
	@Override
	public String toString() {
		return "<" + defender.toString() + ", " + attacker.toString() + ">";
	}
	
}
