package segua;

import java.util.Map;

public abstract class Solver {
	
	public abstract void solve() throws Exception;
	
	public abstract MixedStrategy getDefenderMixedStrategy();
	
	public abstract Map<AttackerType, Target> getAttackerPureStrategies();
	
	public abstract double getDefenderMaxEU();
	
}
