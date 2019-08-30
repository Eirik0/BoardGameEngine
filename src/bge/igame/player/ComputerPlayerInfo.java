package bge.igame.player;

import java.util.HashMap;
import java.util.Map;

import bge.analysis.IPositionEvaluator;
import bge.analysis.ITreeSearcher;
import bge.analysis.montecarlo.MonteCarloTreeSearcher;
import bge.analysis.montecarlo.RandomMonteCarloChildren;
import bge.analysis.montecarlo.WeightedMonteCarloChildren;
import bge.analysis.search.IterativeDeepeningTreeSearcher;
import bge.analysis.strategy.AlphaBetaQStrategy;
import bge.analysis.strategy.AlphaBetaStrategy;
import bge.analysis.strategy.IAlphaBetaStrategy;
import bge.analysis.strategy.MinimaxStrategy;
import bge.analysis.strategy.MoveListProvider;
import bge.igame.IPosition;
import bge.igame.MoveListFactory;
import bge.main.GameRegistry;

public class ComputerPlayerInfo {
    // * TS: ForkJoin
    //   * FJS: MinMax
    //     - PE: { PE1, ... }
    //     - threads: [1 ... ]
    //     - msPerMove [50 ...]
    //   * FJS: AlphaBeta
    //     ...
    //   * FJS: AlphaBetaQ
    //     ...
    // * TS: MonteCarlo
    //   * MCS: Not Weighted
    //     - PE: { PE1, ... }
    //     - simulations: [1 ... ]
    //     - msPerMove [50 ...]
    //   * MCS: Weighted
    //     ...
    public static final String KEY_TS = "KeyTS";
    // Single-Core Minmax
    public static final String TS_FORK_JOIN = "Fork Join";
    public static final String TS_MONTE_CARLO = "Monte Carlo";
    public static final String[] ALL_TREE_SEARCHERS = { TS_FORK_JOIN, TS_MONTE_CARLO };

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

    private static final Map<String, Map<String, IPositionEvaluator<?, ?>>> positionEvaluators = new HashMap<>(); // TODO Move to GameRegistry

    public static void registerPositionEvaluator(String gameName, String evaluatorName, IPositionEvaluator<?, ?> evaluator) {
        Map<String, IPositionEvaluator<?, ?>> evaluatorMap = positionEvaluators.get(gameName);
        if (evaluatorMap == null) {
            evaluatorMap = new HashMap<>(); // TODO consider ordering
            positionEvaluators.put(gameName, evaluatorMap);
        }
        evaluatorMap.put(evaluatorName, evaluator);
    }

    private final Map<String, String> optionsMap = new HashMap<>();

    public void setOption(String key, String value) {
        optionsMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <M> ITreeSearcher<M, IPosition<M>> newTreeSearcher(String gameName) {
        MoveListFactory<M> moveListFactory = GameRegistry.getMoveListFactory(gameName);
        String evaluatorName = optionsMap.get(KEY_EVALUATOR);
        IPositionEvaluator<?, ?> iPositionEvaluator = positionEvaluators.get(gameName).get(optionsMap.get(evaluatorName));
        IPositionEvaluator<M, IPosition<M>> positionEvaluator = (IPositionEvaluator<M, IPosition<M>>) iPositionEvaluator;

        String treeSearcher = optionsMap.get(KEY_TS);
        if (TS_FORK_JOIN.equals(treeSearcher)) {
            String fjStrategy = optionsMap.get(KEY_FJ_STRATEGY);
            int numThreads = Integer.parseInt(optionsMap.get(KEY_NUM_THREADS));
            IAlphaBetaStrategy<M, IPosition<M>> strategy;
            if (FJ_MINMAX.equals(fjStrategy)) {
                strategy = new MinimaxStrategy<>(positionEvaluator, new MoveListProvider<>(moveListFactory));
            } else if (FJ_ALPHA_BETA.equals(fjStrategy)) {
                strategy = new AlphaBetaStrategy<>(positionEvaluator, new MoveListProvider<>(moveListFactory));
            } else if (FJ_ALPHA_BETA_Q.equals(fjStrategy)) {
                strategy = new AlphaBetaQStrategy<>(positionEvaluator, new MoveListProvider<>(moveListFactory));
            } else {
                throw new IllegalStateException("Unknown fork join strategy: " + treeSearcher);
            }
            return new IterativeDeepeningTreeSearcher<>(strategy, moveListFactory, numThreads);
        } else if (TS_MONTE_CARLO.equals(treeSearcher)) {
            int numSimulations = Integer.parseInt(optionsMap.get(KEY_NUM_SIMULATIONS));
            String mcStrategy = optionsMap.get(KEY_MC_STRATEGY);
            int maxDepth = 100; // TODO this is defined for each game
            if (MC_RANDOM.equals(mcStrategy)) {
                return new MonteCarloTreeSearcher<>(new RandomMonteCarloChildren<>(0), positionEvaluator, moveListFactory, numSimulations, maxDepth);
            } else if (MC_WEIGHTED.equals(mcStrategy)) {
                return new MonteCarloTreeSearcher<>(new WeightedMonteCarloChildren<>(0), positionEvaluator, moveListFactory, numSimulations, maxDepth);
            } else {
                throw new IllegalStateException("Unknown monte carlo strategy " + mcStrategy);
            }
        } else {
            throw new IllegalStateException("Unknown tree searcher: " + treeSearcher);
        }
    }

    public ComputerPlayer newComputerPlayer(String gameName) {
        long msPerMove = Long.parseLong(optionsMap.get(KEY_MS_PER_MOVE));
        return new ComputerPlayer(newTreeSearcher(gameName), msPerMove, true); // TODO evaluate escape early
    }
}
