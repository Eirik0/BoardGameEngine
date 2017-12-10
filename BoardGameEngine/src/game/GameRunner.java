package game;

import java.util.List;

import analysis.search.ThreadNumber;
import gui.GuiPlayer;
import gui.gamestate.IPositionObserver;

public class GameRunner<M, P extends IPosition<M, P>> {
	public static final int NO_PLAYER = -1;

	private volatile boolean stopRequested = false;
	private volatile boolean isRunning = false;

	private final GameObserver<M, P> gameObserver;
	private IPositionObserver<M, P> positionObserver;

	private final IGame<M, P> game;
	private final MoveListFactory<M> moveListFactory;
	private P position;

	private final MoveHistory<M, P> moveHistory;
	private P positionCopy;
	private MoveList<M> possibleMovesCopy;
	private M lastMove;

	private List<IPlayer> players;
	private IPlayer currentPlayer;

	public GameRunner(IGame<M, P> game, GameObserver<M, P> gameObserver, MoveListFactory<M> moveListFactory) {
		this.game = game;
		this.gameObserver = gameObserver;
		this.moveListFactory = moveListFactory;
		moveHistory = new MoveHistory<M, P>(game.getNumberOfPlayers());
		position = game.newInitialPosition();
		setPositionCopy(NO_PLAYER, null, true);
	}

	public void setPositionObserver(IPositionObserver<M, P> positionObserver) {
		this.positionObserver = positionObserver;
	}

	public P getCurrentPositionCopy() {
		return positionCopy;
	}

	public MoveList<M> getPossibleMovesCopy() {
		return possibleMovesCopy;
	}

	public M getLastMove() {
		return lastMove;
	}

	private synchronized void setPositionCopy(int playerWhoMoved, IPlayer currentPlayer, boolean updateHistory) {
		P newPositionCopy = position.createCopy();
		MoveList<M> newPossibleMoves = moveListFactory.newArrayMoveList();
		newPositionCopy.getPossibleMoves(newPossibleMoves);
		positionCopy = newPositionCopy;
		possibleMovesCopy = newPossibleMoves;
		notifyPositionObserver();
		if (updateHistory) {
			moveHistory.addMove(lastMove, playerWhoMoved);
		}
		gameObserver.notifyPositionChanged(new PositionChangedInfo<>(positionCopy, currentPlayer, moveHistory));
	}

	private void notifyPositionObserver() {
		if (positionObserver != null) {
			positionObserver.notifyPositionChanged(positionCopy, possibleMovesCopy);
		}
	}

	public synchronized void createNewGame() {
		pauseGame(true);
		position = game.newInitialPosition();
		lastMove = null;
		setPositionCopy(NO_PLAYER, null, true);
	}

	public void setPlayersAndResume(List<IPlayer> players) {
		this.players = players;
		resumeGame();
	}

	public synchronized void resumeGame() {
		if (possibleMovesCopy.size() == 0) {
			notifyGameEnded(players);
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
					currentPlayer = players.get(playerToMove - 1);
					setPositionCopy(NO_PLAYER, currentPlayer, false);
				}
				while (!stopRequested && getPossibleMovesCopy().size() > 0) {
					M move = currentPlayer.getMove(positionCopy);
					if (!stopRequested) {
						synchronized (this) {
							lastMove = move;
							position.makeMove(move);
							currentPlayer.notifyTurnEnded();
							currentPlayer = players.get(position.getCurrentPlayer() - 1);
							setPositionCopy(playerToMove, currentPlayer, true); // now previous player
							playerToMove = positionCopy.getCurrentPlayer();
						}
					}
				}
			} finally {
				notifyGameEnded(players);
			}
		}, "Game_Runner_Thread_" + ThreadNumber.getThreadNum(getClass())).start();

		waitForRunningToBe(true);
		gameObserver.notifyGameRunning();
	}

	public synchronized void pauseGame(boolean notifyObserver) {
		stopRequested = true;
		if (currentPlayer != null) {
			currentPlayer.notifyGameEnded();
		}
		if (isRunning) {
			waitForRunningToBe(false);
		} else if (notifyObserver) {
			gameObserver.notifyGamePaused();
		}
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
		pauseGame(false);
		P newPosition = game.newInitialPosition();
		lastMove = moveHistory.setPositionFromHistory(newPosition, moveNumToFind, playerNumToFind);
		position = newPosition;
		if (players.get(position.getCurrentPlayer() - 1) instanceof GuiPlayer) {
			resumeGame();
		} else {
			setPositionCopy(NO_PLAYER, currentPlayer, false);
		}
	}

	private synchronized void notifyGameEnded(List<IPlayer> players) {
		for (IPlayer player : players) {
			player.notifyGameEnded();
		}
		gameObserver.notifyGamePaused();
		isRunning = false;
		currentPlayer = null;
		notify();
	}
}
