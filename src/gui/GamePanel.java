package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JPanel;

import main.BoardGameEngineMain;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	private final GamePanelController controller;

	public GamePanel(Consumer<Graphics2D> drawFunction, BiConsumer<Integer, Integer> resizeFunction) {
		setBackground(BoardGameEngineMain.BACKGROUND_COLOR);
		controller = new GamePanelController(drawFunction);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				controller.gameImage.checkResized(getWidth(), getHeight());
				resizeFunction.accept(Integer.valueOf(getWidth()), Integer.valueOf(getHeight()));
			}
		});
	}

	public void addToGameLoop(String name) {
		controller.addToGameLoop(name, this::repaint);
	}

	public void removeFromGameLoop(String name) {
		controller.removeFromGameLoop(name);
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		controller.drawOn(g);
	}
}
