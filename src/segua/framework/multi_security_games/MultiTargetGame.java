package segua.framework.multi_security_games;

import java.util.Map;

import segua.data_structures.AdvancedSet;
import segua.framework.AttackerType;
import segua.framework.MultiSecurityGame;
import segua.framework.PureStrategyProfile;
import segua.framework.Target;
import segua.framework.payoffs.PayoffSingle;

public abstract class MultiTargetGame<T extends PayoffSingle> extends MultiSecurityGame<T> {
	
	private AdvancedSet<Target> targets;
	
	public MultiTargetGame(AdvancedSet<Target> t, Map<AttackerType, Double> a) {
		super(a);
		targets = t;
	}
	
	@Override
	public AdvancedSet<Target> getTargets() {
		return targets;
	}
	
	@Override
	public AdvancedSet<PureStrategyProfile> getPureStrategyProfiles() {
		AdvancedSet<PureStrategyProfile> pureStrategyProfiles = new AdvancedSet<PureStrategyProfile>();
		for(Target x : targets) {
			for(Target y : targets) {
				pureStrategyProfiles.add(new PureStrategyProfile(x, y));
			}
		}
		return pureStrategyProfiles;
	}
	
}
