package bge.igame;

public interface IGame<M, P extends IPosition<M>> {
    String getName();

    int getNumberOfPlayers();

    default int getPlayerIndexOffset() {
        return 1;
    }

    int getMaxMoves();

    P newInitialPosition();
}
