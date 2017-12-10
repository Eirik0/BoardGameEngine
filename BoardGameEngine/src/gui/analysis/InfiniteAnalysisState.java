package gui.analysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicBoolean;

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

	volatile boolean isRunning = false;
	private final AtomicBoolean keepRunning = new AtomicBoolean(false);

	private final JPanel optionsPanel;
	private final JLabel depthLabel;

	public InfiniteAnalysisState(String gameName, P position, ComputerPlayerInfo<M, P> computerPlayerInfo) {
		this.position = position;

		optionsPanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));

		ComputerConfigurationPanel<?, ?> computerConfiurationPanel = new ComputerConfigurationPanel<>(gameName, computerPlayerInfo, true);

		depthLabel = BoardGameEngineMain.initComponent(new JLabel(String.format("depth = %-3d", Integer.valueOf(0))));

		JButton analyzeButton = BoardGameEngineMain.initComponent(new JButton("Analyze"));
		JButton stopButton = BoardGameEngineMain.initComponent(new JButton("Stop"));

		analyzeButton.addActionListener(e -> {
			if (isRunning) {
				setPosition(this.position);
			} else {
				computerConfiurationPanel.updateComputerPlayerInfo();
				computerPlayer = (ComputerPlayer) GameRegistry.getPlayer(gameName, ComputerPlayer.NAME, computerPlayerInfo);

				observer = new ComputerPlayerObserver(computerPlayer, this.position.getCurrentPlayer(), name -> {
				}, depth -> depthLabel.setText(depth));

				startAnalysisThread();
			}
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

	private void startAnalysisThread() {
		new Thread(() -> {
			synchronized (this) {
				isRunning = true;
				notify();
			}
			try {
				do {
					synchronized (this) {
						keepRunning.set(false); // Only keep analyzing if we have set another position
						notify();
					}
					computerPlayer.getMove(position.createCopy());
				} while (keepRunning.get());
			} finally {
				computerPlayer.notifyGameEnded();
				synchronized (this) {
					isRunning = false;
					notify();
				}
			}
		}, "Infinite_Analysis_Thread_" + ThreadNumber.getThreadNum(getClass())).start();

		waitForRunningToBe(true);
	}

	private synchronized void waitForRunningToBe(boolean startStop) {
		while (isRunning != startStop) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
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
		fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		if (observer != null) {
			observer.drawOn(graphics);
		}
	}

	@Override
	public synchronized void setPosition(P position) {
		this.position = position;
		if (isRunning) {
			keepRunning.set(true);
			computerPlayer.stopSearch(false);
			observer.setPlayerNum(position.getCurrentPlayer());
			while (keepRunning.get()) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public synchronized void stopAnalysis() {
		if (computerPlayer != null) {
			computerPlayer.notifyGameEnded();
		}
		if (observer != null) {
			observer.stopObserving();
		}
		waitForRunningToBe(false);
	}

	@Override
	public JPanel getTopPanel() {
		return optionsPanel;
	}
}
