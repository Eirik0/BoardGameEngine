package gui;

import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	private final GameImage gameImage;
	private volatile boolean paintComplete = false;

	public GamePanel(GameImage gameImage) {
		this.gameImage = gameImage;
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		g.drawImage(gameImage.getImage(), 0, 0, null);
		paintComplete = true;
		notify();
	}

	public synchronized void repaintAndWait() {
		paintComplete = false;
		repaint();
		while (!paintComplete) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
