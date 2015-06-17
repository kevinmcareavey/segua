package segua.decision_rules.multi_single_profile_decision_rules;

import segua.AttackerType;
import segua.PureStrategyProfile;
import segua.decision_rules.MultiSingleProfileDecisionRule;
import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.player_pairs.PlayerPairPayoffSingle;
import data_structures.Interval;

public class HurwiczCriterion extends MultiSingleProfileDecisionRule {
	
	private double alpha;
	
	public HurwiczCriterion(MultiSingleProfileGame<BBAPayoff> s, double a) throws Exception {
		super(s);
		
		if(a >= 0 && a <= 1) {
			alpha = a;
		} else {
			throw new Exception("alpha must be in the interval " + new Interval(0, 1));
		}
	}
	
	@Override
	public PlayerPairPayoffSingle<NormalFormPayoff> getNormalForm(AttackerType t, PureStrategyProfile p) {
		double defender = (alpha * super.getDefenderExpectedUtilityMin(t, p)) + ((1 - alpha) * super.getDefenderExpectedUtilityMax(t, p));
		double attacker = (alpha * super.getAttackerExpectedUtilityMin(t, p)) + ((1 - alpha) * super.getAttackerExpectedUtilityMax(t, p));
		
		return new PlayerPairPayoffSingle<NormalFormPayoff>(
				new DoublePayoff(defender),
				new DoublePayoff(attacker)
		);
	}
	
}
