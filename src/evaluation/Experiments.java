package evaluation;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import segua.AttackerProbabilities;
import segua.AttackerType;
import segua.MixedStrategy;
import segua.PureStrategyProfile;
import segua.Target;
import segua.decision_rules.multi_single_profile_decision_rules.GammaMaximin;
import segua.decision_rules.multi_single_profile_decision_rules.HurwiczCriterion;
import segua.decision_rules.multi_single_profile_decision_rules.Maximax;
import segua.decision_rules.multi_single_profile_decision_rules.OWABasedModel;
import segua.decision_rules.multi_single_profile_decision_rules.SingleDefault;
import segua.decision_rules.multi_single_profile_decision_rules.TransferableBeliefModel;
import segua.multi_security_games.profile_games.MultiSingleProfileGame;
import segua.multi_security_games.target_games.MultiSingleTargetGame;
import segua.payoffs.PayoffPair;
import segua.payoffs.payoff_single.BBAPayoff;
import segua.payoffs.payoff_single.NormalFormPayoff;
import segua.payoffs.payoff_single.bba_payoffs.AbsentPayoff;
import segua.payoffs.payoff_single.bba_payoffs.AmbiguityLotteryPayoff;
import segua.payoffs.payoff_single.bba_payoffs.IntervalPayoff;
import segua.payoffs.payoff_single.normal_form_payoffs.IntegerPayoff;
import segua.player_pairs.PlayerPairPayoffPair;
import segua.player_pairs.PlayerPairPayoffSingle;
import segua.security_games.profile_games.SingleProfileGame;
import segua.security_games.target_games.SingleTargetGame;
import segua.solvers.DOBSS;
import data_structures.AdvancedSet;
import data_structures.BBA;
import data_structures.Interval;
import data_structures.Range;
import data_structures.ranges.NegativeRange;
import data_structures.ranges.PositiveRange;
import evaluation.Randomizer.IntervalRestriction;
import evaluation.Randomizer.LotteryRestriction;

public class Experiments {
	
	private final String BASE = "D:/SeGUA/";
	
//	private final double[] ALPHAS = {0.0, 0.2, 0.4, 0.5, 0.7, 0.9, 1.0};
	private final double[] ALPHAS = {0.2, 0.4, 0.6, 0.8};
	
	private String directory;
	
	private IntervalRestriction intervalRestriction;
	private LotteryRestriction lotteryRestriction;
	
	public void part2Interval(int id, String range, int numTargets, int numTypes, NegativeRange negative, PositiveRange positive) throws Exception {
		
		MultiSingleTargetGame<BBAPayoff> originalGame = Randomizer.randomBBASingleTargetMultiGame(numTargets, numTypes, negative, positive);
		System.out.print(".");
		
		PrintWriter targetsInput = new PrintWriter(directory + range + "/step_1a/" + id + "-interval-targets.txt", "UTF-8");
		targetsInput.println(originalGame.toString());
		targetsInput.close();
		System.out.print(".");
		
		PrintWriter pureStrategiesInput = new PrintWriter(directory + range + "/step_1b/" + id + "-interval-pure_strategies.txt", "UTF-8");
		pureStrategiesInput.println(originalGame.toMultiSinglePureStrategyGame().toString());
		pureStrategiesInput.close();
		System.out.print(".");
		
		// Get random attacker type.
		AttackerProbabilities attackerTypes = originalGame.getAttackerProbabilities();
		int i = 0;
		int selectedType = Randomizer.randomInteger(0, attackerTypes.size() - 1);
		AttackerType type = null;
		for(Entry<AttackerType, Double> entry : attackerTypes.entrySet()) {
			if(i == selectedType) {
				type = entry.getKey();
				break;
			}
			i++;
		}
		
		// Get random target.
		AdvancedSet<Target> targets = originalGame.getTargets();
		int j = 0;
		int selectedTarget = Randomizer.randomInteger(0, targets.size() - 1);
		Target focalTarget = null;
		for(Target t : targets) {
			if(j == selectedTarget) {
				focalTarget = t;
				break;
			}
			j++;
		}
		
		// get new random payoffs for focal set
		PlayerPairPayoffPair<BBAPayoff> existingPayoff = originalGame.getSecurityGames().get(type).getPayoffs().get(focalTarget);
		BBAPayoff defNeg = existingPayoff.getDefender().getNegative();
		BBAPayoff defPos = existingPayoff.getDefender().getPositive();
		BBAPayoff attNeg = existingPayoff.getAttacker().getNegative();
		BBAPayoff attPos = existingPayoff.getAttacker().getPositive();
		
		// Randomly select either defender or attacker payoff to modify.
		int playerIndex = 0; //Randomizer.randomInteger(0, 1);
		int alterIndex;
		String player;
		Interval alter;
		if(range.equals("negative")) {
			if(playerIndex == 0) {
				alterIndex = 0;
				player = "defender";
				while(defPos instanceof AbsentPayoff) {
					defPos = Randomizer.randomAmbiguousPayoff(positive);
				}
			} else if(playerIndex == 1) {
				alterIndex = 2;
				player = "attacker";
				while(attPos instanceof AbsentPayoff) {
					attPos = Randomizer.randomAmbiguousPayoff(positive);
				}
			} else {
				throw new Exception("invald player index: " + playerIndex);
			}
			alter = (Interval) negative;
		} else if(range.equals("positive")) {
			if(playerIndex == 0) {
				alterIndex = 1;
				player = "defender";
				while(defNeg instanceof AbsentPayoff) {
					defNeg = Randomizer.randomAmbiguousPayoff(negative);
				}
			} else if(playerIndex == 1) {
				alterIndex = 3;
				player = "attacker";
				while(attNeg instanceof AbsentPayoff) {
					attNeg = Randomizer.randomAmbiguousPayoff(negative);
				}
			} else {
				throw new Exception("invald player index: " + playerIndex);
			}
			alter = (Interval) positive;
		} else {
			throw new Exception("invald frame: " + range);
		}
		
		PrintWriter results = new PrintWriter(directory + range + "/step_2/" + id + "-interval.csv", "UTF-8");
		
		// alter postive or negative payoff of defender or attacker and compute mixed strategy
		int min = alter.getLeft();
		int max = alter.getRight();
		int counter = 0;
		while(min <= max) {
			alter = new Interval(min, max);
			IntervalPayoff alteredPayoff;
			PlayerPairPayoffPair<BBAPayoff> alteredPayoffPair;
			
			switch(alterIndex) {
				case 0:
					alteredPayoff = new IntervalPayoff(negative, alter);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(alteredPayoff, defPos),
							new PayoffPair<BBAPayoff>(attNeg, attPos)
					);
					break;
				case 1:
					alteredPayoff = new IntervalPayoff(positive, alter);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, alteredPayoff),
							new PayoffPair<BBAPayoff>(attNeg, attPos)
					);
					break;
				case 2:
					alteredPayoff = new IntervalPayoff(negative, alter);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, defPos),
							new PayoffPair<BBAPayoff>(alteredPayoff, attPos)
					);
					break;
				case 3:
					alteredPayoff = new IntervalPayoff(positive, alter);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, defPos),
							new PayoffPair<BBAPayoff>(attNeg, alteredPayoff)
					);
					break;
				default:
					throw new Exception("invalid index: " + alterIndex);
			}
			
			originalGame.getSecurityGames().get(type).setPayoff(focalTarget, alteredPayoffPair);
			
			PrintWriter inputs = new PrintWriter(directory + range + "/step_1c/" + id + "-" + counter + "-interval.txt", "UTF-8");
			inputs.println(originalGame);
			inputs.close();
			
			MultiSingleProfileGame<NormalFormPayoff> seguaNormalForm = new SingleDefault(originalGame.toMultiSinglePureStrategyGame()).toNormalForm();
			
			PrintWriter inputsNormalForm = new PrintWriter(directory + range + "/step_1d/" + id + "-" + counter + "-interval.txt", "UTF-8");
			inputsNormalForm.println(seguaNormalForm);
			inputsNormalForm.close();
			
			DOBSS dobss = new DOBSS(seguaNormalForm);
			dobss.solve();
			results.println(counter + "," + type + "," + focalTarget + "," + player + "," + range + "," + alteredPayoff.toString().replace(',', ';') + "," + dobss.getDefenderMixedStrategy().get(focalTarget) + "," + dobss.getDefenderMixedStrategy().toString().replace(',', ';') + "," + dobss.getAttackerPureStrategies().toString().replace(',', ';') + "," + dobss.getDefenderMaxEU());
			
			min++;
			max--;
			counter++;
			System.out.print(".");
		}
		
		results.close();
		
	}
	
	public void part2PointValueLottery(int id, String range, int numTargets, int numTypes, NegativeRange negative, PositiveRange positive) throws Exception {
		
		MultiSingleTargetGame<BBAPayoff> originalGame = Randomizer.randomBBASingleTargetMultiGame(numTargets, numTypes, negative, positive);
		System.out.print(".");
		
		PrintWriter targetsInput = new PrintWriter(directory + range + "/step_1a/" + id + "-point_value_lottery-targets.txt", "UTF-8");
		targetsInput.println(originalGame.toString());
		targetsInput.close();
		System.out.print(".");
		
		PrintWriter pureStrategiesInput = new PrintWriter(directory + range + "/step_1b/" + id + "-point_value_lottery-pure_strategies.txt", "UTF-8");
		pureStrategiesInput.println(originalGame.toMultiSinglePureStrategyGame().toString());
		pureStrategiesInput.close();
		System.out.print(".");
		
		// Get random attacker type.
		AttackerProbabilities attackerTypes = originalGame.getAttackerProbabilities();
		int i = 0;
		int selectedType = Randomizer.randomInteger(0, attackerTypes.size() - 1);
		AttackerType type = null;
		for(Entry<AttackerType, Double> entry : attackerTypes.entrySet()) {
			if(i == selectedType) {
				type = entry.getKey();
				break;
			}
			i++;
		}
		
		// Get random target.
		AdvancedSet<Target> targets = originalGame.getTargets();
		int j = 0;
		int selectedTarget = Randomizer.randomInteger(0, targets.size() - 1);
		Target focalTarget = null;
		for(Target t : targets) {
			if(j == selectedTarget) {
				focalTarget = t;
				break;
			}
			j++;
		}
		
		// get new random payoffs for focal set
		PlayerPairPayoffPair<BBAPayoff> existingPayoff = originalGame.getSecurityGames().get(type).getPayoffs().get(focalTarget);
		BBAPayoff defNeg = existingPayoff.getDefender().getNegative();
		BBAPayoff defPos = existingPayoff.getDefender().getPositive();
		BBAPayoff attNeg = existingPayoff.getAttacker().getNegative();
		BBAPayoff attPos = existingPayoff.getAttacker().getPositive();
		
		// Randomly select either defender or attacker payoff to modify.
		int playerIndex = 0; //Randomizer.randomInteger(0, 1);
		int alterIndex;
		String player;
		Range frame;
		if(range.equals("negative")) {
			if(playerIndex == 0) {
				alterIndex = 0;
				player = "defender";
				while(defPos instanceof AbsentPayoff) {
					defPos = Randomizer.randomAmbiguousPayoff(positive);
				}
			} else if(playerIndex == 1) {
				alterIndex = 2;
				player = "attacker";
				while(attPos instanceof AbsentPayoff) {
					attPos = Randomizer.randomAmbiguousPayoff(positive);
				}
			} else {
				throw new Exception("invald player index: " + playerIndex);
			}
			frame = negative;
		} else if(range.equals("positive")) {
			if(playerIndex == 0) {
				alterIndex = 1;
				player = "defender";
				while(defNeg instanceof AbsentPayoff) {
					defNeg = Randomizer.randomAmbiguousPayoff(negative);
				}
			} else if(playerIndex == 1) {
				alterIndex = 3;
				player = "attacker";
				while(attNeg instanceof AbsentPayoff) {
					attNeg = Randomizer.randomAmbiguousPayoff(negative);
				}
			} else {
				throw new Exception("invald player index: " + playerIndex);
			}
			frame = positive;
		} else {
			throw new Exception("invald frame: " + range);
		}
		
		PrintWriter results = new PrintWriter(directory + range + "/step_2/" + id + "-point_value_lottery.csv", "UTF-8");
		
		// alter postive or negative payoff of defender or attacker and compute mixed strategy
		int pointValue = Randomizer.randomInteger(frame.getLeft(), frame.getRight());
		AdvancedSet<Integer> pointValueSet = new AdvancedSet<Integer>(pointValue);
		int counter = 0;
		for(int ip = 0; ip <= 100; ip+=2) {
			double p = (double)ip/(double)100;
			BBA<Integer> bba = new BBA<Integer>(frame.getAdvancedSet());
			bba.addMass(pointValueSet, p);
			bba.addMass(frame.getAdvancedSet(), 1-p);
			
			AmbiguityLotteryPayoff alteredPayoff;
			PlayerPairPayoffPair<BBAPayoff> alteredPayoffPair;
			
			switch(alterIndex) {
				case 0:
					alteredPayoff = new AmbiguityLotteryPayoff(negative, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(alteredPayoff, defPos),
							new PayoffPair<BBAPayoff>(attNeg, attPos)
					);
					break;
				case 1:
					alteredPayoff = new AmbiguityLotteryPayoff(positive, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, alteredPayoff),
							new PayoffPair<BBAPayoff>(attNeg, attPos)
					);
					break;
				case 2:
					alteredPayoff = new AmbiguityLotteryPayoff(negative, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, defPos),
							new PayoffPair<BBAPayoff>(alteredPayoff, attPos)
					);
					break;
				case 3:
					alteredPayoff = new AmbiguityLotteryPayoff(positive, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, defPos),
							new PayoffPair<BBAPayoff>(attNeg, alteredPayoff)
					);
					break;
				default:
					throw new Exception("invalid index: " + alterIndex);
			}
			
			originalGame.getSecurityGames().get(type).setPayoff(focalTarget, alteredPayoffPair);
			
			PrintWriter inputs = new PrintWriter(directory + range + "/step_1c/" + id + "-" + counter + "-point_value_lottery.txt", "UTF-8");
			inputs.println(originalGame);
			inputs.close();
			
			MultiSingleProfileGame<NormalFormPayoff> seguaNormalForm = new SingleDefault(originalGame.toMultiSinglePureStrategyGame()).toNormalForm();
			
			PrintWriter inputsNormalForm = new PrintWriter(directory + range + "/step_1d/" + id + "-" + counter + "-point_value_lottery.txt", "UTF-8");
			inputsNormalForm.println(seguaNormalForm);
			inputsNormalForm.close();
			
			DOBSS dobss = new DOBSS(seguaNormalForm);
			dobss.solve();
			results.println(counter + "," + type + "," + focalTarget + "," + player + "," + range + "," + alteredPayoff.toString().replace(',', ';') + "," + dobss.getDefenderMixedStrategy().get(focalTarget) + "," + dobss.getDefenderMixedStrategy().toString().replace(',', ';') + "," + dobss.getAttackerPureStrategies().toString().replace(',', ';') + "," + dobss.getDefenderMaxEU());
			
			counter++;
			System.out.print(".");
		}
		
		results.close();
		
	}
	
	public void part2IntervalLottery(int id, String range, int numTargets, int numTypes, NegativeRange negative, PositiveRange positive) throws Exception {
		
		MultiSingleTargetGame<BBAPayoff> originalGame = Randomizer.randomBBASingleTargetMultiGame(numTargets, numTypes, negative, positive);
		System.out.print(".");
		
		PrintWriter targetsInput = new PrintWriter(directory + range + "/step_1a/" + id + "-interval_lottery-targets.txt", "UTF-8");
		targetsInput.println(originalGame.toString());
		targetsInput.close();
		System.out.print(".");
		
		PrintWriter pureStrategiesInput = new PrintWriter(directory + range + "/step_1b/" + id + "-interval_lottery-pure_strategies.txt", "UTF-8");
		pureStrategiesInput.println(originalGame.toMultiSinglePureStrategyGame().toString());
		pureStrategiesInput.close();
		System.out.print(".");
		
		// Get random attacker type.
		AttackerProbabilities attackerTypes = originalGame.getAttackerProbabilities();
		int i = 0;
		int selectedType = Randomizer.randomInteger(0, attackerTypes.size() - 1);
		AttackerType type = null;
		for(Entry<AttackerType, Double> entry : attackerTypes.entrySet()) {
			if(i == selectedType) {
				type = entry.getKey();
				break;
			}
			i++;
		}
		
		// Get random target.
		AdvancedSet<Target> targets = originalGame.getTargets();
		int j = 0;
		int selectedTarget = Randomizer.randomInteger(0, targets.size() - 1);
		Target focalTarget = null;
		for(Target t : targets) {
			if(j == selectedTarget) {
				focalTarget = t;
				break;
			}
			j++;
		}
		
		// get new random payoffs for focal set
		PlayerPairPayoffPair<BBAPayoff> existingPayoff = originalGame.getSecurityGames().get(type).getPayoffs().get(focalTarget);
		BBAPayoff defNeg = existingPayoff.getDefender().getNegative();
		BBAPayoff defPos = existingPayoff.getDefender().getPositive();
		BBAPayoff attNeg = existingPayoff.getAttacker().getNegative();
		BBAPayoff attPos = existingPayoff.getAttacker().getPositive();
		
		// Randomly select either defender or attacker payoff to modify.
		int playerIndex = 0; //Randomizer.randomInteger(0, 1);
		int alterIndex;
		String player;
		Interval alter;
		Range frame;
		if(range.equals("negative")) {
			if(playerIndex == 0) {
				alterIndex = 0;
				player = "defender";
				while(defPos instanceof AbsentPayoff) {
					defPos = Randomizer.randomAmbiguousPayoff(positive);
				}
			} else if(playerIndex == 1) {
				alterIndex = 2;
				player = "attacker";
				while(attPos instanceof AbsentPayoff) {
					attPos = Randomizer.randomAmbiguousPayoff(positive);
				}
			} else {
				throw new Exception("invald player index: " + playerIndex);
			}
			alter = (Interval) negative;
			frame = negative;
		} else if(range.equals("positive")) {
			if(playerIndex == 0) {
				alterIndex = 1;
				player = "defender";
				while(defNeg instanceof AbsentPayoff) {
					defNeg = Randomizer.randomAmbiguousPayoff(negative);
				}
			} else if(playerIndex == 1) {
				alterIndex = 3;
				player = "attacker";
				while(attNeg instanceof AbsentPayoff) {
					attNeg = Randomizer.randomAmbiguousPayoff(negative);
				}
			} else {
				throw new Exception("invald player index: " + playerIndex);
			}
			alter = (Interval) positive;
			frame = positive;
		} else {
			throw new Exception("invald frame: " + range);
		}
		
		PrintWriter results = new PrintWriter(directory + range + "/step_2/" + id + "-interval_lottery.csv", "UTF-8");
		
		// alter postive or negative payoff of defender or attacker and compute mixed strategy
		int min = alter.getLeft();
		int max = alter.getRight();
		double p = Randomizer.randomDouble(0, 1);
		int counter = 0;
		while(min <= max) {
			alter = new Interval(min, max);
			
			BBA<Integer> bba = new BBA<Integer>(frame.getAdvancedSet());
			if(alter.getAdvancedSet().equals(frame.getAdvancedSet())) {
				bba.addMass(frame.getAdvancedSet(), 1);
			} else {
				bba.addMass(alter.getAdvancedSet(), p);
				bba.addMass(frame.getAdvancedSet(), 1-p);
			}
			
			AmbiguityLotteryPayoff alteredPayoff;
			PlayerPairPayoffPair<BBAPayoff> alteredPayoffPair;
			
			switch(alterIndex) {
				case 0:
					alteredPayoff = new AmbiguityLotteryPayoff(negative, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(alteredPayoff, defPos),
							new PayoffPair<BBAPayoff>(attNeg, attPos)
					);
					break;
				case 1:
					alteredPayoff = new AmbiguityLotteryPayoff(positive, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, alteredPayoff),
							new PayoffPair<BBAPayoff>(attNeg, attPos)
					);
					break;
				case 2:
					alteredPayoff = new AmbiguityLotteryPayoff(negative, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, defPos),
							new PayoffPair<BBAPayoff>(alteredPayoff, attPos)
					);
					break;
				case 3:
					alteredPayoff = new AmbiguityLotteryPayoff(positive, bba);
					alteredPayoffPair = new PlayerPairPayoffPair<BBAPayoff>(
							new PayoffPair<BBAPayoff>(defNeg, defPos),
							new PayoffPair<BBAPayoff>(attNeg, alteredPayoff)
					);
					break;
				default:
					throw new Exception("invalid index: " + alterIndex);
			}
			
			originalGame.getSecurityGames().get(type).setPayoff(focalTarget, alteredPayoffPair);
			
			PrintWriter inputs = new PrintWriter(directory + range + "/step_1c/" + id + "-" + counter + "-interval_lottery.txt", "UTF-8");
			inputs.println(originalGame);
			inputs.close();
			
			MultiSingleProfileGame<NormalFormPayoff> seguaNormalForm = new SingleDefault(originalGame.toMultiSinglePureStrategyGame()).toNormalForm();
			
			PrintWriter inputsNormalForm = new PrintWriter(directory + range + "/step_1d/" + id + "-" + counter + "-interval_lottery.txt", "UTF-8");
			inputsNormalForm.println(seguaNormalForm);
			inputsNormalForm.close();
			
			DOBSS dobss = new DOBSS(seguaNormalForm);
			dobss.solve();
			results.println(counter + "," + type + "," + focalTarget + "," + player + "," + range + "," + alteredPayoff.toString().replace(',', ';') + "," + dobss.getDefenderMixedStrategy().get(focalTarget) + "," + dobss.getDefenderMixedStrategy().toString().replace(',', ';') + "," + dobss.getAttackerPureStrategies().toString().replace(',', ';') + "," + dobss.getDefenderMaxEU());
			
			min++;
			max--;
			counter++;
			System.out.print(".");
		}
		
		results.close();
		
	}
	
	public void part3Interval(int id, MultiSingleTargetGame<IntegerPayoff> pointValueGame, NegativeRange negative, PositiveRange positive) throws Exception {
		
		Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesNew = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
		
		for(Entry<AttackerType, SingleTargetGame<IntegerPayoff>> entry : pointValueGame.getSecurityGames().entrySet()) {
			SingleTargetGame<IntegerPayoff> atg = entry.getValue();
			SingleTargetGame<BBAPayoff> atgNew = new SingleTargetGame<BBAPayoff>(atg.getTargets());
			
			for(Target target : atg.getTargets()) {
				PlayerPairPayoffPair<IntegerPayoff> payoff = atg.getPayoffs().get(target);
				
				atgNew.setPayoff(target, 
						Randomizer.randomlyExtendToInterval(payoff, negative, positive, intervalRestriction)
//						Randomizer.randomlyExtendToFixedSizeInterval(payoff, negative, positive, a, b)
				);
			}
			attackerGamesNew.put(entry.getKey(), atgNew);
		}
		
		MultiSingleTargetGame<BBAPayoff> intervalGame = new MultiSingleTargetGame<BBAPayoff>(
				pointValueGame.getTargets(),
				pointValueGame.getAttackerProbabilities(),
				attackerGamesNew);
		
		try {
			printAmbiguousGame("interval", id, intervalGame);
			printNormalFormGames("interval", id, intervalGame);
			
			PrintWriter eus = new PrintWriter(directory + "step_2b-3/" + id + "-interval.csv", "UTF-8");
			printExpectedUtilities(eus, id, pointValueGame, intervalGame);
			eus.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void part3PointValueLottery(int id, MultiSingleTargetGame<IntegerPayoff> pointValueGame, NegativeRange negative, PositiveRange positive) throws Exception {
		
		Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesNew = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
		
		for(Entry<AttackerType, SingleTargetGame<IntegerPayoff>> entry : pointValueGame.getSecurityGames().entrySet()) {
			SingleTargetGame<IntegerPayoff> atg = entry.getValue();
			SingleTargetGame<BBAPayoff> atgNew = new SingleTargetGame<BBAPayoff>(atg.getTargets());
			
			for(Target target : atg.getTargets()) {
				PlayerPairPayoffPair<IntegerPayoff> payoff = atg.getPayoffs().get(target);
				
				atgNew.setPayoff(target, 
						Randomizer.randomlyExtendToPointValueLottery(payoff, negative, positive, lotteryRestriction)
				);
			}
			attackerGamesNew.put(entry.getKey(), atgNew);
		}
		
		MultiSingleTargetGame<BBAPayoff> pointValueLotteryGame = new MultiSingleTargetGame<BBAPayoff>(
				pointValueGame.getTargets(),
				pointValueGame.getAttackerProbabilities(),
				attackerGamesNew);
		
		try {
			printAmbiguousGame("point_value_lottery", id, pointValueLotteryGame);
			printNormalFormGames("point_value_lottery", id, pointValueLotteryGame);
			
			PrintWriter eus = new PrintWriter(directory + "step_2b-3/" + id + "-point_value_lottery.csv", "UTF-8");
			printExpectedUtilities(eus, id, pointValueGame, pointValueLotteryGame);
			eus.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void part3IntervalLottery(int id, MultiSingleTargetGame<IntegerPayoff> pointValueGame, NegativeRange negative, PositiveRange positive) throws Exception {
		
		Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesNew = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
		
		for(Entry<AttackerType, SingleTargetGame<IntegerPayoff>> entry : pointValueGame.getSecurityGames().entrySet()) {
			SingleTargetGame<IntegerPayoff> atg = entry.getValue();
			SingleTargetGame<BBAPayoff> atgNew = new SingleTargetGame<BBAPayoff>(atg.getTargets());
			
			for(Target target : atg.getTargets()) {
				PlayerPairPayoffPair<IntegerPayoff> payoff = atg.getPayoffs().get(target);
				
				atgNew.setPayoff(target, 
						Randomizer.randomlyExtendToIntervalLottery(payoff, negative, positive, intervalRestriction, lotteryRestriction)
				);
			}
			attackerGamesNew.put(entry.getKey(), atgNew);
		}
		
		MultiSingleTargetGame<BBAPayoff> intervalLotteryGame = new MultiSingleTargetGame<BBAPayoff>(
				pointValueGame.getTargets(),
				pointValueGame.getAttackerProbabilities(),
				attackerGamesNew);
		
		try {
			printAmbiguousGame("interval_lottery", id, intervalLotteryGame);
			printNormalFormGames("interval_lottery", id, intervalLotteryGame);
			
			PrintWriter eus = new PrintWriter(directory + "step_2b-3/" + id + "-interval_lottery.csv", "UTF-8");
			printExpectedUtilities(eus, id, pointValueGame, intervalLotteryGame);
			eus.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void part3MixedAmbiguities(int id, MultiSingleTargetGame<IntegerPayoff> pointValueGame, NegativeRange negative, PositiveRange positive) throws Exception {
		
		Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesNew = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
		
		for(Entry<AttackerType, SingleTargetGame<IntegerPayoff>> entry : pointValueGame.getSecurityGames().entrySet()) {
			SingleTargetGame<IntegerPayoff> atg = entry.getValue();
			SingleTargetGame<BBAPayoff> atgNew = new SingleTargetGame<BBAPayoff>(atg.getTargets());
			
			for(Target target : atg.getTargets()) {
				PlayerPairPayoffPair<IntegerPayoff> payoff = atg.getPayoffs().get(target);
				
				switch(Randomizer.randomInteger(0, 2)) {
					case 0:
						atgNew.setPayoff(target, 
								Randomizer.randomlyExtendToInterval(payoff, negative, positive, intervalRestriction)
						);
						break;
					case 1:
						atgNew.setPayoff(target, 
								Randomizer.randomlyExtendToPointValueLottery(payoff, negative, positive, lotteryRestriction)
						);
						break;
					case 2:
						atgNew.setPayoff(target, 
								Randomizer.randomlyExtendToIntervalLottery(payoff, negative, positive, intervalRestriction, lotteryRestriction)
						);
						break;
					default:
						throw new Exception("unexpected mixed ambiguity type");
				}
			}
			attackerGamesNew.put(entry.getKey(), atgNew);
		}
		
		MultiSingleTargetGame<BBAPayoff> mixedAmbiguitiesGame = new MultiSingleTargetGame<BBAPayoff>(
				pointValueGame.getTargets(),
				pointValueGame.getAttackerProbabilities(),
				attackerGamesNew);
		
		try {
			printAmbiguousGame("mixed_ambiguities", id, mixedAmbiguitiesGame);
			printNormalFormGames("mixed_ambiguities", id, mixedAmbiguitiesGame);
			
			PrintWriter eus = new PrintWriter(directory + "step_2b-3/" + id + "-mixed_ambiguities.csv", "UTF-8");
			printExpectedUtilities(eus, id, pointValueGame, mixedAmbiguitiesGame);
			eus.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void part3MixedLottery(int id, MultiSingleTargetGame<IntegerPayoff> pointValueGame, NegativeRange negative, PositiveRange positive) throws Exception {
		
		Map<AttackerType, SingleTargetGame<BBAPayoff>> attackerGamesNew = new HashMap<AttackerType, SingleTargetGame<BBAPayoff>>();
		
		for(Entry<AttackerType, SingleTargetGame<IntegerPayoff>> entry : pointValueGame.getSecurityGames().entrySet()) {
			SingleTargetGame<IntegerPayoff> atg = entry.getValue();
			SingleTargetGame<BBAPayoff> atgNew = new SingleTargetGame<BBAPayoff>(atg.getTargets());
			
			for(Target target : atg.getTargets()) {
				PlayerPairPayoffPair<IntegerPayoff> payoff = atg.getPayoffs().get(target);
				
				atgNew.setPayoff(target, 
						Randomizer.randomlyExtendToMixedLottery(payoff, negative, positive)
				);
			}
			attackerGamesNew.put(entry.getKey(), atgNew);
		}
		
		MultiSingleTargetGame<BBAPayoff> mixedLotteryGame = new MultiSingleTargetGame<BBAPayoff>(
				pointValueGame.getTargets(),
				pointValueGame.getAttackerProbabilities(),
				attackerGamesNew);
		
		try {
			printAmbiguousGame("mixed_lottery", id, mixedLotteryGame);
			printNormalFormGames("mixed_lottery", id, mixedLotteryGame);
			
			PrintWriter eus = new PrintWriter(directory + "step_2b-3/" + id + "-mixed_lottery.csv", "UTF-8");
			printExpectedUtilities(eus, id, pointValueGame, mixedLotteryGame);
			eus.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void printAmbiguousGame(String label, int id, MultiSingleTargetGame<BBAPayoff> targetGame) throws Exception {
		
		PrintWriter targets = new PrintWriter(directory + "step_1c/" + id + "-targets-" + label + ".txt", "UTF-8");
		targets.println(targetGame.toString());
		targets.close();
		
		MultiSingleProfileGame<BBAPayoff> pureStrategyGame = targetGame.toMultiSinglePureStrategyGame();
		
		PrintWriter pureStrategies = new PrintWriter(directory + "step_1d/" + id + "-pure_strategies-" + label + ".txt", "UTF-8");
		pureStrategies.println(pureStrategyGame);
		pureStrategies.close();
		
	}
	
	public void printNormalFormGames(String label, int id, MultiSingleTargetGame<BBAPayoff> targetGame) throws Exception {
		
		PrintWriter segua = new PrintWriter(directory + "step_2a/" + id + "-segua-pure_strategies-" + label + ".txt", "UTF-8");
		MultiSingleProfileGame<NormalFormPayoff> seguaNormalForm = new SingleDefault(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		segua.println(seguaNormalForm.toString());
		segua.close();
		
//		PrintWriter preferenceDegree = new PrintWriter(directory + "step_2a/" + id + "-preference_degree-pure_strategies-" + label + ".txt", "UTF-8");
//		MultiSinglePureStrategyGame<NormalFormPayoff> preferenceDegreeNormalForm = new MultiPreferenceDegree(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
//		preferenceDegree.println(preferenceDegreeNormalForm.toString());
//		preferenceDegree.close();
		
		PrintWriter tbm = new PrintWriter(directory + "step_2a/" + id + "-tbm-pure_strategies-" + label + ".txt", "UTF-8");
		MultiSingleProfileGame<NormalFormPayoff> tbmNormalForm = new TransferableBeliefModel(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		tbm.println(tbmNormalForm.toString());
		tbm.close();
		
		PrintWriter maximax = new PrintWriter(directory + "step_2a/" + id + "-maximax-pure_strategies-" + label + ".txt", "UTF-8");
		MultiSingleProfileGame<NormalFormPayoff> maximaxNormalForm = new Maximax(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		maximax.println(maximaxNormalForm.toString());
		maximax.close();
		
		Map<Double, MultiSingleProfileGame<NormalFormPayoff>> hurwitzNormalForms = new HashMap<Double, MultiSingleProfileGame<NormalFormPayoff>>();
		for(Double alpha : ALPHAS) {
			PrintWriter hutwitz = new PrintWriter(directory + "step_2a/" + id + "-hurwitz_criterion(alpha=" + alpha + ")-pure_strategies-" + label + ".txt", "UTF-8");
			MultiSingleProfileGame<NormalFormPayoff> hutwitzNormalForm = new HurwiczCriterion(targetGame.toMultiSinglePureStrategyGame(), alpha).toNormalForm();
			hurwitzNormalForms.put(alpha, hutwitzNormalForm);
			hutwitz.println(hutwitzNormalForm.toString());
			hutwitz.close();
		}
		
		PrintWriter gammaMaximin = new PrintWriter(directory + "step_2a/" + id + "-gamma_maximin-pure_strategies-" + label + ".txt", "UTF-8");
		MultiSingleProfileGame<NormalFormPayoff> gammaMaximinNormalForm = new GammaMaximin(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		gammaMaximin.println(gammaMaximinNormalForm.toString());
		gammaMaximin.close();
		
		PrintWriter owa = new PrintWriter(directory + "step_2a/" + id + "-owa-pure_strategies-" + label + ".txt", "UTF-8");
		MultiSingleProfileGame<NormalFormPayoff> owaNormalForm = new OWABasedModel(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		owa.println(owaNormalForm.toString());
		owa.close();
		
	}
	
	public void printExpectedUtilities(PrintWriter eus, int id, MultiSingleTargetGame<IntegerPayoff> integerPointValueGame, MultiSingleTargetGame<BBAPayoff> targetGame) throws Exception {
		
		MultiSingleTargetGame<NormalFormPayoff> pointValueGame = toNormalFormGame(integerPointValueGame);
		
		// Original results.
		MultiSingleProfileGame<NormalFormPayoff> originalNormalForm = pointValueGame.toMultiSinglePureStrategyGame();
		DOBSS originalDOBSS = new DOBSS(originalNormalForm);
		originalDOBSS.solve();
		MixedStrategy dobssDefenderMixedStrategy = originalDOBSS.getDefenderMixedStrategy();
		Map<AttackerType, Target> ourAttackerPureStrategies = getAttackerStrategiesPureStrategy(originalNormalForm, dobssDefenderMixedStrategy);
		double dobssDefenderMaxEU = originalDOBSS.getDefenderMaxEU();
		double ourDefenderMaxEU = Experiments.expectedUtilityPureStrategy(originalNormalForm, dobssDefenderMixedStrategy, ourAttackerPureStrategies);
		
		double deviation = 1e-2;
		if(!(ourDefenderMaxEU >= dobssDefenderMaxEU - deviation && ourDefenderMaxEU <= dobssDefenderMaxEU + deviation)) {
			System.err.println("floating-point error: game " + id + " (expected " + dobssDefenderMaxEU + " but found " + ourDefenderMaxEU + ")");
			return;
		}
		
//		eus.println("original," + dobssDefenderMixedStrategy.toString().replace(',', ';') + "," + ourAttackerPureStrategies.toString().replace(',', ';') + "," + ourDefenderMaxEU);
		
		// SeGUA results.
		MultiSingleProfileGame<NormalFormPayoff> seguaNormalForm = new SingleDefault(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		DOBSS seguaDOBSS = new DOBSS(seguaNormalForm);
		seguaDOBSS.solve();
		MixedStrategy seguaDMS = seguaDOBSS.getDefenderMixedStrategy();
		Map<AttackerType, Target> seguaAS = getAttackerStrategiesTarget(pointValueGame, seguaDMS);
		double seguaDefenderMaxEU = expectedUtilityTarget(pointValueGame, seguaDMS, seguaAS);
//		eus.println("segua," + seguaDMS.toString().replace(',', ';') + "," + seguaAS.toString().replace(',', ';') + "," + seguaDefenderMaxEU);
		
//		// Preference degree results.
//		MultiSinglePureStrategyGame<NormalFormPayoff> preferenceDegreeNormalForm = new MultiPreferenceDegree(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
//		DOBSS preferenceDegreeDOBSS = new DOBSS(preferenceDegreeNormalForm);
//		preferenceDegreeDOBSS.solve();
//		MixedStrategy preferenceDegreeDMS = preferenceDegreeDOBSS.getDefenderMixedStrategy();
//		Map<AttackerType, Target> preferenceDegreeAS = getAttackerStrategiesTarget(pointValueGame, preferenceDegreeDMS);
//		double preferenceDegreeDefenderMaxEU = expectedUtilityTarget(pointValueGame, preferenceDegreeDMS, preferenceDegreeAS);
		
		// TBM results.
		MultiSingleProfileGame<NormalFormPayoff> tbmNormalForm = new TransferableBeliefModel(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		DOBSS tbmDOBSS = new DOBSS(tbmNormalForm);
		tbmDOBSS.solve();
		MixedStrategy tbmDMS = tbmDOBSS.getDefenderMixedStrategy();
		Map<AttackerType, Target> tbmAS = getAttackerStrategiesTarget(pointValueGame, tbmDMS);
		double tbmDefenderMaxEU = expectedUtilityTarget(pointValueGame, tbmDMS, tbmAS);
//		eus.println("tbm," + tbmDMS.toString().replace(',', ';') + "," + tbmAS.toString().replace(',', ';') + "," + tbmDefenderMaxEU);
		
		// Maximax results.
				MultiSingleProfileGame<NormalFormPayoff> maximaxNormalForm = new Maximax(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
				DOBSS maximaxDOBSS = new DOBSS(maximaxNormalForm);
				maximaxDOBSS.solve();
				MixedStrategy maximaxDMS = maximaxDOBSS.getDefenderMixedStrategy();
				Map<AttackerType, Target> maximaxAS = getAttackerStrategiesTarget(pointValueGame, maximaxDMS);
				double maximaxDefenderMaxEU = expectedUtilityTarget(pointValueGame, maximaxDMS, maximaxAS);
//				eus.println("maximax," + maximaxDMS.toString().replace(',', ';') + "," + maximaxAS.toString().replace(',', ';') + "," + maximaxDefenderMaxEU);
		
		// Hurwitz criterion results.
		Map<Double, MultiSingleProfileGame<NormalFormPayoff>> hurwitzNormalForms = new HashMap<Double, MultiSingleProfileGame<NormalFormPayoff>>();
		for(Double alpha : ALPHAS) {
			MultiSingleProfileGame<NormalFormPayoff> hutwitzNormalForm = new HurwiczCriterion(targetGame.toMultiSinglePureStrategyGame(), alpha).toNormalForm();
			hurwitzNormalForms.put(alpha, hutwitzNormalForm);
		}
		Map<Double, MixedStrategy> hurwitzDMS = new HashMap<Double, MixedStrategy>();
		Map<Double, Map<AttackerType, Target>> hurwitzAS = new HashMap<Double, Map<AttackerType, Target>>();
		Map<Double, Double> hurwitzDefenderMaxEU = new HashMap<Double, Double>();
		for(Entry<Double, MultiSingleProfileGame<NormalFormPayoff>> entry : hurwitzNormalForms.entrySet()) {
			double alpha = entry.getKey();
			MultiSingleProfileGame<NormalFormPayoff> hurwitzNormalForm = entry.getValue();
			DOBSS hurwitzDOBSS = new DOBSS(hurwitzNormalForm);
			hurwitzDOBSS.solve();
			hurwitzDMS.put(alpha, hurwitzDOBSS.getDefenderMixedStrategy());
			hurwitzAS.put(alpha, getAttackerStrategiesTarget(pointValueGame, hurwitzDMS.get(alpha)));
			hurwitzDefenderMaxEU.put(alpha, expectedUtilityTarget(pointValueGame, hurwitzDMS.get(alpha), hurwitzAS.get(alpha)));
//			eus.println("hurwitz_criterion(alpha=" + alpha + ")," + hurwitzDMS.get(alpha).toString().replace(',', ';') + "," + hurwitzAS.get(alpha).toString().replace(',', ';') + "," + hurwitzDefenderMaxEU.get(alpha));
		}
		
		// Gamma-maximin results.
		MultiSingleProfileGame<NormalFormPayoff> gammaMaximinNormalForm = new GammaMaximin(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		DOBSS gammaMaximinDOBSS = new DOBSS(gammaMaximinNormalForm);
		gammaMaximinDOBSS.solve();
		MixedStrategy gammaMaximinDMS = gammaMaximinDOBSS.getDefenderMixedStrategy();
		Map<AttackerType, Target> gammaMaximinAS = getAttackerStrategiesTarget(pointValueGame, gammaMaximinDMS);
		double gammaMaximinDefenderMaxEU = expectedUtilityTarget(pointValueGame, gammaMaximinDMS, gammaMaximinAS);
//		eus.println("gamma_maximin," + gammaMaximinDMS.toString().replace(',', ';') + "," + gammaMaximinAS.toString().replace(',', ';') + "," + gammaMaximinDefenderMaxEU);
		
		// OWA results.
		MultiSingleProfileGame<NormalFormPayoff> owaNormalForm = new OWABasedModel(targetGame.toMultiSinglePureStrategyGame()).toNormalForm();
		DOBSS owaDOBSS = new DOBSS(owaNormalForm);
		owaDOBSS.solve();
		MixedStrategy owaDMS = owaDOBSS.getDefenderMixedStrategy();
		Map<AttackerType, Target> owaAS = getAttackerStrategiesTarget(pointValueGame, owaDMS);
		double owaDefenderMaxEU = expectedUtilityTarget(pointValueGame, owaDMS, owaAS);
//		eus.println("owa," + owaDMS.toString().replace(',', ';') + "," + owaAS.toString().replace(',', ';') + "," + owaDefenderMaxEU);
		
		/*
		 * Calculate rankings.
		 */
		TreeSet<BigDecimal> orderedSet = new TreeSet<BigDecimal>();
		orderedSet.add(round(ourDefenderMaxEU));
		orderedSet.add(round(seguaDefenderMaxEU));
//		orderedSet.add(round(preferenceDegreeDefenderMaxEU));
		orderedSet.add(round(tbmDefenderMaxEU));
		orderedSet.add(round(maximaxDefenderMaxEU));
		for(Double alpha : ALPHAS) {
			orderedSet.add(round(hurwitzDefenderMaxEU.get(alpha)));
		}
		orderedSet.add(round(gammaMaximinDefenderMaxEU));
		orderedSet.add(round(owaDefenderMaxEU));
		
		int rank = 1;
		Map<String, Integer> ranking = new HashMap<String, Integer>();
		for(BigDecimal element : orderedSet.descendingSet()) {
			if(round(ourDefenderMaxEU).equals(element)) ranking.put("original", rank);
			if(round(seguaDefenderMaxEU).equals(element)) ranking.put("segua", rank);
//			if(round(preferenceDegreeDefenderMaxEU).equals(element)) ranking.put("preference_degree", rank);
			if(round(tbmDefenderMaxEU).equals(element)) ranking.put("tbm", rank);
			if(round(maximaxDefenderMaxEU).equals(element)) ranking.put("maximax", rank);
			for(Double alpha : ALPHAS) {
				if(round(hurwitzDefenderMaxEU.get(alpha)).equals(element)) ranking.put("hurwitz_criterion(alpha=" + alpha + ")", rank);
			}
			if(round(gammaMaximinDefenderMaxEU).equals(element)) ranking.put("gamma_maximin", rank);
			if(round(owaDefenderMaxEU).equals(element)) ranking.put("owa", rank);
			rank++;
		}
		/*
		 * End calculating rankings.
		 */
		
		eus.println("original," + dobssDefenderMixedStrategy.toString().replace(',', ';') + "," + ourAttackerPureStrategies.toString().replace(',', ';') + "," + ourDefenderMaxEU + "," + ranking.get("original"));
		eus.println("segua," + seguaDMS.toString().replace(',', ';') + "," + seguaAS.toString().replace(',', ';') + "," + seguaDefenderMaxEU + "," + ranking.get("segua"));
//		eus.println("preference_degree," + preferenceDegreeDMS.toString().replace(',', ';') + "," + preferenceDegreeAS.toString().replace(',', ';') + "," + preferenceDegreeDefenderMaxEU + "," + ranking.get("preference_degree"));
		eus.println("tbm," + tbmDMS.toString().replace(',', ';') + "," + tbmAS.toString().replace(',', ';') + "," + tbmDefenderMaxEU + "," + ranking.get("tbm"));
		eus.println("maximax," + maximaxDMS.toString().replace(',', ';') + "," + maximaxAS.toString().replace(',', ';') + "," + maximaxDefenderMaxEU + "," + ranking.get("maximax"));
		for(Double alpha : ALPHAS) {
			eus.println("hurwitz_criterion(alpha=" + alpha + ")," + hurwitzDMS.get(alpha).toString().replace(',', ';') + "," + hurwitzAS.get(alpha).toString().replace(',', ';') + "," + hurwitzDefenderMaxEU.get(alpha) + "," + ranking.get("hurwitz_criterion(alpha=" + alpha + ")"));
		}
		eus.println("gamma_maximin," + gammaMaximinDMS.toString().replace(',', ';') + "," + gammaMaximinAS.toString().replace(',', ';') + "," + gammaMaximinDefenderMaxEU + "," + ranking.get("gamma_maximin"));
		eus.println("owa," + owaDMS.toString().replace(',', ';') + "," + owaAS.toString().replace(',', ';') + "," + owaDefenderMaxEU + "," + ranking.get("owa"));
		
	}
	
	private BigDecimal round(double d) {
		int decimalPlaces = 5;
//		return new BigDecimal(String.format("%." + decimalPlaces + "g", d));
		return new BigDecimal(String.valueOf(d)).setScale(decimalPlaces, RoundingMode.HALF_UP);
	}
	
	public Map<AttackerType, Target> getAttackerStrategiesTarget(MultiSingleTargetGame<NormalFormPayoff> pointValue, MixedStrategy defMixedStrategy) throws Exception {
		return getAttackerStrategiesPureStrategy(pointValue.toMultiSinglePureStrategyGame(), defMixedStrategy);
	}
	
	public Map<AttackerType, Target> getAttackerStrategiesPureStrategy(MultiSingleProfileGame<NormalFormPayoff> pointValue, MixedStrategy defMixedStrategy) throws Exception {
		
		ArrayList<AdvancedSet<AttackerPureStrategy>> possibleStrategies = new ArrayList<AdvancedSet<AttackerPureStrategy>>();
		
		for(Entry<AttackerType, SingleProfileGame<NormalFormPayoff>> entry : pointValue.getSecurityGames().entrySet()) {
			AttackerType attackerType = entry.getKey();
			SingleProfileGame<NormalFormPayoff> spsg = entry.getValue();
			
			Double maxValue = null;
			AdvancedSet<AttackerPureStrategy> maxPossibleTargets = null;
			
			for(Target attTarget : pointValue.getTargets()) {
				double sum = 0;
				for(Target defTarget : pointValue.getTargets()) {
					PureStrategyProfile psp = new PureStrategyProfile(defTarget, attTarget);
					
					PlayerPairPayoffSingle<NormalFormPayoff> payoff = spsg.getPayoffs().get(psp);
					sum += defMixedStrategy.get(defTarget) * payoff.getAttacker().getDouble();
				}
				
				double deviation = 1e-2;
				
//				System.err.println(attackerType + "=" + attTarget + " := " + sum);
				
				if(maxValue == null) {
					maxValue = sum;
					maxPossibleTargets = new AdvancedSet<AttackerPureStrategy>(new AttackerPureStrategy(attackerType, attTarget));
				} else if(sum >= maxValue - deviation && sum <= maxValue + deviation) { // if sum \in [maxValue - deviation, maxValue + deviation]
					maxValue = sum;
					maxPossibleTargets.add(new AttackerPureStrategy(attackerType, attTarget));
				} else if(sum > maxValue + deviation) {
					maxValue = sum;
					maxPossibleTargets = new AdvancedSet<AttackerPureStrategy>(new AttackerPureStrategy(attackerType, attTarget));
				}
			}
			
			if(maxPossibleTargets == null || maxPossibleTargets.isEmpty()) {
				throw new Exception("error attacker strategies with maximum value");
			}
			
			possibleStrategies.add(maxPossibleTargets);
		}
		
//		System.err.println("possible attacker pure strategies: " + possibleStrategies);
		AdvancedSet<AdvancedSet<AttackerPureStrategy>> product = cartesianProduct(possibleStrategies);
		Double highestDefenderMaxEU = null;
		Map<AttackerType, Target> optimalAttackerPureStrategies = null;
		for(AdvancedSet<AttackerPureStrategy> set : product) {
			Map<AttackerType, Target> possible = new HashMap<AttackerType, Target>();
			for(AttackerPureStrategy aps : set) {
				possible.put(aps.getAttackerType(), aps.getTarget());
			}
			double defenderMaxEU = expectedUtilityPureStrategy(pointValue, defMixedStrategy, possible);
//			System.err.println(possible + " := " + defenderMaxEU);
			if(highestDefenderMaxEU == null || defenderMaxEU > highestDefenderMaxEU) {
				highestDefenderMaxEU = defenderMaxEU;
				optimalAttackerPureStrategies = possible;
			}
		}
		
		return optimalAttackerPureStrategies;
	}
	
	private class AttackerPureStrategy {
		
		public AttackerType attackerType;
		public Target target;
		
		public AttackerPureStrategy(AttackerType a, Target t) {
			attackerType= a;
			target = t;
		}
		
		public AttackerType getAttackerType() {
			return attackerType;
		}
		
		public Target getTarget() {
			return target;
		}
		
		@Override
		public String toString() {
			return attackerType.toString() + "="+ target.toString();
		}
		
	}
	
	public static AdvancedSet<AdvancedSet<AttackerPureStrategy>> cartesianProduct(ArrayList<AdvancedSet<AttackerPureStrategy>> sets) {
	    if(sets.size() < 2) {
	    	throw new IllegalArgumentException("Can't have a product of fewer than two sets (got " + sets.size() + ")");
	    }
	    return cartesianProduct(0, sets);
	}

	private static AdvancedSet<AdvancedSet<AttackerPureStrategy>> cartesianProduct(int index, ArrayList<AdvancedSet<AttackerPureStrategy>> sets) {
		AdvancedSet<AdvancedSet<AttackerPureStrategy>> ret = new AdvancedSet<AdvancedSet<AttackerPureStrategy>>();
	    if(index == sets.size()) {
	        ret.add(new AdvancedSet<AttackerPureStrategy>());
	    } else {
	        for(AttackerPureStrategy obj : sets.get(index)) {
	            for(AdvancedSet<AttackerPureStrategy> set : cartesianProduct(index+1, sets)) {
	                set.add(obj);
	                ret.add(set);
	            }
	        }
	    }
	    return ret;
	}
	
	public static double expectedUtilityTarget(MultiSingleTargetGame<NormalFormPayoff> pointValue, MixedStrategy defMixedStrategy, Map<AttackerType, Target> attackerStrategies) throws Exception {
		return expectedUtilityPureStrategy(pointValue.toMultiSinglePureStrategyGame(), defMixedStrategy, attackerStrategies);
	}
	
	public static double expectedUtilityPureStrategy(MultiSingleProfileGame<NormalFormPayoff> pointValue, MixedStrategy defMixedStrategy, Map<AttackerType, Target> attackerStrategies) throws Exception {
		
		double sum = 0;
		
		for(Map.Entry<Target, Double> defenderEntry : defMixedStrategy.entrySet()) {
			
			Target defenderStrategy = defenderEntry.getKey();
			double defenderStrategyProbability = defenderEntry.getValue();
			
			for(Map.Entry<AttackerType, Double> attackerEntry : pointValue.getAttackerProbabilities().entrySet()) {
				
				AttackerType attackerType = attackerEntry.getKey();
				double attackerProbability = attackerEntry.getValue();
				Target attackerStrategy = attackerStrategies.get(attackerType);
				
				PureStrategyProfile psp = new PureStrategyProfile(defenderStrategy, attackerStrategy);
				SingleProfileGame<NormalFormPayoff> game = pointValue.getSecurityGames().get(attackerType);
				PlayerPairPayoffSingle<NormalFormPayoff> payoff = game.getPayoffs().get(psp);
				
				sum += defenderStrategyProbability * attackerProbability * payoff.getDefender().getDouble();
				
			}
			
		}
		
		return sum;
		
	}
	
	public static MultiSingleTargetGame<NormalFormPayoff> toNormalFormGame(MultiSingleTargetGame<IntegerPayoff> sg) throws Exception {
		
		AdvancedSet<Target> targetset = sg.getTargets();
		Map<AttackerType, SingleTargetGame<NormalFormPayoff>> attackerGamesNew = new HashMap<AttackerType, SingleTargetGame<NormalFormPayoff>>();
		for(Entry<AttackerType, SingleTargetGame<IntegerPayoff>> entryOuter : sg.getSecurityGames().entrySet()) {
			SingleTargetGame<NormalFormPayoff> stg = new SingleTargetGame<NormalFormPayoff>(targetset);
			for(Entry<Target, PlayerPairPayoffPair<IntegerPayoff>> entryInner : entryOuter.getValue().getPayoffs().entrySet()) {
				PlayerPairPayoffPair<IntegerPayoff> payoffOld = entryInner.getValue();
				PlayerPairPayoffPair<NormalFormPayoff> payoffNew = new PlayerPairPayoffPair<NormalFormPayoff>(
						new PayoffPair<NormalFormPayoff>(
								payoffOld.getDefender().getNegative(),
								payoffOld.getDefender().getPositive()
						),
						new PayoffPair<NormalFormPayoff>(
								payoffOld.getAttacker().getNegative(),
								payoffOld.getAttacker().getPositive()
						)
				);
				stg.setPayoff(entryInner.getKey(), payoffNew);
			}
			attackerGamesNew.put(entryOuter.getKey(), stg);
		}
		return new MultiSingleTargetGame<NormalFormPayoff>(
				targetset,
				sg.getAttackerProbabilities(),
				attackerGamesNew
		);
		
	}
	
	public void part2() {
		
		try {
			for(int i = 0; i < 50; i++) {
				NegativeRange negative = new NegativeRange(-100, 0);
				PositiveRange positive = new PositiveRange(0, 100);
				
				System.out.print("i=" + i);
				part2Interval(i, "negative", Randomizer.randomInteger(2, 9), Randomizer.randomInteger(2, 9), negative, positive);
				part2Interval(i, "positive", Randomizer.randomInteger(2, 9), Randomizer.randomInteger(2, 9), negative, positive);
				part2PointValueLottery(i, "negative", Randomizer.randomInteger(2, 9), Randomizer.randomInteger(2, 9), negative, positive);
				part2PointValueLottery(i, "positive", Randomizer.randomInteger(2, 9), Randomizer.randomInteger(2, 9), negative, positive);
				part2IntervalLottery(i, "negative", Randomizer.randomInteger(2, 9), Randomizer.randomInteger(2, 9), negative, positive);
				part2IntervalLottery(i, "positive", Randomizer.randomInteger(2, 9), Randomizer.randomInteger(2, 9), negative, positive);
				System.out.println("done");
			}
			System.out.println("finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void part3(int count) {
		
		try {
			System.out.println(directory);
			
			for(int i = 0; i < count; i++) {
				System.out.print("i=" + i);
				
				int numTargets = Randomizer.randomInteger(2, 9);
				int numTypes = Randomizer.randomInteger(2, 9);
				NegativeRange negative = new NegativeRange(-100, 0);
				PositiveRange positive = new PositiveRange(0, 100);
				
				MultiSingleTargetGame<IntegerPayoff> originalIntegerGame = Randomizer.randomIntegerSingleTargetMultiGame(numTargets, numTypes, negative, positive);
				System.out.print(".");
				
				PrintWriter targets = new PrintWriter(directory + "step_1a/" + i + "-targets-point_value.txt", "UTF-8");
				targets.println(originalIntegerGame.toString());
				targets.close();
				System.out.print(".");
				
				PrintWriter pureStrategies = new PrintWriter(directory + "step_1b/" + i + "-pure_strategies-point_value.txt", "UTF-8");
				pureStrategies.println(originalIntegerGame.toMultiSinglePureStrategyGame().toString());
				pureStrategies.close();
				System.out.print(".");
				
				if(lotteryRestriction == LotteryRestriction.L1) {
					part3Interval(i, originalIntegerGame, negative, positive);
					System.out.print(".");
				}
				if(intervalRestriction == IntervalRestriction.I1) {
					part3PointValueLottery(i, originalIntegerGame, negative, positive);
					System.out.print(".");
				}
				part3IntervalLottery(i, originalIntegerGame, negative, positive);
				
				System.out.println("done");
			}
			System.out.println("finished");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public Experiments(int part, IntervalRestriction ir, LotteryRestriction lr) throws Exception {
		directory = BASE + "part_" + part + "/";
		
		intervalRestriction = ir;
		lotteryRestriction = lr;
		
		switch(intervalRestriction) {
			case I1:
				directory += "i1";
				break;
			case I2:
				directory += "i2";
				break;
			case I3:
				directory += "i3";
				break;
			case I4:
				directory += "i4";
				break;
			case I5:
				directory += "i5";
				break;
			default:
				throw new Exception("unsupported option");
		}
		
		directory += "_";
		
		switch(lotteryRestriction) {
			case L1:
				directory += "l1";
				break;
			case L2:
				directory += "l2";
				break;
			case L3:
				directory += "l3";
				break;
			default:
				throw new Exception("unsupported option");
		}
		
		directory += "/";
	}
	
	public static void main(String[] args) {
		try {
			int samples = 1000;
			
			ArrayList<IntervalRestriction> intervalRestrictions = new ArrayList<IntervalRestriction>();
			intervalRestrictions.add(IntervalRestriction.I1);
			intervalRestrictions.add(IntervalRestriction.I2);
			intervalRestrictions.add(IntervalRestriction.I3);
			intervalRestrictions.add(IntervalRestriction.I4);
			intervalRestrictions.add(IntervalRestriction.I5);
			
			ArrayList<LotteryRestriction> lotteryRestrictions = new ArrayList<LotteryRestriction>();
			lotteryRestrictions.add(LotteryRestriction.L1);
			lotteryRestrictions.add(LotteryRestriction.L2);
			lotteryRestrictions.add(LotteryRestriction.L3);
			
			for(IntervalRestriction ir : intervalRestrictions) {
				for(LotteryRestriction lr : lotteryRestrictions) {
					new Experiments(3, ir, lr).part3(samples);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
