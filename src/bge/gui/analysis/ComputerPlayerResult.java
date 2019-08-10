package bge.gui.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bge.analysis.AnalysisResult;
import bge.analysis.MoveAnalysis;

public class ComputerPlayerResult {
    final List<ObservedMoveWithScore> moves;
    final Integer depth;
    final boolean isDecided;

    public ComputerPlayerResult(AnalysisResult<Object> analysisResult, Map<Object, MoveAnalysis> partialResults, int depth) {
        if (analysisResult == null && partialResults.size() == 0) {
            moves = null;
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

    private static void addMovesToMap(Map<String, ObservedMoveWithScore> moveMap, Map<Object, MoveAnalysis> movesWithScore, boolean partial) {
        for (Entry<Object, MoveAnalysis> moveWithScore : movesWithScore.entrySet()) {
            String moveString = moveWithScore.getKey() == null ? "-" : moveWithScore.getKey().toString();
            moveMap.put(moveString, new ObservedMoveWithScore(moveString, moveWithScore.getValue().score, partial));
        }
    }
}
