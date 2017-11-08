package game.chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;
import game.chess.move.BasicChessMove;
import game.chess.move.CastleBreakingMove;
import game.chess.move.CastleMove;
import game.chess.move.EnPassantCaptureMove;
import game.chess.move.IChessMove;
import game.chess.move.KingMove;
import game.chess.move.PawnPromotionMove;

public class ChessPosition implements IPosition<IChessMove, ChessPosition>, ChessConstants {
	public final int[][] squares;

	public final ChessPositionHistory positionHistory;

	public Coordinate[] kingSquares; // 0 = null, 1 = whiteKing, 2 = blackKing

	public boolean white;

	public int currentPlayer;
	public int otherPlayer;

	public int castleState;
	public Coordinate enPassantSquare;

	public int halfMoveClock; // The number of half moves since the last capture or pawn advance
	// XXX 3 fold repetition

	double[] materialScore;

	public ChessPosition() {
		this(ChessConstants.newInitialPosition(), new ChessPositionHistory(), ChessConstants.newInitialKingSquares(), TwoPlayers.PLAYER_1, TwoPlayers.PLAYER_2, true, INITIAL_CASTLE_STATE, null, 0,
				ChessConstants.newInitialMaterialScore());
	}

	public ChessPosition(int[][] squares, ChessPositionHistory positionHistory, Coordinate[] kingSquares, int currentPlayer, int otherPlayer, boolean white, int castleState,
			Coordinate enPassantSquare, int halfMoveClock, double[] materialScore) {
		this.squares = squares;
		this.positionHistory = positionHistory;
		this.currentPlayer = currentPlayer;
		this.otherPlayer = otherPlayer;
		this.white = white;
		this.castleState = castleState;
		this.enPassantSquare = enPassantSquare;
		this.kingSquares = kingSquares;
		this.halfMoveClock = halfMoveClock;
		this.materialScore = materialScore;
	}

	@Override
	public List<IChessMove> getPossibleMoves() {
		if (halfMoveClock == 100) {
			return Collections.emptyList();
		}
		int pawnDirection = white ? 1 : -1;
		List<IChessMove> possibleMoves = new ArrayList<>();
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			boolean pawnPromotes = white ? y == RANK_7 : y == RANK_2;
			boolean isStartingPawnRank = white ? y == RANK_2 : y == RANK_7;
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				int piece = squares[y][x];
				if ((piece & currentPlayer) == currentPlayer) {
					Coordinate from = Coordinate.valueOf(x, y);
					if ((piece & PAWN) == PAWN) {
						addPawnMoves(possibleMoves, from, x, y, pawnDirection, isStartingPawnRank, pawnPromotes);
					} else if ((piece & KNIGHT) == KNIGHT) {
						addKnightMoves(possibleMoves, from, x, y);
					} else if ((piece & BISHOP) == BISHOP) {
						addQueenOrBishopMoves(possibleMoves, from, x, y);
					} else if ((piece & ROOK) == ROOK) {
						addQueenOrRookMoves(possibleMoves, from, x, y, true);
					} else if ((piece & QUEEN) == QUEEN) {
						addQueenOrBishopMoves(possibleMoves, from, x, y);
						addQueenOrRookMoves(possibleMoves, from, x, y, false);
					} else if ((piece & KING) == KING) {
						addKingMoves(possibleMoves, from, x, y);
					}
				}
			}
		}
		addEnPassantCaptures(possibleMoves, pawnDirection);
		addCastlingMoves(possibleMoves);
		return possibleMoves;
	}

	private void addPossibleMove(List<IChessMove> possibleMoves, IChessMove chessMove) {
		// Move the pieces and then check if the king is attacked before adding the move
		chessMove.applyMove(this, false);
		Coordinate kingCoordinate = kingSquares[currentPlayer];
		if (!ChessFunctions.isSquareAttacked(this, kingCoordinate.x, kingCoordinate.y, otherPlayer)) {
			possibleMoves.add(chessMove);
		}
		chessMove.unapplyMove(this);
	}

	private void addPawnMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int direction, boolean isStartingPawnRank, boolean pawnPromotes) {
		if (isStartingPawnRank && squares[y + direction][x] == UNPLAYED && squares[y + direction + direction][x] == UNPLAYED) { // up 2 if on starting rank
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x, y + direction + direction), UNPLAYED, Coordinate.valueOf(x, y + direction));
			addPossibleMove(possibleMoves, basicMove);
		}
		int pawn = squares[y][x];
		if (squares[y + direction][x] == UNPLAYED) { // up 1
			addPawnMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x, y + direction), UNPLAYED, null), pawn, pawnPromotes);
		}
		if (x < BOARD_WIDTH - 1 && (squares[y + direction][x + 1] & otherPlayer) == otherPlayer) { // capture right
			addPawnMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x + 1, y + direction), squares[y + direction][x + 1], null), pawn, pawnPromotes);
		}
		if (x > 0 && (squares[y + direction][x - 1] & otherPlayer) == otherPlayer) { // capture left
			addPawnMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x - 1, y + direction), squares[y + direction][x - 1], null), pawn, pawnPromotes);
		}
	}

	private void addPawnMove(List<IChessMove> possibleMoves, BasicChessMove basicMove, int pawn, boolean pawnPromotes) {
		if (pawnPromotes) {
			addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | QUEEN, pawn));
			addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | ROOK, pawn));
			addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | BISHOP, pawn));
			addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | KNIGHT, pawn));
		} else {
			addPossibleMove(possibleMoves, basicMove);
		}
	}

	private void addEnPassantCaptures(List<IChessMove> possibleMoves, int pawnDirection) {
		if (enPassantSquare == null) {
			return;
		}
		int playerPawn = currentPlayer | PAWN;
		if (enPassantSquare.x < BOARD_WIDTH - 1 && squares[enPassantSquare.y - pawnDirection][enPassantSquare.x + 1] == playerPawn) {
			Coordinate from = Coordinate.valueOf(enPassantSquare.x + 1, enPassantSquare.y - pawnDirection);
			BasicChessMove basicChessMove = new BasicChessMove(from, enPassantSquare, squares[enPassantSquare.y - pawnDirection][enPassantSquare.x], null);
			addPossibleMove(possibleMoves, new EnPassantCaptureMove(basicChessMove, pawnDirection));
		}
		if (enPassantSquare.x > 0 && squares[enPassantSquare.y - pawnDirection][enPassantSquare.x - 1] == playerPawn) {
			Coordinate from = Coordinate.valueOf(enPassantSquare.x - 1, enPassantSquare.y - pawnDirection);
			BasicChessMove basicChessMove = new BasicChessMove(from, enPassantSquare, squares[enPassantSquare.y - pawnDirection][enPassantSquare.x], null);
			addPossibleMove(possibleMoves, new EnPassantCaptureMove(basicChessMove, pawnDirection));
		}
	}

	private void addKnightMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y) {
		// clockwise starting upper left
		if (x > 0 && y > 1 && (squares[y - 2][x - 1] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x - 1, y - 2), squares[y - 2][x - 1], null));
		}
		if (x < BOARD_WIDTH - 1 && y > 1 && (squares[y - 2][x + 1] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x + 1, y - 2), squares[y - 2][x + 1], null));
		}
		if (x < BOARD_WIDTH - 2 && y > 1 && (squares[y - 1][x + 2] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x + 2, y - 1), squares[y - 1][x + 2], null));
		}
		if (x < BOARD_WIDTH - 2 && y < BOARD_WIDTH - 1 && (squares[y + 1][x + 2] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x + 2, y + 1), squares[y + 1][x + 2], null));
		}
		if (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 2 && (squares[y + 2][x + 1] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x + 1, y + 2), squares[y + 2][x + 1], null));
		}
		if (x > 0 && y < BOARD_WIDTH - 2 && (squares[y + 2][x - 1] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x - 1, y + 2), squares[y + 2][x - 1], null));
		}
		if (x > 1 && y < BOARD_WIDTH - 1 && (squares[y + 1][x - 2] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x - 2, y + 1), squares[y + 1][x - 2], null));
		}
		if (x > 1 && y > 0 && (squares[y - 1][x - 2] & currentPlayer) != currentPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x - 2, y - 1), squares[y - 1][x - 2], null));
		}
	}

	private void addQueenOrBishopMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y) {
		int bx = x;
		int by = y;
		while (bx < BOARD_WIDTH - 1 && by < BOARD_WIDTH - 1 && squares[++by][++bx] == UNPLAYED) { // down right
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null));
		}
		bx = x;
		by = y;
		while (bx > 0 && by < BOARD_WIDTH - 1 && squares[++by][--bx] == UNPLAYED) { // down left
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null));
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by > 0 && squares[--by][++bx] == UNPLAYED) { // up right
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null));
		}
		bx = x;
		by = y;
		while (bx > 0 && by > 0 && squares[--by][--bx] == UNPLAYED) { // up left
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null));
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			addPossibleMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null));
		}
	}

	private void addQueenOrRookMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, boolean isRook) {
		int bx = x;
		int by = y;
		while (bx > 0 && squares[by][--bx] == UNPLAYED) { // left
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && squares[by][++bx] == UNPLAYED) { // right
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
		bx = x;
		by = y;
		while (by > 0 && squares[--by][bx] == UNPLAYED) { // up
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
		bx = x;
		by = y;
		while (by < BOARD_WIDTH - 1 && squares[++by][bx] == UNPLAYED) { // down
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], null);
			addQueenOrRookMove(possibleMoves, basicMove, isRook);
		}
	}

	private void addQueenOrRookMove(List<IChessMove> possibleMoves, BasicChessMove basicMove, boolean isRook) {
		if (isRook) {
			if (white) {
				addRookMove(possibleMoves, basicMove, WHITE_KING_CASTLE, WHITE_QUEEN_CASTLE, H1, A1);
			} else {
				addRookMove(possibleMoves, basicMove, BLACK_KING_CASTLE, BLACK_QUEEN_CASTLE, H8, A8);
			}
		} else {
			addPossibleMove(possibleMoves, basicMove);
		}
	}

	private void addRookMove(List<IChessMove> possibleMoves, BasicChessMove basicMove, int kingCastle, int queenCastle, Coordinate kingsRookCoordinate, Coordinate queensRookCoordinate) {
		if ((castleState & kingCastle) == kingCastle && basicMove.from.equals(kingsRookCoordinate)) {
			addPossibleMove(possibleMoves, new CastleBreakingMove(basicMove, kingCastle));
		} else if ((castleState & queenCastle) == queenCastle && basicMove.from.equals(queensRookCoordinate)) {
			addPossibleMove(possibleMoves, new CastleBreakingMove(basicMove, queenCastle));
		} else {
			addPossibleMove(possibleMoves, basicMove);
		}
	}

	private void addKingMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y) {
		// clockwise starting upper left
		if (x > 0 && y > 0 && (squares[y - 1][x - 1] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x - 1, y - 1), squares[y - 1][x - 1], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
		if (y > 0 && (squares[y - 1][x] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x, y - 1), squares[y - 1][x], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
		if (x < BOARD_WIDTH - 1 && y > 0 && (squares[y - 1][x + 1] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x + 1, y - 1), squares[y - 1][x + 1], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
		if (x < BOARD_WIDTH - 1 && (squares[y][x + 1] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x + 1, y), squares[y][x + 1], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
		if (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && (squares[y + 1][x + 1] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x + 1, y + 1), squares[y + 1][x + 1], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
		if (y < BOARD_WIDTH - 1 && (squares[y + 1][x] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x, y + 1), squares[y + 1][x], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
		if (x > 0 && y < BOARD_WIDTH - 1 && (squares[y + 1][x - 1] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x - 1, y + 1), squares[y + 1][x - 1], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
		if (x > 0 && (squares[y][x - 1] & currentPlayer) != currentPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x - 1, y), squares[y][x - 1], null);
			addPossibleMove(possibleMoves, new KingMove(basicMove));
		}
	}

	private void addCastlingMoves(List<IChessMove> possibleMoves) {
		if (white) {
			addCastingMoves(possibleMoves, RANK_1, WHITE_KING_CASTLE, WHITE_QUEEN_CASTLE);
		} else {
			addCastingMoves(possibleMoves, RANK_8, BLACK_KING_CASTLE, BLACK_QUEEN_CASTLE);
		}
	}

	private void addCastingMoves(List<IChessMove> possibleMoves, int rank, int kingCastle, int queenCastle) {
		if ((castleState & kingCastle) == kingCastle && squares[rank][F_FILE] == UNPLAYED && squares[rank][G_FILE] == UNPLAYED &&
				!ChessFunctions.isSquareAttacked(this, E_FILE, rank, otherPlayer) && !ChessFunctions.isSquareAttacked(this, F_FILE, rank, otherPlayer)) {
			BasicChessMove basicMove = new BasicChessMove(Coordinate.valueOf(E_FILE, rank), Coordinate.valueOf(G_FILE, rank), UNPLAYED, null);
			addPossibleMove(possibleMoves, new CastleMove(basicMove, kingCastle, Coordinate.valueOf(H_FILE, rank), Coordinate.valueOf(F_FILE, rank)));
		}
		if ((castleState & queenCastle) == queenCastle && squares[rank][D_FILE] == UNPLAYED && squares[rank][C_FILE] == UNPLAYED && squares[rank][B_FILE] == UNPLAYED &&
				!ChessFunctions.isSquareAttacked(this, E_FILE, rank, otherPlayer) && !ChessFunctions.isSquareAttacked(this, D_FILE, rank, otherPlayer)) {
			BasicChessMove basicMove = new BasicChessMove(Coordinate.valueOf(E_FILE, rank), Coordinate.valueOf(C_FILE, rank), UNPLAYED, null);
			addPossibleMove(possibleMoves, new CastleMove(basicMove, queenCastle, Coordinate.valueOf(A_FILE, rank), Coordinate.valueOf(D_FILE, rank)));
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(IChessMove move) {
		positionHistory.makeMove(this);

		Coordinate from = move.getFrom();
		Coordinate to = move.getTo();
		boolean notPawn = (squares[from.y][from.x] & PAWN) == 0;
		int pieceCaptured;
		if (move.getClass().equals(EnPassantCaptureMove.class)) {
			EnPassantCaptureMove enPassantCaptureMove = (EnPassantCaptureMove) move;
			pieceCaptured = squares[to.y - enPassantCaptureMove.pawnDirection][to.x];
		} else {
			pieceCaptured = squares[to.y][to.x];
		}
		halfMoveClock = notPawn && pieceCaptured == 0 ? halfMoveClock + 1 : 0;
		materialScore[otherPlayer] = materialScore[otherPlayer] - ChessFunctions.getPieceScore(pieceCaptured);
		if (move.getClass().equals(PawnPromotionMove.class)) {
			PawnPromotionMove pawnPromotionMove = (PawnPromotionMove) move;
			materialScore[currentPlayer] = materialScore[currentPlayer] + ChessFunctions.getPieceScore(pawnPromotionMove.promotion) - PAWN_SCORE;
		}

		move.applyMove(this, true);

		otherPlayer = currentPlayer;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
		white = !white;
	}

	@Override
	public void unmakeMove(IChessMove move) {
		white = !white;
		otherPlayer = currentPlayer;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);

		move.unapplyMove(this);

		Coordinate to = move.getTo();
		int pieceCaptured;
		if (move.getClass().equals(EnPassantCaptureMove.class)) {
			EnPassantCaptureMove enPassantCaptureMove = (EnPassantCaptureMove) move;
			pieceCaptured = squares[to.y - enPassantCaptureMove.pawnDirection][to.x];
		} else {
			pieceCaptured = squares[to.y][to.x];
		}

		materialScore[otherPlayer] = materialScore[otherPlayer] + ChessFunctions.getPieceScore(pieceCaptured);
		if (move.getClass().equals(PawnPromotionMove.class)) {
			PawnPromotionMove pawnPromotionMove = (PawnPromotionMove) move;
			materialScore[currentPlayer] = materialScore[currentPlayer] - ChessFunctions.getPieceScore(pawnPromotionMove.promotion) + PAWN_SCORE;
		}

		positionHistory.unmakeMove(this);
	}

	@Override
	public ChessPosition createCopy() {
		int[][] squaresCopy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int y = 0; y < squares.length; ++y) {
			System.arraycopy(squares[y], 0, squaresCopy[y], 0, BOARD_WIDTH);
		}

		Coordinate[] kingSquaresCopy = new Coordinate[3];
		System.arraycopy(kingSquares, 1, kingSquaresCopy, 1, 2);

		double[] materialScoreCopy = new double[3];
		System.arraycopy(materialScore, 1, materialScoreCopy, 1, 2);

		return new ChessPosition(squaresCopy, positionHistory.createCopy(), kingSquaresCopy, currentPlayer, otherPlayer, white, castleState, enPassantSquare, halfMoveClock, materialScoreCopy);
	}
}
