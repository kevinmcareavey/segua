package segua.framework.decision_rules.multi_single_profile_decision_rules;

import java.util.Map;

import segua.data_structures.AdvancedSet;
import segua.framework.AttackerType;
import segua.framework.PureStrategyProfile;
import segua.framework.decision_rules.MultiSingleProfileDecisionRule;
import segua.framework.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.framework.payoffs.payoff_single.BBAPayoff;
import segua.framework.payoffs.payoff_single.NormalFormPayoff;
import segua.framework.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.framework.player_pairs.PlayerPairPayoffSingle;

public class TransferableBeliefModel extends MultiSingleProfileDecisionRule {
	
	public TransferableBeliefModel(MultiSingleProfileGame<BBAPayoff> s) {
		super(s);
	}
	
	public double weighting(BBAPayoff payoff) {
		Map<AdvancedSet<Integer>, Double> masses = payoff.getBBA().getMasses();
		
		double sum = 0;
		for(Map.Entry<AdvancedSet<Integer>, Double> entry : masses.entrySet()) {
			AdvancedSet<Integer> set = entry.getKey();
			double mass = entry.getValue();
			for(Integer element : set) {
				sum += (mass / (double)set.size()) * (double)element;
			}
		}
		
		return sum;
	}
	
	public PlayerPairPayoffSingle<NormalFormPayoff> getNormalForm(AttackerType t, PureStrategyProfile p) {
		PlayerPairPayoffSingle<BBAPayoff> payoff = super.getMultiSecurityGame().getSecurityGames().get(t).getPayoffs().get(p);
		
		return new PlayerPairPayoffSingle<NormalFormPayoff>(
				new DoublePayoff(weighting(payoff.getDefender())),
				new DoublePayoff(weighting(payoff.getAttacker()))
		);
	}
	
}
