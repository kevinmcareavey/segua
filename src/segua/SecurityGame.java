package segua;

import segua.payoffs.PayoffSingle;
import segua.security_games.ProfileGame;
import data_structures.AdvancedSet;

public abstract class SecurityGame<T extends PayoffSingle> {
	
	public abstract AdvancedSet<PureStrategyProfile> getPureStrategyProfiles();
	
	public abstract ProfileGame<T> getPureStrategyGame() throws Exception;
	
	public abstract boolean includesDefenderNegativeAbsentPayoffs();
	
	public abstract boolean includesAttackerNegativeAbsentPayoffs();
	
}
