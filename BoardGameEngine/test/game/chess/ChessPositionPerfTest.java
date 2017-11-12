package game.chess;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import game.chess.fen.ForsythEdwardsNotation;
import game.chess.move.IChessMove;

public class ChessPositionPerfTest {
	@Test
	public void testCountAtDepths() {
		for (int depth = 0; depth <= 3; ++depth) {
			countAtDepth(depth);
		}
	}

	@Test
	@Ignore
	public void testCountAtDepthFour() {
		countAtDepth(4);
	}

	@Test
	@Ignore
	public void testCountAtDepthFive() {
		countAtDepth(5);
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

	private static void countAtDepth(int depth) {
		List<PerfTest> perfTests = loadPerfTests();
		long start = System.currentTimeMillis();
		long totalPositions = 0;
		for (PerfTest perfTest : perfTests) {
			long countPositions = countPositions(perfTest.position, depth);
			if (perfTest.expectedPositions[depth] != countPositions) {
				System.out.println(depth + ": " + countPositions + " != " + perfTest.expectedPositions[depth] + " " + perfTest.fen);
				System.out.println(ChessPositionTest.getBoardStr(perfTest.position));
			}
			assertEquals(depth + ": " + perfTest.fen, perfTest.expectedPositions[depth], countPositions);
			totalPositions += countPositions;
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("Count Positions: depth= " + depth + ", totalPositions= " + totalPositions + ", time= " + time + "ms");
	}

	private static long countPositions(ChessPosition position, int depth) {
		List<IChessMove> possibleMoves = position.getPossibleMoves();
		if (depth == 0) {
			return possibleMoves.size();
		}
		long sum = 0;
		for (IChessMove move : possibleMoves) {
			position.makeMove(move);
			sum += countPositions(position, depth - 1);
			position.unmakeMove(move);
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
		List<IChessMove> possibleMoves = position.getPossibleMoves();
		if (depth == 0) {
			ChessPositionTest.assertPositionIntegrity(position);
			return;
		}
		ChessPosition positionCopy = position.createCopy();
		for (IChessMove move : possibleMoves) {
			position.makeMove(move);
			checkPositionIntegrity(position, depth - 1);
			position.unmakeMove(move);
			ChessPositionTest.assertPositionIntegrity(position);
			ChessPositionTest.assertPositionsEqual(positionCopy, position);
		}
	}

	private static List<PerfTest> loadPerfTests() {
		List<PerfTest> perfTests = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ChessPositionPerfTest.class.getResourceAsStream("/game/chess/perftsuite.epd")))) {
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
