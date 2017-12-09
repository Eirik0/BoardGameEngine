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
	private final GameImage gameImage;
	private final FixedDurationGameLoop gameLoop;
	private volatile boolean paintComplete = false;

	public GamePanel(Consumer<Graphics2D> drawFunction, BiConsumer<Integer, Integer> resizeFunction) {
		gameImage = new GameImage();
		gameLoop = new FixedDurationGameLoop(() -> {
			drawFunction.accept(gameImage.getGraphics());
			repaintAndWait();
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				gameImage.checkResized(getWidth(), getHeight());
				resizeFunction.accept(getWidth(), getHeight());
			}
		});
	}

	public void startGameLoop(String threadName) {
		new Thread(() -> gameLoop.runLoop(), threadName).start();
	}

	public void stopGameLoop() {
		gameLoop.stop();
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
		while (!paintComplete) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
