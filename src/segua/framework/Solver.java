package segua.framework;

import java.util.Map;

public abstract class Solver {
	
	public abstract void solve() throws Exception;
	
	public abstract Map<Target, Double> getDefenderMixedStrategy();
	
	public abstract Map<AttackerType, Target> getAttackerPureStrategies();
	
	public abstract double getDefenderMaxEU();
	
}
