package segua.security_games;

import segua.PureStrategyProfile;
import segua.SecurityGame;
import segua.payoffs.PayoffSingle;
import data_structures.AdvancedSet;

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
