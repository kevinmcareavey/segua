package segua.decision_rules.multi_single_profile_decision_rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import segua.AttackerType;
import segua.PureStrategyProfile;
import segua.decision_rules.MultiSingleProfileDecisionRule;
import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.player_pairs.PlayerPairPayoffSingle;
import segua.security_games.profile_games.SingleProfileGame;
import data_structures.AdvancedSet;
import data_structures.PartitionedBBA;

public class SingleDefault extends MultiSingleProfileDecisionRule {
	
	private PartitionedBBA<AttackerType, PureStrategyProfile> defenderBBA;
	private PartitionedBBA<AttackerType, PureStrategyProfile> attackerBBA;
	
	private Map<AttackerType, Map<PureStrategyProfile, Double>> defenderPreferenceDegrees;
	private Map<AttackerType, Map<PureStrategyProfile, Double>> attackerPreferenceDegrees;
	private Map<AttackerType, Map<PureStrategyProfile, Double>> defenderRelativePayoffs;
	private Map<AttackerType, Map<PureStrategyProfile, Double>> attackerRelativePayoffs;
	
	public SingleDefault(MultiSingleProfileGame<BBAPayoff> s) {
		super(s);
		constructBBAs();
	}
	
	private void constructBBAs() {
		AdvancedSet<AttackerType> attackerTypes = super.getMultiSecurityGame().getAttackerTypes();
		AdvancedSet<PureStrategyProfile> pureStrategyProfiles = super.getMultiSecurityGame().getPureStrategyProfiles();
		defenderBBA = new PartitionedBBA<AttackerType, PureStrategyProfile>(attackerTypes, pureStrategyProfiles);
		attackerBBA = new PartitionedBBA<AttackerType, PureStrategyProfile>(attackerTypes, pureStrategyProfiles);
		
		boolean includesDefenderNegativeAbsentPayoffs = super.getMultiSecurityGame().includesDefenderNegativeAbsentPayoffs();
		boolean includesAttackerNegativeAbsentPayoffs = super.getMultiSecurityGame().includesAttackerNegativeAbsentPayoffs();
		
		defenderPreferenceDegrees = new HashMap<AttackerType, Map<PureStrategyProfile, Double>>();
		attackerPreferenceDegrees = new HashMap<AttackerType, Map<PureStrategyProfile, Double>>();
		defenderRelativePayoffs = new HashMap<AttackerType, Map<PureStrategyProfile, Double>>();
		attackerRelativePayoffs = new HashMap<AttackerType, Map<PureStrategyProfile, Double>>();
		for(AttackerType type : attackerTypes) {
			defenderPreferenceDegrees.put(type, new HashMap<PureStrategyProfile, Double>());
			attackerPreferenceDegrees.put(type, new HashMap<PureStrategyProfile, Double>());
			defenderRelativePayoffs.put(type, new HashMap<PureStrategyProfile, Double>());
			attackerRelativePayoffs.put(type, new HashMap<PureStrategyProfile, Double>());
		}
		
		for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType type = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
				PureStrategyProfile focalElement = entryInner.getKey();
				AdvancedSet<PureStrategyProfile> focalSet = new AdvancedSet<PureStrategyProfile>(focalElement);
        		
	        	if(getDefenderRelativePayoff(type, focalElement) != 0) {
	        		// if game has negative absent payoffs
	        		if(includesDefenderNegativeAbsentPayoffs) {
	        			defenderBBA.setMass(type, focalSet, getDefenderIRRRMass(type, focalElement));
	        		} else {
	        			defenderBBA.setMass(type, focalSet, getDefenderCRRRMass(type, focalElement));
	        		}
	        	}
	        	
	        	if(getAttackerRelativePayoff(type, focalElement) != 0) {
	        		// if game has negative absent payoffs
	        		if(includesAttackerNegativeAbsentPayoffs) {
	        			attackerBBA.setMass(type, focalSet, getAttackerIRRRMass(type, focalElement));
	        		} else {
	        			attackerBBA.setMass(type, focalSet, getAttackerCRRRMass(type, focalElement));
	        		}
	        	}
	        }
		}
		
		// if game has negative absent payoffs
		if(includesDefenderNegativeAbsentPayoffs) {
			defenderBBA.setFrameMass(getDefenderIRRRMass());
		}
		if(includesAttackerNegativeAbsentPayoffs) {
			attackerBBA.setFrameMass(getAttackerIRRRMass());
		}
		
		if(!defenderBBA.isValid()) {
			throw new IllegalArgumentException("Defender BBA is not valid.");
		}
		
		if(!attackerBBA.isValid()) {
			throw new IllegalArgumentException("Attacker BBA is not valid.");
		}
    }
	
	private double getDefenderAmbiguityDegree(AttackerType t, PureStrategyProfile psp) {
		double result = super.getMultiSecurityGame().getSecurityGames().get(t).getPayoffs().get(psp).getDefender().getBBA().getAmbiguityDegree();
        return result;
    }
	
	private double getAttackerAmbiguityDegree(AttackerType t, PureStrategyProfile psp) {
		double result = super.getMultiSecurityGame().getSecurityGames().get(t).getPayoffs().get(psp).getAttacker().getBBA().getAmbiguityDegree();
        return result;
    }
	
	private double getDefenderPreferenceDegree(AttackerType t, PureStrategyProfile psp) {
		if(defenderPreferenceDegrees.get(t).containsKey(psp)) {
			return defenderPreferenceDegrees.get(t).get(psp);
		} else {
			double min = super.getDefenderExpectedUtilityMin(t, psp);
			double result = (2 * min + (1 - getDefenderAmbiguityDegree(t, psp)) * (super.getDefenderExpectedUtilityMax(t, psp) - min)) / 2;
			defenderPreferenceDegrees.get(t).put(psp, result);
			return result;
		}
	}
	private double getAttackerPreferenceDegree(AttackerType t, PureStrategyProfile psp) {
		if(attackerPreferenceDegrees.get(t).containsKey(psp)) {
			return attackerPreferenceDegrees.get(t).get(psp);
		} else {
			double min = super.getAttackerExpectedUtilityMin(t, psp);
			double result = (2 * min + (1 - getAttackerAmbiguityDegree(t, psp)) * (super.getAttackerExpectedUtilityMax(t, psp) - min)) / 2;
			attackerPreferenceDegrees.get(t).put(psp, result);
			return result;
		}
	}
	
	private double getDefenderWorstDegree() {
	    Double minimum = null;
	    for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType type = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double element = getDefenderPreferenceDegree(type, entryInner.getKey());
				
				if(minimum == null || element < minimum) {
					minimum = element;
				}
			}
	    }
		return minimum;
	}
	
	private double getAttackerWorstDegree() {
	    Double minimum = null;
	    for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType type = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double element = getAttackerPreferenceDegree(type, entryInner.getKey());
				
				if(minimum == null || element < minimum) {
					minimum = element;
				}
			}
	    }
		return minimum;
	}
	
	private double getDefenderRelativePayoff(AttackerType t, PureStrategyProfile psp) {
		if(defenderRelativePayoffs.get(t).containsKey(psp)) {
			return defenderRelativePayoffs.get(t).get(psp);
		} else {
			double result = getDefenderPreferenceDegree(t, psp) - getDefenderWorstDegree();
			defenderRelativePayoffs.get(t).put(psp, result);
			return result;
		}
	}
	private double getAttackerRelativePayoff(AttackerType t, PureStrategyProfile psp) {
		if(attackerRelativePayoffs.get(t).containsKey(psp)) {
			return attackerRelativePayoffs.get(t).get(psp);
		} else {
			double result = getAttackerPreferenceDegree(t, psp) - getAttackerWorstDegree();
			attackerRelativePayoffs.get(t).put(psp, result);
			return result;
		}
	}
	
	private double getDefenderIRRRMass(AttackerType t, PureStrategyProfile psp) {
		int n = 0;
		double sum = 0;
		
		for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType tprime = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double payoff = getDefenderRelativePayoff(tprime, entryInner.getKey());
				
		    	if(payoff != 0) {
		    		n++;
		    		sum += payoff;
		    	}
			}
		}
		
		sum += Math.sqrt(n);
		double result = getDefenderRelativePayoff(t, psp) / sum;
		return result;
	}
	
	private double getAttackerIRRRMass(AttackerType t, PureStrategyProfile psp) {
		int n = 0;
		double sum = 0;
		
		for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType tprime = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double payoff = getAttackerRelativePayoff(tprime, entryInner.getKey());
				
		    	if(payoff != 0) {
		    		n++;
		    		sum += payoff;
		    	}
			}
		}
		
		sum += Math.sqrt(n);
		double result = getAttackerRelativePayoff(t, psp) / sum;
		return result;
	}
	
	private double getDefenderIRRRMass() {
		int n = 0;
		double sum = 0;
		
		for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType t = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double payoff = getDefenderRelativePayoff(t, entryInner.getKey());
				
		    	if(payoff != 0) {
		    		n++;
		    		sum += payoff;
		    	}
			}
		}
		
		sum += Math.sqrt(n);
		double result = Math.sqrt(n) / sum;
		return result;
	}
	
	private double getAttackerIRRRMass() {
		int n = 0;
		double sum = 0;
		
		for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType t = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double payoff = getAttackerRelativePayoff(t, entryInner.getKey());
				
		    	if(payoff != 0) {
		    		n++;
		    		sum += payoff;
		    	}
			}
		}
		
		sum += Math.sqrt(n);
		double result = Math.sqrt(n) / sum;
		return result;
	}
	
	private double getDefenderCRRRMass(AttackerType t, PureStrategyProfile psp) {
		double sum = 0;
		
		for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType tprime = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double payoff = getDefenderRelativePayoff(tprime, entryInner.getKey());
				
		    	if(payoff != 0) {
		    		sum += payoff;
		    	}
			}
		}
		
		return getDefenderRelativePayoff(t, psp) / sum;
	}
	
	private double getAttackerCRRRMass(AttackerType t, PureStrategyProfile psp) {
		double sum = 0;
		
		for(Entry<AttackerType, SingleProfileGame<BBAPayoff>> entryOuter : super.getMultiSecurityGame().getSecurityGames().entrySet()) {
			AttackerType tprime = entryOuter.getKey();
			SingleProfileGame<BBAPayoff> game = entryOuter.getValue();
			for(Entry<PureStrategyProfile, PlayerPairPayoffSingle<BBAPayoff>> entryInner : game.getPayoffs().entrySet()) {
		    	double payoff = getAttackerRelativePayoff(tprime, entryInner.getKey());
				
		    	if(payoff != 0) {
		    		sum += payoff;
		    	}
			}
		}
		
		return getAttackerRelativePayoff(t, psp) / sum;
	}
	
	@Override
	public PlayerPairPayoffSingle<NormalFormPayoff> getNormalForm(AttackerType t, PureStrategyProfile p) {
		AdvancedSet<PureStrategyProfile> focalSet = new AdvancedSet<PureStrategyProfile>(p);
		return new PlayerPairPayoffSingle<NormalFormPayoff>(
				new DoublePayoff(defenderBBA.getPointValuedBelief(t, focalSet)),
				new DoublePayoff(attackerBBA.getPointValuedBelief(t, focalSet))
		);
	}
	
}
