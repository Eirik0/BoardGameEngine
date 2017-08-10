package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import main.BoardGameEngineMain;

public class FixedDurationGameLoop {
	private static final double TARGET_FPS = 60;

	public static final long NANOS_PER_SECOND = 1000000000; // in nanoseconds
	public static final long NANOS_PER_MILLISECOND = 1000000;

	private final JComponent component;

	private BufferedImage image;
	private Graphics2D graphics;

	public FixedDurationGameLoop(JComponent component) {
		this.component = component;
		resizeImage(BoardGameEngineMain.DEFAULT_WIDTH, BoardGameEngineMain.DEFAULT_HEIGHT);
	}

	public void runLoop() {
		long loopStart;
		for (;;) {
			loopStart = System.nanoTime();
			redraw();
			double timeToSleep = NANOS_PER_SECOND / TARGET_FPS - (System.nanoTime() - loopStart);
			if (timeToSleep > 0) {
				try {
					Thread.sleep(Math.round(timeToSleep / NANOS_PER_MILLISECOND));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public synchronized void checkResized() {
		if (component.getWidth() <= 0 || component.getHeight() < 0) {
			return;
		}
		if (image.getWidth() != component.getWidth() || image.getHeight() != component.getHeight()) {
			resizeImage(component.getWidth(), component.getHeight());
		}
	}

	private void resizeImage(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		graphics = image.createGraphics();
		GameGuiManager.setComponentSize(width, height);
	}

	public synchronized void redraw() {
		GameGuiManager.getGameState().drawOn(graphics);
		Graphics g = component.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}
}
