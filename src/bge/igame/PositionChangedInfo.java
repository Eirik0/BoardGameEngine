package bge.igame;

import java.util.List;

import bge.igame.MoveHistory.HistoryMove;
import bge.igame.MoveHistory.MoveIndex;
import bge.igame.player.IPlayer;

public class PositionChangedInfo<M> {
    public final IPosition<M> position;
    public final MoveList<M> possibleMoves;
    public final IPlayer currentPlayer;
    public final M lastMove;
    public final List<HistoryMove<M>> moveHistoryList;
    public final MoveIndex historyMoveIndex;
    public final boolean isRunning;

    public PositionChangedInfo(IPosition<M> position, MoveList<M> possibleMoves, IPlayer currentPlayer, M lastMove,
            List<HistoryMove<M>> moveHistoryList, MoveIndex historyMoveIndex, boolean isRunning) {
        this.position = position;
        this.possibleMoves = possibleMoves;
        this.currentPlayer = currentPlayer;
        this.moveHistoryList = moveHistoryList;
        this.lastMove = lastMove;
        this.historyMoveIndex = historyMoveIndex;
        this.isRunning = isRunning;
    }
}
