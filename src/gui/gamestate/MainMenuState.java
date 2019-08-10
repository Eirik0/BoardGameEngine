package gui.gamestate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gui.Drawable;
import gui.GameGuiManager;
import gui.GameRegistry;
import main.BoardGameEngineMain;

public class MainMenuState implements GameState {
	private final List<MenuItem> menuItems = new ArrayList<>();

	public MainMenuState() {
		Set<String> gameNames = GameRegistry.getGameNames();

		double widthPercentStart = 0.25;
		double widthPercentEnd = 0.75;
		double gap = 0.05;
		double height = 1.0 / gameNames.size() - gap * (gameNames.size() + 1) / gameNames.size();

		double currentHeight = gap;
		for (String gameName : gameNames) {
			menuItems.add(new MenuItem(gameName, widthPercentStart, currentHeight, widthPercentEnd, currentHeight + height));
			currentHeight += height + gap;
		}
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		graphics.setColor(BoardGameEngineMain.BACKGROUND_COLOR);
		graphics.fillRect(0, 0, GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
		for (MenuItem menuItem : menuItems) {
			menuItem.drawOn(graphics);
		}
	}

	@Override
	public void componentResized(int width, int height) {
		// do nothing
	}

	@Override
	public void handleUserInput(UserInput input) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			for (MenuItem menuItem : menuItems) {
				if (menuItem.checkContainsCursor()) {
					GameGuiManager.setGame(menuItem.gameName);
					break;
				}
			}
		}
	}

	static class MenuItem implements Drawable {
		final String gameName;

		final double widthPercentStart;
		final double heightPercentStart;
		final double widthPercentEnd;
		final double heightPercentEnd;

		public MenuItem(String gameName, double widthPercentStart, double heightPercentStart, double widthPercentEnd, double heightPercentEnd) {
			this.gameName = gameName;
			this.widthPercentStart = widthPercentStart;
			this.widthPercentEnd = widthPercentEnd;
			this.heightPercentStart = heightPercentStart;
			this.heightPercentEnd = heightPercentEnd;
		}

		@Override
		public void drawOn(Graphics2D graphics) {
			graphics.setColor(checkContainsCursor() ? Color.BLUE : Color.RED);
			graphics.drawRect(getX0(), getY0(), getX1() - getX0(), getY1() - getY0());
			drawCenteredString(graphics, gameName, getCenterX(), getCenterY());
		}

		boolean checkContainsCursor() {
			return GameGuiManager.isMouseEntered() && GameGuiManager.getMouseY() >= getY0() && GameGuiManager.getMouseY() <= getY1()
					&& GameGuiManager.getMouseX() >= getX0() && GameGuiManager.getMouseX() <= getX1();
		}

		int getX0() {
			return round(GameGuiManager.getComponentWidth() * widthPercentStart);
		}

		private int getY0() {
			return round(GameGuiManager.getComponentHeight() * heightPercentStart);
		}

		private int getX1() {
			return round(GameGuiManager.getComponentWidth() * widthPercentEnd);
		}

		private int getY1() {
			return round(GameGuiManager.getComponentHeight() * heightPercentEnd);
		}

		private double getCenterX() {
			return (GameGuiManager.getComponentWidth() * (widthPercentStart + widthPercentEnd)) / 2;
		}

		private double getCenterY() {
			return (GameGuiManager.getComponentHeight() * (heightPercentStart + heightPercentEnd)) / 2;
		}
	}
}
