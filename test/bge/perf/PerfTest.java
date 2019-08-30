package bge.perf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bge.game.sudoku.SudokuConstants;
import bge.igame.ArrayMoveList;
import bge.igame.IPosition;
import bge.igame.MoveList;

public class PerfTest {
    public static <M, P extends IPosition<M>> long countPositions(P position, int depth) {
        MoveList<M> possibleMoves = new ArrayMoveList<>(SudokuConstants.MAX_MOVES);
        position.getPossibleMoves(possibleMoves);
        if (depth == 0) {
            return possibleMoves.size();
        }
        long sum = 0;
        int i = 0;
        while (i < possibleMoves.size()) {
            M move = possibleMoves.get(i);
            position.makeMove(move);
            sum += countPositions(position, depth - 1);
            position.unmakeMove(move);
            ++i;
        }
        return sum;
    }

    public static <M> void countPos(IPosition<M> position, int depth, long expectedPositions) {
        long startPos = System.currentTimeMillis();
        long countPositions = countPositions(position, depth);
        long posTime = System.currentTimeMillis() - startPos;
        long posPerSec = (long) (((double) countPositions / posTime) * 1000);
        System.out.println(position.getClass().getSimpleName() + "; D" + (depth + 1) + " " + countPositions + ", " + posTime + "ms, pps= " + posPerSec);
        assertEquals(expectedPositions, countPositions);
    }
}
