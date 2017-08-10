package gui;

import game.IPlayer;
import game.IPosition;

public class GuiPlayer implements IPlayer {
	private static final Object NULL_MOVE = new Object();

	public static final GuiPlayer HUMAN = new GuiPlayer();

	private volatile boolean isRequestingMove = false;

	private Object move = null;

	private GuiPlayer() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <M, P extends IPosition<M, P>> M getMove(IPosition<M, P> position) {
		move = null;
		isRequestingMove = true;
		try {
			while (move == null) {
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
	public void notifyGameEnded() {
		setMove(NULL_MOVE);
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
		return "Human";
	}
}
