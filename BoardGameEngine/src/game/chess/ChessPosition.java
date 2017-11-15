package game.chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.IPosition;
import game.TwoPlayers;
import game.chess.move.BasicChessMove;
import game.chess.move.CastleMove;
import game.chess.move.EnPassantCaptureMove;
import game.chess.move.IChessMove;
import game.chess.move.KingMove;
import game.chess.move.PawnPromotionMove;

public class ChessPosition implements IPosition<IChessMove, ChessPosition>, ChessConstants {
	public final int[] squares;

	public final ChessPositionHistory positionHistory;

	public int[] kingSquares; // 0 = null, 1 = whiteKing, 2 = blackKing

	public boolean white;

	public int currentPlayer;
	public int otherPlayer;

	public int castleState;
	public int enPassantSquare;

	public int halfMoveClock; // The number of half moves since the last capture or pawn advance
	// XXX 3 fold repetition

	public double[] materialScore;

	public ChessPosition() {
		this(ChessConstants.newInitialPosition(), new ChessPositionHistory(), ChessConstants.newInitialKingSquares(), TwoPlayers.PLAYER_1, TwoPlayers.PLAYER_2, true, ALL_CASTLES, NO_SQUARE, 0,
				ChessConstants.newInitialMaterialScore());
	}

	public ChessPosition(int[] squares, ChessPositionHistory positionHistory, int[] kingSquares, int currentPlayer, int otherPlayer, boolean white, int castleState,
			int enPassantSquare, int halfMoveClock, double[] materialScore) {
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
		List<IChessMove> possibleMoves = new ArrayList<>();
		int pawnOffset = white ? PAWN_OFFSET : -PAWN_OFFSET;
		int square = H1;
		do {
			boolean pawnPromotes = white ? square == H7 : square == H2;
			boolean isStartingPawnRank = white ? square == H2 : square == H7;
			int fileCounter = 0;
			do {
				int piece = squares[square];
				if ((piece & currentPlayer) == currentPlayer) {
					if ((piece & PAWN) == PAWN) {
						addPawnMoves(possibleMoves, square, pawnOffset, isStartingPawnRank, pawnPromotes);
					} else if ((piece & KNIGHT) == KNIGHT) {
						addKnightMoves(possibleMoves, square);
					} else if ((piece & BISHOP) == BISHOP) {
						addSlidingMoves(possibleMoves, square, BISHOP_OFFSETS);
					} else if ((piece & ROOK) == ROOK) {
						addSlidingMoves(possibleMoves, square, ROOK_OFFSETS);
					} else if ((piece & QUEEN) == QUEEN) {
						addSlidingMoves(possibleMoves, square, QUEEN_OFFSETS);
					} else if ((piece & KING) == KING) {
						addKingMoves(possibleMoves, square);
					}
				}
				++square;
				++fileCounter;
			} while (fileCounter < BOARD_WIDTH);
			square += 2;
		} while (square <= A8);
		addEnPassantCaptures(possibleMoves, pawnOffset);
		addCastlingMoves(possibleMoves);
		return possibleMoves;
	}

	private void addPossibleMove(List<IChessMove> possibleMoves, IChessMove chessMove) {
		// Move the pieces and then check if the king is attacked before adding the move
		chessMove.applyMove(this, false);
		int kingSquare = kingSquares[currentPlayer];
		if (!ChessFunctions.isSquareAttacked(this, kingSquare, otherPlayer)) {
			possibleMoves.add(chessMove);
		}
		chessMove.unapplyMove(this, false);
	}

	private void addPawnMoves(List<IChessMove> possibleMoves, int from, int offset, boolean isStartingPawnRank, boolean pawnPromotes) {
		int moveUpOne = from + offset;
		int pawn = squares[from];
		if (squares[moveUpOne] == UNPLAYED) { // up 1
			addPawnMove(possibleMoves, new BasicChessMove(from, moveUpOne, UNPLAYED, NO_SQUARE), pawn, pawnPromotes);
			int moveUpTwo = moveUpOne + offset;
			if (isStartingPawnRank && squares[moveUpTwo] == UNPLAYED) { // up 2 if on starting rank
				addPossibleMove(possibleMoves, new BasicChessMove(from, moveUpTwo, UNPLAYED, moveUpOne));
			}
		}
		addPawnCapture(possibleMoves, from, moveUpOne + 1, pawn, pawnPromotes);
		addPawnCapture(possibleMoves, from, moveUpOne - 1, pawn, pawnPromotes);
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

	private void addPawnCapture(List<IChessMove> possibleMoves, int from, int to, int pawn, boolean pawnPromotes) {
		int capture = squares[to];
		if ((capture & SENTINEL) == otherPlayer) {
			addPawnMove(possibleMoves, new BasicChessMove(from, to, capture, NO_SQUARE), pawn, pawnPromotes);
		}
	}

	private void addEnPassantCaptures(List<IChessMove> possibleMoves, int offset) {
		if (enPassantSquare == NO_SQUARE) {
			return;
		}
		int playerPawn = currentPlayer | PAWN;
		addEnPassantCapture(possibleMoves, enPassantSquare - offset + 1, offset, playerPawn);
		addEnPassantCapture(possibleMoves, enPassantSquare - offset - 1, offset, playerPawn);
	}

	private void addEnPassantCapture(List<IChessMove> possibleMoves, int from, int offset, int playerPawn) {
		if (squares[from] == playerPawn) {
			BasicChessMove basicChessMove = new BasicChessMove(from, enPassantSquare, squares[enPassantSquare - offset], NO_SQUARE);
			addPossibleMove(possibleMoves, new EnPassantCaptureMove(basicChessMove, offset));
		}
	}

	private void addKnightMoves(List<IChessMove> possibleMoves, int from) {
		for (int i = 0; i < KNIGHT_OFFSETS.length; ++i) {
			int offset = KNIGHT_OFFSETS[i];
			int to = from + offset;
			int pieceCaptured = squares[to];
			if ((pieceCaptured & currentPlayer) != currentPlayer) {
				addPossibleMove(possibleMoves, new BasicChessMove(from, to, pieceCaptured, NO_SQUARE));
			}
		}
	}

	private void addKingMoves(List<IChessMove> possibleMoves, int from) {
		for (int i = 0; i < KING_OFFSETS.length; ++i) {
			int offset = KING_OFFSETS[i];
			int to = from + offset;
			int pieceCaptured = squares[to];
			if ((pieceCaptured & currentPlayer) != currentPlayer) {
				BasicChessMove basicMove = new BasicChessMove(from, to, pieceCaptured, NO_SQUARE);
				addPossibleMove(possibleMoves, new KingMove(basicMove));
			}
		}
	}

	private void addSlidingMoves(List<IChessMove> possibleMoves, int from, int[] offsets) {
		for (int i = 0; i < offsets.length; ++i) {
			int offset = offsets[i];
			int to = from + offset;
			int pieceCaptured;
			while ((pieceCaptured = squares[to]) == UNPLAYED) {
				addPossibleMove(possibleMoves, new BasicChessMove(from, to, UNPLAYED, NO_SQUARE));
				to += offset;
			}
			if ((pieceCaptured & SENTINEL) == otherPlayer) {
				addPossibleMove(possibleMoves, new BasicChessMove(from, to, pieceCaptured, NO_SQUARE));
			}
		}
	}

	private void addCastlingMoves(List<IChessMove> possibleMoves) {
		if (white) {
			addCastingMoves(possibleMoves, E1, WHITE_KING_CASTLE, WHITE_QUEEN_CASTLE);
		} else {
			addCastingMoves(possibleMoves, E8, BLACK_KING_CASTLE, BLACK_QUEEN_CASTLE);
		}
	}

	private void addCastingMoves(List<IChessMove> possibleMoves, int kingSquare, int kingCastle, int queenCastle) {
		int fSquare = kingSquare - 1;
		int gSquare = fSquare - 1;
		if ((castleState & kingCastle) == kingCastle && squares[fSquare] == UNPLAYED && squares[gSquare] == UNPLAYED &&
				!ChessFunctions.isSquareAttacked(this, kingSquare, otherPlayer) && !ChessFunctions.isSquareAttacked(this, fSquare, otherPlayer)) {
			BasicChessMove basicMove = new BasicChessMove(kingSquare, gSquare, UNPLAYED, NO_SQUARE);
			addPossibleMove(possibleMoves, new CastleMove(basicMove, gSquare - 1, fSquare));
		}
		int dSquare = kingSquare + 1;
		int cSquare = dSquare + 1;
		int bSquare = cSquare + 1;
		if ((castleState & queenCastle) == queenCastle && squares[dSquare] == UNPLAYED && squares[cSquare] == UNPLAYED && squares[bSquare] == UNPLAYED &&
				!ChessFunctions.isSquareAttacked(this, kingSquare, otherPlayer) && !ChessFunctions.isSquareAttacked(this, dSquare, otherPlayer)) {
			BasicChessMove basicMove = new BasicChessMove(kingSquare, cSquare, UNPLAYED, NO_SQUARE);
			addPossibleMove(possibleMoves, new CastleMove(basicMove, bSquare + 1, dSquare));
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(IChessMove move) {
		positionHistory.saveState(this);

		int from = move.getFrom();
		int to = move.getTo();
		castleState &= CASTLING_PERMISSIONS[from];
		castleState &= CASTLING_PERMISSIONS[to];
		enPassantSquare = move.getEnPassantSquare();
		int fromSquare = squares[from];
		halfMoveClock = move.getPieceCaptured() != 0 || (fromSquare & PAWN) == PAWN ? 0 : halfMoveClock + 1;

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

		move.unapplyMove(this, true);

		positionHistory.unmakeMove(this);
	}

	@Override
	public ChessPosition createCopy() {
		int[] squaresCopy = new int[BOARD_ARRAY_SIZE];
		System.arraycopy(squares, 0, squaresCopy, 0, BOARD_ARRAY_SIZE);

		int[] kingSquaresCopy = new int[3];
		System.arraycopy(kingSquares, 1, kingSquaresCopy, 1, 2);

		double[] materialScoreCopy = new double[3];
		System.arraycopy(materialScore, 1, materialScoreCopy, 1, 2);

		return new ChessPosition(squaresCopy, positionHistory.createCopy(), kingSquaresCopy, currentPlayer, otherPlayer, white, castleState, enPassantSquare, halfMoveClock, materialScoreCopy);
	}
}
