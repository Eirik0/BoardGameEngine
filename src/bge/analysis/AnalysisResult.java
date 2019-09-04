package bge.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import bge.game.chess.ChessConstants;

public class AnalysisResult<M> {
    public static final double WIN = Double.POSITIVE_INFINITY;
    public static final double LOSS = Double.NEGATIVE_INFINITY;
    public static final double DRAW = Double.NaN;
    public static final double WIN_INT = Integer.MAX_VALUE;
    public static final double LOSS_INT = -Integer.MAX_VALUE;

    private static final int MAX_POSSIBLE_MOVES = ChessConstants.MAX_MOVES;

    private final int player;

    private final Map<M, Double> allValidMoves = new LinkedHashMap<>();
    private final Map<M, Double> wonAndDrawnMoves = new HashMap<>();
    private final Map<M, Double> lostMoves = new HashMap<>();
    private final Set<M> invalidMoves = new HashSet<>();

    private AnalyzedMove<M> bestMove;

    private boolean searchComplete = false;

    public AnalysisResult(int player) {
        this.player = player;
    }

    public AnalysisResult(int player, M move, double score) {
        this(player);
        addMoveWithScore(move, score, true);
    }

    public void addMoveWithScore(M move, double score) {
        addMoveWithScore(move, score, true);
    }

    public synchronized void addMoveWithScore(M move, double score, boolean isValid) {
        if (!isValid) {
            invalidMoves.add(move);
            return;
        }
        allValidMoves.put(move, score);
        if (bestMove == null || AnalysisResult.isGreater(score, bestMove.score)) {
            bestMove = new AnalyzedMove<>(move, score);
        }
        if (isWin(score) || isDraw(score)) {
            wonAndDrawnMoves.put(move, score);
        } else if (isLoss(score)) {
            lostMoves.put(move, score);
        }
    }

    public int getPlayer() {
        return player;
    }

    public void searchCompleted() {
        searchComplete = true;
    }

    public boolean isSearchComplete() {
        return searchComplete;
    }

    public synchronized AnalysisResult<M> mergeWith(AnalysisResult<M> resultToMerge) {
        AnalysisResult<M> mergedResult = new AnalysisResult<>(player);
        Map<M, Double> mergedMoveMap = new HashMap<>(allValidMoves);
        mergedMoveMap.putAll(resultToMerge.allValidMoves);
        for (Entry<M, Double> moveWithScore : mergedMoveMap.entrySet()) {
            mergedResult.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue());
        }
        return mergedResult;
    }

    public synchronized Map<M, Double> getMovesWithScore() {
        return new LinkedHashMap<>(allValidMoves);
    }

    public synchronized AnalyzedMove<M> getBestMove(int currentPlayer) {
        if (bestMove == null) {
            return null;
        }
        return isDraw(bestMove.score) && !isDecided() ? new AnalyzedMove<>(bestMove.move, 0.0) : bestMove.transform(player == currentPlayer);
    }

    public synchronized List<M> getBestMoves() {
        if (bestMove == null) {
            return Collections.emptyList();
        }
        List<M> bestMoves = new ArrayList<>();
        double maxScore = bestMove.score;
        for (Entry<M, Double> moveWithScore : allValidMoves.entrySet()) {
            double score = moveWithScore.getValue();
            if (maxScore == score || isDraw(maxScore) && isDraw(score)) {
                bestMoves.add(moveWithScore.getKey());
            }
        }
        return bestMoves;
    }

    public synchronized Map<M, Double> getDecidedMoves() {
        Map<M, Double> decidedMoves = new HashMap<>(wonAndDrawnMoves);
        decidedMoves.putAll(lostMoves);
        return decidedMoves;
    }

    public synchronized Set<M> getInvalidMoves() {
        return invalidMoves;
    }

    public synchronized boolean isWin() {
        return bestMove != null && isWin(bestMove.score);
    }

    public synchronized boolean isLoss() {
        return bestMove != null && isLoss(bestMove.score);
    }

    public synchronized boolean onlyOneMove() {
        return allValidMoves.size() == lostMoves.size() + 1;
    }

    public synchronized boolean isDecided() {
        return allValidMoves.size() > 0 && wonAndDrawnMoves.size() + lostMoves.size() == allValidMoves.size();
    }

    public static boolean isGreater(double l, double r) {
        return l > r || (r < 0 && isDraw(l)) || (l >= 0 && isDraw(r));
    }

    public static boolean isWin(double score) {
        return score == WIN || score > WIN_INT - MAX_POSSIBLE_MOVES;
    }

    public static boolean isLoss(double score) {
        return score == LOSS || score < LOSS_INT + MAX_POSSIBLE_MOVES;
    }

    public static boolean isDraw(double score) {
        return score != score;
    }

    public static boolean isGameOver(double score) {
        return isWin(score) || isLoss(score) || isDraw(score);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<M, Double>> moveScoreIter = allValidMoves.entrySet().iterator();
        while (moveScoreIter.hasNext()) {
            Entry<M, Double> moveWithScore = moveScoreIter.next();
            sb.append(AnalyzedMove.toString(moveWithScore.getKey(), moveWithScore.getValue()));
            if (moveScoreIter.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
