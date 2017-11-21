package gui.analysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

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

	private ComputerPlayerResult currentResult = new ComputerPlayerResult(null, 0);

	private final ComputerPlayer computerPlayer;
	private final int playerNum;

	private final JLabel depthLabel;
	private final JPanel titlePanel;

	public ComputerPlayerObservationState(ComputerPlayer computerPlayer, int playerNum) {
		this.computerPlayer = computerPlayer;
		this.playerNum = playerNum;

		JLabel nameLabel = BoardGameEngineMain.initComponent(new JLabel(computerPlayer.toString()));
		depthLabel = BoardGameEngineMain.initComponent(new JLabel(String.format("depth = %-3d", currentResult.depth)));

		titlePanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));

		JPanel titleLabelPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
		titleLabelPanel.add(nameLabel);

		JPanel depthLabelPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.TRAILING)));
		depthLabelPanel.add(depthLabel);

		titlePanel.add(titleLabelPanel, BorderLayout.WEST);
		titlePanel.add(depthLabelPanel, BorderLayout.EAST);

		new Thread(() -> {
			nameLabel.setText(computerPlayer.toString() + "...");
			synchronized (this) {
				do {
					currentResult = computerPlayer.getCurrentResult();
					depthLabel.setText(String.format("depth = %-3d", currentResult.depth));
					try {
						wait(MS_PER_UPDATE);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				} while (keepObserving);
				nameLabel.setText(computerPlayer.toString());
			}
		}, "Computer_Observation_Thread_" + ThreadNumber.getThreadNum(getClass())).start();
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
		List<MoveWithScore<Object>> currentMoves = currentResult.moves;
		if (currentMoves == null) {
			return;
		}
		fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		graphics.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
		graphics.setFont(BoardGameEngineMain.DEFAULT_FONT_SMALL);
		FontMetrics metrics = graphics.getFontMetrics();
		int stringHeight = metrics.getHeight() + 2;
		int i = 0;
		int startY = stringHeight;
		while (i < currentMoves.size()) {
			int y = startY + i * stringHeight;
			MoveWithScore<Object> moveWithScore = currentMoves.get(i);
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
		currentResult = computerPlayer.getCurrentResult();
		depthLabel.setText(String.format("depth = %-3d", currentResult.depth));
		notify();
	}

	@Override
	public JPanel getTopPanel() {
		return titlePanel;
	}
}
