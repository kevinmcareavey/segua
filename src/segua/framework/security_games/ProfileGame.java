package segua.framework.security_games;

import segua.data_structures.AdvancedSet;
import segua.framework.PureStrategyProfile;
import segua.framework.SecurityGame;
import segua.framework.payoffs.PayoffSingle;

public abstract class ProfileGame<T extends PayoffSingle> extends SecurityGame<T> {
	
	private AdvancedSet<PureStrategyProfile> pureStrategyProfiles;
	
	public ProfileGame(AdvancedSet<PureStrategyProfile> p) {
		pureStrategyProfiles = p;
	}
	
	@Override
	public AdvancedSet<PureStrategyProfile> getPureStrategyProfiles() {
		return pureStrategyProfiles;
	}
	
	@Override
	public ProfileGame<T> getPureStrategyGame() {
		return this;
	}
	
}
