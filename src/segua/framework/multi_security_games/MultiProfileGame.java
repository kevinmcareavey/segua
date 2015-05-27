package segua.framework.multi_security_games;

import java.util.Map;

import segua.data_structures.AdvancedSet;
import segua.framework.AttackerType;
import segua.framework.MultiSecurityGame;
import segua.framework.PureStrategyProfile;
import segua.framework.Target;
import segua.framework.payoffs.PayoffSingle;

public abstract class MultiProfileGame<T extends PayoffSingle> extends MultiSecurityGame<T> {
	
	private AdvancedSet<PureStrategyProfile> pureStrategyProfiles;
	
	public MultiProfileGame(AdvancedSet<PureStrategyProfile> p, Map<AttackerType, Double> a) {
		super(a);
		pureStrategyProfiles = p;
	}
	
	@Override
	public AdvancedSet<Target> getTargets() {
		AdvancedSet<Target> targets = new AdvancedSet<Target>();
		for(PureStrategyProfile psp : pureStrategyProfiles) {
			targets.add(psp.getDefender());
			targets.add(psp.getAttacker());
		}
		return targets;
	}
	
	@Override
	public AdvancedSet<PureStrategyProfile> getPureStrategyProfiles() {
		return pureStrategyProfiles;
	}
	
}
