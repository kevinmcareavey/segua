package segua.framework.multi_security_games.profile_games;

import java.util.Map;

import segua.data_structures.AdvancedSet;
import segua.framework.AttackerType;
import segua.framework.PureStrategyProfile;
import segua.framework.multi_security_games.MultiProfileGame;
import segua.framework.payoffs.PayoffSingle;
import segua.framework.security_games.profile_games.SetProfileGame;

public class MultiSetProfileGame<T extends PayoffSingle> extends MultiProfileGame<T> {
	
	private Map<AttackerType, SetProfileGame<T>> securityGames;
	
	public MultiSetProfileGame(AdvancedSet<PureStrategyProfile> p, Map<AttackerType, Double> a, Map<AttackerType, SetProfileGame<T>> s) {
		super(p, a);
		securityGames = s;
	}
	
	public Map<AttackerType, SetProfileGame<T>> getSecurityGames() {
		return securityGames;
	}
	
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SetProfileGame<T>> entry : securityGames.entrySet()) {
			if(entry.getValue().includesDefenderNegativeAbsentPayoffs()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SetProfileGame<T>> entry : securityGames.entrySet()) {
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
		for(Map.Entry<AttackerType, SetProfileGame<T>> entry : securityGames.entrySet()) {
			output += delim + "P(" + entry.getKey().toString() + ") = " + super.getAttackerProbabilities().get(entry.getKey()) + "\n" + entry.getValue().toString();
			delim = "\n\n";
		}
        return output;
	}
	
}
