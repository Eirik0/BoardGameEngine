package gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.BoardGameEngineMain;

public class GameImage implements DrawingMethods {
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
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D newGraphics = newImage.createGraphics();
		fillRect(newGraphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		if (image != null) {
			newGraphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
		image = newImage;
		graphics = newGraphics;
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
