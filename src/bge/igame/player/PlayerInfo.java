package bge.igame.player;

import java.util.HashMap;
import java.util.Map;

import bge.analysis.IPositionEvaluator;
import bge.igame.IPosition;
import bge.igame.MoveListFactory;
import bge.main.GameRegistry;
import bge.strategy.IStrategy;
import bge.strategy.RandomMoveStrategy;
import bge.strategy.ts.ITreeSearcher;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.TreeSearchStrategy;
import bge.strategy.ts.forkjoin.ForkJoinTreeSearcher;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory;
import bge.strategy.ts.forkjoin.alphabeta.AlphaBetaPositionEvaluator;
import bge.strategy.ts.forkjoin.alphabeta.AlphaBetaQPositionEvaluator;
import bge.strategy.ts.forkjoin.alphabeta.ForkableAlphaBetaFactory;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimaxFactory;
import bge.strategy.ts.forkjoin.minmax.MinimaxPositionEvaluator;
import bge.strategy.ts.montecarlo.MonteCarloTreeSearcher;
import bge.strategy.ts.montecarlo.RandomMonteCarloChildren;
import bge.strategy.ts.montecarlo.WeightedMonteCarloChildren;

public class PlayerInfo {
    // * Strategy:
    //   - Random
    // * Strategy: ForkJoin
    //   * FJS: MinMax
    //     - PE: { PE1, ... }
    //     - threads: [1 ... ]
    //     - msPerMove [50 ...]
    //   * FJS: AlphaBeta
    //     ...
    //   * FJS: AlphaBetaQ
    //     ...
    // * Strategy: MonteCarlo
    //   * MCS: Not Weighted
    //     - PE: { PE1, ... }
    //     - simulations: [1 ... ]
    //     - msPerMove [50 ...]
    //   * MCS: Weighted
    //     ...
    public static final String KEY_ISTRATEGY = "KeyIStrategy";
    // Single-Core Minmax
    public static final String TS_RANDOM = "Random";
    public static final String TS_FORK_JOIN = "Fork Join";
    public static final String TS_MONTE_CARLO = "Monte Carlo";
    public static final String[] ALL_TREE_SEARCHERS = { TS_RANDOM, TS_FORK_JOIN, TS_MONTE_CARLO };

    public static final String KEY_FJ_STRATEGY = "KeyFJStrategy";
    public static final String FJ_MINMAX = "MinMax";
    public static final String FJ_ALPHA_BETA = "AlphaBeta";
    public static final String FJ_ALPHA_BETA_Q = "AlphaBetaQ";
    public static final String[] ALL_FJ_STRATEGIES = { FJ_MINMAX, FJ_ALPHA_BETA, FJ_ALPHA_BETA_Q };

    public static final String KEY_MC_STRATEGY = "KeyMCStrategy";
    public static final String MC_RANDOM = "Random";
    public static final String MC_WEIGHTED = "Weighted";
    public static final String[] ALL_MC_STRATEGIES = { MC_RANDOM, MC_WEIGHTED };

    public static final String KEY_EVALUATOR = "KeyEvaluator";
    public static final String KEY_NUM_THREADS = "KeyNumThreads";
    public static final String KEY_NUM_SIMULATIONS = "KeyNumSimulations";
    public static final String KEY_MS_PER_MOVE = "KeyMsPerMove";

    private final Map<String, String> optionsMap = new HashMap<>();

    public void setOption(String key, String value) {
        optionsMap.put(key, value);
    }

    public void setOption(String key, Integer value) {
        optionsMap.put(key, value.toString());
    }

    public String getOption(String key) {
        return optionsMap.get(key);
    }

    public Integer getOptionInt(String key) {
        String value = optionsMap.get(key);
        return value == null ? null : Integer.valueOf(value);
    }

    private <M> IStrategy<M> newStrategy(String gameName) {
        MoveListFactory<M> moveListFactory = GameRegistry.getMoveListFactory(gameName);
        IPositionEvaluator<M, IPosition<M>> positionEvaluator = GameRegistry.getPositionEvaluator(gameName, optionsMap.get(KEY_EVALUATOR));

        String iStrategy = optionsMap.get(KEY_ISTRATEGY);
        if (TS_RANDOM.equals(iStrategy)) {
            return new RandomMoveStrategy<>(moveListFactory);
        }
        ITreeSearcher<M, IPosition<M>> treeSearcher;
        if (TS_FORK_JOIN.equals(iStrategy)) {
            String fjStrategy = optionsMap.get(KEY_FJ_STRATEGY);
            int numThreads = getOptionInt(KEY_NUM_THREADS).intValue();
            ForkableTreeSearchFactory<M, IPosition<M>> forkableFactory;
            if (FJ_MINMAX.equals(fjStrategy)) {
                forkableFactory = new ForkableMinimaxFactory<>(new MinimaxPositionEvaluator<>(positionEvaluator, new MoveListProvider<>(moveListFactory)));
            } else if (FJ_ALPHA_BETA.equals(fjStrategy)) {
                forkableFactory = new ForkableAlphaBetaFactory<>(new AlphaBetaPositionEvaluator<>(positionEvaluator, new MoveListProvider<>(moveListFactory)));
            } else if (FJ_ALPHA_BETA_Q.equals(fjStrategy)) {
                forkableFactory = new ForkableAlphaBetaFactory<>(new AlphaBetaQPositionEvaluator<>(positionEvaluator, new MoveListProvider<>(moveListFactory)));
            } else {
                throw new IllegalStateException("Unknown fork join strategy: " + iStrategy);
            }
            treeSearcher = new ForkJoinTreeSearcher<>(forkableFactory, moveListFactory, numThreads);
        } else if (TS_MONTE_CARLO.equals(iStrategy)) {
            int numSimulations = getOptionInt(KEY_NUM_SIMULATIONS).intValue();
            String mcStrategy = optionsMap.get(KEY_MC_STRATEGY);
            int maxDepth = 500; // TODO this is defined for each game
            if (MC_RANDOM.equals(mcStrategy)) {
                treeSearcher = new MonteCarloTreeSearcher<>(new RandomMonteCarloChildren<>(0), positionEvaluator, moveListFactory, numSimulations, maxDepth);
            } else if (MC_WEIGHTED.equals(mcStrategy)) {
                treeSearcher = new MonteCarloTreeSearcher<>(new WeightedMonteCarloChildren<>(0), positionEvaluator, moveListFactory, numSimulations, maxDepth);
            } else {
                throw new IllegalStateException("Unknown monte carlo strategy " + mcStrategy);
            }
        } else {
            throw new IllegalStateException("Unknown tree searcher: " + iStrategy);
        }
        long msPerMove = Long.parseLong(optionsMap.get(KEY_MS_PER_MOVE));
        return new TreeSearchStrategy<>(treeSearcher, msPerMove, true);// TODO evaluate escape early
    }

    public ComputerPlayer newComputerPlayer(String gameName) {
        return new ComputerPlayer(newStrategy(gameName));
    }
}
