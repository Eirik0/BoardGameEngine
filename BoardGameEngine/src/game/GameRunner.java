package game;

import java.util.List;

import analysis.search.ThreadNumber;

public class GameRunner<M, P extends IPosition<M, P>> {
	private volatile boolean stopRequested = false;
	private volatile boolean isRunning = false;

	private Runnable endGameAction;

	private final IGame<M, P> game;
	private P position;

	private P positionCopy;
	private List<M> possibleMovesCopy;

	private IPlayer currentPlayer;

	public GameRunner(IGame<M, P> game) {
		this.game = game;
		position = game.newInitialPosition();
		setPositionCopy();
	}

	public void setEndGameAction(Runnable endGameAction) {
		this.endGameAction = endGameAction;
	}

	public P getCurrentPositionCopy() {
		return positionCopy;
	}

	public List<M> getPossibleMovesCopy() {
		return possibleMovesCopy;
	}

	private void setPositionCopy() {
		positionCopy = position.createCopy();
		possibleMovesCopy = positionCopy.getPossibleMoves();
	}

	public synchronized void startNewGame(List<IPlayer> players) {
		if (players.isEmpty()) {
			throw new UnsupportedOperationException("Running a game with no players is not supported.");
		}

		if (isRunning) {
			endGame();
		}

		position = game.newInitialPosition();
		if (position.getPossibleMoves().isEmpty()) {
			notifyGameStarted();
		}

		new Thread(() -> {
			try {
				int playerNum = 0;
				while (!stopRequested && position.getPossibleMoves().size() > 0) {
					currentPlayer = players.get(playerNum);
					if (!isRunning) {
						notifyGameStarted();
					}
					M move = currentPlayer.getMove(position);
					if (!stopRequested) {
						position.makeMove(move);
						setPositionCopy();
						playerNum = (playerNum + 1) % players.size();
					}
				}
			} finally {
				notifyGameEnded();
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
		stopRequested = false;
	}

	private void maybeNotifyPlayerGameEnded() {
		if (currentPlayer != null) {
			currentPlayer.notifyGameEnded();
		}
	}

	private synchronized void notifyGameEnded() {
		isRunning = false;
		maybeNotifyPlayerGameEnded();
		currentPlayer = null;
		if (endGameAction != null) {
			endGameAction.run();
		}
		notify();
	}
}
