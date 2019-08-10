package gui.movehistory;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import game.MoveHistory;
import game.MoveHistory.HistoryMove;
import game.MoveHistory.MoveIndex;
import gui.Drawable;
import gui.MouseTracker;
import main.BoardGameEngineMain;

public class GuiMoveHistory<M> implements Drawable {
	static final int MOVE_X_PADDING = 40;
	static final int MOVE_WIDTH = 66;

	private final int height;

	private final List<MoveMenuItem> menuItemList = new ArrayList<>();
	private final MoveMenuItem seletedMove;
	private final int maxMoves;
	private final MouseTracker mouseTracker;

	public GuiMoveHistory(MoveHistory<M> moveHistory, MouseTracker mouseTracker) {
		this.mouseTracker = mouseTracker;
		List<HistoryMove<M>> historyMoveList = moveHistory.getMoveHistoryListCopy();
		MoveIndex selectedIndex = moveHistory.selectedMoveIndex;
		MoveMenuItem seletedMove = null;
		int moveNum = 0;
		while (moveNum < historyMoveList.size()) {
			HistoryMove<M> historyMove = historyMoveList.get(moveNum);
			int playerNum = 0;
			do {
				if (historyMove.moves[playerNum] != null) {
					MoveMenuItem moveMenuItem = new MoveMenuItem(historyMove.moves[playerNum].toString(), moveNum, playerNum);
					if (moveNum == selectedIndex.moveNumber && playerNum == selectedIndex.playerNum) {
						seletedMove = moveMenuItem;
					}
					menuItemList.add(moveMenuItem);
				}
				++playerNum;
			} while (playerNum < historyMove.moves.length);
			++moveNum;
		}
		height = round(BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT * (historyMoveList.size() + 1.5));
		this.seletedMove = moveHistory.selectedMoveIndex.equals(moveHistory.maxMoveIndex) ? null : seletedMove;
		maxMoves = historyMoveList.size();
	}

	public int getHeight() {
		return height;
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		graphics.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
		graphics.setFont(BoardGameEngineMain.DEFAULT_SMALL_FONT);
		int moveNum = 0;
		do {
			int y = BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT * (moveNum + 1);
			drawCenteredYString(graphics, (moveNum + 1) + ". ", 10, y);
			++moveNum;
		} while (moveNum < maxMoves);
		for (MoveMenuItem moveMenuItem : menuItemList) {
			moveMenuItem.drawOn(graphics);
		}
		if (mouseTracker.isMouseEntered) {
			for (MoveMenuItem moveMenuItem : menuItemList) {
				if (moveMenuItem.checkContainsCursor(mouseTracker)) {
					moveMenuItem.highlightMove(graphics);
					break;
				}
			}
		}
		if (seletedMove != null) {
			seletedMove.highlightMove(graphics);
		}
	}

	public void addMove(M move, int moveNum, int playerNum) {
		menuItemList.add(new MoveMenuItem(move.toString(), moveNum, playerNum));
	}

	public MoveMenuItem getSelectedMove() {
		for (MoveMenuItem moveMenuItem : menuItemList) {
			if (moveMenuItem.checkContainsCursor(mouseTracker)) {
				return moveMenuItem;
			}
		}
		return null;
	}

	static class MoveMenuItem implements Drawable {
		final String moveString;
		public final int moveNum;
		public final int playerNum;
		private final int x0;
		private final int y0;
		private final int x1;
		private final int y1;

		public MoveMenuItem(String moveString, int moveNum, int playerNum) {
			this.moveString = moveString;
			this.moveNum = moveNum;
			this.playerNum = playerNum;
			x0 = MOVE_X_PADDING + playerNum * MOVE_WIDTH;
			x1 = x0 + MOVE_WIDTH - 1;
			y0 = round(BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT * (moveNum + 0.5));
			y1 = y0 + BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT - 1;
		}

		@Override
		public void drawOn(Graphics2D graphics) {
			drawCenteredString(graphics, BoardGameEngineMain.DEFAULT_SMALL_FONT, moveString, x0 + MOVE_WIDTH / 2, y0 + BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT / 2);
		}

		public void highlightMove(Graphics2D graphics) {
			graphics.drawRect(x0, y0, MOVE_WIDTH - 1, BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT - 1);
		}

		boolean checkContainsCursor(MouseTracker mouseTracker) {
			return mouseTracker.isMouseEntered && mouseTracker.mouseY >= y0 && mouseTracker.mouseY <= y1 && mouseTracker.mouseX >= x0 && mouseTracker.mouseX <= x1;
		}
	}
}
