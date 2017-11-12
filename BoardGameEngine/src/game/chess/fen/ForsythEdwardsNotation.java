package game.chess.fen;

import java.util.ArrayList;
import java.util.List;

import game.Coordinate;
import game.TwoPlayers;
import game.chess.ChessConstants;
import game.chess.ChessFunctions;
import game.chess.ChessPosition;
import game.chess.ChessPositionHistory;

public class ForsythEdwardsNotation implements ChessConstants {
	public static ChessPosition stringToPosition(String string) {
		String[] split = string.split(" ");
		int[][] squares = getSquares(split[0]);
		int currentPlayer = getCurrentPlayer(split[1]);
		int otherPlayer = TwoPlayers.otherPlayer(currentPlayer);
		boolean white = currentPlayer == TwoPlayers.PLAYER_1;
		int castleState = getCastleState(split[2]);
		Coordinate enPassantSquare = getEnpassantSquare(split[3]);
		int halfMoveClock = Integer.parseInt(split[4]);
		int plyCount = Integer.parseInt(split[5]) * 2 - (currentPlayer == TwoPlayers.PLAYER_1 ? 2 : 1);
		Coordinate whiteKingSquare = findPiece(squares, WHITE_KING);
		Coordinate blackKingSquare = findPiece(squares, BLACK_KING);
		Coordinate[] kingSquares = new Coordinate[] { null, whiteKingSquare, blackKingSquare };
		ChessPositionHistory positionHistory = new ChessPositionHistory(plyCount);
		double[] materialScore = getMaterialScore(squares);
		return new ChessPosition(squares, positionHistory, kingSquares, currentPlayer, otherPlayer, white, castleState, enPassantSquare, halfMoveClock, materialScore);
	}

	private static int[][] getSquares(String piecePlacement) {
		int[][] squares = new int[BOARD_WIDTH][];
		String[] rows = piecePlacement.split("/");
		for (int i = 0; i < BOARD_WIDTH; ++i) {
			squares[BOARD_WIDTH - i - 1] = convertRow(rows[i]);
		}
		return squares;
	}

	private static int[] convertRow(String string) {
		char[] chars = string.toCharArray();
		int[] row = new int[BOARD_WIDTH];
		int pos = 0;
		for (int i = chars.length - 1; i >= 0; --i) {
			if (Character.isDigit(chars[i])) {
				for (int j = 0; j < Character.getNumericValue(chars[i]); ++j) {
					row[pos++] = UNPLAYED;
				}
			} else {
				row[pos++] = getPiece(chars[i]);
			}
		}
		return row;
	}

	private static int getPiece(char c) {
		switch (c) {
		case 'B':
			return WHITE_BISHOP;
		case 'K':
			return WHITE_KING;
		case 'N':
			return WHITE_KNIGHT;
		case 'P':
			return WHITE_PAWN;
		case 'Q':
			return WHITE_QUEEN;
		case 'R':
			return WHITE_ROOK;
		case 'b':
			return BLACK_BISHOP;
		case 'k':
			return BLACK_KING;
		case 'n':
			return BLACK_KNIGHT;
		case 'p':
			return BLACK_PAWN;
		case 'q':
			return BLACK_QUEEN;
		case 'r':
			return BLACK_ROOK;
		default:
			throw new UnsupportedOperationException("Unknown piece: " + c);
		}
	}

	private static int getCurrentPlayer(String activeColor) {
		return "w".equals(activeColor) ? TwoPlayers.PLAYER_1 : TwoPlayers.PLAYER_2;
	}

	private static int getCastleState(String castlingAvailability) {
		int whiteKingCastle = castlingAvailability.contains("K") ? WHITE_KING_CASTLE : 0;
		int whiteQueenCastle = castlingAvailability.contains("Q") ? WHITE_QUEEN_CASTLE : 0;
		int blackKingCastle = castlingAvailability.contains("k") ? BLACK_KING_CASTLE : 0;
		int blackQueenCastle = castlingAvailability.contains("q") ? BLACK_QUEEN_CASTLE : 0;
		return whiteKingCastle | whiteQueenCastle | blackKingCastle | blackQueenCastle;
	}

	private static Coordinate findPiece(int[][] squares, int pieceToFind) {
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				if (squares[y][x] == pieceToFind) {
					return Coordinate.valueOf(x, y);
				}
			}
		}
		return null;
	}

	private static Coordinate getEnpassantSquare(String enPassantTargetSquare) {
		if (enPassantTargetSquare.equals("-")) {
			return null;
		}
		return getCoordinate(enPassantTargetSquare);
	}

	private static Coordinate getCoordinate(String algebraicCoordinate) {
		char[] chars = algebraicCoordinate.toCharArray();
		return Coordinate.valueOf(getFile(chars[0]), Character.getNumericValue(chars[0]) - 1);
	}

	private static int getFile(char c) {
		switch (c) {
		case 'a':
			return 0;
		case 'b':
			return 1;
		case 'c':
			return 2;
		case 'd':
			return 3;
		case 'e':
			return 4;
		case 'f':
			return 5;
		case 'g':
			return 6;
		case 'h':
			return 7;
		default:
			throw new UnsupportedOperationException("Unknown file: " + c);
		}
	}

	public static String positionToString(ChessPosition position) {
		String piecePlacement = getPiecePlacement(position.squares);
		String activeColor = getActiveColor(position.currentPlayer);
		String castlingAvailability = getCastlingAvailability(position.castleState);
		String enPassantTargetSquare = getEnPassantTargetSquare(position.enPassantSquare);
		String halfMoveClock = Integer.toString(position.halfMoveClock);
		String fullMoveNumber = Integer.toString(position.positionHistory.plyCount / 2 + 1);
		return piecePlacement + " " + activeColor + " " + castlingAvailability + " " + enPassantTargetSquare + " " + halfMoveClock + " " + fullMoveNumber;
	}

	private static String getPiecePlacement(int[][] squares) {
		List<String> piecePlacements = new ArrayList<>();
		for (int i = BOARD_WIDTH - 1; i >= 0; --i) {
			piecePlacements.add(getRowString(squares[i]));
		}
		return String.join("/", piecePlacements);
	}

	private static String getRowString(int[] row) {
		StringBuffer rowSb = new StringBuffer();
		int i = BOARD_WIDTH - 1;
		do {
			if (row[i] == UNPLAYED) {
				int numUnplayed = 0;
				while (i >= 0 && row[i] == UNPLAYED) {
					++numUnplayed;
					--i;
				}
				rowSb.append(numUnplayed);
			} else {
				rowSb.append(getPieceString(row[i--]));
			}
		} while (i >= 0);
		return rowSb.toString();
	}

	public static String getPieceString(int piece) {
		switch (piece) {
		case UNPLAYED:
			return "."; // Not used by FEN
		case WHITE_PAWN:
			return "P";
		case BLACK_PAWN:
			return "p";
		case WHITE_KNIGHT:
			return "N";
		case BLACK_KNIGHT:
			return "n";
		case WHITE_BISHOP:
			return "B";
		case BLACK_BISHOP:
			return "b";
		case WHITE_ROOK:
			return "R";
		case BLACK_ROOK:
			return "r";
		case WHITE_QUEEN:
			return "Q";
		case BLACK_QUEEN:
			return "q";
		case WHITE_KING:
			return "K";
		case BLACK_KING:
			return "k";
		default:
			throw new UnsupportedOperationException("Unknkwn piece " + piece);
		}
	}

	private static String getActiveColor(int currentPlayer) {
		return currentPlayer == TwoPlayers.PLAYER_1 ? "w" : "b";
	}

	private static String getCastlingAvailability(int castleState) {
		if (castleState == 0) {
			return "-";
		}
		String whiteKingCastle = (castleState & WHITE_KING_CASTLE) == WHITE_KING_CASTLE ? "K" : "";
		String whiteQueenCastle = (castleState & WHITE_QUEEN_CASTLE) == WHITE_QUEEN_CASTLE ? "Q" : "";
		String blackKingCastle = (castleState & BLACK_KING_CASTLE) == BLACK_KING_CASTLE ? "k" : "";
		String blackQueenCastle = (castleState & BLACK_QUEEN_CASTLE) == BLACK_QUEEN_CASTLE ? "q" : "";
		return whiteKingCastle + whiteQueenCastle + blackKingCastle + blackQueenCastle;
	}

	private static String getEnPassantTargetSquare(Coordinate enPassantSquare) {
		return enPassantSquare == null ? "-" : algebraicCoordinate(enPassantSquare);
	}

	public static String algebraicCoordinate(Coordinate coordinate) {
		return getFile(coordinate.x) + (coordinate.y + 1);
	}

	private static String getFile(int x) {
		switch (x) {
		case H_FILE:
			return "h";
		case G_FILE:
			return "g";
		case F_FILE:
			return "f";
		case E_FILE:
			return "e";
		case D_FILE:
			return "d";
		case C_FILE:
			return "c";
		case B_FILE:
			return "b";
		case A_FILE:
			return "a";
		default:
			throw new UnsupportedOperationException("Unknown file: " + x);
		}
	}

	public static double[] getMaterialScore(int[][] squares) {
		double[] materialScore = new double[3];
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				int piece = squares[y][x];
				addScore(materialScore, piece, TwoPlayers.PLAYER_1);
				addScore(materialScore, piece, TwoPlayers.PLAYER_2);
			}
		}
		return materialScore;
	}

	private static void addScore(double[] materialScore, int piece, int player) {
		if ((piece & player) == player) {
			materialScore[player] = materialScore[player] + ChessFunctions.getPieceScore(piece);
		}
	}
}
