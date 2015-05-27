package segua.multi_security_games.target_games;

import java.util.HashMap;
import java.util.Map;

import segua.AttackerType;
import segua.PureStrategyProfile;
import segua.Target;
import segua.multi_security_games.MultiTargetGame;
import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.payoffs.PayoffSingle;
import segua.security_games.profile_games.SingleProfileGame;
import segua.security_games.target_games.SingleTargetGame;
import data_structures.AdvancedSet;

public class MultiSingleTargetGame<T extends PayoffSingle> extends MultiTargetGame<T> {
	
	private Map<AttackerType, SingleTargetGame<T>> securityGames;
	
	public MultiSingleTargetGame(AdvancedSet<Target> t, Map<AttackerType, Double> a, Map<AttackerType, SingleTargetGame<T>> s) {
		super(t, a);
		securityGames = s;
	}
	
	public Map<AttackerType, SingleTargetGame<T>> getSecurityGames() {
		return securityGames;
	}
	
	public MultiSingleProfileGame<T> toMultiSinglePureStrategyGame() throws Exception {
		AdvancedSet<PureStrategyProfile> pureStrategyProfiles = new AdvancedSet<PureStrategyProfile>();
		for(Target x : super.getTargets()) {
			for(Target y : super.getTargets()) {
				pureStrategyProfiles.add(new PureStrategyProfile(x, y));
			}
		}
		Map<AttackerType, SingleProfileGame<T>> pureStrategyGames = new HashMap<AttackerType, SingleProfileGame<T>>();
		for(Map.Entry<AttackerType, SingleTargetGame<T>> entry : securityGames.entrySet()) {
			pureStrategyGames.put(entry.getKey(), entry.getValue().getPureStrategyGame());
		}
		return new MultiSingleProfileGame<T>(pureStrategyProfiles, super.getAttackerProbabilities(), pureStrategyGames);
	}
	
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SingleTargetGame<T>> entry : securityGames.entrySet()) {
			if(entry.getValue().includesDefenderNegativeAbsentPayoffs()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<AttackerType, SingleTargetGame<T>> entry : securityGames.entrySet()) {
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
		for(Map.Entry<AttackerType, SingleTargetGame<T>> entry : securityGames.entrySet()) {
			output += delim + "P(" + entry.getKey().toString() + ") = " + super.getAttackerProbabilities().get(entry.getKey()) + "\n" + entry.getValue().toString();
			delim = "\n\n";
		}
        return output;
	}
	
}
