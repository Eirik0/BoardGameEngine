package bge.game.chess.fen;

import java.util.ArrayList;
import java.util.List;

import bge.game.Coordinate;
import bge.game.TwoPlayers;
import bge.game.chess.ChessConstants;
import bge.game.chess.ChessFunctions;
import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionHistory;
import bge.util.Pair;

public class ForsythEdwardsNotation implements ChessConstants {
    public static ChessPosition stringToPosition(String string) {
        String[] split = string.split(" ");

        int[][] squares2d = getSquares(split[0]);
        int[] squares = translateSquares(squares2d);

        int currentPlayer = "w".equals(split[1]) ? TwoPlayers.PLAYER_1 : TwoPlayers.PLAYER_2;
        int otherPlayer = TwoPlayers.otherPlayer(currentPlayer);
        boolean white = currentPlayer == TwoPlayers.PLAYER_1;

        int castleState = getCastleState(split[2]);
        int enPassantSquare = getEnpassantSquare(split[3]);
        int halfMoveClock = Integer.parseInt(split[4]);

        Pair<int[][][], int[][]> pieceSquaresPair = getPieceSquares(squares);
        int[][][] pieceSquares = pieceSquaresPair.getFirst();
        int[][] numPieces = pieceSquaresPair.getSecond();

        int[] kingSquares = new int[] { NO_SQUARE, pieceSquares[5][TwoPlayers.PLAYER_1][0], pieceSquares[5][TwoPlayers.PLAYER_2][0] };

        int plyCount = Integer.parseInt(split[5]) * 2 - (currentPlayer == TwoPlayers.PLAYER_1 ? 2 : 1);
        ChessPositionHistory positionHistory = new ChessPositionHistory(plyCount);

        double[] materialScore = getMaterialScore(squares);

        return new ChessPosition(squares, positionHistory,
                pieceSquares[0], pieceSquares[1], pieceSquares[2], pieceSquares[3], pieceSquares[4],
                numPieces[0], numPieces[1], numPieces[2], numPieces[3], numPieces[4],
                kingSquares,
                currentPlayer, otherPlayer, white,
                castleState, enPassantSquare, halfMoveClock,
                materialScore);
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

    private static int[] translateSquares(int[][] squares2d) {
        int[] squares = new int[BOARD_ARRAY_SIZE];
        for (int i = 0; i < BOARD_ARRAY_SIZE; ++i) {
            squares[i] = SENTINEL;
        }
        for (int y = 0; y < BOARD_WIDTH; ++y) {
            for (int x = 0; x < BOARD_WIDTH; ++x) {
                squares[SQUARE_64_TO_SQUARE[y][x]] = squares2d[y][x];
            }
        }
        return squares;
    }

    private static int getCastleState(String castlingAvailability) {
        int whiteKingCastle = castlingAvailability.contains("K") ? WHITE_KING_CASTLE : 0;
        int whiteQueenCastle = castlingAvailability.contains("Q") ? WHITE_QUEEN_CASTLE : 0;
        int blackKingCastle = castlingAvailability.contains("k") ? BLACK_KING_CASTLE : 0;
        int blackQueenCastle = castlingAvailability.contains("q") ? BLACK_QUEEN_CASTLE : 0;
        return whiteKingCastle | whiteQueenCastle | blackKingCastle | blackQueenCastle;
    }

    private static Pair<int[][][], int[][]> getPieceSquares(int[] squares) {
        int[][][] pieceSquares = new int[6][][];
        int[][] numPieces = new int[6][3];
        for (int i = 0; i < 6; ++i) {
            pieceSquares[i] = ChessConstants.newInitialPieces();
            numPieces[i] = new int[] { 0, 0, 0 };
        }
        for (int y = 0; y < BOARD_WIDTH; ++y) {
            for (int x = 0; x < BOARD_WIDTH; ++x) {
                int square = SQUARE_64_TO_SQUARE[y][x];
                int piece = squares[square];
                switch (piece) {
                case UNPLAYED:
                    continue;
                case WHITE_PAWN:
                    pieceSquares[0][TwoPlayers.PLAYER_1][numPieces[0][TwoPlayers.PLAYER_1]++] = square;
                    continue;
                case BLACK_PAWN:
                    pieceSquares[0][TwoPlayers.PLAYER_2][numPieces[0][TwoPlayers.PLAYER_2]++] = square;
                    continue;
                case WHITE_KNIGHT:
                    pieceSquares[1][TwoPlayers.PLAYER_1][numPieces[1][TwoPlayers.PLAYER_1]++] = square;
                    continue;
                case BLACK_KNIGHT:
                    pieceSquares[1][TwoPlayers.PLAYER_2][numPieces[1][TwoPlayers.PLAYER_2]++] = square;
                    continue;
                case WHITE_BISHOP:
                    pieceSquares[2][TwoPlayers.PLAYER_1][numPieces[2][TwoPlayers.PLAYER_1]++] = square;
                    continue;
                case BLACK_BISHOP:
                    pieceSquares[2][TwoPlayers.PLAYER_2][numPieces[2][TwoPlayers.PLAYER_2]++] = square;
                    continue;
                case WHITE_ROOK:
                    pieceSquares[3][TwoPlayers.PLAYER_1][numPieces[3][TwoPlayers.PLAYER_1]++] = square;
                    continue;
                case BLACK_ROOK:
                    pieceSquares[3][TwoPlayers.PLAYER_2][numPieces[3][TwoPlayers.PLAYER_2]++] = square;
                    continue;
                case WHITE_QUEEN:
                    pieceSquares[4][TwoPlayers.PLAYER_1][numPieces[4][TwoPlayers.PLAYER_1]++] = square;
                    continue;
                case BLACK_QUEEN:
                    pieceSquares[4][TwoPlayers.PLAYER_2][numPieces[4][TwoPlayers.PLAYER_2]++] = square;
                    continue;
                case WHITE_KING:
                    pieceSquares[5][TwoPlayers.PLAYER_1][numPieces[5][TwoPlayers.PLAYER_1]++] = square;
                    continue;
                case BLACK_KING:
                    pieceSquares[5][TwoPlayers.PLAYER_2][numPieces[5][TwoPlayers.PLAYER_2]++] = square;
                    continue;
                default:
                    throw new IllegalStateException("Unknown piece: " + piece);
                }
            }
        }
        return Pair.valueOf(pieceSquares, numPieces);
    }

    private static int getEnpassantSquare(String enPassantTargetSquare) {
        if (enPassantTargetSquare.equals("-")) {
            return NO_SQUARE;
        }
        Coordinate coordinate = getCoordinate(enPassantTargetSquare);
        return SQUARE_64_TO_SQUARE[coordinate.y][coordinate.x];
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

    private static String getPiecePlacement(int[] squares) {
        List<String> piecePlacements = new ArrayList<>();
        for (int i = H8; i >= H1; i -= 10) {
            piecePlacements.add(getRowString(squares, i));
        }
        return String.join("/", piecePlacements);
    }

    private static String getRowString(int[] squares, int start) {
        StringBuffer rowSb = new StringBuffer();
        int i = BOARD_WIDTH - 1;
        do {
            if (squares[start + i] == UNPLAYED) {
                int numUnplayed = 0;
                while (i >= 0 && squares[start + i] == UNPLAYED) {
                    ++numUnplayed;
                    --i;
                }
                rowSb.append(numUnplayed);
            } else {
                rowSb.append(getPieceString(squares[start + i--]));
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

    private static String getEnPassantTargetSquare(int enPassantSquare) {
        return enPassantSquare == NO_SQUARE ? "-" : algebraicCoordinate(enPassantSquare);
    }

    public static String algebraicCoordinate(int square) {
        Coordinate coordinate = SQUARE_TO_COORDINATE[square];
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

    public static double[] getMaterialScore(int[] squares) {
        double[] materialScore = new double[3];
        for (int y = 0; y < BOARD_WIDTH; ++y) {
            for (int x = 0; x < BOARD_WIDTH; ++x) {
                int piece = squares[SQUARE_64_TO_SQUARE[y][x]];
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
