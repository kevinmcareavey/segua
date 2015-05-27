package segua.framework;

public final class PureStrategyProfile {
	
	private final Target defender;
	private final Target attacker;
	
	public PureStrategyProfile(Target d, Target a) {
		defender = d;
		attacker = a;
	}
	
	public Target getDefender() {
		return defender;
	}
	
	public Target getAttacker() {
		return attacker;
	}
	
	@Override
	public String toString() {
		return "(" + defender + ", " + attacker + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attacker == null) ? 0 : attacker.hashCode());
		result = prime * result
				+ ((defender == null) ? 0 : defender.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		PureStrategyProfile other = (PureStrategyProfile) obj;
		if(attacker == null) {
			if(other.attacker != null) {
				return false;
			}
		} else if(!attacker.equals(other.attacker)) {
			return false;
		}
		if(defender == null) {
			if(other.defender != null) {
				return false;
			}
		} else if(!defender.equals(other.defender)) {
			return false;
		}
		return true;
	}
	
}
