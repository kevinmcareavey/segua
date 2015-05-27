package data_structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PartitionedBBA<T, U> {
	
	private class Pair {
		
		private T left;
		private U right;
		
		public Pair(T t, U u) {
			left = t;
			right = u;
		}

		@Override
		public String toString() {
			return "(" + left.toString() + "," + right.toString() + ")";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((left == null) ? 0 : left.hashCode());
			result = prime * result + ((right == null) ? 0 : right.hashCode());
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
			@SuppressWarnings("unchecked")
			Pair other = (Pair) obj;
			if(!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if(left == null) {
				if(other.left != null) {
					return false;
				}
			} else if(!left.equals(other.left)) {
				return false;
			}
			if(right == null) {
				if(other.right != null) {
					return false;
				}
			} else if(!right.equals(other.right)) {
				return false;
			}
			return true;
		}

		private PartitionedBBA<T, U> getOuterType() {
			return PartitionedBBA.this;
		}
		
	}
	
	private AdvancedSet<T> partitioner;
	private AdvancedSet<U> framePartition;
	private Map<T, Map<AdvancedSet<U>, Double>> massesPartition;
	private double frameMass;
	
	public PartitionedBBA(AdvancedSet<T> p, AdvancedSet<U> f) {
		partitioner = p;
		framePartition = f;
		
		massesPartition = new HashMap<T, Map<AdvancedSet<U>, Double>>();
		for(T type : partitioner) {
			massesPartition.put(type, new HashMap<AdvancedSet<U>, Double>());
		}
		
		frameMass = 0;
	}
	
	public AdvancedSet<Pair> getFrame() {
		AdvancedSet<Pair> pairFrame = new AdvancedSet<Pair>();
		for(T t : partitioner) {
			for(U u : framePartition) {
				pairFrame.add(new Pair(t, u));
			}
		}
		return pairFrame;
	}
	
	public Map<AdvancedSet<Pair>, Double> getMasses() {
		Map<AdvancedSet<Pair>, Double> pairMasses = new HashMap<AdvancedSet<Pair>, Double>();
		for(Entry<T, Map<AdvancedSet<U>, Double>> entryOuter : massesPartition.entrySet()) {
			T t = entryOuter.getKey();
			Map<AdvancedSet<U>, Double> tMasses = entryOuter.getValue();
			for(Entry<AdvancedSet<U>, Double> entryInner : tMasses.entrySet()) {
				AdvancedSet<U> uSet = entryInner.getKey();
				double uSetMass = entryInner.getValue();
				AdvancedSet<Pair> uPairSet = new AdvancedSet<Pair>();
				for(U u : uSet) {
					uPairSet.add(new Pair(t, u));
				}
				pairMasses.put(uPairSet, uSetMass);
			}
		}
		if(frameMass > 0) {
			pairMasses.put(getFrame(), frameMass);
		}
		return pairMasses;
	}
	
	public void setMass(T t, AdvancedSet<U> subset, double value) {
		if(value < 0 || value > 1) {
			throw new IllegalArgumentException("The mass value must be in the range [0, 1].");
		}
		
		if(!subset.subsetOf(framePartition)) {
			throw new IllegalArgumentException("The input must be a subset of the frame of discernment.");
		}

		if(value == 0) {
			massesPartition.get(t).remove(subset);
		} else {
			massesPartition.get(t).put(subset, value);
		}
	}
	
	public void setFrameMass(double value) {
		if(value < 0 || value > 1) {
			throw new IllegalArgumentException("The mass value must be in the range [0, 1].");
		}
		
		frameMass = value;
	}
	
	public double getMass(T t, AdvancedSet<U> subset) {
        double result = 0;
        
        if(massesPartition.get(t).containsKey(subset)) {
        	result = massesPartition.get(t).get(subset);
        }
        
        return result;
    }
	
	public double getFrameMass() {
        return frameMass;
    }
	
	public double getBelief(T t, AdvancedSet<U> subset) {
		double sum = frameMass;
        
        for(Map.Entry<AdvancedSet<U>, Double> entry : massesPartition.get(t).entrySet()) {
        	
        	AdvancedSet<U> subsetFocal = entry.getKey();
        	
        	if(subsetFocal.subsetOf(subset)) {
        		double mass = entry.getValue();
        		sum += mass;
        	}
            
        }
        
        return sum;
    }
	
	public double getPlausibility(T t, AdvancedSet<U> subset) {
		double sum = frameMass;
        
        for(Map.Entry<AdvancedSet<U>, Double> outer : massesPartition.get(t).entrySet()) {
        	
        	AdvancedSet<U> subsetFocal = outer.getKey();
        	
        	if(subset.intersects(subsetFocal)) {
    			double mass = outer.getValue();
    			sum += mass;
    		}
        	
        }
        
        return sum;
    }
	
	public double getAmbiguityDegree(T t) {
        double sum = frameMass * log(partitioner.size() * framePartition.size(), 2);
        
        for(Map.Entry<AdvancedSet<U>, Double> outer : massesPartition.get(t).entrySet()) {
        	
        	AdvancedSet<U> subsetFocal = outer.getKey();
    		double mass = outer.getValue();
    		sum += mass * log(subsetFocal.size(), 2);
            
        }
        
        return sum / log(partitioner.size() * framePartition.size(), 2);
    }
	
	public double getAmbiguityDegree(T t, AdvancedSet<U> subset) {
        double sum = frameMass * log(partitioner.size() * framePartition.size(), 2);
        
        for(Map.Entry<AdvancedSet<U>, Double> outer : massesPartition.get(t).entrySet()) {
        	
        	AdvancedSet<U> subsetFocal = outer.getKey();
        	
        	if(subset.intersects(subsetFocal)) {
        		double mass = outer.getValue();
        		sum += mass * log(subsetFocal.size(), 2);
        	}
            
        }
        
        return sum / log(partitioner.size() * framePartition.size(), 2);
    }
	
	public double getPointValuedBelief(T t, AdvancedSet<U> subset) {
		double belief = getBelief(t, subset);
		return (2 * belief + (1 - getAmbiguityDegree(t, subset)) * (getPlausibility(t, subset) - belief)) / 2;
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
		double sum = frameMass;
		for(Entry<T, Map<AdvancedSet<U>, Double>> entryOuter : massesPartition.entrySet()) {
			for(Map.Entry<AdvancedSet<U>, Double> entryInner : entryOuter.getValue().entrySet()) {
				sum += entryInner.getValue();
			}
		}
		return sum;
	}
	
	@Override
	public String toString() {
		String output = "{";
		String delim = "";
		
		for(Entry<T, Map<AdvancedSet<U>, Double>> entryOuter : massesPartition.entrySet()) {
			T type = entryOuter.getKey();
	        for(Map.Entry<AdvancedSet<U>, Double> entryInner : entryOuter.getValue().entrySet()) {
	        	
        		AdvancedSet<Pair> focalSet = new AdvancedSet<Pair>();
        		for(U psp : entryInner.getKey()) {
        			focalSet.add(new Pair(type, psp));
        		}
	        	
	        	output += delim + "m(" + focalSet.toString() + ") = " + String.format("%.2f", entryInner.getValue());
	        	delim = ", ";
	        	
	        }
		}
		
		output += delim + "m(...) = " + String.format("%.2f", frameMass);
        output += "}";
        
        return output;
	}

}
