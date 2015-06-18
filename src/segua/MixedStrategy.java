package segua;

import java.util.HashMap;
import java.util.Map;

import utilities.Utilities;

public class MixedStrategy extends HashMap<Target, Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7766746341560480657L;
	
	@Override
	public String toString() {
		String output = "{";
		String delim = "";
		for(Map.Entry<Target, Double> entry : this.entrySet()) {
			output += delim + "P(" + entry.getKey() + ")=" + Utilities.format(entry.getValue());
			delim = ", ";
		}
		output += "}";
		return output;
	}
	
}
