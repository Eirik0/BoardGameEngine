package gui.analysis;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import analysis.AnalysisResult;
import analysis.ComputerPlayer;
import game.TwoPlayers;
import gui.DrawingMethods;
import gui.FixedDurationGameLoop;
import main.BoardGameEngineMain;

public class ComputerPlayerObserver implements DrawingMethods {
	public static final String NAME = "Computer Observer";

	private final ComputerPlayer computerPlayer;
	private int playerNum;
	private final Consumer<String> nameConsumer;
	private final Consumer<String> currentDepthConsumer;

	private ComputerPlayerResult currentResult = new ComputerPlayerResult(null, Collections.emptyList(), 0);

	public ComputerPlayerObserver(ComputerPlayer computerPlayer, int playerNum, Consumer<String> nameConsumer, Consumer<String> currentDepthConsumer) {
		this.computerPlayer = computerPlayer;
		this.playerNum = playerNum;
		this.nameConsumer = nameConsumer;
		this.currentDepthConsumer = currentDepthConsumer;
		nameConsumer.accept(computerPlayer.toString() + "...");
		FixedDurationGameLoop.addRunnable(NAME, () -> {
			currentResult = computerPlayer.getCurrentResult();
			currentDepthConsumer.accept(String.format("depth = %-3d", currentResult.depth));
			if (currentResult.isDecided) {
				stopObserving();
			}
		});
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	public void drawOn(Graphics2D graphics) {
		List<ObservedMoveWithScore> currentMoves = currentResult.moves;
		if (currentMoves == null) {
			return;
		}
		graphics.setFont(BoardGameEngineMain.DEFAULT_FONT_SMALL);
		FontMetrics metrics = graphics.getFontMetrics();
		int stringHeight = metrics.getHeight() + 2;
		int i = 0;
		int startY = stringHeight;
		while (i < currentMoves.size()) {
			int y = startY + i * stringHeight;
			ObservedMoveWithScore moveWithScore = currentMoves.get(i);
			graphics.setColor(moveWithScore.isPartial || AnalysisResult.isGameOver(moveWithScore.score) ? BoardGameEngineMain.FOREGROUND_COLOR : BoardGameEngineMain.LIGHTER_FOREGROUND_COLOR);
			graphics.drawString(i < 9 ? (i + 1) + ".   " : (i + 1) + ". ", 20, y);
			graphics.drawString(String.format("%-13s", getScoreString(moveWithScore.score, playerNum == TwoPlayers.PLAYER_1)), 45, y);
			graphics.drawString(moveWithScore.moveString, 100, y);
			++i;
		}
	}

	private static String getScoreString(double score, boolean isPlayerOne) {
		if (AnalysisResult.isDraw(score)) {
			return "(Draw)";
		} else if (AnalysisResult.WIN == score) {
			return "(Win)";
		} else if (AnalysisResult.LOSS == score) {
			return "(Loss)";
		} else {
			long playerScore = Math.round(100 * (isPlayerOne ? score : -score));
			double roundScore = playerScore / 100.0;
			return String.format("(%.2f)", Double.valueOf(roundScore));
		}
	}

	public void stopObserving() {
		FixedDurationGameLoop.removeRunnable(NAME);
		nameConsumer.accept(computerPlayer.toString());
		currentDepthConsumer.accept(String.format("depth = %-3d", currentResult.depth));
	}
}
