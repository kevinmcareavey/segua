package segua;

public final class Target {
	
	private final String label;
	
	public Target(String l) {
		label = l;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		Target other = (Target) obj;
		if(label == null) {
			if(other.label != null) {
				return false;
			}
		} else if(!label.equals(other.label)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
