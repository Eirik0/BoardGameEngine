package gui.analysis;

import java.awt.Graphics2D;

import main.BoardGameEngineMain;

public class InfiniteAnalysisState implements IAnalysisState {
	private int width;
	private int height;

	public InfiniteAnalysisState(int playerNum) {
	}

	@Override
	public void componentResized(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void handleUserInput(UserInput input) {
		// Do nothing
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		graphics.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
		graphics.drawString("Infinite Anaylsis", 20, 20);
	}

	@Override
	public void stopAnalysis() {
		// TODO Auto-generated method stub
	}
}
