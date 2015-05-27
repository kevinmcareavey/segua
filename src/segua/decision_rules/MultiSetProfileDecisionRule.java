package segua.decision_rules;

import java.util.Map;

import data_structures.AdvancedSet;
import segua.AttackerType;
import segua.DecisionRule;
import segua.PureStrategyProfile;
import segua.multi_security_games.profile_games.MultiSetProfileGame;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.player_pairs.PlayerPairPayoffSingle;
import utilities.Utilities;

public abstract class MultiSetProfileDecisionRule extends DecisionRule {
	
	private MultiSetProfileGame<BBAPayoff> securityGame;
	
	public MultiSetProfileDecisionRule(MultiSetProfileGame<BBAPayoff> s) {
		securityGame = s;
	}
	
	@Override
	public MultiSetProfileGame<BBAPayoff> getMultiSecurityGame() {
		return securityGame;
	}
	
	public double getDefenderExpectedUtilityMax(AttackerType t, AdvancedSet<PureStrategyProfile> p) {
		PlayerPairPayoffSingle<BBAPayoff> payoff = securityGame.getSecurityGames().get(t).getPayoffs().get(p);
		Map<AdvancedSet<Integer>, Double> masses = payoff.getDefender().getBBA().getMasses();
		
		double sum = 0;
		for(Map.Entry<AdvancedSet<Integer>, Double> outer : masses.entrySet()) {
			AdvancedSet<Integer> subsetFocal = outer.getKey();
			double mass = outer.getValue();
			sum += mass * Utilities.max(subsetFocal);
		}
        
        return sum;
    }
	
	public double getAttackerExpectedUtilityMax(AttackerType t, AdvancedSet<PureStrategyProfile> p) {
		PlayerPairPayoffSingle<BBAPayoff> payoff = securityGame.getSecurityGames().get(t).getPayoffs().get(p);
		Map<AdvancedSet<Integer>, Double> masses = payoff.getAttacker().getBBA().getMasses();
		
		double sum = 0;
		for(Map.Entry<AdvancedSet<Integer>, Double> outer : masses.entrySet()) {
			AdvancedSet<Integer> subsetFocal = outer.getKey();
			double mass = outer.getValue();
			sum += mass * Utilities.max(subsetFocal);
		}
        
        return sum;
    }
	
	public double getDefenderExpectedUtilityMin(AttackerType t, AdvancedSet<PureStrategyProfile> p) {
		PlayerPairPayoffSingle<BBAPayoff> payoff = securityGame.getSecurityGames().get(t).getPayoffs().get(p);
		Map<AdvancedSet<Integer>, Double> masses = payoff.getDefender().getBBA().getMasses();
		
		double sum = 0;
	    for(Map.Entry<AdvancedSet<Integer>, Double> outer : masses.entrySet()) {
	    	AdvancedSet<Integer> subsetFocal = outer.getKey();
	    	double mass = outer.getValue();
	    	sum += mass * Utilities.min(subsetFocal);
	    }
	    
	    return sum;
	}
	
	public double getAttackerExpectedUtilityMin(AttackerType t, AdvancedSet<PureStrategyProfile> p) {
		PlayerPairPayoffSingle<BBAPayoff> payoff = securityGame.getSecurityGames().get(t).getPayoffs().get(p);
		Map<AdvancedSet<Integer>, Double> masses = payoff.getAttacker().getBBA().getMasses();
		
		double sum = 0;
	    for(Map.Entry<AdvancedSet<Integer>, Double> outer : masses.entrySet()) {
	    	AdvancedSet<Integer> subsetFocal = outer.getKey();
	    	double mass = outer.getValue();
	    	sum += mass * Utilities.min(subsetFocal);
	    }
	    
	    return sum;
	}
	
	@Override
	public String toString() {
		try {
			return toNormalForm().toString();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
