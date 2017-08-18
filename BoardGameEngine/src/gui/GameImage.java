package gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.BoardGameEngineMain;

public class GameImage {
	private BufferedImage image;
	private Graphics2D graphics;

	public GameImage() {
		resizeImage(BoardGameEngineMain.DEFAULT_WIDTH, BoardGameEngineMain.DEFAULT_HEIGHT);
	}

	public BufferedImage getImage() {
		return image;
	}

	public Graphics2D getGraphics() {
		return graphics;
	}

	private void resizeImage(int width, int height) {
		BufferedImage oldImage = image;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		graphics = image.createGraphics();
		graphics.drawImage(oldImage, 0, 0, width, height, null);
	}

	public void checkResized(int width, int height) {
		if (width <= 0 || height <= 0) {
			return;
		}
		if (image.getWidth() != width || image.getHeight() != height) {
			resizeImage(width, height);
		}
	}
}
