package gui.gamestate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import game.IGame;
import game.forkjoinexample.ForkJoinExampleGame;
import game.tictactoe.TicTacToeGame;
import game.ultimatetictactoe.UltimateTicTacToeGame;
import gui.Drawable;
import gui.GameGuiManager;

public class MainMenuState implements GameState {
	List<MenuItem> menuItems = new ArrayList<>();

	public MainMenuState() {
		menuItems.add(new MenuItem("Tic Tac Toe", TicTacToeGame.class, .25, .15, .75, .30));
		menuItems.add(new MenuItem("Ultimate Tic Tac Toe", UltimateTicTacToeGame.class, .20, .35, .80, .50));
		menuItems.add(new MenuItem("Fork Join Example", ForkJoinExampleGame.class, .20, .55, .80, .70));
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
		for (MenuItem menuItem : menuItems) {
			menuItem.drawOn(graphics);
		}
	}

	@Override
	public void componentResized() {
		// do nothing
	}

	@Override
	public void handleUserInput(UserInput input) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			for (MenuItem menuItem : menuItems) {
				if (menuItem.checkContainsCursor()) {
					GameGuiManager.setGame(menuItem.gameClass);
					break;
				}
			}
		}
	}

	private static class MenuItem implements Drawable {
		final String title;

		final Class<? extends IGame<?, ?>> gameClass;

		final double widthPercentStart;
		final double heightPercentStart;
		final double widthPercentEnd;
		final double heightPercentEnd;

		public MenuItem(String title, Class<? extends IGame<?, ?>> gameClass, double widthPercentStart, double heightPercentStart, double widthPercentEnd, double heightPercentEnd) {
			this.title = title;
			this.gameClass = gameClass;
			this.widthPercentStart = widthPercentStart;
			this.widthPercentEnd = widthPercentEnd;
			this.heightPercentStart = heightPercentStart;
			this.heightPercentEnd = heightPercentEnd;
		}

		@Override
		public void drawOn(Graphics2D graphics) {
			graphics.setColor(checkContainsCursor() ? Color.BLUE : Color.RED);
			graphics.drawRect(getX0(), getY0(), getX1() - getX0(), getY1() - getY0());
			drawCenteredString(graphics, title, getCenterX(), getCenterY());
		}

		private boolean checkContainsCursor() {
			return GameGuiManager.getMouseY() >= getY0() && GameGuiManager.getMouseY() <= getY1() && GameGuiManager.getMouseX() >= getX0() && GameGuiManager.getMouseX() <= getX1();
		}

		private int getX0() {
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
