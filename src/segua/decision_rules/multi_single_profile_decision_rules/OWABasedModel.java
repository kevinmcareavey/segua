package segua.decision_rules.multi_single_profile_decision_rules;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

import data_structures.AdvancedSet;
import data_structures.BBA;
import segua.AttackerType;
import segua.PureStrategyProfile;
import segua.decision_rules.MultiSingleProfileDecisionRule;
import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.player_pairs.PlayerPairPayoffSingle;

public class OWABasedModel extends MultiSingleProfileDecisionRule {
	
	public OWABasedModel(MultiSingleProfileGame<BBAPayoff> s) throws Exception {
		super(s);
	}
	
	public double getWeight(int index, int size) throws Exception {
		if(index > size) {
			throw new Exception("index out of bounds");
		}
		double sum = (double)size * ((double)size + (double)1) / (double)2;
		return ((double)1 / sum) * (double)index;
	}
	
	public double val(AdvancedSet<Integer> a) throws Exception {
		TreeSet<Integer> ordered = new TreeSet<Integer>();
		ordered.addAll(a);
		ArrayList<Integer> sequence = new ArrayList<Integer>();
		sequence.addAll(ordered);
		
		double sum = 0;
		int index = 1;
		int size = sequence.size();
		for(Integer value : sequence) {
			sum += getWeight(index, size) * value;
			index++;
		}
		return sum;
	}
	
	public double owa(BBA<Integer> bba) throws Exception {
		double sum = 0;
		for(Map.Entry<AdvancedSet<Integer>, Double> entry : bba.getMasses().entrySet()) {
			sum += val(entry.getKey()) * entry.getValue();
		}
		return sum;
	}
	
	@Override
	public PlayerPairPayoffSingle<NormalFormPayoff> getNormalForm(AttackerType t, PureStrategyProfile p) throws Exception {
		PlayerPairPayoffSingle<BBAPayoff> payoff = super.getMultiSecurityGame().getSecurityGames().get(t).getPayoffs().get(p);
		return new PlayerPairPayoffSingle<NormalFormPayoff>(
				new DoublePayoff(this.owa(payoff.getDefender().getBBA())),
				new DoublePayoff(this.owa(payoff.getAttacker().getBBA()))
		);
	}
	
//	public static void main(String[] args) {
//		try {
//			AdvancedSet<Integer> set;
//			set = new Interval(0, 9).getAdvancedSet();
//			System.out.println("val(" + set + ") = " + val(set));
//			set = new Interval(2, 6).getAdvancedSet();
//			System.out.println("val(" + set + ") = " + val(set));
//			set = new Interval(-6, -2).getAdvancedSet();
//			System.out.println("val(" + set + ") = " + val(set));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
