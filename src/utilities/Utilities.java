package utilities;

import data_structures.AdvancedSet;

public class Utilities {
	
	public static Integer min(AdvancedSet<Integer> set) {
		if(set.isEmpty()) {
			throw new IllegalArgumentException("The input must be a non-empty set.");
		}
		
	    Integer mimimum = null;
		for(Integer element : set) {
			if(mimimum == null || element < mimimum) {
				mimimum = element;
			}
		}
		
		return mimimum;
	}
	
	public static Integer max(AdvancedSet<Integer> set) {
		if(set.isEmpty()) {
			throw new IllegalArgumentException("The input must be a non-empty set.");
		}
		
	    Integer maximum = null;
		for(Integer element : set) {
			if(maximum == null || element > maximum) {
				maximum = element;
			}
		}
		
		return maximum;
	}
	
}
