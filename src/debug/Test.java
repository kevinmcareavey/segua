package debug;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import segua.AttackerProbabilities;
import segua.AttackerType;
import segua.MixedStrategy;
import segua.Target;
import segua.decision_rules.multi_set_profile_decision_rules.SetDefault;
import segua.decision_rules.multi_single_profile_decision_rules.GammaMaximin;
import segua.decision_rules.multi_single_profile_decision_rules.HurwiczCriterion;
import segua.decision_rules.multi_single_profile_decision_rules.Maximax;
import segua.decision_rules.multi_single_profile_decision_rules.OWABasedModel;
import segua.decision_rules.multi_single_profile_decision_rules.SingleDefault;
import segua.decision_rules.multi_single_profile_decision_rules.TransferableBeliefModel;
import segua.multi_security_games.profile_games.MultiSetProfileGame;
import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.multi_security_games.target_games.MultiSetTargetGame;
import segua.multi_security_games.target_games.MultiSingleTargetGame;
import segua.payoffs.PayoffPair;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.payoffs.payoff_single.bba_payoffs.AbsentPayoff;
import segua.payoffs.payoff_single.bba_payoffs.AmbiguityLotteryPayoff;
import segua.payoffs.payoff_single.bba_payoffs.IntervalPayoff;
import segua.payoffs.payoff_single.bba_payoffs.PointValuePayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.IntegerPayoff;
import segua.player_pairs.PlayerPairPayoffPair;
import segua.security_games.profile_games.SingleProfileGame;
import segua.security_games.target_games.SetTargetGame;
import segua.security_games.target_games.SingleTargetGame;
import segua.solvers.DOBSS;
import data_structures.AdvancedSet;
import data_structures.BBA;
import data_structures.Interval;
import data_structures.ranges.NegativeRange;
import data_structures.ranges.PositiveRange;
import evaluation.Experiments;
import evaluation.Randomizer;

public class Test {
	
	public static void example() {
		
		try {
			
			NegativeRange negative = new NegativeRange(-9, 0);
			PositiveRange positive = new PositiveRange(0, 9);
			
			Target sa = new Target("SA");
			Target pr = new Target("PR");
			Target sl = new Target("SL");
			Target vl = new Target("VL");
			Target h = new Target("H");
			AdvancedSet<Target> targets = new AdvancedSet<Target>(sa, pr, sl, vl, h);
			
			SetTargetGame<BBAPayoff> tg = new SetTargetGame<BBAPayoff>(targets);
			
			tg.addPayoff(
					new AdvancedSet<Target>(sa),
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new IntervalPayoff(negative, new Interval(-7, -3)), 
									new IntervalPayoff(positive, new Interval(2, 6))
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -7), 
									new PointValuePayoff(positive, 5)
							)
					)
			);
			
			BBA<Integer> negativeBBA = new BBA<Integer>(negative.getAdvancedSet());
			negativeBBA.addMass(new Interval(-8, -7).getAdvancedSet(), 0.8);
			negativeBBA.addMass(new Interval(-9, 0).getAdvancedSet(), 0.2);
			
			BBA<Integer> positiveBBA = new BBA<Integer>(positive.getAdvancedSet());
			positiveBBA.addMass(new Interval(5, 6).getAdvancedSet(), 0.8);
			positiveBBA.addMass(new Interval(0, 9).getAdvancedSet(), 0.2);
			
			tg.addPayoff(
					new AdvancedSet<Target>(pr), 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new AmbiguityLotteryPayoff(negative, negativeBBA), 
									new AmbiguityLotteryPayoff(positive, positiveBBA)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -5), 
									new PointValuePayoff(positive, 3)
							)
					)
			);
			
			tg.addPayoff(
					new AdvancedSet<Target>(sl), 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new AbsentPayoff(negative), 
									new AbsentPayoff(positive)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -4), 
									new PointValuePayoff(positive, 2)
							)
					)
			);
			
			tg.addPayoff(
					new AdvancedSet<Target>(vl, h), 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -8), 
									new PointValuePayoff(positive, 7)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -9), 
									new PointValuePayoff(positive, 7)
							)
					)
			);
			
			AttackerType a0 = new AttackerType("a0");
			AttackerProbabilities attackerProbabilities = new AttackerProbabilities();
			attackerProbabilities.put(a0, 1.0);
			Map<AttackerType, SetTargetGame<BBAPayoff>> attackerGames = new HashMap<AttackerType, SetTargetGame<BBAPayoff>>();
			attackerGames.put(a0, tg);
			MultiSetTargetGame<BBAPayoff> mtg = new MultiSetTargetGame<BBAPayoff>(targets, attackerProbabilities, attackerGames);
			
			System.out.println("target set game:");
			System.out.println(mtg);
			System.out.println();
			
			System.out.println("pure strategy set game:");
			MultiSetProfileGame<BBAPayoff> spsg = mtg.toMultiSetPureStrategyGame();
			System.out.println(spsg);
			System.out.println();
			
			System.out.println("normal form game:");
			MultiSingleProfileGame<NormalFormPayoff> normalForm = new SetDefault(spsg).toNormalForm();
			System.out.println(normalForm);
			System.out.println();
			
			new SetDefault(spsg).printAmbiguityDegrees();
			
			DOBSS dobss = new DOBSS(normalForm);
			dobss.solve();
			System.out.println("defender's mixed strategy: " + dobss.getDefenderMixedStrategy());
			System.out.println("attacker pure strategies: " + dobss.getAttackerPureStrategies());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void singleTargetGame() {
		
		try {
			
			NegativeRange negative = new NegativeRange(-9, 0);
			PositiveRange positive = new PositiveRange(0, 9);
			
			Target sa = new Target("SA");
			Target pr = new Target("PR");
			Target sl = new Target("SL");
			Target vl = new Target("VL");
			Target h = new Target("H");
			AdvancedSet<Target> targets = new AdvancedSet<Target>(sa, pr, sl, vl, h);
			
			SingleTargetGame<BBAPayoff> tg = new SingleTargetGame<BBAPayoff>(targets);
			
			tg.setPayoff(
					sa,
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new IntervalPayoff(negative, new Interval(-7, -3)), 
									new IntervalPayoff(positive, new Interval(2, 6))
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -7), 
									new PointValuePayoff(positive, 5)
							)
					)
			);
			
			BBA<Integer> negativeBBA = new BBA<Integer>(negative.getAdvancedSet());
			negativeBBA.addMass(new Interval(-8, -7).getAdvancedSet(), 0.8);
			negativeBBA.addMass(new Interval(-9, 0).getAdvancedSet(), 0.2);
			
			BBA<Integer> positiveBBA = new BBA<Integer>(positive.getAdvancedSet());
			positiveBBA.addMass(new Interval(5, 6).getAdvancedSet(), 0.8);
			positiveBBA.addMass(new Interval(0, 9).getAdvancedSet(), 0.2);
			
			tg.setPayoff(
					pr, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new AmbiguityLotteryPayoff(negative, negativeBBA), 
									new AmbiguityLotteryPayoff(positive, positiveBBA)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -5), 
									new PointValuePayoff(positive, 3)
							)
					)
			);
			
			tg.setPayoff(
					sl, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new AbsentPayoff(negative), 
									new AbsentPayoff(positive)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -4), 
									new PointValuePayoff(positive, 2)
							)
					)
			);
			
			tg.setPayoff(
					vl, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -8), 
									new PointValuePayoff(positive, 7)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -9), 
									new PointValuePayoff(positive, 7)
							)
					)
			);
			
			tg.setPayoff(
					h, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -8), 
									new PointValuePayoff(positive, 7)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -9), 
									new PointValuePayoff(positive, 7)
							)
					)
			);
			
			AttackerType a0 = new AttackerType("a0");
			AttackerProbabilities attackerProbabilities = new AttackerProbabilities();
			attackerProbabilities.put(a0, 1.0);
			Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGames = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
			attackerGames.put(a0, tg);
			MultiSingleTargetGame<BBAPayoff> mtg = new MultiSingleTargetGame<BBAPayoff>(targets, attackerProbabilities, attackerGames);
			
			System.out.println("target set game:");
			System.out.println(mtg);
			System.out.println();
			
			System.out.println("pure strategy set game:");
			MultiSingleProfileGame<BBAPayoff> spsg = mtg.toMultiSinglePureStrategyGame();
			System.out.println(spsg);
			System.out.println();
			
			System.out.println("normal form game:");
			MultiSingleProfileGame<NormalFormPayoff> segua = new SingleDefault(spsg).toNormalForm();
			System.out.println(segua);
			System.out.println();
			
			DOBSS dobss = new DOBSS(segua);
			dobss.solve();
			System.out.println("defender's mixed strategy: " + dobss.getDefenderMixedStrategy());
			System.out.println("attacker pure strategies: " + dobss.getAttackerPureStrategies());
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void random() {
		
		try {
			SingleTargetGame<BBAPayoff> tg = Randomizer.randomBBASingleTargetGame(2, new NegativeRange(-9, 0), new PositiveRange(0, 9));
			AttackerType a0 = new AttackerType("a0");
			AttackerProbabilities attackerProbabilities = new AttackerProbabilities();
			attackerProbabilities.put(a0, 1.0);
			Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGames = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
			attackerGames.put(a0, tg);
			MultiSingleTargetGame<BBAPayoff> mtg = new MultiSingleTargetGame<BBAPayoff>(tg.getTargets(), attackerProbabilities, attackerGames);
			System.out.println(mtg);
			System.out.println();
			System.out.println(mtg.toMultiSinglePureStrategyGame());
			System.out.println();
			System.out.println(new SingleDefault(mtg.toMultiSinglePureStrategyGame()));
			System.out.println();
			System.out.println(new TransferableBeliefModel(mtg.toMultiSinglePureStrategyGame()));
			System.out.println();
			System.out.println(new HurwiczCriterion(mtg.toMultiSinglePureStrategyGame(), 0.5));
			System.out.println();
			System.out.println(new GammaMaximin(mtg.toMultiSinglePureStrategyGame()));
			System.out.println();
			System.out.println(new Maximax(mtg.toMultiSinglePureStrategyGame()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void testDOBSS() {
		
		try{
			Target t1 = new Target("t1");
			Target t2 = new Target("t2");
			AdvancedSet<Target> targets = new AdvancedSet<Target>(t1, t2);
			
			AttackerType a1 = new AttackerType("a1");
			AttackerType a2 = new AttackerType("a2");
			AttackerProbabilities attackerProbabilities = new AttackerProbabilities();
			attackerProbabilities.put(a1, 0.6128708019969);
			attackerProbabilities.put(a2, 0.387129198003099);
			
			/*
			 * ORIGINAL POINT-VALUE GAME
			 */
			SingleTargetGame<NormalFormPayoff> stg1PointValue = new SingleTargetGame<NormalFormPayoff>(targets);
			stg1PointValue.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-15), new IntegerPayoff(21)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-73), new IntegerPayoff(51))));
			stg1PointValue.setPayoff(t2, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-91), new IntegerPayoff(89)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-79), new IntegerPayoff(38))));
			
			SingleTargetGame<NormalFormPayoff> stg2PointValue = new SingleTargetGame<NormalFormPayoff>(targets);
			stg2PointValue.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-7), new IntegerPayoff(38)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-33), new IntegerPayoff(11))));
			stg2PointValue.setPayoff(t2, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-70), new IntegerPayoff(81)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-30), new IntegerPayoff(22))));
			
			Map<AttackerType, SingleTargetGame<NormalFormPayoff>> attackerGamesPointValue = new HashMap<AttackerType, SingleTargetGame<NormalFormPayoff>>();
			attackerGamesPointValue.put(a1, stg1PointValue);
			attackerGamesPointValue.put(a2, stg2PointValue);
			
			MultiSingleTargetGame<NormalFormPayoff> pointValue = new MultiSingleTargetGame<NormalFormPayoff>(
					targets,
					attackerProbabilities,
					attackerGamesPointValue);
			
			System.out.println("ORIGINAL POINT-VALUE GAME");
			System.out.println();
			System.out.println(pointValue);
			System.out.println();
			
			DOBSS dobssPointValue = new DOBSS(pointValue.toMultiSinglePureStrategyGame());
			dobssPointValue.solve();
			
			System.out.println("defender's mixed strategy: " + dobssPointValue.getDefenderMixedStrategy());
			System.out.println("attacker strategies (DOBSS): " + dobssPointValue.getAttackerPureStrategies());
//			System.out.println("attacker strategies (ours): " + Experiments.getAttackerStrategiesPureStrategy(pointValue, dobssPointValue.getDefenderMixedStrategy()));
			System.out.println("defender's maximum EU (DOBSS): " + dobssPointValue.getDefenderMaxEU());
//			System.out.println("defender's maximum EU (ours with DOBSS attacker pure strategies): " + Experiments.expectedUtilityPureStrategy(pointValue, dobssPointValue.getDefenderMixedStrategy(), dobssPointValue.getAttackerPureStrategies()));
//			System.out.println("defender's maximum EU (ours with our attacker strategies): " + Experiments.expectedUtilityPureStrategy(pointValue, dobssPointValue.getDefenderMixedStrategy(), Experiments.getAttackerStrategiesPureStrategy(pointValue, dobssPointValue.getDefenderMixedStrategy())));
			System.out.println();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void milindExample() {
		
		try{
			Target t1 = new Target("t1");
			Target t2 = new Target("t2");
			Target t3 = new Target("t3");
			Target t4 = new Target("t4");
			AdvancedSet<Target> targets = new AdvancedSet<Target>(t1, t2, t3, t4);
			
			AttackerType a1 = new AttackerType("a1");
			AttackerProbabilities attackerProbabilities = new AttackerProbabilities();
			attackerProbabilities.put(a1, 1.0);
			
			/*
			 * ORIGINAL POINT-VALUE GAME
			 */
			SingleTargetGame<NormalFormPayoff> stg1PointValue = new SingleTargetGame<NormalFormPayoff>(targets);
			stg1PointValue.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(1)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(1))));
			stg1PointValue.setPayoff(t2, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(3)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(2))));
			stg1PointValue.setPayoff(t3, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(7)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(3))));
			stg1PointValue.setPayoff(t4, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(5)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(0), new IntegerPayoff(4))));
			
			Map<AttackerType, SingleTargetGame<NormalFormPayoff>> attackerGamesPointValue = new HashMap<AttackerType, SingleTargetGame<NormalFormPayoff>>();
			attackerGamesPointValue.put(a1, stg1PointValue);
			
			MultiSingleTargetGame<NormalFormPayoff> targetGame = new MultiSingleTargetGame<NormalFormPayoff>(
					targets,
					attackerProbabilities,
					attackerGamesPointValue);
			
			System.out.println("TARGET GAME");
			System.out.println();
			System.out.println(targetGame);
			System.out.println();
			
			System.out.println("PURE STRATEGY GAME");
			System.out.println();
			System.out.println(targetGame.toMultiSinglePureStrategyGame());
			System.out.println();
			
			DOBSS dobssPointValue = new DOBSS(targetGame.toMultiSinglePureStrategyGame());
			dobssPointValue.solve();
			
			System.out.println("defender's mixed strategy: " + dobssPointValue.getDefenderMixedStrategy());
			System.out.println("attacker strategies (DOBSS): " + dobssPointValue.getAttackerPureStrategies());
//			System.out.println("attacker strategies (ours): " + Experiments.getAttackerStrategiesPureStrategy(pureStrategyGame, dobssPointValue.getDefenderMixedStrategy()));
			System.out.println("defender's maximum EU (DOBSS): " + dobssPointValue.getDefenderMaxEU());
//			System.out.println("defender's maximum EU (ours with DOBSS attacker pure strategies): " + Experiments.expectedUtilityPureStrategy(pureStrategyGame, dobssPointValue.getDefenderMixedStrategy(), dobssPointValue.getAttackerPureStrategies()));
//			System.out.println("defender's maximum EU (ours with our attacker strategies): " + Experiments.expectedUtilityPureStrategy(pureStrategyGame, dobssPointValue.getDefenderMixedStrategy(), Experiments.getAttackerStrategiesPureStrategy(pureStrategyGame, dobssPointValue.getDefenderMixedStrategy())));
			System.out.println();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
//	public static void solve() {
//		
//		try {
//			
//			double deviation = 1e-2;
//			double dobssDefenderMaxEU = 0;
//			double ourDefenderMaxEU = 0;
//			
//			int count = 0;
//			
//			while(ourDefenderMaxEU >= dobssDefenderMaxEU - deviation && ourDefenderMaxEU <= dobssDefenderMaxEU + deviation) {
//				count++;
//				System.out.println(count);
//				
//				MultiSecurityGameOld<SingleTargetGame<NormalFormPayoff>> targetGame = Randomizer.randomNormalFormSingleTargetMultiGame(9, 9, new NegativeRange(-100, 0), new PositiveRange(0, 100));
//				
//				System.out.println("TARGET GAME");
//				System.out.println();
//				System.out.println(targetGame);
//				System.out.println();
//				
//				Map<AttackerType, SinglePureStrategyGame<NormalFormPayoff>> attackerGamesNew = new HashMap<AttackerType, SinglePureStrategyGame<NormalFormPayoff>>();
//				for(Entry<AttackerType, SingleTargetGame<NormalFormPayoff>> entry : targetGame.getSecurityGames().entrySet()) {
//					SinglePureStrategyGame<NormalFormPayoff> stg = entry.getValue().getPureStrategyGame();
//					attackerGamesNew.put(entry.getKey(), stg);
//				}
//				
//				MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>> pureStrategyProfileGame = new MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>>(
//						targetGame.getTargets(),
//						targetGame.getAttackerProbabilities(),
//						attackerGamesNew);
//				
//				System.out.println("PURE STRATEGY PROFILE GAME");
//				System.out.println();
//				System.out.println(pureStrategyProfileGame);
//				System.out.println();
//				
//				OldDOBSS dobss = new OldDOBSS(pureStrategyProfileGame);
//				dobss.solve();
//				
//				System.out.println("RESULTS");
//				System.out.println();
//				MixedStrategy dobssDefenderMixedStrategy = dobss.getDefenderMixedStrategy();
//				System.out.println("defender's mixed strategy: " + dobssDefenderMixedStrategy);
//				Map<AttackerType, Target> dobssAttackerPureStrategies = dobss.getAttackerPureStrategies();
//				System.out.println("attacker pure strategies (DOBSS): " + dobssAttackerPureStrategies);
//				Map<AttackerType, Target> ourAttackerPureStrategies = Experiments.getAttackerStrategiesPureStrategy(pureStrategyProfileGame, dobssDefenderMixedStrategy);
//				System.out.println("attacker pure strategies (ours): " + ourAttackerPureStrategies);
//				dobssDefenderMaxEU = dobss.getDefenderMaxEU();
//				System.out.println("defender's maximum EU (DOBSS): " + dobssDefenderMaxEU);
//				System.out.println("defender's maximum EU (ours with DOBSS attacker pure strategies): " + Experiments.expectedUtilityPureStrategy(pureStrategyProfileGame, dobssDefenderMixedStrategy, dobssAttackerPureStrategies));
//				ourDefenderMaxEU = Experiments.expectedUtilityPureStrategy(pureStrategyProfileGame, dobssDefenderMixedStrategy, ourAttackerPureStrategies);
//				System.out.println("defender's maximum EU (ours with our attacker pure strategies): " + ourDefenderMaxEU);
//				System.out.println();
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
	
//	public static void preferenceDegree() {
//		
//		try{
//			Target t0 = new Target("t0");
//			Target t1 = new Target("t1");
//			AdvancedSet<Target> targets = new AdvancedSet<Target>(t0, t1);
//			
//			AttackerType a0 = new AttackerType("a0");
//			AttackerType a1 = new AttackerType("a1");
//			AttackerType a2 = new AttackerType("a2");
//			AttackerType a3 = new AttackerType("a3");
//			AttackerProbabilities attackerProbabilities = new HashAttackerProbabilities();
//			attackerProbabilities.put(a0, 0.058461686840538395);
//			attackerProbabilities.put(a1, 0.0679531805147269);
//			attackerProbabilities.put(a2, 0.3082247326671632);
//			attackerProbabilities.put(a3, 0.5653603999775715);
//			
//			/*
//			 * ORIGINAL POINT-VALUE GAME
//			 */
//			SingleTargetGame<NormalFormPayoff> stg0PointValue = new SingleTargetGame<NormalFormPayoff>(targets);
//			stg0PointValue.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-97), new IntegerPayoff(58)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-15), new IntegerPayoff(35))));
//			stg0PointValue.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-52), new IntegerPayoff(65)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-86), new IntegerPayoff(61))));
//			
//			SingleTargetGame<NormalFormPayoff> stg1PointValue = new SingleTargetGame<NormalFormPayoff>(targets);
//			stg1PointValue.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-6), new IntegerPayoff(54)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-88), new IntegerPayoff(52))));
//			stg1PointValue.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-85), new IntegerPayoff(33)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-39), new IntegerPayoff(38))));
//			
//			SingleTargetGame<NormalFormPayoff> stg2PointValue = new SingleTargetGame<NormalFormPayoff>(targets);
//			stg2PointValue.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-22), new IntegerPayoff(26)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-12), new IntegerPayoff(1))));
//			stg2PointValue.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-98), new IntegerPayoff(70)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-39), new IntegerPayoff(80))));
//			
//			SingleTargetGame<NormalFormPayoff> stg3PointValue = new SingleTargetGame<NormalFormPayoff>(targets);
//			stg3PointValue.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-58), new IntegerPayoff(34)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-23), new IntegerPayoff(34))));
//			stg3PointValue.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-4), new IntegerPayoff(25)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-59), new IntegerPayoff(29))));
//			
//			Map<AttackerType, SingleTargetGame<NormalFormPayoff>> attackerGamesPointValue = new HashMap<AttackerType, SingleTargetGame<NormalFormPayoff>>();
//			attackerGamesPointValue.put(a0, stg0PointValue);
//			attackerGamesPointValue.put(a1, stg1PointValue);
//			attackerGamesPointValue.put(a2, stg2PointValue);
//			attackerGamesPointValue.put(a3, stg3PointValue);
//			
//			Map<AttackerType, SinglePureStrategyGame<NormalFormPayoff>> attackerGamesNewPointValue = new HashMap<AttackerType, SinglePureStrategyGame<NormalFormPayoff>>();
//			for(Entry<AttackerType, SingleTargetGame<NormalFormPayoff>> entry : attackerGamesPointValue.entrySet()) {
//				SinglePureStrategyGame<NormalFormPayoff> spsg = entry.getValue().getPureStrategyGame();
//				attackerGamesNewPointValue.put(entry.getKey(), spsg);
//			}
//			
//			MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>> pointValue = new MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>>(
//					targets,
//					attackerProbabilities,
//					attackerGamesNewPointValue);
//			
//			System.out.println("POINT-VALUE GAME");
//			System.out.println();
//			System.out.println(pointValue);
//			System.out.println();
//			
//			NegativeRange negative = new NegativeRange(-100, 0);
//			PositiveRange positive = new PositiveRange(0, 100);
//			
//			SingleTargetGame<BBAPayoff> stg0Interval = new SingleTargetGame<BBAPayoff>(targets);
//			stg0Interval.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-98, -86)), new IntervalPayoff(positive, new Interval(57, 69))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-16, -4)), new IntervalPayoff(positive, new Interval(34, 46)))));
//			stg0Interval.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-54, -18)), new IntervalPayoff(positive, new Interval(63, 99))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-88, -52)), new IntervalPayoff(positive, new Interval(59, 95)))));
//			
//			SingleTargetGame<BBAPayoff> stg1Interval = new SingleTargetGame<BBAPayoff>(targets);
//			stg1Interval.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-8, 0)), new IntervalPayoff(positive, new Interval(52, 60))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-90, -82)), new IntervalPayoff(positive, new Interval(50, 58)))));
//			stg1Interval.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-90, -69)), new IntervalPayoff(positive, new Interval(28, 49))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-44, -23)), new IntervalPayoff(positive, new Interval(33, 54)))));
//			
//			SingleTargetGame<BBAPayoff> stg2Interval = new SingleTargetGame<BBAPayoff>(targets);
//			stg2Interval.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-23, -13)), new IntervalPayoff(positive, new Interval(25, 35))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-13, -3)), new IntervalPayoff(positive, new Interval(0, 10)))));
//			stg2Interval.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-99, -80)), new IntervalPayoff(positive, new Interval(69, 88))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-40, -21)), new IntervalPayoff(positive, new Interval(79, 98)))));
//			
//			SingleTargetGame<BBAPayoff> stg3Interval = new SingleTargetGame<BBAPayoff>(targets);
//			stg3Interval.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-59, -47)), new IntervalPayoff(positive, new Interval(33, 45))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-24, -12)), new IntervalPayoff(positive, new Interval(33, 45)))));
//			stg3Interval.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-5, -2)), new IntervalPayoff(positive, new Interval(24, 27))), new PayoffPair<BBAPayoff>(new IntervalPayoff(negative, new Interval(-60, -57)), new IntervalPayoff(positive, new Interval(28, 31)))));
//			
//			Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesInterval = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
//			attackerGamesInterval.put(a0, stg0Interval);
//			attackerGamesInterval.put(a1, stg1Interval);
//			attackerGamesInterval.put(a2, stg2Interval);
//			attackerGamesInterval.put(a3, stg3Interval);
//			
//			Map<AttackerType, SinglePureStrategyGame<BBAPayoff>> attackerGamesNewInterval = new HashMap<AttackerType, SinglePureStrategyGame<BBAPayoff>>();
//			for(Entry<AttackerType, SingleTargetGame<BBAPayoff>> entry : attackerGamesInterval.entrySet()) {
//				SinglePureStrategyGame<BBAPayoff> spsg = entry.getValue().getPureStrategyGame();
//				attackerGamesNewInterval.put(entry.getKey(), spsg);
//			}
//			
//			MultiSecurityGameOld<SinglePureStrategyGame<BBAPayoff>> interval = new MultiSecurityGameOld<SinglePureStrategyGame<BBAPayoff>>(
//					targets,
//					attackerProbabilities,
//					attackerGamesNewInterval);
//			
//			System.out.println("INTERVAL GAME");
//			System.out.println();
//			System.out.println(interval);
//			System.out.println();
//			
//			Map<AttackerType, SinglePureStrategyGame<NormalFormPayoff>> seguaIntervalGames = new HashMap<AttackerType, SinglePureStrategyGame<NormalFormPayoff>>();
//			for(Entry<AttackerType, SinglePureStrategyGame<BBAPayoff>> entry : interval.getSecurityGames().entrySet()) {
//				SinglePureStrategyGame<NormalFormPayoff> spsg = new SingleDefault(entry.getValue()).toNormalForm();
//				seguaIntervalGames.put(entry.getKey(), spsg);
//			}
//			
//			MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>> seguaInterval = new MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>>(
//					targets,
//					attackerProbabilities,
//					seguaIntervalGames);
//			
//			Map<AttackerType, SinglePureStrategyGame<NormalFormPayoff>> preferenceDegreeIntervalGames = new HashMap<AttackerType, SinglePureStrategyGame<NormalFormPayoff>>();
//			for(Entry<AttackerType, SinglePureStrategyGame<BBAPayoff>> entry : attackerGamesNewInterval.entrySet()) {
//				SinglePureStrategyGame<NormalFormPayoff> spsg = new PreferenceDegree(entry.getValue()).toNormalForm();
//				preferenceDegreeIntervalGames.put(entry.getKey(), spsg);
//			}
//			
//			MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>> preferenceDegreeInterval = new MultiSecurityGameOld<SinglePureStrategyGame<NormalFormPayoff>>(
//					targets,
//					attackerProbabilities,
//					preferenceDegreeIntervalGames);
//			
//			OldDOBSS dobssPointValue = new OldDOBSS(pointValue);
//			dobssPointValue.solve();
//			MixedStrategy dmsPointValue = dobssPointValue.getDefenderMixedStrategy();
//			Map<AttackerType, Target> apsPointValue = Experiments.getAttackerStrategiesPureStrategy(pointValue, dmsPointValue);
//			double euPointValue = Experiments.expectedUtilityPureStrategy(pointValue, dmsPointValue, apsPointValue);
//			
//			System.out.println("POINT-VALUE RESULTS");
//			System.out.println();
//			System.out.println("defender's mixed strategy: " + dmsPointValue);
//			System.out.println("attacker strategies (DOBSS): " + apsPointValue);
//			System.out.println("defender's maximum EU (DOBSS): " + euPointValue);
//			System.out.println();
//			
//			OldDOBSS dobssSeguaInterval = new OldDOBSS(seguaInterval);
//			dobssSeguaInterval.solve();
//			MixedStrategy dmsSeguaInterval = dobssSeguaInterval.getDefenderMixedStrategy();
//			Map<AttackerType, Target> apsSeguaInterval = Experiments.getAttackerStrategiesPureStrategy(pointValue, dmsSeguaInterval);
//			double euSeguaInterval = Experiments.expectedUtilityPureStrategy(pointValue, dmsSeguaInterval, apsSeguaInterval);
//			
//			System.out.println("SEGUA NORMAL FORM RESULTS");
//			System.out.println();
//			System.out.println("defender's mixed strategy: " + dmsSeguaInterval);
//			System.out.println("attacker strategies (DOBSS): " + apsSeguaInterval);
//			System.out.println("defender's maximum EU (DOBSS): " + euSeguaInterval);
//			System.out.println();
//			
//			OldDOBSS dobssPreferenceDegreeInterval = new OldDOBSS(preferenceDegreeInterval);
//			dobssPreferenceDegreeInterval.solve();
//			MixedStrategy dmsPreferenceDegreeInterval = dobssPreferenceDegreeInterval.getDefenderMixedStrategy();
//			Map<AttackerType, Target> apsPreferenceDegreeInterval = Experiments.getAttackerStrategiesPureStrategy(pointValue, dmsPreferenceDegreeInterval);
//			double euPreferenceDegreeInterval = Experiments.expectedUtilityPureStrategy(pointValue, dmsPreferenceDegreeInterval, apsPreferenceDegreeInterval);
//			
//			System.out.println("PREFERENCE DEGREE NORMAL FORM RESULTS");
//			System.out.println();
//			System.out.println("defender's mixed strategy: " + dmsPreferenceDegreeInterval);
//			System.out.println("attacker strategies (DOBSS): " + apsPreferenceDegreeInterval);
//			System.out.println("defender's maximum EU (DOBSS): " + euPreferenceDegreeInterval);
//			System.out.println();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
	
	public static boolean match(MixedStrategy a, MixedStrategy b) {
		double deviation = 0.01;
		for(Entry<Target, Double> entry : a.entrySet()) {
			if(b.get(entry.getKey()) < entry.getValue() - deviation || b.get(entry.getKey()) > entry.getValue() + deviation) {
				return false;
			}
		}
		return true;
	}
	
	public static void randomCompleteVsIncomplete() {
		
		MixedStrategy dmsOriginal = null;
		MixedStrategy dmsSeguaPointValue = null;
		int count = 1;
		
		long estimatedTotalTime = 0;
		do {
			System.out.print(count);
			count++;
		
			try{
				NegativeRange negative = new NegativeRange(-100, 0);
				PositiveRange positive = new PositiveRange(0, 100);
				
				/*
				 * ORIGINAL GAME
				 */
				// generate game
				MultiSingleTargetGame<IntegerPayoff> originalIntegerTarget = Randomizer.randomIntegerSingleTargetMultiGameNew(9, 9, negative, positive);
				MultiSingleTargetGame<NormalFormPayoff> originalTarget = Experiments.toNormalFormGame(originalIntegerTarget);
				
				// convert to pure strategy game
				MultiSingleProfileGame<NormalFormPayoff> original = originalTarget.toMultiSinglePureStrategyGame();
				
//				System.out.println("ORIGINAL GAME");
//				System.out.println();
//				System.out.println(original);
//				System.out.println();
				
				// solve original game
				DOBSS dobssOriginal = new DOBSS(original);
				dobssOriginal.solve();
				dmsOriginal = dobssOriginal.getDefenderMixedStrategy();
//				Map<AttackerType, Target> apsOriginal = dobssOriginal.getAttackerPureStrategies();
				
//				System.out.println("ORIGINAL RESULTS");
//				System.out.println();
//				System.out.println("defender's mixed strategy: " + dmsOriginal);
//				System.out.println("attacker strategies (DOBSS): " + apsOriginal);
//				System.out.println();
				
				/*
				 * POINT-VALUE GAME
				 */
				// construct bba pure strategy game
				Map<AttackerType, SingleProfileGame<BBAPayoff>> attackerGamesNewPointValue = new HashMap<AttackerType, SingleProfileGame<BBAPayoff>>();
				for(Entry<AttackerType, SingleTargetGame<IntegerPayoff>> entry : originalIntegerTarget.getSecurityGames().entrySet()) {
					SingleTargetGame<BBAPayoff> spsg = new SingleTargetGame<BBAPayoff>(original.getTargets());
					for(Entry<Target, PlayerPairPayoffPair<IntegerPayoff>> entryInner : entry.getValue().getPayoffs().entrySet()) {
						PlayerPairPayoffPair<IntegerPayoff> playerPairPayoffPair = entryInner.getValue();
						spsg.setPayoff(entryInner.getKey(),
								new PlayerPairPayoffPair<BBAPayoff>(
										new PayoffPair<BBAPayoff>(
												new PointValuePayoff(negative, playerPairPayoffPair.getDefender().getNegative().getInteger()),
												new PointValuePayoff(positive, playerPairPayoffPair.getDefender().getPositive().getInteger())
										), 
										new PayoffPair<BBAPayoff>(
												new PointValuePayoff(negative, playerPairPayoffPair.getAttacker().getNegative().getInteger()),
												new PointValuePayoff(positive, playerPairPayoffPair.getAttacker().getPositive().getInteger())
										)
								)
						);
					}
					attackerGamesNewPointValue.put(entry.getKey(), spsg.getPureStrategyGame());
				}
				
				MultiSingleProfileGame<BBAPayoff> pointValue = new MultiSingleProfileGame<BBAPayoff>(
						original.getPureStrategyProfiles(),
						original.getAttackerProbabilities(),
						attackerGamesNewPointValue);
				
//				System.out.println("POINT-VALUE GAME");
//				System.out.println();
//				System.out.println(pointValue);
//				System.out.println();
				
				// construct normal form game using segua
				System.out.print(".");
				long startTime = System.currentTimeMillis();
				MultiSingleProfileGame<NormalFormPayoff> seguaPointValue = new SingleDefault(pointValue).toNormalForm();
				System.out.print(".");
				long estimatedTime = System.currentTimeMillis() - startTime;
				estimatedTotalTime += estimatedTime;
				
				// solve segua game
				DOBSS dobssSeguaPointValue = new DOBSS(seguaPointValue);
				dobssSeguaPointValue.solve();
				dmsSeguaPointValue = dobssSeguaPointValue.getDefenderMixedStrategy();
//				Map<AttackerType, Target> apsSeguaPointValue = dobssSeguaPointValue.getAttackerPureStrategies();
				
//				System.out.println("SEGUA NORMAL FORM RESULTS");
//				System.out.println();
//				System.out.println("defender's mixed strategy: " + dmsSeguaPointValue);
//				System.out.println("attacker strategies (DOBSS): " + apsSeguaPointValue);
//				System.out.println();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("done");
		
		} while(match(dmsOriginal, dmsSeguaPointValue));
		System.err.println("original: " + dmsOriginal);
		System.err.println("segua: " + dmsSeguaPointValue);
		
		System.out.println("END");
		System.out.println(((double)estimatedTotalTime / (count*1000)) + " s (average)");
		
	}
	
	public static void multiDecisionRule() {
		
		try{
			Target t0 = new Target("t0");
			Target t1 = new Target("t1");
			AdvancedSet<Target> targets = new AdvancedSet<Target>(t0, t1);
			
			AttackerType a0 = new AttackerType("a0");
			AttackerType a1 = new AttackerType("a1");
			AttackerType a2 = new AttackerType("a2");
			AttackerType a3 = new AttackerType("a3");
			AttackerProbabilities attackerProbabilities = new AttackerProbabilities();
			attackerProbabilities.put(a0, 0.058461686840538395);
			attackerProbabilities.put(a1, 0.0679531805147269);
			attackerProbabilities.put(a2, 0.3082247326671632);
			attackerProbabilities.put(a3, 0.5653603999775715);
			
			/*
			 * ORIGINAL GAME
			 */
			SingleTargetGame<NormalFormPayoff> stg0Original = new SingleTargetGame<NormalFormPayoff>(targets);
			stg0Original.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-97), new IntegerPayoff(58)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-15), new IntegerPayoff(35))));
			stg0Original.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-52), new IntegerPayoff(65)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-86), new IntegerPayoff(61))));
			
			SingleTargetGame<NormalFormPayoff> stg1Original = new SingleTargetGame<NormalFormPayoff>(targets);
			stg1Original.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-6), new IntegerPayoff(54)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-88), new IntegerPayoff(52))));
			stg1Original.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-85), new IntegerPayoff(33)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-39), new IntegerPayoff(38))));
			
			SingleTargetGame<NormalFormPayoff> stg2Original = new SingleTargetGame<NormalFormPayoff>(targets);
			stg2Original.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-22), new IntegerPayoff(26)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-12), new IntegerPayoff(1))));
			stg2Original.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-98), new IntegerPayoff(70)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-39), new IntegerPayoff(80))));
			
			SingleTargetGame<NormalFormPayoff> stg3Original = new SingleTargetGame<NormalFormPayoff>(targets);
			stg3Original.setPayoff(t0, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-58), new IntegerPayoff(34)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-23), new IntegerPayoff(34))));
			stg3Original.setPayoff(t1, new PlayerPairPayoffPair<NormalFormPayoff>(new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-4), new IntegerPayoff(25)), new PayoffPair<NormalFormPayoff>(new IntegerPayoff(-59), new IntegerPayoff(29))));
			
			Map<AttackerType, SingleTargetGame<NormalFormPayoff>> attackerGamesOriginal = new HashMap<AttackerType, SingleTargetGame<NormalFormPayoff>>();
			attackerGamesOriginal.put(a0, stg0Original);
			attackerGamesOriginal.put(a1, stg1Original);
			attackerGamesOriginal.put(a2, stg2Original);
			attackerGamesOriginal.put(a3, stg3Original);
			
			MultiSingleTargetGame<NormalFormPayoff> original = new MultiSingleTargetGame<NormalFormPayoff>(
					targets,
					attackerProbabilities,
					attackerGamesOriginal);
			
			System.out.println("ORIGINAL TARGET GAME");
			System.out.println();
			System.out.println(original);
			System.out.println();
			
			System.out.println("ORIGINAL PURE STRATEGY PROFILE GAME");
			System.out.println();
			System.out.println(original.toMultiSinglePureStrategyGame());
			System.out.println();
			
			DOBSS dobssOriginal = new DOBSS(original.toMultiSinglePureStrategyGame());
			dobssOriginal.solve();
			MixedStrategy dmsOriginal = dobssOriginal.getDefenderMixedStrategy();
			Map<AttackerType, Target> apsOriginal = dobssOriginal.getAttackerPureStrategies();
			double euOriginal = dobssOriginal.getDefenderMaxEU();
			
			System.out.println("ORIGINAL RESULTS");
			System.out.println();
			System.out.println("defender's mixed strategy: " + dmsOriginal);
			System.out.println("attacker strategies (DOBSS): " + apsOriginal);
			System.out.println("defender's maximum EU (DOBSS): " + euOriginal);
			System.out.println();
			
			/*
			 * POINT-VALUE GAME
			 */
			NegativeRange negative = new NegativeRange(-100, 0);
			PositiveRange positive = new PositiveRange(0, 100);
			
			SingleTargetGame<BBAPayoff> stg0PointValue = new SingleTargetGame<BBAPayoff>(targets);
			stg0PointValue.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -97), new PointValuePayoff(positive, 58)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -15), new PointValuePayoff(positive, 35))));
			stg0PointValue.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -52), new PointValuePayoff(positive, 65)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -86), new PointValuePayoff(positive, 61))));
			
			SingleTargetGame<BBAPayoff> stg1PointValue = new SingleTargetGame<BBAPayoff>(targets);
			stg1PointValue.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -6), new PointValuePayoff(positive, 54)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -88), new PointValuePayoff(positive, 52))));
			stg1PointValue.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -85), new PointValuePayoff(positive, 33)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -39), new PointValuePayoff(positive, 38))));
			
			SingleTargetGame<BBAPayoff> stg2PointValue = new SingleTargetGame<BBAPayoff>(targets);
			stg2PointValue.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -22), new PointValuePayoff(positive, 26)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -12), new PointValuePayoff(positive, 1))));
			stg2PointValue.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -98), new PointValuePayoff(positive, 70)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -39), new PointValuePayoff(positive, 80))));
			
			SingleTargetGame<BBAPayoff> stg3PointValue = new SingleTargetGame<BBAPayoff>(targets);
			stg3PointValue.setPayoff(t0, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -58), new PointValuePayoff(positive, 34)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -23), new PointValuePayoff(positive, 34))));
			stg3PointValue.setPayoff(t1, new PlayerPairPayoffPair<BBAPayoff>(new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -4), new PointValuePayoff(positive, 25)), new PayoffPair<BBAPayoff>(new PointValuePayoff(negative, -59), new PointValuePayoff(positive, 29))));
			
			Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesPointValue = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
			attackerGamesPointValue.put(a0, stg0PointValue);
			attackerGamesPointValue.put(a1, stg1PointValue);
			attackerGamesPointValue.put(a2, stg2PointValue);
			attackerGamesPointValue.put(a3, stg3PointValue);
			
			MultiSingleTargetGame<BBAPayoff> pointValue = new MultiSingleTargetGame<BBAPayoff>(
					targets,
					attackerProbabilities,
					attackerGamesPointValue);
			
			System.out.println("POINT-VALUE TARGET GAME");
			System.out.println();
			System.out.println(pointValue);
			System.out.println();
			
			System.out.println("POINT-VALUE PURE STRATEGY PROFILE GAME");
			System.out.println();
			System.out.println(pointValue.toMultiSinglePureStrategyGame());
			System.out.println();
			
			MultiSingleProfileGame<NormalFormPayoff> seguaPointValue = new SingleDefault(pointValue.toMultiSinglePureStrategyGame()).toNormalForm();
			
			System.out.println("SEGUA NORMAL FROM GAME");
			System.out.println();
			System.out.println(seguaPointValue);
			System.out.println();
			
			DOBSS dobssSeguaPointValue = new DOBSS(seguaPointValue);
			dobssSeguaPointValue.solve();
			MixedStrategy dmsSeguaPointValue = dobssSeguaPointValue.getDefenderMixedStrategy();
			Map<AttackerType, Target> apsSeguaPointValue = dobssSeguaPointValue.getAttackerPureStrategies();
			double euSeguaPointValue = dobssSeguaPointValue.getDefenderMaxEU();
			
			System.out.println("SEGUA NORMAL FORM RESULTS");
			System.out.println();
			System.out.println("defender's mixed strategy: " + dmsSeguaPointValue);
			System.out.println("attacker strategies (DOBSS): " + apsSeguaPointValue);
			System.out.println("defender's maximum EU (DOBSS): " + euSeguaPointValue);
			System.out.println();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void owa() {
		
		try {
			NegativeRange negative = new NegativeRange(-9, 0);
			PositiveRange positive = new PositiveRange(0, 9);
			
			Target t1 = new Target("t1");
			Target t2 = new Target("t2");
			Target t3 = new Target("t3");
			AdvancedSet<Target> targets = new AdvancedSet<Target>(t1, t2, t3);
			
			AttackerType a1 = new AttackerType("a1");
			AttackerType a2 = new AttackerType("a2");
			AttackerProbabilities attackerProbabilities = new AttackerProbabilities();
			attackerProbabilities.put(a1, 0.6128708019969);
			attackerProbabilities.put(a2, 0.387129198003099);
			
			/*
			 * ORIGINAL POINT-VALUE GAME
			 */
			SingleTargetGame<BBAPayoff> stg1PointValue = new SingleTargetGame<BBAPayoff>(targets);
			
			stg1PointValue.setPayoff(
					t1,
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new IntervalPayoff(negative, new Interval(-7, -3)), 
									new IntervalPayoff(positive, new Interval(2, 6))
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -7), 
									new PointValuePayoff(positive, 5)
							)
					)
			);
			
			BBA<Integer> negativeBBA = new BBA<Integer>(negative.getAdvancedSet());
			negativeBBA.addMass(new Interval(-8, -7).getAdvancedSet(), 0.8);
			negativeBBA.addMass(new Interval(-9, 0).getAdvancedSet(), 0.2);
			
			BBA<Integer> positiveBBA = new BBA<Integer>(positive.getAdvancedSet());
			positiveBBA.addMass(new Interval(5, 6).getAdvancedSet(), 0.8);
			positiveBBA.addMass(new Interval(0, 9).getAdvancedSet(), 0.2);
			
			stg1PointValue.setPayoff(
					t2, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new AmbiguityLotteryPayoff(negative, negativeBBA), 
									new AmbiguityLotteryPayoff(positive, positiveBBA)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -5), 
									new PointValuePayoff(positive, 3)
							)
					)
			);
			
			stg1PointValue.setPayoff(
					t3, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -8), 
									new PointValuePayoff(positive, 7)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -9), 
									new PointValuePayoff(positive, 7)
							)
					)
			);
			
			SingleTargetGame<BBAPayoff> stg2PointValue = new SingleTargetGame<BBAPayoff>(targets);
			
			stg2PointValue.setPayoff(
					t1, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new AbsentPayoff(negative), 
									new AbsentPayoff(positive)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -4), 
									new PointValuePayoff(positive, 2)
							)
					)
			);
			
			stg2PointValue.setPayoff(
					t2, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -8), 
									new PointValuePayoff(positive, 7)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -9), 
									new PointValuePayoff(positive, 7)
							)
					)
			);
			
			stg2PointValue.setPayoff(
					t3, 
					new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -9), 
									new PointValuePayoff(positive, 0)
							),
							new PayoffPair<BBAPayoff>(
									new PointValuePayoff(negative, -9), 
									new PointValuePayoff(positive, 6)
							)
					)
			);
			
			Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesPointValue = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
			attackerGamesPointValue.put(a1, stg1PointValue);
			attackerGamesPointValue.put(a2, stg2PointValue);
			
			MultiSingleTargetGame<BBAPayoff> mtg = new MultiSingleTargetGame<BBAPayoff>(
					targets,
					attackerProbabilities,
					attackerGamesPointValue);
			
			System.out.println("target game:");
			System.out.println(mtg);
			System.out.println();
			
			System.out.println("pure strategy game:");
			MultiSingleProfileGame<BBAPayoff> spsg = mtg.toMultiSinglePureStrategyGame();
			System.out.println(spsg);
			System.out.println();
			
			System.out.println("tbm:");
			MultiSingleProfileGame<NormalFormPayoff> tbm = new TransferableBeliefModel(spsg).toNormalForm();
			System.out.println(tbm);
			System.out.println();
			
			DOBSS tbmDOBSS = new DOBSS(tbm);
			tbmDOBSS.solve();
			System.out.println("defender's mixed strategy: " + tbmDOBSS.getDefenderMixedStrategy());
			System.out.println("attacker pure strategies: " + tbmDOBSS.getAttackerPureStrategies());
			System.out.println();
			
			System.out.println("hurwicz criterion (0.5):");
			MultiSingleProfileGame<NormalFormPayoff> hurwicz = new HurwiczCriterion(spsg, 0.5).toNormalForm();
			System.out.println(hurwicz);
			System.out.println();
			
			DOBSS hurwiczDOBSS = new DOBSS(hurwicz);
			hurwiczDOBSS.solve();
			System.out.println("defender's mixed strategy: " + hurwiczDOBSS.getDefenderMixedStrategy());
			System.out.println("attacker pure strategies: " + hurwiczDOBSS.getAttackerPureStrategies());
			System.out.println();
			
			System.out.println("owa:");
			MultiSingleProfileGame<NormalFormPayoff> owa = new OWABasedModel(spsg).toNormalForm();
			System.out.println(owa);
			System.out.println();
			
			DOBSS owaDOBSS = new DOBSS(owa);
			owaDOBSS.solve();
			System.out.println("defender's mixed strategy: " + owaDOBSS.getDefenderMixedStrategy());
			System.out.println("attacker pure strategies: " + owaDOBSS.getAttackerPureStrategies());
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
//		example();
//		singleTargetGame();
//		random();
//		testDOBSS();
//		milindExample();
//		solve();
//		preferenceDegree();
//		completeVsIncomplete();
//		randomCompleteVsIncomplete();
//		multiDecisionRule();
		owa();
	}
	
}
