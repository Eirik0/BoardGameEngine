package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.Consumer;

public class GamePanelController {
	public final GameImage gameImage = new GameImage();
	private final Consumer<Graphics2D> drawFunction;

	private volatile boolean ignoreWait = false;
	private volatile boolean paintComplete = false;

	public GamePanelController(Consumer<Graphics2D> drawFunction) {
		this.drawFunction = drawFunction;
	}

	public void addToGameLoop(String name, Runnable repaintRunnable) {
		FixedDurationGameLoop.addRunnable(name, () -> {
			drawFunction.accept(gameImage.getGraphics());
			repaintAndWait(repaintRunnable);
		});
	}

	public void removeFromGameLoop(String name) {
		FixedDurationGameLoop.removeRunnable(name);
		synchronized (this) {
			ignoreWait = true;
			notify();
		}
	}

	private void repaintAndWait(Runnable repaintRunnable) {
		paintComplete = false;
		repaintRunnable.run();
		synchronized (this) {
			int tries = 0;
			while (!ignoreWait && !paintComplete && ++tries < 5) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void drawOn(Graphics g) {
		g.drawImage(gameImage.getImage(), 0, 0, null);
		synchronized (this) {
			paintComplete = true;
			notify();
		}
	}
}
