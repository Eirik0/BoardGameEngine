package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import gui.gamestate.GameState.UserInput;

public class GameMouseAdapter extends MouseAdapter {
	private final MouseTracker mouseTracker;

	public GameMouseAdapter(MouseTracker mouseTracker) {
		this.mouseTracker = mouseTracker;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseTracker.handleUserInput(UserInput.LEFT_BUTTON_PRESSED);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseTracker.handleUserInput(UserInput.LEFT_BUTTON_RELEASED);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseTracker.setMouseEntered(true);
		mouseTracker.setMouseXY(e.getX(), e.getY());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseTracker.setMouseEntered(false);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseTracker.setMouseXY(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseTracker.setMouseXY(e.getX(), e.getY());
	}
}
