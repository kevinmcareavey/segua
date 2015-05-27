package segua.framework.security_games.profile_games;

import java.util.HashMap;
import java.util.Map;

import segua.data_structures.AdvancedSet;
import segua.framework.PureStrategyProfile;
import segua.framework.payoffs.PayoffSingle;
import segua.framework.player_pairs.PlayerPairPayoffSingle;
import segua.framework.security_games.ProfileGame;

public class SingleProfileGame<T extends PayoffSingle> extends ProfileGame<T> {
	
	private Map<PureStrategyProfile, PlayerPairPayoffSingle<T>> payoffs;
	
	public SingleProfileGame(AdvancedSet<PureStrategyProfile> p) {
		super(p);
		payoffs = new HashMap<PureStrategyProfile, PlayerPairPayoffSingle<T>>();
	}
	
	public Map<PureStrategyProfile, PlayerPairPayoffSingle<T>> getPayoffs() {
		return payoffs;
	}
	
	public void addPayoff(PureStrategyProfile psp, PlayerPairPayoffSingle<T> value) throws Exception {
		payoffs.put(psp, value);
	}
	
	public void removePayoff(PureStrategyProfile psp) throws Exception {
		payoffs.remove(psp);
	}
	
	@Override
	public String toString() {
		String output = "";
		String delim = "";
		for(Map.Entry<PureStrategyProfile, PlayerPairPayoffSingle<T>> entry : payoffs.entrySet()) {
			output += delim + entry.getKey() + ": " + entry.getValue();
			delim = "\n";
		}
		return output;
	}
	
	@Override
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<PureStrategyProfile, PlayerPairPayoffSingle<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getDefender().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<PureStrategyProfile, PlayerPairPayoffSingle<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getAttacker().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}
	
}
