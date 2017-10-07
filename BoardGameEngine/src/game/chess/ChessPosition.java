package game.chess;

import java.util.ArrayList;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;
import game.chess.move.BasicChessMove;
import game.chess.move.EnPassantMove;
import game.chess.move.IChessMove;

public class ChessPosition implements IPosition<IChessMove, ChessPosition>, ChessConstants {
	public final int[][] squares;
	public int currentPlayer;

	public int castleState;
	public Coordinate enPassantSquare;
	// XXX 50 move counter
	// XXX 3 fold repetition
	// XXX check

	public ChessPosition() {
		this(ChessConstants.newInitialPosition(), TwoPlayers.PLAYER_1, INITIAL_CASTLE_STATE, null);
	}

	public ChessPosition(int[][] squares, int currentPlayer, int castleState, Coordinate enPassantSquare) {
		this.squares = squares;
		this.currentPlayer = currentPlayer;
		this.castleState = castleState;
		this.enPassantSquare = enPassantSquare;
	}

	@Override
	public List<IChessMove> getPossibleMoves() {
		int otherPlayer = TwoPlayers.otherPlayer(currentPlayer);
		List<IChessMove> possibleMoves = new ArrayList<>();
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				int piece = squares[y][x];
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

	private void addPawnMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer) {
		int direction = currentPlayer == TwoPlayers.PLAYER_1 ? 1 : -1;
		int startingPawnRank = currentPlayer == TwoPlayers.PLAYER_1 ? 1 : 6;
		if (squares[y + direction][x] == UNPLAYED) { // up 1
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x, y + direction), UNPLAYED, enPassantSquare));
		}
		if (y == startingPawnRank && squares[y + direction][x] == UNPLAYED && squares[y + direction + direction][x] == UNPLAYED) { // up 2 if on starting rank
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x, y + direction + direction), UNPLAYED, enPassantSquare);
			possibleMoves.add(new EnPassantMove(basicMove, Coordinate.valueOf(x, y + direction)));
		}
		if (x < BOARD_WIDTH - 1 && (squares[y + direction][x + 1] & otherPlayer) == otherPlayer) { // capture right
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 1, y + direction), squares[y + direction][x + 1], enPassantSquare));
		}
		if (x > 0 && (squares[y + direction][x - 1] & otherPlayer) == otherPlayer) { // capture left
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 1, y + direction), squares[y + direction][x - 1], enPassantSquare));
		}
		// XXX en passant capture
		// XXX queening
	}

	private void addKnightMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y) {
		// clockwise starting upper left
		if (x > 0 && y > 1 && (squares[y - 2][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 1, y - 2), squares[y - 2][x - 1], enPassantSquare));
		}
		if (x < BOARD_WIDTH - 1 && y > 1 && (squares[y - 2][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 1, y - 2), squares[y - 2][x + 1], enPassantSquare));
		}
		if (x < BOARD_WIDTH - 2 && y > 1 && (squares[y - 1][x + 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 2, y - 1), squares[y - 1][x + 2], enPassantSquare));
		}
		if (x < BOARD_WIDTH - 2 && y < BOARD_WIDTH - 1 && (squares[y + 1][x + 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 2, y + 1), squares[y + 1][x + 2], enPassantSquare));
		}
		if (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 2 && (squares[y + 2][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 1, y + 2), squares[y + 2][x + 1], enPassantSquare));
		}
		if (x > 0 && y < BOARD_WIDTH - 2 && (squares[y + 2][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 1, y + 2), squares[y + 2][x - 1], enPassantSquare));
		}
		if (x > 1 && y < BOARD_WIDTH - 1 && (squares[y + 1][x - 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 2, y + 1), squares[y + 1][x - 2], enPassantSquare));
		}
		if (x > 1 && y > 0 && (squares[y - 1][x - 2] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 2, y - 1), squares[y - 1][x - 2], enPassantSquare));
		}
	}

	private void addBishopMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer) {
		int bx = x;
		int by = y;
		while (bx < BOARD_WIDTH - 1 && by < BOARD_WIDTH - 1 && squares[++by][++bx] == UNPLAYED) { // down right
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
		bx = x;
		by = y;
		while (bx > 0 && by < BOARD_WIDTH - 1 && squares[++by][--bx] == UNPLAYED) { // down left
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by > 0 && squares[--by][++bx] == UNPLAYED) { // up right
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
		bx = x;
		by = y;
		while (bx > 0 && by > 0 && squares[--by][--bx] == UNPLAYED) { // up left
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
	}

	private void addRookMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer) {
		// XXX castle breaking
		int bx = x;
		int by = y;
		while (bx > 0 && squares[by][--bx] == UNPLAYED) { // left
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && squares[by][++bx] == UNPLAYED) { // right
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
		bx = x;
		by = y;
		while (by > 0 && squares[--by][bx] == UNPLAYED) { // up
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
		bx = x;
		by = y;
		while (by < BOARD_WIDTH - 1 && squares[++by][bx] == UNPLAYED) { // down
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare));
		}
	}

	private void addKingMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y) {
		// XXX castle
		// XXX castle breaking
		// clockwise starting upper left
		if (x > 0 && y > 0 && (squares[y - 1][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 1, y - 1), squares[y - 1][x - 1], enPassantSquare));
		}
		if (y > 0 && (squares[y - 1][x] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x, y - 1), squares[y - 1][x], enPassantSquare));
		}
		if (x < BOARD_WIDTH - 1 && y > 0 && (squares[y - 1][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 1, y - 1), squares[y - 1][x + 1], enPassantSquare));
		}
		if (x < BOARD_WIDTH - 1 && (squares[y][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 1, y), squares[y][x + 1], enPassantSquare));
		}
		if (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && (squares[y + 1][x + 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x + 1, y + 1), squares[y + 1][x + 1], enPassantSquare));
		}
		if (y < BOARD_WIDTH - 1 && (squares[y + 1][x] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x, y + 1), squares[y + 1][x], enPassantSquare));
		}
		if (x > 0 && y < BOARD_WIDTH - 1 && (squares[y + 1][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 1, y + 1), squares[y + 1][x - 1], enPassantSquare));
		}
		if (x > 0 && (squares[y][x - 1] & currentPlayer) != currentPlayer) {
			possibleMoves.add(new BasicChessMove(from, Coordinate.valueOf(x - 1, y), squares[y][x - 1], enPassantSquare));
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(IChessMove move) {
		move.applyMove(this);
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(IChessMove move) {
		move.unapplyMove(this);
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public ChessPosition createCopy() {
		int[][] squaresCopy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int y = 0; y < squares.length; ++y) {
			System.arraycopy(squares[y], 0, squaresCopy[y], 0, BOARD_WIDTH);
		}
		return new ChessPosition(squaresCopy, currentPlayer, castleState, enPassantSquare);
	}
}
