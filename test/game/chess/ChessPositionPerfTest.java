package game.chess;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import game.ArrayMoveList;
import game.IPosition;
import game.MoveList;
import game.chess.fen.ForsythEdwardsNotation;
import game.chess.move.IChessMove;
import game.sudoku.SudokuGame;

public class ChessPositionPerfTest {
	@Test
	public void testCountAtDepths() {
		for (int depth = 0; depth <= 3; ++depth) {
			countAtDepth(depth, false);
		}
	}

	@Test
	@Ignore
	public void testCountAtDepthFour() {
		countAtDepth(4, true);
	}

	@Test
	@Ignore
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
	@Ignore
	public void testPositionIntegrityAtDepthThree() {
		checkIntegrityAtDepth(3);
	}

	@Test
	@Ignore
	public void testPositionIntegrityAtDepthFour() {
		checkIntegrityAtDepth(4);
	}

	private static void countAtDepth(int depth, boolean verbose) {
		List<PerfTest> perfTests = loadPerfTests();
		long start = System.currentTimeMillis();
		long totalPositions = 0;
		for (PerfTest perfTest : perfTests) {
			ChessPosition position = perfTest.position;
			long startPos = System.currentTimeMillis();
			long countPositions = countPositions(position, depth);
			if (verbose) {
				long posTime = System.currentTimeMillis() - startPos;
				long posPerSec = (long) (((double) countPositions / posTime) * 1000);
				System.out.println(perfTest.fen + "; D" + (depth + 1) + " " + countPositions + ", " + (posTime / 1000) + "s, pps= " + posPerSec);
			}
			if (perfTest.expectedPositions[depth] != countPositions) {
				System.out.println(depth + ": " + countPositions + " != " + perfTest.expectedPositions[depth] + " " + perfTest.fen);
				System.out.println(ChessFunctions.getBoardStr(position));
			}
			assertEquals(depth + ": " + perfTest.fen, perfTest.expectedPositions[depth], countPositions);
			totalPositions += countPositions;
		}
		long time = System.currentTimeMillis() - start;
		long posPerSec = (long) (((double) totalPositions / time) * 1000);
		System.out.println("Count Positions: depth= " + depth + ", totalPositions= " + totalPositions + ", time= " + time + "ms, pps= " + posPerSec);
	}

	public static <M, P extends IPosition<M>> long countPositions(P position, int depth) {
		MoveList<M> possibleMoves = new ArrayMoveList<>(SudokuGame.MAX_MOVES);
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

	private static void checkIntegrityAtDepth(int depth) {
		List<PerfTest> perfTests = loadPerfTests();
		long start = System.currentTimeMillis();
		for (PerfTest perfTest : perfTests) {
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

	private static List<PerfTest> loadPerfTests() {
		List<PerfTest> perfTests = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ChessPositionPerfTest.class.getResourceAsStream("/game/chess/perftsuite.epd"), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				perfTests.add(new PerfTest(line));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return perfTests;
	}

	static class PerfTest {
		final String fen;
		final ChessPosition position;
		long[] expectedPositions;

		public PerfTest(String epd) {
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
