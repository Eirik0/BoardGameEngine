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

	public synchronized void drawOn(Graphics g) {
		g.drawImage(gameImage.getImage(), 0, 0, null);
		paintComplete = true;
		notify();
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

	private synchronized void repaintAndWait(Runnable repaintRunnable) {
		paintComplete = false;
		repaintRunnable.run();
		while (!ignoreWait && !paintComplete) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
