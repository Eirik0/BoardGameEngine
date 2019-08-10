package game.chess;

import game.TwoPlayers;
import game.chess.fen.ForsythEdwardsNotation;

public class ChessFunctions implements ChessConstants {
	public static ChessPosition copyBoard(ChessPosition position) {
		int[] squaresCopy = new int[BOARD_ARRAY_SIZE];
		System.arraycopy(position.squares, 0, squaresCopy, 0, BOARD_ARRAY_SIZE);

		int[][] pawnsCopy = ChessConstants.newInitialPieces();
		int[][] knightsCopy = ChessConstants.newInitialPieces();
		int[][] bishopsCopy = ChessConstants.newInitialPieces();
		int[][] rooksCopy = ChessConstants.newInitialPieces();
		int[][] queensCopy = ChessConstants.newInitialPieces();
		int i = 1;
		while (i < 3) {
			System.arraycopy(position.pawns[i], 0, pawnsCopy[i], 0, MAX_PIECE_NUM);
			System.arraycopy(position.knights[i], 0, knightsCopy[i], 0, MAX_PIECE_NUM);
			System.arraycopy(position.bishops[i], 0, bishopsCopy[i], 0, MAX_PIECE_NUM);
			System.arraycopy(position.rooks[i], 0, rooksCopy[i], 0, MAX_PIECE_NUM);
			System.arraycopy(position.queens[i], 0, queensCopy[i], 0, MAX_PIECE_NUM);
			++i;
		}
		int[] numPawnsCopy = new int[MAX_PIECE_NUM];
		int[] numKnightsCopy = new int[MAX_PIECE_NUM];
		int[] numBishopsCopy = new int[MAX_PIECE_NUM];
		int[] numRooksCopy = new int[MAX_PIECE_NUM];
		int[] numQueensCopy = new int[MAX_PIECE_NUM];
		System.arraycopy(position.numPawns, 1, numPawnsCopy, 1, 2);
		System.arraycopy(position.numKnights, 1, numKnightsCopy, 1, 2);
		System.arraycopy(position.numBishops, 1, numBishopsCopy, 1, 2);
		System.arraycopy(position.numRooks, 1, numRooksCopy, 1, 2);
		System.arraycopy(position.numQueens, 1, numQueensCopy, 1, 2);

		int[] kingSquaresCopy = new int[3];
		System.arraycopy(position.kingSquares, 1, kingSquaresCopy, 1, 2);

		double[] materialScoreCopy = new double[3];
		System.arraycopy(position.materialScore, 1, materialScoreCopy, 1, 2);

		return new ChessPosition(squaresCopy, position.positionHistory.createCopy(),
				pawnsCopy, knightsCopy, bishopsCopy, rooksCopy, queensCopy,
				numPawnsCopy, numKnightsCopy, numBishopsCopy, numRooksCopy, numQueensCopy,
				kingSquaresCopy,
				position.currentPlayer, position.otherPlayer, position.white,
				position.castleState, position.enPassantSquare, position.halfMoveClock,
				materialScoreCopy);
	}

	public static boolean isSquareAttacked(ChessPosition position, int square, int opponent) {
		boolean white = opponent == TwoPlayers.PLAYER_2;
		int opponentPawn = opponent | PAWN;
		int opponentKnight = opponent | KNIGHT;
		int opponentBishop = opponent | BISHOP;
		int opponentRook = opponent | ROOK;
		int opponentQueen = opponent | QUEEN;
		int opponentKing = opponent | KING;
		return isAttackedSliding(position, square, opponentBishop, opponentQueen, BISHOP_OFFSETS, BISHOP_OFFSETS.length) ||
				isAttackedSliding(position, square, opponentRook, opponentQueen, ROOK_OFFSETS, ROOK_OFFSETS.length) ||
				isAttackedNonSliding(position, square, opponentKnight, KNIGHT_OFFSETS, KNIGHT_OFFSETS.length) ||
				isAttackedNonSliding(position, square, opponentKing, KING_OFFSETS, KING_OFFSETS.length) ||
				isAttackedByPawn(position, square, opponentPawn, white);
	}

	private static boolean isAttackedByPawn(ChessPosition position, int square, int opponentPawn, boolean white) {
		if (white) {
			return (position.squares[square + 9] == opponentPawn) || // capture right
					(position.squares[square + 11] == opponentPawn); // capture left
		} else {
			return (position.squares[square - 11] == opponentPawn) || // capture right
					(position.squares[square - 9] == opponentPawn); // capture left
		}
	}

	private static boolean isAttackedNonSliding(ChessPosition position, int square, int opponentPiece, int[] offsets, int numOffsets) {
		int i = 0;
		while (i < numOffsets) {
			if (position.squares[square + offsets[i]] == opponentPiece) {
				return true;
			}
			++i;
		}
		return false;
	}

	private static boolean isAttackedSliding(ChessPosition position, int square, int opponentRookOrBishop, int opponentQueen, int[] offsets, int numOffsets) {
		int i = 0;
		while (i < numOffsets) {
			int offset = offsets[i];
			int to = square + offset;
			int piece;
			while ((piece = position.squares[to]) == UNPLAYED) {
				to += offset;
			}
			if (piece == opponentRookOrBishop || piece == opponentQueen) {
				return true;
			}
			++i;
		}
		return false;
	}

	public static boolean isRank2(int square) {
		switch (square) {
		case H2:
		case G2:
		case F2:
		case E2:
		case D2:
		case C2:
		case B2:
		case A2:
			return true;
		default:
			return false;
		}
	}

	public static boolean isRank7(int square) {
		switch (square) {
		case H7:
		case G7:
		case F7:
		case E7:
		case D7:
		case C7:
		case B7:
		case A7:
			return true;
		default:
			return false;
		}
	}

	public static void removePiece(ChessPosition position, int captureSquare, int piece, int player) {
		switch (piece & ALL_PIECES) {
		case PAWN:
			int[] pawns = position.pawns[player];
			int numPawns = position.numPawns[player];
			int i = 0;
			while (i < numPawns) {
				if (pawns[i] == captureSquare) {
					pawns[i] = pawns[--position.numPawns[player]];
					return;
				}
				++i;
			}
			throw new IllegalStateException("Pawn not found at " + captureSquare);
		case KNIGHT:
			int[] knights = position.knights[player];
			int numKnights = position.numKnights[player];
			i = 0;
			while (i < numKnights) {
				if (knights[i] == captureSquare) {
					knights[i] = knights[--position.numKnights[player]];
					return;
				}
				++i;
			}
			throw new IllegalStateException("Knight not found at " + captureSquare);
		case BISHOP:
			int[] bishops = position.bishops[player];
			int numBishops = position.numBishops[player];
			i = 0;
			while (i < numBishops) {
				if (bishops[i] == captureSquare) {
					bishops[i] = bishops[--position.numBishops[player]];
					return;
				}
				++i;
			}
			throw new IllegalStateException("Bishop not found at " + captureSquare);
		case ROOK:
			int[] rooks = position.rooks[player];
			int numRooks = position.numRooks[player];
			i = 0;
			while (i < numRooks) {
				if (rooks[i] == captureSquare) {
					rooks[i] = rooks[--position.numRooks[player]];
					return;
				}
				++i;
			}
			throw new IllegalStateException("Rook not found at " + captureSquare);
		case QUEEN:
			int[] queens = position.queens[player];
			int numQueens = position.numQueens[player];
			i = 0;
			while (i < numQueens) {
				if (queens[i] == captureSquare) {
					queens[i] = queens[--position.numQueens[player]];
					return;
				}
				++i;
			}
			throw new IllegalStateException("Queen not found at " + captureSquare);
		default:
			throw new IllegalStateException("Unexpected piece: " + piece);
		}
	}

	public static void updatePiece(ChessPosition position, int from, int to, int piece, int player) {
		switch (piece & ALL_PIECES) {
		case PAWN:
			int[] pawns = position.pawns[player];
			int numPawns = position.numPawns[player];
			int i = 0;
			while (i < numPawns) {
				if (pawns[i] == from) {
					pawns[i] = to;
					return;
				}
				++i;
			}
			for (int c : pawns) {
				System.out.println(c);
			}
			throw new IllegalStateException("\n" + getBoardStr(position) + "Pawn not found at " + from);
		case KNIGHT:
			int[] knights = position.knights[player];
			int numKnights = position.numKnights[player];
			i = 0;
			while (i < numKnights) {
				if (knights[i] == from) {
					knights[i] = to;
					return;
				}
				++i;
			}
			throw new IllegalStateException("Knight not found at " + from);
		case BISHOP:
			int[] bishops = position.bishops[player];
			int numBishops = position.numBishops[player];
			i = 0;
			while (i < numBishops) {
				if (bishops[i] == from) {
					bishops[i] = to;
					return;
				}
				++i;
			}
			throw new IllegalStateException("Bishop not found at " + from);
		case ROOK:
			int[] rooks = position.rooks[player];
			int numRooks = position.numRooks[player];
			i = 0;
			while (i < numRooks) {
				if (rooks[i] == from) {
					rooks[i] = to;
					return;
				}
				++i;
			}
			throw new IllegalStateException("Rook not found at " + from);
		case QUEEN:
			int[] queens = position.queens[player];
			int numQueens = position.numQueens[player];
			i = 0;
			while (i < numQueens) {
				if (queens[i] == from) {
					queens[i] = to;
					return;
				}
				++i;
			}
			throw new IllegalStateException("Queen not found at " + from);
		default:
			throw new IllegalStateException("Unexpected piece: " + piece + " at " + from);
		}
	}

	public static String getBoardStr(ChessPosition expected) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				sb.append(ForsythEdwardsNotation.getPieceString(expected.squares[SQUARE_64_TO_SQUARE[y][x]]));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public static void addPiece(ChessPosition position, int captureSquare, int piece, int player) {
		switch (piece & ALL_PIECES) { // Add the piece to end
		case PAWN:
			position.pawns[player][position.numPawns[player]++] = captureSquare;
			return;
		case KNIGHT:
			position.knights[player][position.numKnights[player]++] = captureSquare;
			return;
		case BISHOP:
			position.bishops[player][position.numBishops[player]++] = captureSquare;
			return;
		case ROOK:
			position.rooks[player][position.numRooks[player]++] = captureSquare;
			return;
		case QUEEN:
			position.queens[player][position.numQueens[player]++] = captureSquare;
			return;
		default:
			throw new IllegalStateException("Unexpected piece: " + piece);
		}
	}

	public static double getPieceScore(int piece) {
		switch (piece & ALL_PIECES) {
		case UNPLAYED:
			return 0;
		case PAWN:
			return PAWN_SCORE;
		case KNIGHT:
			return KNIGHT_SCORE;
		case BISHOP:
			return BISHOP_SCORE;
		case ROOK:
			return ROOK_SCORE;
		case QUEEN:
			return QUEEN_SCORE;
		default:
			throw new IllegalStateException("Unknown piece: " + piece);
		}
	}
}
