package game.chess;

import java.util.ArrayList;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;

public class ChessPosition implements IPosition<ChessMove, ChessPosition>, ChessConstants {
	final int[][] cells;
	int currentPlayer;

	public ChessPosition() {
		this(ChessConstants.newInitialPosition(), TwoPlayers.PLAYER_1);
	}

	public ChessPosition(int[][] cells, int currentPlayer) {
		this.cells = cells;
		this.currentPlayer = currentPlayer;
	}

	@Override
	public List<ChessMove> getPossibleMoves() {
		int otherPlayer = TwoPlayers.otherPlayer(currentPlayer);
		List<ChessMove> possibleMoves = new ArrayList<>();
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				int piece = cells[y][x];
				if ((piece & currentPlayer) == currentPlayer) {
					Coordinate from = Coordinate.valueOf(x, y);
					// XXX pins
					if ((piece & PAWN) == PAWN) {
						addPawnMoves(possibleMoves, from, x, y, otherPlayer);
					} else if ((piece & KNIGHT) == KNIGHT) {
						addKnightMoves(possibleMoves, from, x, y);
					} else if ((piece & BISHOP) == BISHOP) {
						addBishopMoves(possibleMoves, from, x, y, otherPlayer);
					} else if ((piece & ROOK) == ROOK) {
						addRookMoves(possibleMoves, from, x, y, otherPlayer);
					} else if ((piece & QUEEN) == QUEEN) {
						addBishopMoves(possibleMoves, from, x, y, otherPlayer);
						addRookMoves(possibleMoves, from, x, y, otherPlayer);
					} else if ((piece & KING) == KING) {
						addKingMoves(possibleMoves, from, x, y);
					}
				}
			}
		}
		return possibleMoves;
	}

	private void addPawnMoves(List<ChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer) {
		int direction = currentPlayer == TwoPlayers.PLAYER_1 ? 1 : -1;
		int startingPawnRank = currentPlayer == TwoPlayers.PLAYER_1 ? 1 : 6;
		if (cells[y + direction][x] == UNPLAYED) { // up 1
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x, y + direction), this));
		}
		if (y == startingPawnRank && cells[y + direction + direction][x] == UNPLAYED) { // up 2 if on starting rank
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x, y + direction + direction), this));
		}
		if (x < BOARD_WIDTH - 1 && (cells[y + direction][x + 1] & otherPlayer) == otherPlayer) { // capture right
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 1, y + direction), this));
		}
		if (x > 0 && (cells[y + direction][x - 1] & otherPlayer) == otherPlayer) { // capture left
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 1, y + direction), this));
		}
		// XXX en passant
		// XXX queening
	}

	private void addKnightMoves(List<ChessMove> possibleMoves, Coordinate from, int x, int y) {
		// clockwise starting upper left
		if (x > 0 && y > 1 && (cells[y - 2][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 1, y - 2), this));
		}
		if (x < BOARD_WIDTH - 1 && y > 1 && (cells[y - 2][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 1, y - 2), this));
		}
		if (x < BOARD_WIDTH - 2 && y > 1 && (cells[y - 1][x + 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 2, y - 1), this));
		}
		if (x < BOARD_WIDTH - 2 && y < BOARD_WIDTH - 1 && (cells[y + 1][x + 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 2, y + 1), this));
		}
		if (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 2 && (cells[y + 2][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 1, y + 2), this));
		}
		if (x > 0 && y < BOARD_WIDTH - 2 && (cells[y + 2][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 1, y + 2), this));
		}
		if (x > 1 && y < BOARD_WIDTH - 1 && (cells[y + 1][x - 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 2, y + 1), this));
		}
		if (x > 1 && y > 0 && (cells[y - 1][x - 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 2, y - 1), this));
		}
	}

	private void addBishopMoves(List<ChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer) {
		int bx = x;
		int by = y;
		while (bx < BOARD_WIDTH - 1 && by < BOARD_WIDTH - 1 && cells[++by][++bx] == UNPLAYED) { // down right
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		bx = x;
		by = y;
		while (bx > 0 && by < BOARD_WIDTH - 1 && cells[++by][--bx] == UNPLAYED) { // down left
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by > 0 && cells[--by][++bx] == UNPLAYED) { // up right
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		bx = x;
		by = y;
		while (bx > 0 && by > 0 && cells[--by][--bx] == UNPLAYED) { // up left
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
	}

	private void addRookMoves(List<ChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer) {
		int bx = x;
		int by = y;
		while (bx > 0 && cells[by][--bx] == UNPLAYED) { // left
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && cells[by][++bx] == UNPLAYED) { // right
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		bx = x;
		by = y;
		while (by > 0 && cells[--by][bx] == UNPLAYED) { // up
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		bx = x;
		by = y;
		while (by < BOARD_WIDTH - 1 && cells[++by][bx] == UNPLAYED) { // down
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
		if ((cells[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(bx, by), this));
		}
	}

	private void addKingMoves(List<ChessMove> possibleMoves, Coordinate from, int x, int y) {
		// XXX castle
		// clockwise starting upper left
		if (x > 0 && y > 0 && (cells[y - 1][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 1, y - 1), this));
		}
		if (y > 0 && (cells[y - 1][x] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x, y - 1), this));
		}
		if (x < BOARD_WIDTH - 1 && y > 0 && (cells[y - 1][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 1, y - 1), this));
		}
		if (x < BOARD_WIDTH - 1 && (cells[y][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 1, y), this));
		}
		if (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && (cells[y + 1][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x + 1, y + 1), this));
		}
		if (y < BOARD_WIDTH - 1 && (cells[y + 1][x] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x, y + 1), this));
		}
		if (x > 0 && y < BOARD_WIDTH - 1 && (cells[y + 1][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 1, y + 1), this));
		}
		if (x > 0 && (cells[y][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new ChessMove(from, Coordinate.valueOf(x - 1, y), this));
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(ChessMove move) {
		cells[move.to.y][move.to.x] = cells[move.from.y][move.from.x];
		cells[move.from.y][move.from.x] = UNPLAYED;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(ChessMove move) {
		// TODO Auto-generated method stub
	}

	@Override
	public ChessPosition createCopy() {
		int[][] cellsCopy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int y = 0; y < cells.length; ++y) {
			System.arraycopy(cells[y], 0, cellsCopy[y], 0, BOARD_WIDTH);
		}
		return new ChessPosition(cellsCopy, currentPlayer);
	}
}
