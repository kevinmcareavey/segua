package segua.framework;

import segua.data_structures.AdvancedSet;
import segua.framework.payoffs.PayoffSingle;
import segua.framework.security_games.ProfileGame;

public abstract class SecurityGame<T extends PayoffSingle> {
	
	public abstract AdvancedSet<PureStrategyProfile> getPureStrategyProfiles();
	
	public abstract ProfileGame<T> getPureStrategyGame() throws Exception;
	
	public abstract boolean includesDefenderNegativeAbsentPayoffs();
	
	public abstract boolean includesAttackerNegativeAbsentPayoffs();
	
}
