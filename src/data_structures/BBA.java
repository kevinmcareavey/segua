package data_structures;

import java.util.HashMap;
import java.util.Map;

import utilities.Utilities;

public class BBA<T> {
	
	private AdvancedSet<T> frame;
	private Map<AdvancedSet<T>, Double> masses;
	
	public BBA(AdvancedSet<T> f) {

		frame = f;
		masses = new HashMap<AdvancedSet<T>, Double>();

	}
	
	public AdvancedSet<T> getFrame() {
		
		return frame;
		
	}
	
	public Map<AdvancedSet<T>, Double> getMasses() {
		
		return masses;
		
	}
	
	public void addMass(AdvancedSet<T> subset, double value) {

		if(value < 0 || value > 1) {
			throw new IllegalArgumentException("The mass value must be in the range [0, 1].");
		}

		if(!subset.subsetOf(frame)) {
			throw new IllegalArgumentException("The input must be a subset of the frame of discernment.");
		}

		if(value == 0) {
			masses.remove(subset);
		} else {
			masses.put(subset, value);
		}
		
	}
	
	public double getMass(AdvancedSet<T> subset) {
		
        double result = 0;
        
        if(masses.containsKey(subset)) {
        	result = masses.get(subset);
        }
        
        return result;
        
    }
	
	public double getBelief(AdvancedSet<T> subset) {
		
		double sum = 0;
        
        for(Map.Entry<AdvancedSet<T>, Double> outer : masses.entrySet()) {
        	
        	AdvancedSet<T> subsetFocal = outer.getKey();
        	
        	if(subsetFocal.subsetOf(subset)) {
        		double mass = outer.getValue();
        		sum += mass;
        	}
            
        }
        
        return sum;
        
    }
	
	public double getPlausibility(AdvancedSet<T> subset) {
		
		double sum = 0;
        
        for(Map.Entry<AdvancedSet<T>, Double> outer : masses.entrySet()) {
        	
        	AdvancedSet<T> subsetFocal = outer.getKey();
        	
        	if(subset.intersects(subsetFocal)) {
    			double mass = outer.getValue();
    			sum += mass;
    		}
        	
        }
        
        return sum;
        
    }
	
	public double getAmbiguityDegree() {
		
        double sum = 0;
        
        for(Map.Entry<AdvancedSet<T>, Double> outer : masses.entrySet()) {
        	
        	AdvancedSet<T> subsetFocal = outer.getKey();
    		double mass = outer.getValue();
    		sum += mass * log(subsetFocal.size(), 2);
            
        }
        
        return sum / log(frame.size(), 2);
        
    }
	
	public double getAmbiguityDegree(AdvancedSet<T> subset) {
		
        double sum = 0;
        
        for(Map.Entry<AdvancedSet<T>, Double> outer : masses.entrySet()) {
        	
        	AdvancedSet<T> subsetFocal = outer.getKey();
        	
        	if(subset.intersects(subsetFocal)) {
        		double mass = outer.getValue();
        		sum += mass * log(subsetFocal.size(), 2);
        	}
            
        }
        
        return sum / log(frame.size(), 2);
        
    }
	
	public double getPointValuedBelief(AdvancedSet<T> subset) {
		
		double belief = getBelief(subset);
		
		return (2 * belief + (1 - getAmbiguityDegree(subset)) * (getPlausibility(subset) - belief)) / 2;
		
	}
	
	public boolean isValid() {
		
		boolean result = false;
		double sum = sum();
		
		double deviation = 1e-10;
		
		if(sum >= 1 - deviation && sum <= 1 + deviation) {
			result = true;
		}
		
		return result;
		
	}
	
	private static double log(int i, int base) {
		
	    return Math.log(i) / Math.log(base);
	    
	}
	
	public double sum() {
		double sum = 0;
		for(Map.Entry<AdvancedSet<T>, Double> entry : masses.entrySet()) {
			sum += entry.getValue();
		}
		return sum;
	}
	
	@Override
	public String toString() {
		String output = "{";
		String delim = "";
		for(Map.Entry<AdvancedSet<T>, Double> entry : masses.entrySet()) {
			output += delim + "m(";
			if(entry.getKey().equals(frame)) {
				output += "...";
			} else {
				output += entry.getKey().toString();
			}
			output += ")=" + Utilities.format(entry.getValue());
			delim = ", ";
		}
		output += "}";
		return output;
	}

}
