package segua.framework;

import java.util.Map;
import java.util.Map.Entry;

import segua.data_structures.AdvancedSet;
import segua.framework.payoffs.PayoffSingle;

public abstract class MultiSecurityGame<T extends PayoffSingle> {
	
	private Map<AttackerType, Double> attackerProbabilities;
	
	public MultiSecurityGame(Map<AttackerType, Double> a) {
		attackerProbabilities = a;
	}
	
	public Map<AttackerType, Double> getAttackerProbabilities() {
		return attackerProbabilities;
	}
	
	public AdvancedSet<AttackerType> getAttackerTypes() {
		AdvancedSet<AttackerType> attackerTypes = new AdvancedSet<AttackerType>();
		for(Entry<AttackerType, Double> entry : attackerProbabilities.entrySet()) {
			attackerTypes.add(entry.getKey());
		}
		return attackerTypes;
	}
	
	public abstract AdvancedSet<Target> getTargets();
	
	public abstract AdvancedSet<PureStrategyProfile> getPureStrategyProfiles();
	
}
