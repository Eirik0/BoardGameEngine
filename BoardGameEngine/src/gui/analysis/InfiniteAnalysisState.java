package gui.analysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import analysis.ComputerPlayer;
import analysis.ComputerPlayerInfo;
import analysis.search.ThreadNumber;
import game.IPosition;
import gui.GameRegistry;
import main.BoardGameEngineMain;
import main.ComputerConfigurationPanel;

public class InfiniteAnalysisState<M, P extends IPosition<M, P>> implements IAnalysisState<M, P> {
	private int width;
	private int height;

	private P position;

	private ComputerPlayerObserver observer;
	private ComputerPlayer computerPlayer;

	private final JPanel optionsPanel;

	public InfiniteAnalysisState(String gameName, P position, ComputerPlayerInfo<M, P> computerPlayerInfo, int playerNum) {
		this.position = position;

		optionsPanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));

		ComputerConfigurationPanel<?, ?> computerConfiurationPanel = new ComputerConfigurationPanel<>(gameName, computerPlayerInfo, true);

		JLabel depthLabel = BoardGameEngineMain.initComponent(new JLabel(String.format("depth = %-3d", 0)));

		JButton analyzeButton = BoardGameEngineMain.initComponent(new JButton("Analyze"));
		JButton stopButton = BoardGameEngineMain.initComponent(new JButton("Stop"));

		analyzeButton.addActionListener(e -> {
			stopAnalysis();

			computerConfiurationPanel.updateComputerPlayerInfo();

			computerPlayer = (ComputerPlayer) GameRegistry.getPlayer(gameName, ComputerPlayer.NAME, computerPlayerInfo);

			new Thread(() -> {
				computerPlayer.getMove(this.position);
				computerPlayer.notifyGameEnded();
			}, "Infinite_Analysis_Thread_" + ThreadNumber.getThreadNum(getClass())).start();

			observer = new ComputerPlayerObserver(computerPlayer, playerNum, name -> {
			}, depth -> depthLabel.setText(depth));
		});

		stopButton.addActionListener(e -> {
			stopAnalysis();
		});

		JPanel topPanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));
		topPanel.add(computerConfiurationPanel, BorderLayout.WEST);
		topPanel.add(depthLabel, BorderLayout.EAST);

		JPanel bottomPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
		bottomPanel.add(analyzeButton);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(stopButton);

		optionsPanel.add(topPanel, BorderLayout.CENTER);
		optionsPanel.add(bottomPanel, BorderLayout.SOUTH);
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
		if (observer != null) {
			observer.drawOn(graphics, width, height);
		} else {
			fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
			graphics.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
		}
	}

	@Override
	public void setPosition(P position) {
		this.position = position;
	}

	@Override
	public void stopAnalysis() {
		if (computerPlayer != null) {
			computerPlayer.notifyGameEnded();
		}
		if (observer != null) {
			observer.stopObserving();
		}
	}

	@Override
	public JPanel getTopPanel() {
		return optionsPanel;
	}
}
