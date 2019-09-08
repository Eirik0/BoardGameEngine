package bge.game.chess;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bge.game.chess.fen.ForsythEdwardsNotation;
import bge.game.chess.move.IChessMove;
import bge.igame.ArrayMoveList;
import bge.igame.MoveList;
import bge.perf.PerfTest;

public class ChessPositionPerfTest {
    @Test
    public void testCountAtDepths() {
        for (int depth = 0; depth <= 3; ++depth) {
            countAtDepth(depth, false);
        }
    }

    @Test
    @Disabled
    public void testCountAtDepthFour() {
        countAtDepth(4, true);
    }

    @Test
    @Disabled
    public void testCountAtDepthFive() {
        countAtDepth(5, true);
    }

    @Test
    public void testPositionIntegrityAtDepths() {
        for (int depth = 0; depth <= 2; ++depth) {
            checkIntegrityAtDepth(depth);
        }
    }

    @Test
    @Disabled
    public void testPositionIntegrityAtDepthThree() {
        checkIntegrityAtDepth(3);
    }

    @Test
    @Disabled
    public void testPositionIntegrityAtDepthFour() {
        checkIntegrityAtDepth(4);
    }

    private static void countAtDepth(int depth, boolean verbose) {
        List<ChessPerfTest> perfTests = loadChessPerfTests();
        long start = System.currentTimeMillis();
        long totalPositions = 0;
        for (ChessPerfTest perfTest : perfTests) {
            ChessPosition position = perfTest.position;
            long startPos = System.currentTimeMillis();
            long countPositions = PerfTest.countPositions(position, depth);
            if (verbose) {
                long posTime = System.currentTimeMillis() - startPos;
                long posPerSec = (long) (((double) countPositions / posTime) * 1000);
                System.out.println(perfTest.fen + "; D" + (depth + 1) + " " + countPositions + ", " + (posTime / 1000) + "s, pps= " + posPerSec);
            }
            if (perfTest.expectedPositions[depth] != countPositions) {
                System.out.println(depth + ": " + countPositions + " != " + perfTest.expectedPositions[depth] + " " + perfTest.fen);
                System.out.println(ChessFunctions.getBoardStr(position));
            }
            assertEquals(perfTest.expectedPositions[depth], countPositions, depth + ": " + perfTest.fen);
            totalPositions += countPositions;
        }
        long time = System.currentTimeMillis() - start;
        long posPerSec = (long) (((double) totalPositions / time) * 1000);
        System.out.println("Count Positions: depth= " + depth + ", totalPositions= " + totalPositions + ", time= " + time + "ms, pps= " + posPerSec);
    }

    private static void checkIntegrityAtDepth(int depth) {
        List<ChessPerfTest> perfTests = loadChessPerfTests();
        long start = System.currentTimeMillis();
        for (ChessPerfTest perfTest : perfTests) {
            checkPositionIntegrity(perfTest.position, depth);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Position Integrity: depth= " + depth + ", time= " + time + "ms");
    }

    private static void checkPositionIntegrity(ChessPosition position, int depth) {
        MoveList<IChessMove> possibleMoves = new ArrayMoveList<>(ChessGame.MAX_MOVES);
        if (depth == 0) {
            ChessPositionTest.assertPositionIntegrity(position);
            return;
        }
        ChessPosition positionCopy = position.createCopy();
        int i = 0;
        while (i < possibleMoves.size()) {
            IChessMove move = possibleMoves.get(i);
            position.makeMove(move);
            checkPositionIntegrity(position, depth - 1);
            position.unmakeMove(move);
            ChessPositionTest.assertPositionIntegrity(position);
            ChessPositionTest.assertPositionsEqual(positionCopy, position);
            ++i;
        }
    }

    private static List<ChessPerfTest> loadChessPerfTests() {
        List<ChessPerfTest> perfTests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(ChessPositionPerfTest.class.getResourceAsStream("/bge/game/chess/perftsuite.epd"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                perfTests.add(new ChessPerfTest(line));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return perfTests;
    }

    private static class ChessPerfTest {
        final String fen;
        final ChessPosition position;
        long[] expectedPositions;

        public ChessPerfTest(String epd) {
            String[] split = epd.split(" ;");
            fen = split[0];
            position = ForsythEdwardsNotation.stringToPosition(fen);
            expectedPositions = new long[split.length - 1];
            for (int i = 1; i < split.length; ++i) {
                expectedPositions[i - 1] = Long.parseLong(split[i].split(" ")[1]);
            }
        }
    }
}
