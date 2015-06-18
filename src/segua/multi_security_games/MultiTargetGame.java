package segua.multi_security_games;

import segua.AttackerProbabilities;
import segua.MultiSecurityGame;
import segua.PureStrategyProfile;
import segua.Target;
import segua.payoffs.PayoffSingle;
import data_structures.AdvancedSet;

public abstract class MultiTargetGame<T extends PayoffSingle> extends MultiSecurityGame<T> {
	
	private AdvancedSet<Target> targets;
	
	public MultiTargetGame(AdvancedSet<Target> t, AttackerProbabilities a) {
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
