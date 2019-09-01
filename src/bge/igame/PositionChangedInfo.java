package bge.igame;

import java.util.List;

import bge.igame.MoveHistory.HistoryMove;
import bge.igame.player.IPlayer;

public class PositionChangedInfo<M> {
    public final IPosition<M> position;
    public final MoveList<M> possibleMoves;
    public final IPlayer currentPlayer;
    public final List<HistoryMove<M>> moveHistoryList;
    public final M lastMove;

    public PositionChangedInfo(IPosition<M> position, MoveList<M> possibleMoves, IPlayer currentPlayer, List<HistoryMove<M>> moveHistoryList, M lastMove) {
        this.position = position;
        this.possibleMoves = possibleMoves;
        this.currentPlayer = currentPlayer;
        this.moveHistoryList = moveHistoryList;
        this.lastMove = lastMove;
    }
}
