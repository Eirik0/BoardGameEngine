package gui.analysis;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.function.Consumer;

import analysis.ComputerPlayer;
import analysis.search.ThreadNumber;
import game.TwoPlayers;
import gui.DrawingMethods;
import main.BoardGameEngineMain;

public class ComputerPlayerObserver implements DrawingMethods {
	private static final int MS_PER_UPDATE = DrawingMethods.roundS(1000.0 / 60);

	private final int playerNum;

	private ComputerPlayerResult currentResult = new ComputerPlayerResult(null, null, 0);

	private volatile boolean keepObserving = true;

	public ComputerPlayerObserver(ComputerPlayer computerPlayer, int playerNum, Consumer<String> nameConsumer, Consumer<String> currentDepthConsumer) {
		this.playerNum = playerNum;
		new Thread(() -> {
			nameConsumer.accept(computerPlayer.toString() + "...");
			synchronized (this) {
				do {
					currentResult = computerPlayer.getCurrentResult();
					currentDepthConsumer.accept(String.format("depth = %-3d", currentResult.depth));
					try {
						wait(MS_PER_UPDATE);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				} while (keepObserving);
				nameConsumer.accept(computerPlayer.toString());
				currentDepthConsumer.accept(String.format("depth = %-3d", currentResult.depth));
			}
		}, "Computer_Observation_Thread_" + ThreadNumber.getThreadNum(getClass())).start();
	}

	public ComputerPlayerResult getCurrentResult() {
		return currentResult;
	}

	public void drawOn(Graphics2D graphics, int width, int height) {
		List<ObservedMoveWithScore> currentMoves = currentResult.moves;
		if (currentMoves == null) {
			return;
		}
		fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		graphics.setFont(BoardGameEngineMain.DEFAULT_FONT_SMALL);
		FontMetrics metrics = graphics.getFontMetrics();
		int stringHeight = metrics.getHeight() + 2;
		int i = 0;
		int startY = stringHeight;
		while (i < currentMoves.size()) {
			int y = startY + i * stringHeight;
			ObservedMoveWithScore moveWithScore = currentMoves.get(i);
			graphics.setColor(moveWithScore.isPartial ? BoardGameEngineMain.FOREGROUND_COLOR : BoardGameEngineMain.LIGHTER_FOREGROUND_COLOR);
			String indexString = i < 9 ? (i + 1) + ".   " : (i + 1) + ". ";
			double playerScore = (playerNum == TwoPlayers.PLAYER_1 ? moveWithScore.score : -moveWithScore.score) + 0.0;
			String scoreString = Double.isFinite(playerScore) ? String.format("(%.2f)", playerScore) : "(" + playerScore + ")";
			graphics.drawString(indexString, 20, y);
			graphics.drawString(String.format("%-13s", scoreString), 45, y);
			graphics.drawString(moveWithScore.move.toString(), 100, y);
			++i;
		}
	}

	public synchronized void stopObserving() {
		keepObserving = false;
		notify();
	}
}
