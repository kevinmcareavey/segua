package segua.framework.security_games.target_games;

import java.util.HashMap;
import java.util.Map;

import segua.data_structures.AdvancedSet;
import segua.framework.PureStrategyProfile;
import segua.framework.Target;
import segua.framework.payoffs.PayoffSingle;
import segua.framework.payoffs.payoff_single.bba_payoffs.AbsentPayoff;
import segua.framework.player_pairs.PlayerPairPayoffPair;
import segua.framework.player_pairs.PlayerPairPayoffSingle;
import segua.framework.security_games.TargetGame;
import segua.framework.security_games.profile_games.SingleProfileGame;

public class SingleTargetGame<T extends PayoffSingle> extends TargetGame<T> {
	
	private Map<Target, PlayerPairPayoffPair<T>> payoffs;
	
	public SingleTargetGame(AdvancedSet<Target> t) {
		super(t);
		payoffs = new HashMap<Target, PlayerPairPayoffPair<T>>();
	}
	
	public Map<Target, PlayerPairPayoffPair<T>> getPayoffs() {
		return payoffs;
	}
	
	public void setPayoff(Target psp, PlayerPairPayoffPair<T> value) throws Exception {
		
		if(value.getDefender().getNegative() instanceof AbsentPayoff && !(value.getDefender().getPositive() instanceof AbsentPayoff)
				|| value.getDefender().getPositive() instanceof AbsentPayoff && !(value.getDefender().getNegative() instanceof AbsentPayoff)) {
			throw new Exception("neither or both payoffs should be absent for defender");
		} else if(value.getAttacker().getNegative() instanceof AbsentPayoff && !(value.getAttacker().getPositive() instanceof AbsentPayoff)
				|| value.getAttacker().getPositive() instanceof AbsentPayoff && !(value.getAttacker().getNegative() instanceof AbsentPayoff)) {
			throw new Exception("neither or both payoffs should be absent for attacker");
		}
		
		payoffs.put(psp, value);
	}
	
	public void removePayoff(Target psp) throws Exception {
		payoffs.remove(psp);
	}
	
	@Override
	public SingleProfileGame<T> getPureStrategyGame() throws Exception {
		
		SingleProfileGame<T> singlePureStrategyGame = new SingleProfileGame<T>(super.getPureStrategyProfiles());
		
		for(Map.Entry<Target, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			
			Target s = entry.getKey();
			PlayerPairPayoffPair<T> fourPayoffs = entry.getValue();
			
			PureStrategyProfile defWinAttLose = new PureStrategyProfile(s, s);
			singlePureStrategyGame.addPayoff(defWinAttLose, new PlayerPairPayoffSingle<T>(fourPayoffs.getDefender().getPositive(), fourPayoffs.getAttacker().getNegative()));
			
			for(Target x : super.getTargets()) {
				if(!s.equals(x)) {
					PureStrategyProfile defLoseAttWin = new PureStrategyProfile(x, s);
					singlePureStrategyGame.addPayoff(defLoseAttWin, new PlayerPairPayoffSingle<T>(fourPayoffs.getDefender().getNegative(), fourPayoffs.getAttacker().getPositive()));
				}
			}
		}
		
		return singlePureStrategyGame;
	}
	
	@Override
	public String toString() {
		String output = "";
		String delim = "";
		for(Map.Entry<Target, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			output += delim + entry.getKey() + ": " + entry.getValue();
			delim = "\n";
		}
		return output;
	}
	
	@Override
	public boolean includesDefenderNegativeAbsentPayoffs() {
		for(Map.Entry<Target, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getDefender().getNegative().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean includesAttackerNegativeAbsentPayoffs() {
		for(Map.Entry<Target, PlayerPairPayoffPair<T>> entry : payoffs.entrySet()) {
			if(entry.getValue().getAttacker().getNegative().isAbsentPayoff()) {
				return true;
			}
		}
		return false;
	}
	
}
