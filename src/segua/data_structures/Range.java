package segua.data_structures;

public abstract class Range extends Interval {
	
	public Range(int l, int r) {
		super(l, r);
	}
	
	public Interval getInterval() {
		return new Interval(super.getLeft(), super.getRight());
	}
	
}
