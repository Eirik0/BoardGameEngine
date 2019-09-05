package bge.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyResult {
    public final List<ObservedMoveWithScore> moves;
    public final Integer depth;
    public final boolean isDecided;

    public <M> StrategyResult(AnalysisResult<M> analysisResult, List<MoveWithScore<M>> partialResults, int depth) {
        if (analysisResult == null && partialResults.size() == 0) {
            moves = Collections.emptyList();
            isDecided = false;
        } else {
            Map<String, ObservedMoveWithScore> moveMap = new HashMap<>();
            if (analysisResult != null) {
                addMovesToMap(moveMap, analysisResult.getMovesWithScore(), false);
            }
            addMovesToMap(moveMap, partialResults, true);
            moves = new ArrayList<>(moveMap.values());
            Collections.sort(moves, (move1, move2) -> {
                if (move1.score == move2.score || (AnalysisResult.isDraw(move1.score) && AnalysisResult.isDraw(move2.score))) {
                    return move1.moveString.compareTo(move2.moveString);
                }
                return AnalysisResult.isGreater(move1.score, move2.score) ? -1 : 1;
            });
            boolean decided = true;
            for (ObservedMoveWithScore moveWithScore : moves) {
                if (!AnalysisResult.isGameOver(moveWithScore.score)) {
                    decided = false;
                    break;
                }
            }
            isDecided = decided && analysisResult != null && moves.size() == analysisResult.getMovesWithScore().size();
        }
        this.depth = Integer.valueOf(depth);
    }

    private static <M> void addMovesToMap(Map<String, ObservedMoveWithScore> moveMap, List<MoveWithScore<M>> movesWithScore, boolean partial) {
        for (MoveWithScore<?> moveWithScore : movesWithScore) {
            String moveString = moveWithScore.move == null ? "-" : moveWithScore.move.toString();
            moveMap.put(moveString, new ObservedMoveWithScore(moveString, moveWithScore.score, partial));
        }
    }
}
