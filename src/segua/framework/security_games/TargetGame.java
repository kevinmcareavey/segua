package segua.framework.security_games;

import segua.data_structures.AdvancedSet;
import segua.framework.PureStrategyProfile;
import segua.framework.SecurityGame;
import segua.framework.Target;
import segua.framework.payoffs.PayoffSingle;

public abstract class TargetGame<T extends PayoffSingle> extends SecurityGame<T> {
	
	private AdvancedSet<Target> targets;
	
	public TargetGame(AdvancedSet<Target> t) {
		targets = t;
	}
	
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
