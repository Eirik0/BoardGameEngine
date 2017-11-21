package gui.analysis;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import analysis.AnalysisResult;
import analysis.ComputerPlayer;
import analysis.MoveWithScore;
import analysis.search.ThreadNumber;
import game.TwoPlayers;
import gui.DrawingMethods;
import main.BoardGameEngineMain;

public class ComputerPlayerObservationState implements IAnalysisState {
	private static final int MS_PER_UPDATE = DrawingMethods.roundS(1000.0 / 60);

	private int width;
	private int height;

	private volatile boolean keepObserving = true;

	private volatile List<MoveWithScore<Object>> movesWithScore;

	private final ComputerPlayer computerPlayer;
	private final int playerNum;

	public ComputerPlayerObservationState(ComputerPlayer computerPlayer, int playerNum) {
		this.computerPlayer = computerPlayer;
		this.playerNum = playerNum;
		new Thread(() -> {
			synchronized (this) {
				do {
					pollResult(computerPlayer);
					try {
						wait(MS_PER_UPDATE);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				} while (keepObserving);
			}
		}, "Computer_Observation_Thread_" + ThreadNumber.getThreadNum(getClass())).start();
	}

	private void pollResult(ComputerPlayer computerPlayer) {
		AnalysisResult<Object> currentResult = computerPlayer.getCurrentResult();
		if (currentResult != null) {
			List<MoveWithScore<Object>> newMovesWithScore = new ArrayList<>(currentResult.getMovesWithScore());
			Collections.sort(newMovesWithScore, (move1, move2) -> Double.compare(move2.score, move1.score));
			movesWithScore = newMovesWithScore;
		}
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
		if (movesWithScore == null) {
			return;
		}
		fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		graphics.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
		graphics.setFont(BoardGameEngineMain.DEFAULT_FONT_SMALL);
		FontMetrics metrics = graphics.getFontMetrics();
		int height = metrics.getHeight() + 2;
		int startY = 20;
		graphics.drawString(computerPlayer.toString() + (keepObserving ? "..." : ""), startY, 20);
		int i = 0;
		startY += height + height / 2;
		while (i < movesWithScore.size()) {
			int y = startY + i * height;
			MoveWithScore<Object> moveWithScore = movesWithScore.get(i);
			String indexString = i < 9 ? (i + 1) + ".   " : (i + 1) + ". ";
			double playerScore = playerNum == TwoPlayers.PLAYER_1 ? moveWithScore.score : -moveWithScore.score + 0.0;
			String scoreString = Double.isFinite(playerScore) ? String.format("(%.2f)", playerScore) : "(" + playerScore + ")";
			graphics.drawString(indexString, 20, y);
			graphics.drawString(String.format("%-13s", scoreString), 45, y);
			graphics.drawString(moveWithScore.move.toString(), 100, y);
			++i;
		}
	}

	@Override
	public synchronized void stopAnalysis() {
		keepObserving = false;
		pollResult(computerPlayer);
		notify();
	}
}
