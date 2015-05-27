package data_structures;

public class Interval {
	
	private final int left;
	private final int right;
	
	public Interval(int l, int r) {
		
		left = l;
		right = r;
		
	}
	
	public int getLeft() {
		
		return left;
		
	}
	
	public int getRight() {
		
		return right;
		
	}
	
	private int getMin() {
		
		if(left <= right) {
			return left;
		} else {
			return right;
		}
		
	}
	
	private int getMax() {
		
		if(left <= right) {
			return right;
		} else {
			return left;
		}
		
	}
	
	public boolean contains(int i) {
		
		return (i >= getMin()) && (i <= getMax());
		
	}
	
	public AdvancedSet<Integer> getAdvancedSet() {
		
		AdvancedSet<Integer> set = new AdvancedSet<Integer>();
		
		for(int i = getMin(); i <= getMax(); i++) {
			
			set.add(i);
			
		}
		
		return set;
		
	}
	
	@Override
	public String toString() {
		
		return "[" + left + "," + right + "]";
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + left;
		result = prime * result + right;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval other = (Interval) obj;
		if (left != other.left)
			return false;
		if (right != other.right)
			return false;
		return true;
	}

}
