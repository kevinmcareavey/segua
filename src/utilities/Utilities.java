package utilities;

import java.text.DecimalFormat;

import data_structures.AdvancedSet;

public class Utilities {
	
	private static DecimalFormat formatter = new DecimalFormat("#.###");
	
	public static String format(double d) {
		return formatter.format(d);
	}
	
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
