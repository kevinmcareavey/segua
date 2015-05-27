package segua.decision_rules.multi_single_profile_decision_rules;

import segua.AttackerType;
import segua.PureStrategyProfile;
import segua.decision_rules.MultiSingleProfileDecisionRule;
import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.player_pairs.PlayerPairPayoffSingle;

public class Maximax extends MultiSingleProfileDecisionRule {

	public Maximax(MultiSingleProfileGame<BBAPayoff> s) {
		super(s);
	}
	
	@Override
	public PlayerPairPayoffSingle<NormalFormPayoff> getNormalForm(AttackerType t, PureStrategyProfile p) {
		return new PlayerPairPayoffSingle<NormalFormPayoff>(
				new DoublePayoff(super.getDefenderExpectedUtilityMax(t, p)),
				new DoublePayoff(super.getAttackerExpectedUtilityMax(t, p))
		);
	}
	
}
