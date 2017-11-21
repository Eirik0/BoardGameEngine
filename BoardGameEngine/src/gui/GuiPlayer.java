package gui;

import game.IPlayer;
import game.IPosition;

public class GuiPlayer implements IPlayer {
	public static final String NAME = "Human";

	public static final GuiPlayer HUMAN = new GuiPlayer();

	private volatile boolean isRequestingMove = false;
	private volatile boolean isGameRunning = true;

	private Object move = null;

	private GuiPlayer() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <M, P extends IPosition<M, P>> M getMove(P position) {
		move = null;
		isRequestingMove = true;
		isGameRunning = true;
		try {
			while (move == null && isGameRunning) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			return (M) move;
		} finally {
			isRequestingMove = false;
		}
	}

	@Override
	public void notifyTurnEnded() {
		// Do nothing
	}

	@Override
	public synchronized void notifyGameEnded() {
		isGameRunning = false;
		notify();
	}

	public boolean isRequestingMove() {
		return isRequestingMove;
	}

	public synchronized void setMove(Object move) {
		this.move = move;
		notify();
	}

	@Override
	public String toString() {
		return NAME;
	}
}
