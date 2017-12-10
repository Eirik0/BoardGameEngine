package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	private final GameImage gameImage = new GameImage();
	private final Consumer<Graphics2D> drawFunction;
	private volatile boolean ignoreWait = false;
	private volatile boolean paintComplete = false;

	public GamePanel(Consumer<Graphics2D> drawFunction, BiConsumer<Integer, Integer> resizeFunction) {
		this.drawFunction = drawFunction;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				gameImage.checkResized(getWidth(), getHeight());
				resizeFunction.accept(Integer.valueOf(getWidth()), Integer.valueOf(getHeight()));
			}
		});
	}

	public void addToGameLoop(String name) {
		FixedDurationGameLoop.addRunnable(name, () -> {
			drawFunction.accept(gameImage.getGraphics());
			repaintAndWait();
		});
	}

	public void removeFromGameLoop(String name) {
		FixedDurationGameLoop.removeRunnable(name);
		synchronized (this) {
			ignoreWait = true;
			notify();
		}
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		g.drawImage(gameImage.getImage(), 0, 0, null);
		paintComplete = true;
		notify();
	}

	private synchronized void repaintAndWait() {
		paintComplete = false;
		repaint();
		while (!ignoreWait && !paintComplete) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
