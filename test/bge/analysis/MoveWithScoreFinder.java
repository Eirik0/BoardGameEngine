package bge.analysis;

import java.util.List;

public class MoveWithScoreFinder {
    public static <M> MoveWithScore<M> find(List<MoveWithScore<M>> movesWithScore, M move) {
        for (MoveWithScore<M> moveWithScore : movesWithScore) {
            if (move.equals(moveWithScore.move)) {
                return moveWithScore;
            }
        }
        return null;
    }
}
