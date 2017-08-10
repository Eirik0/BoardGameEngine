package gui;

import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	private final GameImage gameImage;

	public GamePanel(GameImage gameImage) {
		this.gameImage = gameImage;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(gameImage.getImage(), 0, 0, null);
	}
}
