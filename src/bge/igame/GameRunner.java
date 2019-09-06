package bge.igame;

import java.util.List;

import bge.igame.player.IPlayer;
import gt.async.ThreadNumber;

public class GameRunner<M> {
    public static final int NO_PLAYER = -1;

    private volatile boolean stopRequested = false;
    private volatile boolean isRunning = false;
    private volatile boolean isSettingPosition = false;

    private final GameObserver<M> gameObserver;

    private final IGame<M, ? extends IPosition<M>> game;
    private final MoveListFactory<M> moveListFactory;
    private IPosition<M> position;

    private final MoveHistory<M> moveHistory;
    private IPosition<M> positionCopy;
    private MoveList<M> possibleMovesCopy;
    private M lastMove;

    private List<IPlayer> players;
    private IPlayer currentPlayer;

    public GameRunner(IGame<M, ? extends IPosition<M>> game, GameObserver<M> gameObserver, MoveListFactory<M> moveListFactory) {
        this.game = game;
        this.moveHistory = new MoveHistory<>(game.getNumberOfPlayers());
        this.gameObserver = gameObserver;
        this.moveListFactory = moveListFactory;
        position = game.newInitialPosition();
        setPositionCopy(NO_PLAYER, null, true);
    }

    private synchronized void setPositionCopy(int playerWhoMoved, IPlayer currentPlayer, boolean updateHistory) {
        IPosition<M> newPositionCopy = position.createCopy();
        MoveList<M> newPossibleMoves = moveListFactory.newArrayMoveList();
        newPositionCopy.getPossibleMoves(newPossibleMoves);
        positionCopy = newPositionCopy;
        possibleMovesCopy = newPossibleMoves;
        if (updateHistory) {
            moveHistory.addMove(lastMove, playerWhoMoved + 1 - game.getPlayerIndexOffset());
        }
        gameObserver.notifyPositionChanged(
                new PositionChangedInfo<>(positionCopy, possibleMovesCopy, currentPlayer, lastMove, moveHistory.getMoveHistoryListCopy(),
                        moveHistory.selectedMoveIndex, isRunning));
    }

    public synchronized void createNewGame() {
        pauseGame(true);
        position = game.newInitialPosition();
        lastMove = null;
        setPositionCopy(NO_PLAYER, null, true);
    }

    public synchronized void setPlayersAndResume(List<IPlayer> players) {
        this.players = players;
        resumeGame();
    }

    private synchronized void resumeGame() {
        if (possibleMovesCopy.size() == 0) {
            notifyGameEnded();
            return;
        }

        new Thread(() -> {
            try {
                synchronized (this) {
                    isRunning = true;
                    notify();
                }
                int playerToMove;
                synchronized (this) {
                    playerToMove = position.getCurrentPlayer();
                    currentPlayer = players.get(playerToMove - game.getPlayerIndexOffset());
                    setPositionCopy(NO_PLAYER, currentPlayer, false);
                }
                while (!stopRequested && possibleMovesCopy.size() > 0) {
                    M move = currentPlayer.getMove(positionCopy);
                    if (!stopRequested) {
                        synchronized (this) {
                            lastMove = move;
                            position.makeMove(move);
                            currentPlayer.notifyTurnEnded();
                            currentPlayer = players.get(position.getCurrentPlayer() - game.getPlayerIndexOffset());
                            setPositionCopy(playerToMove, currentPlayer, true); // now previous player
                            playerToMove = positionCopy.getCurrentPlayer();
                        }
                    }
                }
            } finally {
                notifyGameEnded();
            }
        }, "Game_Runner_Thread_" + ThreadNumber.getThreadNum(getClass())).start();

        waitForRunningToBe(true);
        gameObserver.notifyGameRunning();
    }

    public synchronized void pauseGame(boolean gameEnded) {
        stopRequested = true;
        if (currentPlayer != null) {
            currentPlayer.notifyGameEnded();
        }
        if (isRunning) {
            waitForRunningToBe(false);
        }
        gameObserver.notifyGamePaused(gameEnded);
        stopRequested = false;
    }

    private synchronized void waitForRunningToBe(boolean startStop) {
        while (isRunning != startStop) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void setPositionFromHistory(int moveNumToFind, int playerNumToFind) {
        isSettingPosition = true;
        pauseGame(false);
        isSettingPosition = false;
        IPosition<M> newPosition = game.newInitialPosition();
        lastMove = moveHistory.setPositionFromHistory(newPosition, moveNumToFind, playerNumToFind);
        position = newPosition;
        setPositionCopy(NO_PLAYER, currentPlayer, false);
    }

    private synchronized void notifyGameEnded() {
        for (IPlayer player : players) {
            player.notifyGameEnded();
        }
        // If we are setting the position from history we are about to resume the game, unless there are no moves
        if (!isSettingPosition || possibleMovesCopy.size() == 0) {
            gameObserver.notifyGamePaused(true);
        }
        isRunning = false;
        currentPlayer = null;
        notify();
    }
}
