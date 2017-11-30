package game;

import java.util.List;

import analysis.search.ThreadNumber;
import gui.gamestate.IPositionObserver;

public class GameRunner<M, P extends IPosition<M, P>> {
	private volatile boolean stopRequested = false;
	private volatile boolean isRunning = false;

	private final GameObserver<M, P> gameObserver;
	private Runnable gameOverAction;
	private IPositionObserver<M, P> positionObserver;

	private final IGame<M, P> game;
	private final MoveListFactory<M> moveListFactory;
	private P position;

	private P positionCopy;
	private MoveList<M> possibleMovesCopy;
	private M lastMove;

	private IPlayer currentPlayer;

	public GameRunner(IGame<M, P> game, GameObserver<M, P> gameObserver, MoveListFactory<M> moveListFactory) {
		this.game = game;
		this.gameObserver = gameObserver;
		this.moveListFactory = moveListFactory;
		position = game.newInitialPosition();
		setPositionCopy();
	}

	public synchronized void setGameOverAction(Runnable gameOverAction) {
		this.gameOverAction = gameOverAction;
	}

	public void setPositionObserver(IPositionObserver<M, P> positionObserver) {
		this.positionObserver = positionObserver;
	}

	public P getCurrentPositionCopy() {
		return positionCopy;
	}

	public synchronized MoveList<M> getPossibleMovesCopy() {
		return possibleMovesCopy;
	}

	public M getLastMove() {
		return lastMove;
	}

	private void setPositionCopy() {
		P newPositionCopy = position.createCopy();
		MoveList<M> newPossibleMoves = moveListFactory.newArrayMoveList();
		newPositionCopy.getPossibleMoves(newPossibleMoves);
		positionCopy = newPositionCopy;
		possibleMovesCopy = newPossibleMoves;
		if (positionObserver != null) {
			positionObserver.notifyPositionChanged(positionCopy, possibleMovesCopy);
		}
		gameObserver.notifyPositionChanged(positionCopy);
	}

	public synchronized void startNewGame(List<IPlayer> players) {
		if (players.isEmpty()) {
			throw new UnsupportedOperationException("Running a game with no players is not supported.");
		}

		if (isRunning) {
			endGame();
		}

		position = game.newInitialPosition();
		lastMove = null;
		setPositionCopy();
		if (possibleMovesCopy.size() == 0) {
			notifyGameStarted();
		}

		new Thread(() -> {
			try {
				int playerNum = TwoPlayers.PLAYER_1;
				while (!stopRequested && getPossibleMovesCopy().size() > 0) {
					synchronized (this) {
						currentPlayer = players.get(playerNum - 1);
						gameObserver.notifyPlayerChanged(currentPlayer, playerNum);
					}
					if (!isRunning) {
						notifyGameStarted();
					}
					M move = currentPlayer.getMove(positionCopy);
					if (!stopRequested) {
						synchronized (this) {
							lastMove = move;
							position.makeMove(move);
							setPositionCopy();
							playerNum = positionCopy.getCurrentPlayer();
							currentPlayer.notifyTurnEnded();
						}
					}
				}
			} finally {
				for (IPlayer player : players) {
					player.notifyGameEnded();
				}
				notifyGameEnded();
				if (gameOverAction != null) {
					gameOverAction.run();
				}
			}
		}, "Game_Runner_Thread_" + ThreadNumber.getThreadNum(getClass())).start();

		waitForStart();
	}

	private synchronized void waitForStart() {
		while (!isRunning) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private synchronized void notifyGameStarted() {
		isRunning = true;
		notify();
	}

	public synchronized void endGame() {
		stopRequested = true;
		maybeNotifyPlayerGameEnded();
		while (isRunning) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		gameObserver.notifyGameEnded();
		stopRequested = false;
	}

	private synchronized void maybeNotifyPlayerGameEnded() {
		if (currentPlayer != null) {
			currentPlayer.notifyGameEnded();
		}
	}

	private synchronized void notifyGameEnded() {
		isRunning = false;
		currentPlayer = null;
		notify();
	}
}
