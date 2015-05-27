package segua.multi_security_games.profile_games;

import java.util.Map;

import segua.AttackerType;
import segua.PureStrategyProfile;
import segua.multi_security_games.MultiProfileGame;
import segua.payoffs.PayoffSingle;
import segua.security_games.profile_games.SingleProfileGame;
import data_structures.AdvancedSet;

public class MultiSingleProfileGame<T extends PayoffSingle> extends MultiProfileGame<T> {
	
	private Map<AttackerType, SingleProfileGame<T>> securityGames;
	
	public MultiSingleProfileGame(AdvancedSet<PureStrategyProfile> p, Map<AttackerType, Double> a, Map<AttackerType, SingleProfileGame<T>> s) {
		super(p, a);
		securityGames = s;
	}
	
	public Map<AttackerType, SingleProfileGame<T>> getSecurityGames() {
		return securityGames;
	}
	
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SingleProfileGame<T>> entry : securityGames.entrySet()) {
			if(entry.getValue().includesDefenderNegativeAbsentPayoffs()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SingleProfileGame<T>> entry : securityGames.entrySet()) {
			if(entry.getValue().includesAttackerNegativeAbsentPayoffs()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String output = "";
		String delim = "";
		for(Map.Entry<AttackerType, SingleProfileGame<T>> entry : securityGames.entrySet()) {
			output += delim + "P(" + entry.getKey().toString() + ") = " + super.getAttackerProbabilities().get(entry.getKey()) + "\n" + entry.getValue().toString();
			delim = "\n\n";
		}
        return output;
	}
	
}
