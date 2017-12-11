package gui.analysis;

import java.awt.Graphics2D;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import analysis.AnalysisResult;
import analysis.ComputerPlayer;
import game.TwoPlayers;
import gui.Drawable;
import gui.DrawingMethods;
import gui.FixedDurationGameLoop;
import gui.Sizable;
import main.BoardGameEngineMain;

public class ComputerPlayerObserver implements Drawable, Sizable, DrawingMethods {
	public static final String NAME = "Computer Observer";

	private int width;
	private int height;
	private int componentWidth;
	private int componentHeight;

	private final ComputerPlayer computerPlayer;
	private int playerNum;
	private final Consumer<String> nameConsumer;
	private final Consumer<String> currentDepthConsumer;

	private ComputerPlayerResult currentResult = new ComputerPlayerResult(null, Collections.emptyList(), 0);

	private Runnable onResize;

	public ComputerPlayerObserver() {
		computerPlayer = null;
		nameConsumer = null;
		currentDepthConsumer = null;
	}

	public ComputerPlayerObserver(ComputerPlayer computerPlayer, int playerNum, Consumer<String> nameConsumer, Consumer<String> currentDepthConsumer) {
		this.computerPlayer = computerPlayer;
		this.playerNum = playerNum;
		this.nameConsumer = nameConsumer;
		this.currentDepthConsumer = currentDepthConsumer;
		nameConsumer.accept(computerPlayer.toString() + "...");
		FixedDurationGameLoop.addRunnable(NAME, () -> {
			currentResult = computerPlayer.getCurrentResult();
			currentDepthConsumer.accept(String.format("depth = %-3d", currentResult.depth));
			if (currentResult.moves != null) {
				height = BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT * (currentResult.moves.size() + 1);
				if (onResize != null) {
					onResize.run();
				}
			}
			if (currentResult.isDecided) {
				stopObserving();
			}
		});
	}

	@Override
	public void checkResized(int width, int height) {
		componentWidth = width;
		componentHeight = height;
	}

	@Override
	public int getWidth() {
		return Math.max(width, componentWidth);
	}

	@Override
	public int getHeight() {
		return Math.max(height, componentHeight);
	}

	public Runnable getOnResize() {
		return onResize;
	}

	public void setOnResize(Runnable onResize) {
		this.onResize = onResize;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		fillRect(graphics, 0, 0, getWidth(), getHeight(), BoardGameEngineMain.BACKGROUND_COLOR);
		List<ObservedMoveWithScore> currentMoves = currentResult.moves;
		if (currentMoves == null) {
			return;
		}
		graphics.setFont(BoardGameEngineMain.DEFAULT_SMALL_FONT);
		int i = 0;
		while (i < currentMoves.size()) {
			int y = BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT * (i + 1);
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
		if (nameConsumer != null) {
			nameConsumer.accept(computerPlayer.toString());
		}
		if (currentDepthConsumer != null) {
			currentDepthConsumer.accept(String.format("depth = %-3d", currentResult.depth));
		}
	}
}
