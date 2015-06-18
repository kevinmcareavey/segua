package segua.multi_security_games.target_games;

import java.util.HashMap;
import java.util.Map;

import segua.AttackerProbabilities;
import segua.AttackerType;
import segua.Target;
import segua.multi_security_games.MultiTargetGame;
import segua.multi_security_games.profile_games.MultiSetProfileGame;
import segua.payoffs.PayoffSingle;
import segua.security_games.profile_games.SetProfileGame;
import segua.security_games.target_games.SetTargetGame;
import data_structures.AdvancedSet;

public class MultiSetTargetGame<T extends PayoffSingle> extends MultiTargetGame<T> {
	
	private Map<AttackerType, SetTargetGame<T>> securityGames;
	
	public MultiSetTargetGame(AdvancedSet<Target> t, AttackerProbabilities a, Map<AttackerType, SetTargetGame<T>> s) {
		super(t, a);
		securityGames = s;
	}
	
	public Map<AttackerType, SetTargetGame<T>> getSecurityGames() {
		return securityGames;
	}
	
	public MultiSetProfileGame<T> toMultiSetPureStrategyGame() throws Exception {
		Map<AttackerType, SetProfileGame<T>> pureStrategyGames = new HashMap<AttackerType, SetProfileGame<T>>();
		for(Map.Entry<AttackerType, SetTargetGame<T>> entry : securityGames.entrySet()) {
			pureStrategyGames.put(entry.getKey(), entry.getValue().getPureStrategyGame());
		}
		return new MultiSetProfileGame<T>(super.getPureStrategyProfiles(), super.getAttackerProbabilities(), pureStrategyGames);
	}
	
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SetTargetGame<T>> entry : securityGames.entrySet()) {
			if(entry.getValue().includesDefenderNegativeAbsentPayoffs()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SetTargetGame<T>> entry : securityGames.entrySet()) {
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
		for(Map.Entry<AttackerType, SetTargetGame<T>> entry : securityGames.entrySet()) {
			output += delim + "P(" + entry.getKey().toString() + ") = " + super.getAttackerProbabilities().get(entry.getKey()) + "\n" + entry.getValue().toString();
			delim = "\n\n";
		}
        return output;
	}
	
}
