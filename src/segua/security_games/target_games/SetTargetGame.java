package segua.security_games.target_games;

import java.util.HashMap;
import java.util.Map;

import segua.PureStrategyProfile;
import segua.Target;
import segua.payoffs.PayoffSingle;
import segua.payoffs.payoff_single.bba_payoffs.AbsentPayoff;
import segua.player_pairs.PlayerPairPayoffPair;
import segua.player_pairs.PlayerPairPayoffSingle;
import segua.security_games.TargetGame;
import segua.security_games.profile_games.SetProfileGame;
import data_structures.AdvancedSet;

public class SetTargetGame<T extends PayoffSingle> extends TargetGame<T> {
	
	private Map<AdvancedSet<Target>, PlayerPairPayoffPair<T>> payoffs;
	
	public SetTargetGame(AdvancedSet<Target> t) {
		super(t);
		payoffs = new HashMap<AdvancedSet<Target>, PlayerPairPayoffPair<T>>();
	}
	
	public boolean singletonSetsOnly() {
		for(Map.Entry<AdvancedSet<Target>, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			if(entry.getKey().size() != 1) {
				return false;
			}
		}
		return true;
	}
	
	public void addPayoff(AdvancedSet<Target> psp, PlayerPairPayoffPair<T> value) throws Exception {
		
		if(value.getDefender().getNegative() instanceof AbsentPayoff && !(value.getDefender().getPositive() instanceof AbsentPayoff)
				|| value.getDefender().getPositive() instanceof AbsentPayoff && !(value.getDefender().getNegative() instanceof AbsentPayoff)) {
			throw new Exception("neither or both payoffs should be absent for defender");
		} else if(value.getAttacker().getNegative() instanceof AbsentPayoff && !(value.getAttacker().getPositive() instanceof AbsentPayoff)
				|| value.getAttacker().getPositive() instanceof AbsentPayoff && !(value.getAttacker().getNegative() instanceof AbsentPayoff)) {
			throw new Exception("neither or both payoffs should be absent for attacker");
		}
		
		payoffs.put(psp, value);
	}
	
	public void removePayoff(AdvancedSet<Target> psp) throws Exception {
		payoffs.remove(psp);
	}
	
	@Override
	public SetProfileGame<T> getPureStrategyGame() throws Exception {
		
		SetProfileGame<T> setPureStrategyGame = new SetProfileGame<T>(super.getPureStrategyProfiles());
		
		for(Map.Entry<AdvancedSet<Target>, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			
			AdvancedSet<Target> s = entry.getKey();
			PlayerPairPayoffPair<T> fourPayoffs = entry.getValue();
			
			AdvancedSet<PureStrategyProfile> defWinAttLose = new AdvancedSet<PureStrategyProfile>();
			for(Target x : s) {
				defWinAttLose.add(new PureStrategyProfile(x, x));
			}
			setPureStrategyGame.addPayoff(defWinAttLose, new PlayerPairPayoffSingle<T>(fourPayoffs.getDefender().getPositive(), fourPayoffs.getAttacker().getNegative()));
			
			for(Target x : super.getTargets().setminus(s)) {
				AdvancedSet<PureStrategyProfile> defLoseAttWin = new AdvancedSet<PureStrategyProfile>();
				for(Target y : s) {
					defLoseAttWin.add(new PureStrategyProfile(x, y));
				}
				setPureStrategyGame.addPayoff(defLoseAttWin, new PlayerPairPayoffSingle<T>(fourPayoffs.getDefender().getNegative(), fourPayoffs.getAttacker().getPositive()));
			}
			
			AdvancedSet<PureStrategyProfile> defLoseAttWin = new AdvancedSet<PureStrategyProfile>();
			for(Target x : s) {
				for(Target y : s) {
					if(!x.equals(y)) {
						defLoseAttWin.add(new PureStrategyProfile(x, y));
					}
				}
			}
			if(!defLoseAttWin.isEmpty()) {
				setPureStrategyGame.addPayoff(defLoseAttWin, new PlayerPairPayoffSingle<T>(fourPayoffs.getDefender().getNegative(), fourPayoffs.getAttacker().getPositive()));
			}
		
		}
		
		return setPureStrategyGame;
	}
	
	@Override
	public String toString() {
		String output = "";
		String delim = "";
		for(Map.Entry<AdvancedSet<Target>, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			output += delim + entry.getKey() + ": " + entry.getValue();
			delim = "\n";
		}
		return output;
	}
	
	@Override
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<AdvancedSet<Target>, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getDefender().getNegative().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<AdvancedSet<Target>, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getAttacker().getNegative().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}
	
}
