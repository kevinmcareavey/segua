package segua.framework.decision_rules;

import java.util.Map;

import segua.data_structures.AdvancedSet;
import segua.framework.AttackerType;
import segua.framework.DecisionRule;
import segua.framework.PureStrategyProfile;
import segua.framework.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.framework.payoffs.payoff_single.BBAPayoff;
import segua.framework.player_pairs.PlayerPairPayoffSingle;
import segua.utilities.Utilities;

public abstract class MultiSingleProfileDecisionRule extends DecisionRule {
	
	private MultiSingleProfileGame<BBAPayoff> securityGame;
	
	public MultiSingleProfileDecisionRule(MultiSingleProfileGame<BBAPayoff> s) {
		securityGame = s;
	}
	
	@Override
	public MultiSingleProfileGame<BBAPayoff> getMultiSecurityGame() {
		return securityGame;
	}
	
	public double getDefenderExpectedUtilityMax(AttackerType t, PureStrategyProfile p) {
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
	
	public double getAttackerExpectedUtilityMax(AttackerType t, PureStrategyProfile p) {
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
	
	public double getDefenderExpectedUtilityMin(AttackerType t, PureStrategyProfile p) {
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
	
	public double getAttackerExpectedUtilityMin(AttackerType t, PureStrategyProfile p) {
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
