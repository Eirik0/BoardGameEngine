package bge.igame;

public interface IGame<M> {
    String getName();

    int getNumberOfPlayers();

    default int getPlayerIndexOffset() {
        return 1;
    }

    int getMaxMoves();

    IPosition<M> newInitialPosition();
}
