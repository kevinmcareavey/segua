package segua.framework.decision_rules.multi_single_profile_decision_rules;

import segua.framework.AttackerType;
import segua.framework.PureStrategyProfile;
import segua.framework.decision_rules.MultiSingleProfileDecisionRule;
import segua.framework.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.framework.payoffs.payoff_single.BBAPayoff;
import segua.framework.payoffs.payoff_single.NormalFormPayoff;
import segua.framework.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.framework.player_pairs.PlayerPairPayoffSingle;

public class GammaMaximin extends MultiSingleProfileDecisionRule {
	
	public GammaMaximin(MultiSingleProfileGame<BBAPayoff> s) {
		super(s);
	}
	
	@Override
	public PlayerPairPayoffSingle<NormalFormPayoff> getNormalForm(AttackerType t, PureStrategyProfile p) {
		return new PlayerPairPayoffSingle<NormalFormPayoff>(
				new DoublePayoff(super.getDefenderExpectedUtilityMin(t, p)),
				new DoublePayoff(super.getAttackerExpectedUtilityMin(t, p))
		);
	}
	
}
