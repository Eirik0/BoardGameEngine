package bge.game.chess;

import java.security.SecureRandom;
import java.util.Random;

public class ChessPositionHasher implements ChessConstants {
    public static final long INITIAL_BOARD_HASH;

    public static final long[][] PIECE_POSITION_HASHES = new long[NUM_PIECE_HASHES][BOARD_ARRAY_SIZE];
    public static final long WHITE_TURN_HASH;
    public static final long[] CASTLE_HASHES = new long[NUM_CASTLES];

    static {
        Random random = new SecureRandom(); // because Random.nextLong() has a 48-bit seed and does not return all longs

        for (int piece = 0; piece < ALL_PIECES.length; piece++) {
            for (int rank = 0; rank < BOARD_WIDTH; rank++) {
                for (int file = 0; file < BOARD_WIDTH; file++) {
                    PIECE_POSITION_HASHES[ALL_PIECES[piece]][SQUARE_64_TO_SQUARE[rank][file]] = random.nextLong();
                }
            }
        }

        WHITE_TURN_HASH = random.nextLong();

        for (int castle = 0; castle < NUM_CASTLES; castle++) {
            CASTLE_HASHES[castle] = random.nextLong();
        }

        INITIAL_BOARD_HASH = computeHash(
                ChessConstants.newInitialPosition(),
                true,
                ALL_CASTLES,
                NO_SQUARE);
    }

    public static long computeHash(int[] squares, boolean white, int castleState, int enPassantSquare) {
        long hash = 0;

        for (int square = 0; square < BOARD_ARRAY_SIZE; square++) {
            int piece = squares[square];
            if (piece > SENTINEL) {
                hash ^= PIECE_POSITION_HASHES[piece][square];
            }
        }

        if (white) {
            hash ^= WHITE_TURN_HASH;
        }

        hash ^= CASTLE_HASHES[castleState];

        if (enPassantSquare != NO_SQUARE) {
            hash ^= PIECE_POSITION_HASHES[UNPLAYED][enPassantSquare];
        }

        return hash;
    }
}
