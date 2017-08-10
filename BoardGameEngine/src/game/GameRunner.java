package game;

import java.util.List;

public class GameRunner<M, P extends IPosition<M, P>> {
	private volatile boolean stopRequested = false;
	private volatile boolean isRunning = false;

	private final IGame<M, P> game;
	private P position;

	public GameRunner(IGame<M, P> game) {
		this.game = game;
		position = game.newInitialPosition();
	}

	public P getCurrentPosition() {
		return position;
	}

	public void startNewGame(List<IPlayer> players) {
		if (players.isEmpty()) {
			throw new UnsupportedOperationException("Running a game with no players is not supported.");
		}

		if (isRunning) {
			endGame();
		}

		new Thread(() -> {
			notifyGameStarted();
			try {
				position = game.newInitialPosition();
				int playerNum = 0;
				while (!stopRequested && position.getPossibleMoves().size() > 0) {
					M move = players.get(playerNum).getMove(position);
					position.makeMove(move);
					playerNum = (playerNum + 1) % players.size();
				}
			} finally {
				notifyGameEnded();
			}
		}, "Game_Runner_Thread").start();

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
		while (isRunning) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		stopRequested = false;
	}

	private synchronized void notifyGameEnded() {
		isRunning = false;
		notify();
	}
}
