package evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import data_structures.AdvancedSet;
import data_structures.BBA;
import data_structures.Interval;
import data_structures.Range;
import data_structures.ranges.NegativeRange;
import data_structures.ranges.PositiveRange;
import segua.AttackerType;
import segua.Target;
import segua.multi_security_games.target_games.MultiSingleTargetGame;
import segua.payoffs.PayoffPair;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.payoffs.payoff_single.bba_payoffs.AbsentPayoff;
import segua.payoffs.payoff_single.bba_payoffs.AmbiguityLotteryPayoff;
import segua.payoffs.payoff_single.bba_payoffs.IntervalPayoff;
import segua.payoffs.payoff_single.bba_payoffs.PointValuePayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.DoublePayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.IntegerPayoff;
import segua.player_pairs.PlayerPairPayoffPair;
import segua.security_games.target_games.SetTargetGame;
import segua.security_games.target_games.SingleTargetGame;
import utilities.Utilities;

public abstract class Randomizer {
	
	private static Random rand = new Random();
	
	public static PointValuePayoff randomPointValuePayoff(Range range) {
		int pointValue = randomInteger(range.getLeft(), range.getRight());
		return new PointValuePayoff(range, pointValue);
	}
	
	public static PointValuePayoff randomPointValuePayoffExcludeEnds(Range range) {
		int pointValue;
		if(range.getAdvancedSet().size() > 2) {
			pointValue = randomInteger(range.getLeft() + 1, range.getRight() - 1);
		} else {
			pointValue = randomInteger(range.getLeft(), range.getRight());
		}
		return new PointValuePayoff(range, pointValue);
	}
	
	public static IntervalPayoff randomIntervalPayoff(Range range) {
		Interval interval = randomInterval(range.getLeft(), range.getRight());
		return new IntervalPayoff(range, interval);
	}
	
	public static AmbiguityLotteryPayoff randomAmbiguityLotteryPayoff(Range range) throws Exception {
		AdvancedSet<Integer> focalSet = randomInterval(range.getLeft(), range.getRight()).getAdvancedSet();
		while(focalSet.equals(range.getAdvancedSet())) {
			focalSet = randomInterval(range.getLeft(), range.getRight()).getAdvancedSet();
		}
		double p = randomDouble(0, 1);
		BBA<Integer> bba = new BBA<Integer>(range.getAdvancedSet());
		bba.addMass(focalSet, p);
		bba.addMass(range.getAdvancedSet(), 1-p);
		return new AmbiguityLotteryPayoff(range, bba);
	}
	
	public static BBAPayoff randomAmbiguousPayoff(Range range) throws Exception {
		
		switch(randomInteger(0, 3)) {
			case 0: // absent
				return new AbsentPayoff(range);
			case 1: // point-valued
				return randomPointValuePayoff(range);
			case 2: // interval
				return randomIntervalPayoff(range);
			case 3: // ambiguity lottery
				return randomAmbiguityLotteryPayoff(range);
		}
		
		return null;
		
	}
	
	public static IntegerPayoff randomIntegerPayoff(Range range) {
		return new IntegerPayoff(randomInteger(range.getLeft()+1, range.getRight()-1));
	}
	
	public static DoublePayoff randomDoublePayoff(Range range) {
		return new DoublePayoff(randomDouble(range.getLeft(), range.getRight()));
	}
	
	public static int randomInteger(int min, int max) {
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
	public static Interval randomInterval(int min, int max) {
		int left = randomInteger(min, max);
		int right = randomInteger(min, max);
		while(left == right) {
			left = randomInteger(min, max);
			right = randomInteger(min, max);
		}
		if(left > right) {
			int temp = left;
			left = right;
			right = temp;
		}
		return new Interval(left, right);
	}
	
	public static double randomDouble(double min, double max) {
		double randomNum = min + (max - min) * rand.nextDouble();
		return randomNum;
	}
	
	public static SetTargetGame<BBAPayoff> randomBBASetTargetGame(int numTargets, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = new AdvancedSet<Target>();
		for(int i = 0; i < numTargets; i++) {
			targets.add(new Target("t" + i));
		}
		
		SetTargetGame<BBAPayoff> tg = new SetTargetGame<BBAPayoff>(targets);
		
		for(Target target : targets) {
			
			BBAPayoff defNeg = randomAmbiguousPayoff(negative);
			BBAPayoff defPos = randomAmbiguousPayoff(positive);
			while(defNeg instanceof AbsentPayoff && !(defPos instanceof AbsentPayoff)
					|| defPos instanceof AbsentPayoff && !(defNeg instanceof AbsentPayoff)) {
				defNeg = Randomizer.randomAmbiguousPayoff(negative);
				defPos = Randomizer.randomAmbiguousPayoff(positive);
			}
			BBAPayoff attNeg = randomAmbiguousPayoff(negative);
			BBAPayoff attPos = randomAmbiguousPayoff(positive);
			while(attNeg instanceof AbsentPayoff && !(attPos instanceof AbsentPayoff)
					|| attPos instanceof AbsentPayoff && !(attNeg instanceof AbsentPayoff)) {
				attNeg = Randomizer.randomAmbiguousPayoff(negative);
				attPos = Randomizer.randomAmbiguousPayoff(positive);
			}
			
			tg.addPayoff(
					new AdvancedSet<Target>(target), 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									defNeg, 
									defPos
							),
							new PayoffPair<BBAPayoff>(
									attNeg, 
									attPos
							)
					)
			);
			
		}
		
		return tg;
	}
	
	public static SetTargetGame<IntegerPayoff> randomIntegerSetTargetGame(int numTargets, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = new AdvancedSet<Target>();
		for(int i = 0; i < numTargets; i++) {
			targets.add(new Target("t" + i));
		}
		
		SetTargetGame<IntegerPayoff> tg = new SetTargetGame<IntegerPayoff>(targets);
		
		for(Target target : targets) {
			
			tg.addPayoff(
					new AdvancedSet<Target>(target), 
					new PlayerPairPayoffPair<IntegerPayoff>(
							new PayoffPair<IntegerPayoff>(
									randomIntegerPayoff(negative), 
									randomIntegerPayoff(positive)
							),
							new PayoffPair<IntegerPayoff>(
									randomIntegerPayoff(negative), 
									randomIntegerPayoff(positive)
							)
					)
			);
			
		}
		
		return tg;
	}
	
	public static SingleTargetGame<BBAPayoff> randomBBASingleTargetGame(int numTargets, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = new AdvancedSet<Target>();
		for(int i = 0; i < numTargets; i++) {
			targets.add(new Target("t" + i));
		}
		
		SingleTargetGame<BBAPayoff> tg = new SingleTargetGame<BBAPayoff>(targets);
		
		for(Target target : targets) {
			
			BBAPayoff defNeg = randomAmbiguousPayoff(negative);
			BBAPayoff defPos = randomAmbiguousPayoff(positive);
			while(defNeg instanceof AbsentPayoff && !(defPos instanceof AbsentPayoff)
					|| defPos instanceof AbsentPayoff && !(defNeg instanceof AbsentPayoff)) {
				defNeg = Randomizer.randomAmbiguousPayoff(negative);
				defPos = Randomizer.randomAmbiguousPayoff(positive);
			}
			BBAPayoff attNeg = randomAmbiguousPayoff(negative);
			BBAPayoff attPos = randomAmbiguousPayoff(positive);
			while(attNeg instanceof AbsentPayoff && !(attPos instanceof AbsentPayoff)
					|| attPos instanceof AbsentPayoff && !(attNeg instanceof AbsentPayoff)) {
				attNeg = Randomizer.randomAmbiguousPayoff(negative);
				attPos = Randomizer.randomAmbiguousPayoff(positive);
			}
			
			tg.setPayoff(
					target, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									defNeg, 
									defPos
							),
							new PayoffPair<BBAPayoff>(
									attNeg, 
									attPos
							)
					)
			);
			
		}
		
		return tg;
	}
	
	public static AdvancedSet<Target> getTargetSet(int numTargets) {
		AdvancedSet<Target> targets = new AdvancedSet<Target>();
		for(int i = 0; i < numTargets; i++) {
			targets.add(new Target("t" + i));
		}
		return targets;
	}
	
	public static AdvancedSet<AttackerType> getAttackerTypeSet(int numTypes) {
		AdvancedSet<AttackerType> types = new AdvancedSet<AttackerType>();
		for(int i = 0; i < numTypes; i++) {
			types.add(new AttackerType("a" + i));
		}
		return types;
	}
	
	public static Map<AttackerType, Double> getAttackerTypeProbabilityDistribution(AdvancedSet<AttackerType> types) {
		Map<AttackerType, Double> attackerTypes = new HashMap<AttackerType, Double>();
		double remaining = 1;
		int i = 0;
		int count = types.size();
		for(AttackerType type : types) {
			if(count == 1) {
				attackerTypes.put(type, 1.0);
			} else if(i < count - 1) {
				double random = randomDouble(0, remaining);
				remaining -= random;
				attackerTypes.put(type, random);
			} else {
				attackerTypes.put(type, remaining);
			}
			i++;
		}
		return attackerTypes;
	}
	
	public static MultiSingleTargetGame<BBAPayoff> randomBBASingleTargetMultiGame(int numTargets, int numTypes, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = getTargetSet(numTargets);
		AdvancedSet<AttackerType> types = getAttackerTypeSet(numTypes);
		Map<AttackerType, Double> attackerTypes = getAttackerTypeProbabilityDistribution(types);
		
		Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGames = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
		
		for(AttackerType type : types) {
			
			SingleTargetGame<BBAPayoff> tg = new SingleTargetGame<BBAPayoff>(targets);
			
			for(Target target : targets) {
				
				BBAPayoff defNeg = randomAmbiguousPayoff(negative);
				BBAPayoff defPos = randomAmbiguousPayoff(positive);
				while(defNeg instanceof AbsentPayoff && !(defPos instanceof AbsentPayoff)
						|| defPos instanceof AbsentPayoff && !(defNeg instanceof AbsentPayoff)) {
					defNeg = Randomizer.randomAmbiguousPayoff(negative);
					defPos = Randomizer.randomAmbiguousPayoff(positive);
				}
				BBAPayoff attNeg = randomAmbiguousPayoff(negative);
				BBAPayoff attPos = randomAmbiguousPayoff(positive);
				while(attNeg instanceof AbsentPayoff && !(attPos instanceof AbsentPayoff)
						|| attPos instanceof AbsentPayoff && !(attNeg instanceof AbsentPayoff)) {
					attNeg = Randomizer.randomAmbiguousPayoff(negative);
					attPos = Randomizer.randomAmbiguousPayoff(positive);
				}
				
				tg.setPayoff(
						target, 
						new PlayerPairPayoffPair<BBAPayoff>(
								new PayoffPair<BBAPayoff>(
										defNeg, 
										defPos
								),
								new PayoffPair<BBAPayoff>(
										attNeg, 
										attPos
								)
						)
				);
				
			}
			
			attackerGames.put(type, tg);
		
		}
		
		return new MultiSingleTargetGame<BBAPayoff>(targets, attackerTypes, attackerGames);
	}
	
	public static SingleTargetGame<IntegerPayoff> randomIntegerSingleTargetGame(int numTargets, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = getTargetSet(numTargets);
		
		SingleTargetGame<IntegerPayoff> tg = new SingleTargetGame<IntegerPayoff>(targets);
		
		for(Target target : targets) {
			
			tg.setPayoff(
					target, 
					new PlayerPairPayoffPair<IntegerPayoff>(
							new PayoffPair<IntegerPayoff>(
									randomIntegerPayoff(negative), 
									randomIntegerPayoff(positive)
							),
							new PayoffPair<IntegerPayoff>(
									randomIntegerPayoff(negative), 
									randomIntegerPayoff(positive)
							)
					)
			);
			
		}
		
		return tg;
	}
	
	public static MultiSingleTargetGame<IntegerPayoff> randomIntegerSingleTargetMultiGame(int numTargets, int numTypes, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = getTargetSet(numTargets);
		AdvancedSet<AttackerType> types = getAttackerTypeSet(numTypes);
		Map<AttackerType, Double> attackerTypes = getAttackerTypeProbabilityDistribution(types);
		
		Map<AttackerType, SingleTargetGame<IntegerPayoff>> attackerGames = new HashMap<AttackerType, SingleTargetGame<IntegerPayoff>>();
		
		for(AttackerType type : types) {
			
			SingleTargetGame<IntegerPayoff> tg = new SingleTargetGame<IntegerPayoff>(targets);
			
			for(Target target : targets) {
				
				tg.setPayoff(
						target, 
						new PlayerPairPayoffPair<IntegerPayoff>(
								new PayoffPair<IntegerPayoff>(
										randomIntegerPayoff(negative), 
										randomIntegerPayoff(positive)
								),
								new PayoffPair<IntegerPayoff>(
										randomIntegerPayoff(negative), 
										randomIntegerPayoff(positive)
								)
						)
				);
				
			}
			
			attackerGames.put(type, tg);
		
		}
		
		return new MultiSingleTargetGame<IntegerPayoff>(targets, attackerTypes, attackerGames);
	}
	
	public static MultiSingleTargetGame<IntegerPayoff> randomIntegerSingleTargetMultiGameNew(int numTargets, int numTypes, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = getTargetSet(numTargets);
		AdvancedSet<AttackerType> types = getAttackerTypeSet(numTypes);
		Map<AttackerType, Double> attackerTypes = getAttackerTypeProbabilityDistribution(types);
		
		Map<AttackerType, SingleTargetGame<IntegerPayoff>> attackerGames = new HashMap<AttackerType, SingleTargetGame<IntegerPayoff>>();
		
		for(AttackerType type : types) {
			
			SingleTargetGame<IntegerPayoff> tg = new SingleTargetGame<IntegerPayoff>(targets);
			
			for(Target target : targets) {
				
				tg.setPayoff(
						target, 
						new PlayerPairPayoffPair<IntegerPayoff>(
								new PayoffPair<IntegerPayoff>(
										randomIntegerPayoff(negative), 
										randomIntegerPayoff(positive)
								),
								new PayoffPair<IntegerPayoff>(
										randomIntegerPayoff(negative), 
										randomIntegerPayoff(positive)
								)
						)
				);
				
			}
			
			attackerGames.put(type, tg);
		
		}
		
		return new MultiSingleTargetGame<IntegerPayoff>(targets, attackerTypes, attackerGames);
	}
	
	public static SingleTargetGame<NormalFormPayoff> randomNormalFormSingleTargetGame(int numTargets, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = getTargetSet(numTargets);
		
		SingleTargetGame<NormalFormPayoff> tg = new SingleTargetGame<NormalFormPayoff>(targets);
		
		for(Target target : targets) {
			
			tg.setPayoff(
					target, 
					new PlayerPairPayoffPair<NormalFormPayoff>(
							new PayoffPair<NormalFormPayoff>(
									randomIntegerPayoff(negative), 
									randomIntegerPayoff(positive)
							),
							new PayoffPair<NormalFormPayoff>(
									randomIntegerPayoff(negative), 
									randomIntegerPayoff(positive)
							)
					)
			);
			
		}
		
		return tg;
	}
	
	public static MultiSingleTargetGame<NormalFormPayoff> randomNormalFormSingleTargetMultiGame(int numTargets, int numTypes, NegativeRange negative, PositiveRange positive) throws Exception {
		AdvancedSet<Target> targets = getTargetSet(numTargets);
		AdvancedSet<AttackerType> types = getAttackerTypeSet(numTypes);
		Map<AttackerType, Double> attackerTypes = getAttackerTypeProbabilityDistribution(types);
		
		Map<AttackerType, SingleTargetGame<NormalFormPayoff>> attackerGames = new HashMap<AttackerType, SingleTargetGame<NormalFormPayoff>>();
		
		for(AttackerType type : types) {
			
			SingleTargetGame<NormalFormPayoff> tg = new SingleTargetGame<NormalFormPayoff>(targets);
			
			for(Target target : targets) {
				
				tg.setPayoff(
						target, 
						new PlayerPairPayoffPair<NormalFormPayoff>(
								new PayoffPair<NormalFormPayoff>(
										randomIntegerPayoff(negative), 
										randomIntegerPayoff(positive)
								),
								new PayoffPair<NormalFormPayoff>(
										randomIntegerPayoff(negative), 
										randomIntegerPayoff(positive)
								)
						)
				);
				
			}
			
			attackerGames.put(type, tg);
		
		}
		
		return new MultiSingleTargetGame<NormalFormPayoff>(targets, attackerTypes, attackerGames);
	}
	
	public static PlayerPairPayoffPair<BBAPayoff> randomlyExtendToInterval(PlayerPairPayoffPair<IntegerPayoff> payoff, NegativeRange negative, PositiveRange positive) {
		
		AdvancedSet<Integer> aSet = new AdvancedSet<Integer>();
		aSet.add(Math.abs(negative.getLeft() - payoff.getDefender().getNegative().getInteger()));
		aSet.add(Math.abs(negative.getLeft() - payoff.getAttacker().getNegative().getInteger()));
		aSet.add(Math.abs(positive.getLeft() - payoff.getDefender().getPositive().getInteger()));
		aSet.add(Math.abs(positive.getLeft() - payoff.getAttacker().getPositive().getInteger()));
		int aMax = Utilities.min(aSet);
		
		AdvancedSet<Integer> bSet = new AdvancedSet<Integer>();
		bSet.add(Math.abs(negative.getRight() - payoff.getDefender().getNegative().getInteger()));
		bSet.add(Math.abs(negative.getRight() - payoff.getAttacker().getNegative().getInteger()));
		bSet.add(Math.abs(positive.getRight() - payoff.getDefender().getPositive().getInteger()));
		bSet.add(Math.abs(positive.getRight() - payoff.getAttacker().getPositive().getInteger()));
		int bMax = Utilities.min(bSet);
		
		// Restriction I1: No restriction.
//		int a = Randomizer.randomInteger(1, aMax);
//		int b = Randomizer.randomInteger(1, bMax);
		
		// Restriction I2: a=0, b>0.
		int a = 0;
		int b = Randomizer.randomInteger(1, bMax);
		
//		// Restriction I3: b>a.
//		int a = 0;
//		int b = 1;
//		if(bMax > 1) {
//			a = Randomizer.randomInteger(1, aMax);
//			b = Randomizer.randomInteger(1, bMax);
//			while(!(b > a)) {
//				a = Randomizer.randomInteger(1, aMax);
//				b = Randomizer.randomInteger(1, bMax);
//			}
//		}
		
		// Restriction I4: a>b.
//		int a = 1;
//		int b = 0;
//		if(aMax > 1) {
//			a = Randomizer.randomInteger(1, aMax);
//			b = Randomizer.randomInteger(1, bMax);
//			while(!(a > b)) {
//				a = Randomizer.randomInteger(1, aMax);
//				b = Randomizer.randomInteger(1, bMax);
//			}
//		}
		
		// Extend defender and attacker payoffs to intervals.
		return new PlayerPairPayoffPair<BBAPayoff>(
				new PayoffPair<BBAPayoff>(
						new IntervalPayoff(negative, new Interval(payoff.getDefender().getNegative().getInteger()-a, payoff.getDefender().getNegative().getInteger()+b)), 
						new IntervalPayoff(positive, new Interval(payoff.getDefender().getPositive().getInteger()-a, payoff.getDefender().getPositive().getInteger()+b))
				), 
				new PayoffPair<BBAPayoff>(
						new IntervalPayoff(negative, new Interval(payoff.getAttacker().getNegative().getInteger()-a, payoff.getAttacker().getNegative().getInteger()+b)), 
						new IntervalPayoff(positive, new Interval(payoff.getAttacker().getPositive().getInteger()-a, payoff.getAttacker().getPositive().getInteger()+b))
				)
		);
		
		// Extend only defender payoffs to intervals.
//		return new PlayerPairPayoffPair<BBAPayoff>(
//				new PayoffPair<BBAPayoff>(
//						new IntervalPayoff(negative, new Interval(payoff.getDefender().getNegative().getInteger()-a, payoff.getDefender().getNegative().getInteger()+b)), 
//						new IntervalPayoff(positive, new Interval(payoff.getDefender().getPositive().getInteger()-a, payoff.getDefender().getPositive().getInteger()+b))
//				), 
//				new PayoffPair<BBAPayoff>(
//						new PointValuePayoff(negative, payoff.getAttacker().getNegative().getInteger()), 
//						new PointValuePayoff(positive, payoff.getAttacker().getPositive().getInteger())
//				)
//		);
		
	}
	
	public static PlayerPairPayoffPair<BBAPayoff> randomlyExtendToFixedSizeInterval(PlayerPairPayoffPair<IntegerPayoff> payoff, NegativeRange negative, PositiveRange positive, int a, int b) {
		
		// Extend defender and attacker payoffs to intervals.
		return new PlayerPairPayoffPair<BBAPayoff>(
				new PayoffPair<BBAPayoff>(
						new IntervalPayoff(negative, new Interval(payoff.getDefender().getNegative().getInteger()-a, payoff.getDefender().getNegative().getInteger()+b)), 
						new IntervalPayoff(positive, new Interval(payoff.getDefender().getPositive().getInteger()-a, payoff.getDefender().getPositive().getInteger()+b))
				), 
				new PayoffPair<BBAPayoff>(
						new IntervalPayoff(negative, new Interval(payoff.getAttacker().getNegative().getInteger()-a, payoff.getAttacker().getNegative().getInteger()+b)), 
						new IntervalPayoff(positive, new Interval(payoff.getAttacker().getPositive().getInteger()-a, payoff.getAttacker().getPositive().getInteger()+b))
				)
		);
		
		// Extend only defender payoffs to intervals.
//		return new PlayerPairPayoffPair<BBAPayoff>(
//				new PayoffPair<BBAPayoff>(
//						new IntervalPayoff(negative, new Interval(payoff.getDefender().getNegative().getInteger()-a, payoff.getDefender().getNegative().getInteger()+b)), 
//						new IntervalPayoff(positive, new Interval(payoff.getDefender().getPositive().getInteger()-a, payoff.getDefender().getPositive().getInteger()+b))
//				), 
//				new PayoffPair<BBAPayoff>(
//						new PointValuePayoff(negative, payoff.getAttacker().getNegative().getInteger()), 
//						new PointValuePayoff(positive, payoff.getAttacker().getPositive().getInteger())
//				)
//		);
		
	}

	public static PlayerPairPayoffPair<BBAPayoff> randomlyExtendToPointValueLottery(PlayerPairPayoffPair<IntegerPayoff> payoff, NegativeRange negative, PositiveRange positive) throws Exception {
		
		// Restriction L1: 0<p<1.
//		double p = Randomizer.randomDouble(0.0, 1.0);
//		while(p <= 0.0 || p >= 1.0) {
//			p = Randomizer.randomDouble(0.0, 1.0);
//		}
		
		// Restriction L2: 0<p<0.5.
		double p = Randomizer.randomDouble(0.0, 0.5);
		while(p <= 0.0 || p >= 0.5) {
			p = Randomizer.randomDouble(0.0, 0.5);
		}
		
		// Restriction L3: 0.5<p<1.
//		double p = Randomizer.randomDouble(0.5, 1.0);
//		while(p <= 0.5 || p >= 1.0) {
//			p = Randomizer.randomDouble(0.5, 1.0);
//		}
		
		BBA<Integer> defNegBBA = new BBA<Integer>(negative.getAdvancedSet());
		defNegBBA.addMass(new AdvancedSet<Integer>(payoff.getDefender().getNegative().getInteger()), p);
		defNegBBA.addMass(negative.getAdvancedSet(), 1-p);
		
		BBA<Integer> defPosBBA = new BBA<Integer>(positive.getAdvancedSet());
		defPosBBA.addMass(new AdvancedSet<Integer>(payoff.getDefender().getPositive().getInteger()), p);
		defPosBBA.addMass(positive.getAdvancedSet(), 1-p);
		
		BBA<Integer> attNegBBA = new BBA<Integer>(negative.getAdvancedSet());
		attNegBBA.addMass(new AdvancedSet<Integer>(payoff.getAttacker().getNegative().getInteger()), p);
		attNegBBA.addMass(negative.getAdvancedSet(), 1-p);
		
		BBA<Integer> attPosBBA = new BBA<Integer>(positive.getAdvancedSet());
		attPosBBA.addMass(new AdvancedSet<Integer>(payoff.getAttacker().getPositive().getInteger()), p);
		attPosBBA.addMass(positive.getAdvancedSet(), 1-p);
		
		// Extend defender and attacker payoffs to point-value lotteries.
		return new PlayerPairPayoffPair<BBAPayoff>(
				new PayoffPair<BBAPayoff>(
						new AmbiguityLotteryPayoff(negative, defNegBBA), 
						new AmbiguityLotteryPayoff(positive, defPosBBA)
				),
				new PayoffPair<BBAPayoff>(
						new AmbiguityLotteryPayoff(negative, attNegBBA), 
						new AmbiguityLotteryPayoff(positive, attPosBBA)
				)
		);
		
		// Extend only defender payoffs to point-value lotteries.
//		return new PlayerPairPayoffPair<BBAPayoff>(
//				new PayoffPair<BBAPayoff>(
//						new AmbiguityLotteryPayoff(negative, defNegBBA), 
//						new AmbiguityLotteryPayoff(positive, defPosBBA)
//				),
//				new PayoffPair<BBAPayoff>(
//						new PointValuePayoff(negative, payoff.getAttacker().getNegative().getInteger()), 
//						new PointValuePayoff(positive, payoff.getAttacker().getPositive().getInteger())
//				)
//		);
		
	}

	public static PlayerPairPayoffPair<BBAPayoff> randomlyExtendToIntervalLottery(PlayerPairPayoffPair<IntegerPayoff> payoff, NegativeRange negative, PositiveRange positive) throws Exception {
		
		AdvancedSet<Integer> aSet = new AdvancedSet<Integer>();
		aSet.add(Math.abs(negative.getLeft() - payoff.getDefender().getNegative().getInteger()));
		aSet.add(Math.abs(negative.getLeft() - payoff.getAttacker().getNegative().getInteger()));
		aSet.add(Math.abs(positive.getLeft() - payoff.getDefender().getPositive().getInteger()));
		aSet.add(Math.abs(positive.getLeft() - payoff.getAttacker().getPositive().getInteger()));
		int aMax = Utilities.min(aSet);
		
		AdvancedSet<Integer> bSet = new AdvancedSet<Integer>();
		bSet.add(Math.abs(negative.getRight() - payoff.getDefender().getNegative().getInteger()));
		bSet.add(Math.abs(negative.getRight() - payoff.getAttacker().getNegative().getInteger()));
		bSet.add(Math.abs(positive.getRight() - payoff.getDefender().getPositive().getInteger()));
		bSet.add(Math.abs(positive.getRight() - payoff.getAttacker().getPositive().getInteger()));
		int bMax = Utilities.min(bSet);
		
		// Restriction I1: No restriction.
//		int a = Randomizer.randomInteger(1, aMax);
//		int b = Randomizer.randomInteger(1, bMax);
		
		// Restriction I2: a=0, b>0.
		int a = 0;
		int b = Randomizer.randomInteger(1, bMax);
		
//		// Restriction I3: b>a.
//		int a = 0;
//		int b = 1;
//		if(bMax > 1) {
//			a = Randomizer.randomInteger(1, aMax);
//			b = Randomizer.randomInteger(1, bMax);
//			while(!(b > a)) {
//				a = Randomizer.randomInteger(1, aMax);
//				b = Randomizer.randomInteger(1, bMax);
//			}
//		}
		
		// Restriction I4: a>b.
//		int a = 1;
//		int b = 0;
//		if(aMax > 1) {
//			a = Randomizer.randomInteger(1, aMax);
//			b = Randomizer.randomInteger(1, bMax);
//			while(!(a > b)) {
//				a = Randomizer.randomInteger(1, aMax);
//				b = Randomizer.randomInteger(1, bMax);
//			}
//		}
		
		// Restriction L1: 0<p<1.
//		double p = Randomizer.randomDouble(0.0, 1.0);
//		while(p <= 0.0 || p >= 1.0) {
//			p = Randomizer.randomDouble(0.0, 1.0);
//		}
		
		// Restriction L2: 0<p<0.5.
		double p = Randomizer.randomDouble(0.0, 0.5);
		while(p <= 0.0 || p >= 0.5) {
			p = Randomizer.randomDouble(0.0, 0.5);
		}
		
		// Restriction L3: 0.5<p<1.
//		double p = Randomizer.randomDouble(0.5, 1.0);
//		while(p <= 0.5 || p >= 1.0) {
//			p = Randomizer.randomDouble(0.5, 1.0);
//		}
		
		BBA<Integer> defNegBBA = new BBA<Integer>(negative.getAdvancedSet());
		defNegBBA.addMass(
				new Interval(
						payoff.getDefender().getNegative().getInteger()-a,
						payoff.getDefender().getNegative().getInteger()+b
				).getAdvancedSet(), 
				p);
		defNegBBA.addMass(negative.getAdvancedSet(), 1-p);
		
		BBA<Integer> defPosBBA = new BBA<Integer>(positive.getAdvancedSet());
		defPosBBA.addMass(
				new Interval(
						payoff.getDefender().getPositive().getInteger()-a,
						payoff.getDefender().getPositive().getInteger()+b
				).getAdvancedSet(), 
				p);
		defPosBBA.addMass(positive.getAdvancedSet(), 1-p);
		
		BBA<Integer> attNegBBA = new BBA<Integer>(negative.getAdvancedSet());
		attNegBBA.addMass(
				new Interval(
						payoff.getAttacker().getNegative().getInteger()-a,
						payoff.getAttacker().getNegative().getInteger()+b
				).getAdvancedSet(), 
				p);
		attNegBBA.addMass(negative.getAdvancedSet(), 1-p);
		
		BBA<Integer> attPosBBA = new BBA<Integer>(positive.getAdvancedSet());
		attPosBBA.addMass(
				new Interval(
						payoff.getAttacker().getPositive().getInteger()-a,
						payoff.getAttacker().getPositive().getInteger()+b
				).getAdvancedSet(), 
				p);
		attPosBBA.addMass(positive.getAdvancedSet(), 1-p);
		
		// Extend defender and attacker payoffs to interval lotteries.
		return new PlayerPairPayoffPair<BBAPayoff>(
				new PayoffPair<BBAPayoff>(
						new AmbiguityLotteryPayoff(negative, defNegBBA), 
						new AmbiguityLotteryPayoff(positive, defPosBBA)
				),
				new PayoffPair<BBAPayoff>(
						new AmbiguityLotteryPayoff(negative, attNegBBA), 
						new AmbiguityLotteryPayoff(positive, attPosBBA)
				)
		);
		
		// Extend only defender payoffs to interval lotteries.
//		return new PlayerPairPayoffPair<BBAPayoff>(
//				new PayoffPair<BBAPayoff>(
//						new AmbiguityLotteryPayoff(negative, defNegBBA), 
//						new AmbiguityLotteryPayoff(positive, defPosBBA)
//				),
//				new PayoffPair<BBAPayoff>(
//						new PointValuePayoff(negative, payoff.getAttacker().getNegative().getInteger()), 
//						new PointValuePayoff(positive, payoff.getAttacker().getPositive().getInteger())
//				)
//		);
		
	}
	
	private static AdvancedSet<Integer> randomlyExtendIntegerSet(int element, AdvancedSet<Integer> set) {
		AdvancedSet<Integer> subset = new AdvancedSet<Integer>();
		subset.add(element);
		int remainingElementsToAdd = randomInteger(1, set.size() - subset.size() - 1);
		while(remainingElementsToAdd > 0) {
			AdvancedSet<Integer> remaining = set.setminus(subset);
			Integer[] remainingElements = new Integer[remaining.size()];
			int index = 0;
			for(Integer integer : remaining) {
				remainingElements[index] = integer;
				index++;
			}
			int randomIndex = randomInteger(0, remainingElements.length-1);
			subset.add(remainingElements[randomIndex]);
			remainingElementsToAdd--;
		}
		return subset;
	}
	
	private static double average(AdvancedSet<Integer> set) {
		int sum = 0;
		for(int element : set) {
			sum += element;
		}
		return (double)sum / (double)set.size();
	}
	
	public static PlayerPairPayoffPair<BBAPayoff> randomlyExtendToMixedLottery(PlayerPairPayoffPair<IntegerPayoff> payoff, NegativeRange negative, PositiveRange positive) throws Exception {
		
		// Restriction I1: No restriction.
//		double p = Randomizer.randomDouble(0.0, 1.0);
//		while(p <= 0.0 || p >= 1.0) {
//			p = Randomizer.randomDouble(0.0, 1.0);
//		}
		
		// Restriction I2: p < 0.5.
		double p = Randomizer.randomDouble(0.0, 0.5);
		while(p <= 0.0 || p >= 0.5) {
			p = Randomizer.randomDouble(0.0, 0.5);
		}
		
		BBA<Integer> defNegBBA = new BBA<Integer>(negative.getAdvancedSet());
		int defNegOriginalValue = payoff.getDefender().getNegative().getInteger();
		AdvancedSet<Integer> defNegFocalSet = randomlyExtendIntegerSet(defNegOriginalValue, negative.getAdvancedSet());
		while(!(defNegOriginalValue < average(defNegFocalSet))) {
			defNegFocalSet = randomlyExtendIntegerSet(defNegOriginalValue, negative.getAdvancedSet());
		}
		defNegBBA.addMass(defNegFocalSet, p);
		defNegBBA.addMass(negative.getAdvancedSet(), 1-p);
		
		BBA<Integer> defPosBBA = new BBA<Integer>(positive.getAdvancedSet());
		int defPosOriginalValue = payoff.getDefender().getPositive().getInteger();
		AdvancedSet<Integer> defPosFocalSet = randomlyExtendIntegerSet(defPosOriginalValue, positive.getAdvancedSet());
		while(!(defPosOriginalValue < average(defPosFocalSet))) {
			defPosFocalSet = randomlyExtendIntegerSet(defPosOriginalValue, positive.getAdvancedSet());
		}
		defPosBBA.addMass(defPosFocalSet, p);
		defPosBBA.addMass(positive.getAdvancedSet(), 1-p);
		
		BBA<Integer> attNegBBA = new BBA<Integer>(negative.getAdvancedSet());
		int attNegOriginalValue = payoff.getAttacker().getNegative().getInteger();
		AdvancedSet<Integer> attNegFocalSet = randomlyExtendIntegerSet(attNegOriginalValue, negative.getAdvancedSet());
		while(!(attNegOriginalValue < average(attNegFocalSet))) {
			attNegFocalSet = randomlyExtendIntegerSet(attNegOriginalValue, negative.getAdvancedSet());
		}
		attNegBBA.addMass(attNegFocalSet, p);
		attNegBBA.addMass(negative.getAdvancedSet(), 1-p);
		
		BBA<Integer> attPosBBA = new BBA<Integer>(positive.getAdvancedSet());
		int attPosOriginalValue = payoff.getAttacker().getPositive().getInteger();
		AdvancedSet<Integer> attPosFocalSet = randomlyExtendIntegerSet(attPosOriginalValue, positive.getAdvancedSet());
		while(!(attPosOriginalValue < average(attPosFocalSet))) {
			attPosFocalSet = randomlyExtendIntegerSet(attPosOriginalValue, positive.getAdvancedSet());
		}
		attPosBBA.addMass(attPosFocalSet, p);
		attPosBBA.addMass(positive.getAdvancedSet(), 1-p);
		
		return new PlayerPairPayoffPair<BBAPayoff>(
				new PayoffPair<BBAPayoff>(
						new AmbiguityLotteryPayoff(negative, defNegBBA), 
						new AmbiguityLotteryPayoff(positive, defPosBBA)
				),
				new PayoffPair<BBAPayoff>(
						new AmbiguityLotteryPayoff(negative, attNegBBA), 
						new AmbiguityLotteryPayoff(positive, attPosBBA)
				)
		);
		
	}
	
}
