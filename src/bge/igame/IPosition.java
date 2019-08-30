package bge.igame;

public interface IPosition<M> extends IDeepCopy<IPosition<M>> {
    void getPossibleMoves(MoveList<M> moveList);

    int getCurrentPlayer();

    void makeMove(M move);

    void unmakeMove(M move);
}
