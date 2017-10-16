package game.chess;

import java.util.ArrayList;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;
import game.chess.move.BasicChessMove;
import game.chess.move.CastleBreakingMove;
import game.chess.move.CastleMove;
import game.chess.move.EnPassantCaptureMove;
import game.chess.move.EnPassantMove;
import game.chess.move.IChessMove;
import game.chess.move.KingMove;
import game.chess.move.PawnPromotionMove;

public class ChessPosition implements IPosition<IChessMove, ChessPosition>, ChessConstants {
	public final int[][] squares;
	public int currentPlayer;

	public int castleState;
	public Coordinate enPassantSquare;
	public Coordinate whiteKingSquare;
	public Coordinate blackKingSquare;
	// XXX 50 move counter
	// XXX 3 fold repetition
	// XXX check

	public ChessPosition() {
		this(ChessConstants.newInitialPosition(), TwoPlayers.PLAYER_1, INITIAL_CASTLE_STATE, null, E1, E8);
	}

	public ChessPosition(int[][] squares, int currentPlayer, int castleState, Coordinate enPassantSquare, Coordinate whiteKingSquare, Coordinate blackKingSquare) {
		this.squares = squares;
		this.currentPlayer = currentPlayer;
		this.castleState = castleState;
		this.enPassantSquare = enPassantSquare;
	}

	@Override
	public List<IChessMove> getPossibleMoves() {
		int otherPlayer = TwoPlayers.otherPlayer(currentPlayer);
		boolean white = currentPlayer == TwoPlayers.PLAYER_1;
		int pawnDirection = white ? 1 : -1;
		List<IChessMove> possibleMoves = new ArrayList<>();
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			boolean pawnPromotes = white ? y == RANK_7 : y == RANK_2;
			boolean isStartingPawnRank = white ? y == RANK_2 : y == RANK_7;
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				int piece = squares[y][x];
				if ((piece & currentPlayer) == currentPlayer) {
					Coordinate from = Coordinate.valueOf(x, y);
					// XXX pins
					if ((piece & PAWN) == PAWN) {
						addPawnMoves(possibleMoves, from, x, y, otherPlayer, pawnDirection, isStartingPawnRank, pawnPromotes);
					} else if ((piece & KNIGHT) == KNIGHT) {
						addKnightMoves(possibleMoves, from, x, y);
					} else if ((piece & BISHOP) == BISHOP) {
						addQueenOrBishopMoves(possibleMoves, from, x, y, otherPlayer);
					} else if ((piece & ROOK) == ROOK) {
						addQueenOrRookMoves(possibleMoves, from, x, y, otherPlayer, true, white);
					} else if ((piece & QUEEN) == QUEEN) {
						addQueenOrBishopMoves(possibleMoves, from, x, y, otherPlayer);
						addQueenOrRookMoves(possibleMoves, from, x, y, otherPlayer, false, white);
					} else if ((piece & KING) == KING) {
						addKingMoves(possibleMoves, from, x, y, otherPlayer, white);
					}
				}
			}
		}
		if (enPassantSquare != null) {
			addEnPassantCaptures(possibleMoves, pawnDirection);
		}
		addCastlingMoves(possibleMoves, otherPlayer, white);
		return possibleMoves;
	}

	private void addPawnMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer, int direction, boolean isStartingPawnRank, boolean pawnPromotes) {
		if (isStartingPawnRank && squares[y + direction][x] == UNPLAYED && squares[y + direction + direction][x] == UNPLAYED) { // up 2 if on starting rank
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x, y + direction + direction), UNPLAYED, enPassantSquare);
			possibleMoves.add(new EnPassantMove(basicMove, Coordinate.valueOf(x, y + direction)));
		}
		int pawn = squares[y][x];
		if (squares[y + direction][x] == UNPLAYED) { // up 1
			addPawnMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x, y + direction), UNPLAYED, enPassantSquare), pawn, pawnPromotes);
		}
		if (x < BOARD_WIDTH - 1 && (squares[y + direction][x + 1] & otherPlayer) == otherPlayer) { // capture right
			addPawnMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x + 1, y + direction), squares[y + direction][x + 1], enPassantSquare), pawn, pawnPromotes);
		}
		if (x > 0 && (squares[y + direction][x - 1] & otherPlayer) == otherPlayer) { // capture left
			addPawnMove(possibleMoves, new BasicChessMove(from, Coordinate.valueOf(x - 1, y + direction), squares[y + direction][x - 1], enPassantSquare), pawn, pawnPromotes);
		}
	}

	private void addPawnMove(List<IChessMove> possibleMoves, BasicChessMove basicMove, int pawn, boolean pawnPromotes) {
		if (pawnPromotes) {
			possibleMoves.add(new PawnPromotionMove(basicMove, currentPlayer | QUEEN, pawn));
			possibleMoves.add(new PawnPromotionMove(basicMove, currentPlayer | ROOK, pawn));
			possibleMoves.add(new PawnPromotionMove(basicMove, currentPlayer | BISHOP, pawn));
			possibleMoves.add(new PawnPromotionMove(basicMove, currentPlayer | KNIGHT, pawn));
		} else {
			possibleMoves.add(basicMove);
		}
	}

	private void addEnPassantCaptures(List<IChessMove> possibleMoves, int pawnDirection) {
		int playerPawn = currentPlayer & PAWN;
		if (enPassantSquare.x < BOARD_WIDTH - 1 && (squares[enPassantSquare.y - pawnDirection][enPassantSquare.x + 1] & playerPawn) == playerPawn) {
			Coordinate from = Coordinate.valueOf(enPassantSquare.x + 1, enPassantSquare.y - pawnDirection);
			Coordinate capturedPawnSquare = Coordinate.valueOf(enPassantSquare.x, enPassantSquare.y - pawnDirection);
			possibleMoves.add(new EnPassantCaptureMove(new BasicChessMove(from, capturedPawnSquare, squares[enPassantSquare.y - pawnDirection][enPassantSquare.x], enPassantSquare)));
		}
		if (enPassantSquare.x > 0 && (squares[enPassantSquare.y - pawnDirection][enPassantSquare.x - 1] & playerPawn) == playerPawn) {
			Coordinate from = Coordinate.valueOf(enPassantSquare.x - 1, enPassantSquare.y - pawnDirection);
			possibleMoves.add(new EnPassantCaptureMove(new BasicChessMove(from, enPassantSquare, squares[enPassantSquare.y - pawnDirection][enPassantSquare.x], enPassantSquare)));
		}
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

	private void addQueenOrBishopMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer) {
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

	private void addQueenOrRookMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer, boolean isRook, boolean white) {
		int bx = x;
		int by = y;
		while (bx > 0 && squares[by][--bx] == UNPLAYED) { // left
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && squares[by][++bx] == UNPLAYED) { // right
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
		bx = x;
		by = y;
		while (by > 0 && squares[--by][bx] == UNPLAYED) { // up
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
		bx = x;
		by = y;
		while (by < BOARD_WIDTH - 1 && squares[++by][bx] == UNPLAYED) { // down
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), UNPLAYED, enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
		if ((squares[by][bx] & otherPlayer) == otherPlayer) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(bx, by), squares[by][bx], enPassantSquare);
			addQueenOrRookMove(possibleMoves, basicMove, isRook, white);
		}
	}

	private void addQueenOrRookMove(List<IChessMove> possibleMoves, BasicChessMove basicMove, boolean isRook, boolean white) {
		if (isRook) {
			if (white) {
				addRookMove(possibleMoves, basicMove, WHITE_KING_CASTLE, WHITE_QUEEN_CASTLE, H1, A1);
			} else {
				addRookMove(possibleMoves, basicMove, BLACK_KING_CASTLE, BLACK_QUEEN_CASTLE, H8, A8);
			}
		} else {
			possibleMoves.add(basicMove);
		}
	}

	private void addRookMove(List<IChessMove> possibleMoves, BasicChessMove basicMove, int kingCastle, int queenCastle, Coordinate kingsRookCoordinate, Coordinate queensRookCoordinate) {
		if ((castleState & kingCastle) == kingCastle && basicMove.from.equals(kingsRookCoordinate)) {
			possibleMoves.add(new CastleBreakingMove(basicMove, kingCastle));
		} else if ((castleState & queenCastle) == queenCastle && basicMove.from.equals(queensRookCoordinate)) {
			possibleMoves.add(new CastleBreakingMove(basicMove, queenCastle));
		} else {
			possibleMoves.add(basicMove);
		}
	}

	private void addKingMoves(List<IChessMove> possibleMoves, Coordinate from, int x, int y, int otherPlayer, boolean white) {
		// clockwise starting upper left
		if (x > 0 && y > 0 && (squares[y - 1][x - 1] & currentPlayer) != currentPlayer && !isSquareAttacked(x - 1, y - 1, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x - 1, y - 1), squares[y - 1][x - 1], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
		if (y > 0 && (squares[y - 1][x] & currentPlayer) != currentPlayer && !isSquareAttacked(x, y - 1, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x, y - 1), squares[y - 1][x], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
		if (x < BOARD_WIDTH - 1 && y > 0 && (squares[y - 1][x + 1] & currentPlayer) != currentPlayer && !isSquareAttacked(x + 1, y - 1, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x + 1, y - 1), squares[y - 1][x + 1], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
		if (x < BOARD_WIDTH - 1 && (squares[y][x + 1] & currentPlayer) != currentPlayer && !isSquareAttacked(x + 1, y, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x + 1, y), squares[y][x + 1], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
		if (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && (squares[y + 1][x + 1] & currentPlayer) != currentPlayer && !isSquareAttacked(x + 1, y + 1, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x + 1, y + 1), squares[y + 1][x + 1], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
		if (y < BOARD_WIDTH - 1 && (squares[y + 1][x] & currentPlayer) != currentPlayer && !isSquareAttacked(x, y + 1, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x, y + 1), squares[y + 1][x], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
		if (x > 0 && y < BOARD_WIDTH - 1 && (squares[y + 1][x - 1] & currentPlayer) != currentPlayer && !isSquareAttacked(x - 1, y + 1, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x - 1, y + 1), squares[y + 1][x - 1], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
		if (x > 0 && (squares[y][x - 1] & currentPlayer) != currentPlayer && !isSquareAttacked(x - 1, y, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(from, Coordinate.valueOf(x - 1, y), squares[y][x - 1], enPassantSquare);
			possibleMoves.add(new KingMove(basicMove, castleState, white));
		}
	}

	private void addCastlingMoves(List<IChessMove> possibleMoves, int otherPlayer, boolean white) {
		if (white) {
			addCastingMoves(possibleMoves, otherPlayer, white, RANK_1, WHITE_KING_CASTLE, WHITE_QUEEN_CASTLE);
		} else {
			addCastingMoves(possibleMoves, otherPlayer, white, RANK_8, BLACK_KING_CASTLE, BLACK_QUEEN_CASTLE);
		}
	}

	private void addCastingMoves(List<IChessMove> possibleMoves, int otherPlayer, boolean white, int rank, int kingCastle, int queenCastle) {
		if ((castleState & kingCastle) == kingCastle &&
				squares[rank][F_FILE] == UNPLAYED && !isSquareAttacked(F_FILE, rank, otherPlayer, white) &&
				squares[rank][G_FILE] == UNPLAYED && !isSquareAttacked(G_FILE, rank, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(Coordinate.valueOf(E_FILE, rank), Coordinate.valueOf(H_FILE, rank), 0, enPassantSquare);
			possibleMoves.add(new CastleMove(basicMove, kingCastle, castleState));
		}
		if ((castleState & queenCastle) == queenCastle &&
				squares[rank][D_FILE] == UNPLAYED && !isSquareAttacked(D_FILE, rank, otherPlayer, white) &&
				squares[rank][C_FILE] == UNPLAYED && !isSquareAttacked(C_FILE, rank, otherPlayer, white) &&
				squares[rank][B_FILE] == UNPLAYED && !isSquareAttacked(B_FILE, rank, otherPlayer, white)) {
			BasicChessMove basicMove = new BasicChessMove(Coordinate.valueOf(E_FILE, rank), Coordinate.valueOf(A_FILE, rank), 0, enPassantSquare);
			possibleMoves.add(new CastleMove(basicMove, queenCastle, castleState));
		}
	}

	private boolean isSquareAttacked(int x, int y, int otherPlayer, boolean white) {
		int otherPawn = otherPlayer | PAWN;
		int otherKnight = otherPlayer | KNIGHT;
		int otherBishop = otherPlayer | BISHOP;
		int otherRook = otherPlayer | ROOK;
		int otherQueen = otherPlayer | QUEEN;
		int otherKing = otherPlayer | KING;
		return isAttackedByPawn(x, y, otherPawn, white) ||
				isAttackedByKnight(x, y, otherKnight) ||
				isAttackedByQueenOrBishop(x, y, otherQueen, otherBishop) ||
				isAttackedByQueenOrRook(x, y, otherQueen, otherRook) ||
				isAttackedByKing(x, y, otherKing);
	}

	private boolean isAttackedByPawn(int x, int y, int otherPawn, boolean white) {
		if (white) {
			return (x < BOARD_WIDTH - 1 && y > 0 && squares[y - 1][x + 1] == otherPawn) || // capture right
					(x > 0 && y > 0 && squares[y - 1][x - 1] == otherPawn);// capture left
		} else {
			return (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && squares[y + 1][x + 1] == otherPawn) || // capture right
					(x > 0 && y < BOARD_WIDTH - 1 && squares[y + 1][x - 1] == otherPawn);// capture left
		}
	}

	private boolean isAttackedByKnight(int x, int y, int otherKnight) {
		// clockwise starting upper left
		return (x > 0 && y > 1 && squares[y - 2][x - 1] == otherKnight) ||
				(x < BOARD_WIDTH - 1 && y > 1 && squares[y - 2][x + 1] == otherKnight) ||
				(x < BOARD_WIDTH - 2 && y > 1 && squares[y - 1][x + 2] == otherKnight) ||
				(x < BOARD_WIDTH - 2 && y < BOARD_WIDTH - 1 && squares[y + 1][x + 2] == otherKnight) ||
				(x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 2 && squares[y + 2][x + 1] == otherKnight) ||
				(x > 0 && y < BOARD_WIDTH - 2 && squares[y + 2][x - 1] == otherKnight) ||
				(x > 1 && y < BOARD_WIDTH - 1 && squares[y + 1][x - 2] == otherKnight) ||
				(x > 1 && y > 0 && squares[y - 1][x - 2] == otherKnight);
	}

	private boolean isAttackedByQueenOrBishop(int x, int y, int otherQueen, int otherBishop) {
		int bx = x;
		int by = y;
		while (bx > 0 && by > 0 && squares[--by][--bx] == UNPLAYED) {
		}
		if (squares[by][bx] == otherQueen || squares[by][bx] == otherBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by > 0 && squares[--by][++bx] == UNPLAYED) {
		}
		if (squares[by][bx] == otherQueen || squares[by][bx] == otherBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by < BOARD_WIDTH - 1 && squares[++by][++bx] == UNPLAYED) {
		}
		if (squares[by][bx] == otherQueen || squares[by][bx] == otherBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx > 0 && by < BOARD_WIDTH - 1 && squares[++by][--bx] == UNPLAYED) {
		}
		return squares[by][bx] == otherQueen || squares[by][bx] == otherBishop;
	}

	private boolean isAttackedByQueenOrRook(int x, int y, int otherQueen, int otherRook) {
		int bx = x;
		int by = y;
		while (by > 0 && squares[--by][bx] == UNPLAYED) {
		}
		if (squares[by][bx] == otherQueen || squares[by][bx] == otherRook) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && squares[by][++bx] == UNPLAYED) {
		}
		if (squares[by][bx] == otherQueen || squares[by][bx] == otherRook) {
			return true;
		}
		bx = x;
		by = y;
		while (by < BOARD_WIDTH - 1 && squares[++by][bx] == UNPLAYED) {
		}
		if (squares[by][bx] == otherQueen || squares[by][bx] == otherRook) {
			return true;
		}
		bx = x;
		by = y;
		while (bx > 0 && squares[by][--bx] == UNPLAYED) {
		}
		return squares[by][bx] == otherQueen || squares[by][bx] == otherRook;
	}

	private boolean isAttackedByKing(int x, int y, int otherKing) {
		// clockwise starting upper left
		return (x > 0 && y > 0 && squares[y - 1][x - 1] == otherKing) ||
				(y > 0 && squares[y - 1][x] == otherKing) ||
				(x < BOARD_WIDTH - 1 && y > 0 && squares[y - 1][x + 1] == otherKing) ||
				(x < BOARD_WIDTH - 1 && squares[y][x + 1] == otherKing) ||
				(x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && squares[y + 1][x + 1] == otherKing) ||
				(y < BOARD_WIDTH - 1 && squares[y + 1][x] == otherKing) ||
				(x > 0 && y < BOARD_WIDTH - 1 && squares[y + 1][x - 1] == otherKing) ||
				(x > 0 && squares[y][x - 1] == otherKing);
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
		return new ChessPosition(squaresCopy, currentPlayer, castleState, enPassantSquare, whiteKingSquare, blackKingSquare);
	}
}
