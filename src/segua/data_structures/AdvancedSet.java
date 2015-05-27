package segua.data_structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AdvancedSet<T> extends HashSet<T> {
	
	private static final long serialVersionUID = -8301196773045563033L;

	public AdvancedSet() {
		super();
	}
	
	public AdvancedSet(@SuppressWarnings("unchecked") T... inputs) {
		super();
		for(int i = 0; i < inputs.length; i++) {
			this.add(inputs[i]);
		}
	}
	
	public AdvancedSet(Collection<T> c) {
		super(c);
	}
	
	public boolean subsetOf(AdvancedSet<T> other) {
		return other.containsAll(this);
	}
	
	public boolean supersetOf(AdvancedSet<T> other) {
		return this.containsAll(other);
	}
	
	public boolean intersects(AdvancedSet<T> other) {
		if(!this.isEmpty() && !other.isEmpty()) {
			for(T t : other) {
				if(this.contains(t)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public AdvancedSet<T> setminus(AdvancedSet<T> other) {
		AdvancedSet<T> result = new AdvancedSet<T>();
		for(T element : this) {
			if(!other.contains(element)) {
				result.add(element);
			}
		}
		return result;
	}
	
	public AdvancedSet<AdvancedSet<T>> powerSet() {
		return findPowerSet(this);
	}
	
	private static <T> AdvancedSet<AdvancedSet<T>> findPowerSet(AdvancedSet<T> originalSet) {
		AdvancedSet<AdvancedSet<T>> powerSet = new AdvancedSet<AdvancedSet<T>>();
		if (originalSet.isEmpty()) {
			powerSet.add(new AdvancedSet<T>());
		} else {
			List<T> list = new ArrayList<T>(originalSet);
			T head = list.get(0);
			AdvancedSet<T> rest = new AdvancedSet<T>(list.subList(1, list.size()));
			for(AdvancedSet<T> set : findPowerSet(rest)) {
				AdvancedSet<T> newSet = new AdvancedSet<T>();
				newSet.add(head);
				newSet.addAll(set);
				powerSet.add(newSet);
				powerSet.add(set);
				}
			}
		return powerSet;
	}
	
	@Override
	public String toString() {
		return super.toString().replace("[", "{").replace("]", "}");
	}
	
}
