package segua.security_games.profile_games;

import java.util.HashMap;
import java.util.Map;

import segua.PureStrategyProfile;
import segua.payoffs.PayoffSingle;
import segua.player_pairs.PlayerPairPayoffSingle;
import segua.security_games.ProfileGame;
import data_structures.AdvancedSet;

public class SetProfileGame<T extends PayoffSingle> extends ProfileGame<T> {
	
	private Map<AdvancedSet<PureStrategyProfile>, PlayerPairPayoffSingle<T>> payoffs;
	
	public SetProfileGame(AdvancedSet<PureStrategyProfile> p) {
		super(p);
		payoffs = new HashMap<AdvancedSet<PureStrategyProfile>, PlayerPairPayoffSingle<T>>();
	}
	
	public boolean singletonSetsOnly() {
		for(Map.Entry<AdvancedSet<PureStrategyProfile>, PlayerPairPayoffSingle<T>> entry : payoffs.entrySet()) {
			if(entry.getKey().size() != 1) {
				return false;
			}
		}
		return true;
	}
	
	public Map<AdvancedSet<PureStrategyProfile>, PlayerPairPayoffSingle<T>> getPayoffs() {
		return payoffs;
	}
	
	public void addPayoff(AdvancedSet<PureStrategyProfile> psp, PlayerPairPayoffSingle<T> value) throws Exception {
		payoffs.put(psp, value);
	}
	
	public void removePayoff(AdvancedSet<PureStrategyProfile> psp) throws Exception {
		payoffs.remove(psp);
	}
	
	@Override
	public String toString() {
		String output = "";
		String delim = "";
		for(Map.Entry<AdvancedSet<PureStrategyProfile>, PlayerPairPayoffSingle<T>> entry : payoffs.entrySet()) {
			output += delim + entry.getKey() + ": " + entry.getValue();
			delim = "\n";
		}
		return output;
	}
	
	@Override
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<AdvancedSet<PureStrategyProfile>, PlayerPairPayoffSingle<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getDefender().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<AdvancedSet<PureStrategyProfile>, PlayerPairPayoffSingle<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getAttacker().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}
	
}
