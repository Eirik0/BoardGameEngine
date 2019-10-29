package bge.game.chess;

import static bge.game.chess.ChessConstants.newInitialBishops;
import static bge.game.chess.ChessConstants.newInitialKingSquares;
import static bge.game.chess.ChessConstants.newInitialKnights;
import static bge.game.chess.ChessConstants.newInitialMaterialScore;
import static bge.game.chess.ChessConstants.newInitialNumBishops;
import static bge.game.chess.ChessConstants.newInitialNumKnights;
import static bge.game.chess.ChessConstants.newInitialNumPawns;
import static bge.game.chess.ChessConstants.newInitialNumQueens;
import static bge.game.chess.ChessConstants.newInitialNumRooks;
import static bge.game.chess.ChessConstants.newInitialPawns;
import static bge.game.chess.ChessConstants.newInitialPosition;
import static bge.game.chess.ChessConstants.newInitialQueens;
import static bge.game.chess.ChessConstants.newInitialRooks;

import bge.game.chess.move.BasicChessMove;
import bge.game.chess.move.CastleMove;
import bge.game.chess.move.EnPassantCaptureMove;
import bge.game.chess.move.IChessMove;
import bge.game.chess.move.KingMove;
import bge.game.chess.move.PawnPromotionMove;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.player.TwoPlayers;

public class ChessPosition implements IPosition<IChessMove>, ChessConstants {
    public final int[] squares;

    public final ChessPositionHistory positionHistory;

    public int[][] pawns;
    public int[][] knights;
    public int[][] bishops;
    public int[][] rooks;
    public int[][] queens;
    public int[] numPawns;
    public int[] numKnights;
    public int[] numBishops;
    public int[] numRooks;
    public int[] numQueens;

    public int[] kingSquares; // 0 = null, 1 = whiteKing, 2 = blackKing

    public boolean white;

    public int currentPlayer;
    public int otherPlayer;

    public int castleState;
    public int enPassantSquare;

    public int halfMoveClock; // The number of half moves since the last capture or pawn advance

    public double[] materialScore;

    public long zobristHash;

    public boolean threefoldDrawn;

    public ChessPosition() {
        this(newInitialPosition(), new ChessPositionHistory(),
                newInitialPawns(), newInitialKnights(), newInitialBishops(), newInitialRooks(), newInitialQueens(),
                newInitialNumPawns(), newInitialNumKnights(), newInitialNumBishops(), newInitialNumRooks(), newInitialNumQueens(),
                newInitialKingSquares(),
                TwoPlayers.PLAYER_1, TwoPlayers.PLAYER_2, true,
                ALL_CASTLES, NO_SQUARE, 0,
                newInitialMaterialScore(),
                ChessPositionHasher.INITIAL_BOARD_HASH,
                false);
    }

    public ChessPosition(int[] squares, ChessPositionHistory positionHistory,
            int[][] pawns, int[][] knights, int[][] bishops, int[][] rooks, int[][] queens,
            int[] numPawns, int[] numKnights, int[] numBishops, int[] numRooks, int[] numQueens,
            int[] kingSquares,
            int currentPlayer, int otherPlayer, boolean white,
            int castleState, int enPassantSquare, int halfMoveClock,
            double[] materialScore,
            long zobristHash,
            boolean threefoldDrawn) {
        this.squares = squares;
        this.positionHistory = positionHistory;
        this.pawns = pawns;
        this.knights = knights;
        this.bishops = bishops;
        this.rooks = rooks;
        this.queens = queens;
        this.numPawns = numPawns;
        this.numKnights = numKnights;
        this.numBishops = numBishops;
        this.numRooks = numRooks;
        this.numQueens = numQueens;
        this.enPassantSquare = enPassantSquare;
        this.currentPlayer = currentPlayer;
        this.otherPlayer = otherPlayer;
        this.white = white;
        this.castleState = castleState;
        this.kingSquares = kingSquares;
        this.halfMoveClock = halfMoveClock;
        this.materialScore = materialScore;
        this.zobristHash = zobristHash;
        this.threefoldDrawn = threefoldDrawn;
    }

    @Override
    public void getPossibleMoves(MoveList<IChessMove> possibleMoves) {
        if (halfMoveClock == 100 || threefoldDrawn) {
            return;
        }

        // Pawns
        int pawnOffset = white ? PAWN_OFFSET : -PAWN_OFFSET;
        int[] playerPawns = pawns[currentPlayer];
        int numPlayerPawns = numPawns[currentPlayer];
        int i = 0;
        while (i < numPlayerPawns) {
            addPawnMoves(possibleMoves, playerPawns[i], pawnOffset, white);
            ++i;
        }
        addEnPassantCaptures(possibleMoves, pawnOffset);
        // Knights
        int[] playerKnights = knights[currentPlayer];
        int numPlayerKnights = numKnights[currentPlayer];
        i = 0;
        while (i < numPlayerKnights) {
            addKnightMoves(possibleMoves, playerKnights[i]);
            ++i;
        }
        // Bishops
        int[] playerBishops = bishops[currentPlayer];
        int numPlayerBishops = numBishops[currentPlayer];
        i = 0;
        while (i < numPlayerBishops) {
            addSlidingMoves(possibleMoves, playerBishops[i], BISHOP_OFFSETS, BISHOP_OFFSETS.length);
            ++i;
        }
        // Rooks
        int[] playerRooks = rooks[currentPlayer];
        int numPlayerRooks = numRooks[currentPlayer];
        i = 0;
        while (i < numPlayerRooks) {
            addSlidingMoves(possibleMoves, playerRooks[i], ROOK_OFFSETS, ROOK_OFFSETS.length);
            ++i;
        }
        // Queens
        int[] playerQueens = queens[currentPlayer];
        int numPlayerQueens = numQueens[currentPlayer];
        i = 0;
        while (i < numPlayerQueens) {
            addSlidingMoves(possibleMoves, playerQueens[i], QUEEN_OFFSETS, QUEEN_OFFSETS.length);
            ++i;
        }
        // King
        addKingMoves(possibleMoves, kingSquares[currentPlayer]);
        addCastlingMoves(possibleMoves);
    }

    private void addPossibleMove(MoveList<IChessMove> possibleMoves, IChessMove chessMove) {
        // Move the pieces and then check if the king is attacked before adding the move
        chessMove.applyMove(this);
        int kingSquare = kingSquares[currentPlayer];
        if (!ChessFunctions.isSquareAttacked(this, kingSquare, otherPlayer)) {
            if (chessMove.getPieceCaptured() == 0) {
                possibleMoves.addQuietMove(chessMove, this);
            } else {
                possibleMoves.addDynamicMove(chessMove, this);
            }
        }
        chessMove.unapplyMove(this);
    }

    private void addPawnMoves(MoveList<IChessMove> possibleMoves, int from, int offset, boolean white) {
        boolean isStartingPawnRank = white ? ChessFunctions.isRank2(from) : ChessFunctions.isRank7(from);
        boolean pawnPromotes = white ? ChessFunctions.isRank7(from) : ChessFunctions.isRank2(from);
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

    private void addPawnMove(MoveList<IChessMove> possibleMoves, BasicChessMove basicMove, int pawn, boolean pawnPromotes) {
        if (pawnPromotes) {
            addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | QUEEN, pawn));
            addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | ROOK, pawn));
            addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | BISHOP, pawn));
            addPossibleMove(possibleMoves, new PawnPromotionMove(basicMove, currentPlayer | KNIGHT, pawn));
        } else {
            addPossibleMove(possibleMoves, basicMove);
        }
    }

    private void addPawnCapture(MoveList<IChessMove> possibleMoves, int from, int to, int pawn, boolean pawnPromotes) {
        int capture = squares[to];
        if ((capture & SENTINEL) == otherPlayer) {
            addPawnMove(possibleMoves, new BasicChessMove(from, to, capture, NO_SQUARE), pawn, pawnPromotes);
        }
    }

    private void addEnPassantCaptures(MoveList<IChessMove> possibleMoves, int offset) {
        if (enPassantSquare == NO_SQUARE) {
            return;
        }
        int playerPawn = currentPlayer | PAWN;
        addEnPassantCapture(possibleMoves, enPassantSquare - offset + 1, offset, playerPawn);
        addEnPassantCapture(possibleMoves, enPassantSquare - offset - 1, offset, playerPawn);
    }

    private void addEnPassantCapture(MoveList<IChessMove> possibleMoves, int from, int offset, int playerPawn) {
        if (squares[from] == playerPawn) {
            int captureSquare = enPassantSquare - offset;
            BasicChessMove basicChessMove = new BasicChessMove(from, enPassantSquare, squares[captureSquare], NO_SQUARE);
            addPossibleMove(possibleMoves, new EnPassantCaptureMove(basicChessMove, captureSquare));
        }
    }

    private void addKnightMoves(MoveList<IChessMove> possibleMoves, int from) {
        int i = 0;
        while (i < KNIGHT_OFFSETS.length) {
            int offset = KNIGHT_OFFSETS[i];
            int to = from + offset;
            int pieceCaptured = squares[to];
            if ((pieceCaptured & currentPlayer) != currentPlayer) {
                addPossibleMove(possibleMoves, new BasicChessMove(from, to, pieceCaptured, NO_SQUARE));
            }
            ++i;
        }
    }

    private void addKingMoves(MoveList<IChessMove> possibleMoves, int from) {
        int i = 0;
        while (i < KING_OFFSETS.length) {
            int offset = KING_OFFSETS[i];
            int to = from + offset;
            int pieceCaptured = squares[to];
            if ((pieceCaptured & currentPlayer) != currentPlayer) {
                BasicChessMove basicMove = new BasicChessMove(from, to, pieceCaptured, NO_SQUARE);
                addPossibleMove(possibleMoves, new KingMove(basicMove));
            }
            ++i;
        }
    }

    private void addSlidingMoves(MoveList<IChessMove> possibleMoves, int from, int[] offsets, int numOffsets) {
        int i = 0;
        while (i < numOffsets) {
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
            ++i;
        }
    }

    private void addCastlingMoves(MoveList<IChessMove> possibleMoves) {
        if (white) {
            addCastlingMoves(possibleMoves, E1, WHITE_KING_CASTLE, WHITE_QUEEN_CASTLE);
        } else {
            addCastlingMoves(possibleMoves, E8, BLACK_KING_CASTLE, BLACK_QUEEN_CASTLE);
        }
    }

    private void addCastlingMoves(MoveList<IChessMove> possibleMoves, int kingSquare, int kingCastle, int queenCastle) {
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
        int pieceCaptured = move.getPieceCaptured();

        if (pieceCaptured != UNPLAYED) {
            zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[pieceCaptured][to];
        }

        zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[squares[from]][from];
        zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[squares[from]][to];

        zobristHash ^= ChessPositionHasher.CASTLE_HASHES[castleState];
        castleState &= CASTLING_PERMISSIONS[from];
        castleState &= CASTLING_PERMISSIONS[to];
        zobristHash ^= ChessPositionHasher.CASTLE_HASHES[castleState];

        if (enPassantSquare != NO_SQUARE) {
            zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[UNPLAYED][enPassantSquare];
        }
        enPassantSquare = move.getEnPassantSquare();
        if (enPassantSquare != NO_SQUARE) {
            zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[UNPLAYED][enPassantSquare];
        }

        if (move.getClass() == CastleMove.class) {
            // Also hash the rook out of its old square and into its new square
            CastleMove castleMove = (CastleMove) move;
            int rookFrom = castleMove.getRookFrom();
            int rookTo = castleMove.getRookTo();
            int rook = squares[rookFrom];

            zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[rook][rookFrom];
            zobristHash ^= ChessPositionHasher.PIECE_POSITION_HASHES[rook][rookTo];
        }

        halfMoveClock = pieceCaptured != 0 || (squares[from] & PIECE_MASK) == PAWN ? 0 : halfMoveClock + 1;

        move.updateMaterial(this);
        move.applyMove(this);

        otherPlayer = currentPlayer;
        currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
        white = !white;
        zobristHash ^= ChessPositionHasher.WHITE_TURN_HASH;

        int repetitionCount = 0;
        for (int i = 0; i < halfMoveClock; i++) {
            if (positionHistory.undoChessMoves[positionHistory.plyCount - i - 1].zobristHash == zobristHash) {
                repetitionCount++;
            }
        }

        if (repetitionCount == 3) {
            threefoldDrawn = true;
        }
    }

    @Override
    public void unmakeMove(IChessMove move) {
        threefoldDrawn = false;
        white = !white;
        otherPlayer = currentPlayer;
        currentPlayer = TwoPlayers.otherPlayer(currentPlayer);

        move.unapplyMove(this);
        move.unupdateMaterial(this);

        positionHistory.unmakeMove(this);
    }

    @Override
    public ChessPosition createCopy() {
        return ChessFunctions.copyBoard(this);
    }
}
