package segua.framework.decision_rules.multi_single_profile_decision_rules;

import segua.data_structures.Interval;
import segua.framework.AttackerType;
import segua.framework.PureStrategyProfile;
import segua.framework.decision_rules.MultiSingleProfileDecisionRule;
import segua.framework.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.framework.payoffs.payoff_single.BBAPayoff;
import segua.framework.payoffs.payoff_single.NormalFormPayoff;
import segua.framework.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.framework.player_pairs.PlayerPairPayoffSingle;

public class HurwitzCriterion extends MultiSingleProfileDecisionRule {
	
	private double alpha;
	
	public HurwitzCriterion(MultiSingleProfileGame<BBAPayoff> s, double a) throws Exception {
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
