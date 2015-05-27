package segua;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.player_pairs.PlayerPairPayoffSingle;
import segua.security_games.profile_games.SingleProfileGame;
import data_structures.AdvancedSet;

public abstract class DecisionRule {
	
	public abstract MultiSecurityGame<BBAPayoff> getMultiSecurityGame();
	
	public abstract PlayerPairPayoffSingle<NormalFormPayoff> getNormalForm(AttackerType t, PureStrategyProfile p);
	
	public MultiSingleProfileGame<NormalFormPayoff> toNormalForm() throws Exception {
		AdvancedSet<PureStrategyProfile> pureStrategyProfiles = getMultiSecurityGame().getPureStrategyProfiles();
		Map<AttackerType, SingleProfileGame<NormalFormPayoff>> attackerGames = new HashMap<AttackerType, SingleProfileGame<NormalFormPayoff>>();
		for(Entry<AttackerType, Double> entry : getMultiSecurityGame().getAttackerProbabilities().entrySet()) {
			AttackerType type = entry.getKey();
			SingleProfileGame<NormalFormPayoff> normalForm = new SingleProfileGame<NormalFormPayoff>(pureStrategyProfiles);
			for(PureStrategyProfile psp : pureStrategyProfiles) {
				normalForm.addPayoff(psp, getNormalForm(type, psp));
			}
			attackerGames.put(type, normalForm);
		}
		return new MultiSingleProfileGame<NormalFormPayoff>(
				pureStrategyProfiles, 
				getMultiSecurityGame().getAttackerProbabilities(), 
				attackerGames);
	}
	
}
