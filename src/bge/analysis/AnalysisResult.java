package bge.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import bge.game.chess.ChessConstants;

public class AnalysisResult<M> {
    public static final double WIN = Double.POSITIVE_INFINITY;
    public static final double LOSS = Double.NEGATIVE_INFINITY;
    public static final double DRAW = Double.NaN;
    public static final double WIN_INT = Integer.MAX_VALUE;
    public static final double LOSS_INT = -Integer.MAX_VALUE;

    private static final int MAX_POSSIBLE_MOVES = ChessConstants.MAX_MOVES;

    private final int player;

    private final List<MoveWithScore<M>> allValidMoves = new ArrayList<>();
    private final List<MoveWithScore<M>> wonAndDrawnMoves = new ArrayList<>();
    private final List<MoveWithScore<M>> lostMoves = new ArrayList<>();

    private MoveWithScore<M> bestMove;

    private boolean searchComplete = false;

    public AnalysisResult(int player) {
        this.player = player;
    }

    public void addMoveWithScore(MoveWithScore<M> moveWithScore) {
        allValidMoves.add(moveWithScore);
        double score = moveWithScore.score;
        if (bestMove == null || AnalysisResult.isGreater(score, bestMove.score)) {
            bestMove = moveWithScore;
        }
        if (isWin(score) || isDraw(score)) {
            wonAndDrawnMoves.add(moveWithScore);
        } else if (isLoss(score)) {
            lostMoves.add(moveWithScore);
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

    public AnalysisResult<M> mergeWith(AnalysisResult<M> resultToMerge) {
        AnalysisResult<M> mergedResult = new AnalysisResult<>(player);
        for (MoveWithScore<M> moveWithScore : resultToMerge.allValidMoves) {
            mergedResult.addMoveWithScore(moveWithScore);
        }
        for (MoveWithScore<M> moveWithScore : allValidMoves) {
            boolean found = false;
            for (MoveWithScore<M> mergeMoveWithScore : mergedResult.allValidMoves) {
                if (moveWithScore.move.equals(mergeMoveWithScore.move)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                mergedResult.addMoveWithScore(moveWithScore);
            }
        }
        return mergedResult;
    }

    public List<MoveWithScore<M>> getMovesWithScore() {
        return new ArrayList<>(allValidMoves);
    }

    public MoveWithScore<M> getBestMove(int currentPlayer) {
        if (bestMove == null) {
            return null;
        }
        return isDraw(bestMove.score) && !isDecided() ? new MoveWithScore<>(bestMove.move, 0.0) : bestMove.transform(player == currentPlayer);
    }

    public List<M> getBestMoves() {
        if (bestMove == null) {
            return Collections.emptyList();
        }
        List<M> bestMoves = new ArrayList<>();
        double maxScore = bestMove.score;
        for (MoveWithScore<M> moveWithScore : allValidMoves) {
            double score = moveWithScore.score;
            if (maxScore == score || isDraw(maxScore) && isDraw(score)) {
                bestMoves.add(moveWithScore.move);
            }
        }
        return bestMoves;
    }

    public List<MoveWithScore<M>> getDecidedMoves() {
        List<MoveWithScore<M>> decidedMoves = new ArrayList<>(wonAndDrawnMoves);
        decidedMoves.addAll(lostMoves);
        return decidedMoves;
    }

    public boolean isWin() {
        return bestMove != null && isWin(bestMove.score);
    }

    public boolean isLoss() {
        return bestMove != null && isLoss(bestMove.score);
    }

    public boolean onlyOneMove() {
        return allValidMoves.size() == lostMoves.size() + 1;
    }

    public boolean isDecided() {
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
        Iterator<MoveWithScore<M>> moveScoreIter = allValidMoves.iterator();
        while (moveScoreIter.hasNext()) {
            MoveWithScore<M> moveWithScore = moveScoreIter.next();
            sb.append(moveWithScore);
            if (moveScoreIter.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
