package segua.framework.player_pairs;

import segua.framework.PlayerPair;
import segua.framework.payoffs.PayoffSingle;

public class PlayerPairPayoffSingle<T extends PayoffSingle> extends PlayerPair {
	
	private T defender;
	private T attacker;
	
	public PlayerPairPayoffSingle(T d, T a) {
		defender = d;
		attacker = a;
	}
	
	public T getDefender() {
		return defender;
	}
	
	public T getAttacker() {
		return attacker;
	}
	
	@Override
	public String toString() {
		return "<" + defender.toString() + ", " + attacker.toString() + ">";
	}
	
}
