package data_structures;

import java.util.HashMap;
import java.util.Map;

import utilities.Utilities;

public class ProbabilityDistribution<T> extends HashMap<T, Double> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6378472408271493264L;
	
	@Override
	public Double put(T element, Double value) {
		if(value < 0 || value > 1) {
			throw new IllegalArgumentException("The mass value must be in the range [0, 1].");
		}
		if(value == 0) {
			return super.remove(element);
		} else {
			return super.put(element, value);
		}
	}
	
	@Override
	public String toString() {
		String output = "{";
		String delim = "";
		for(Map.Entry<T, Double> entry : this.entrySet()) {
			output += delim + "P(" + entry.getKey() + ")=" + Utilities.format(entry.getValue());
			delim = ", ";
		}
		output += "}";
		return output;
	}
	
}
