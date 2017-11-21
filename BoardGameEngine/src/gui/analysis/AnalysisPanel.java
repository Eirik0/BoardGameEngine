package gui.analysis;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import analysis.ComputerPlayer;
import analysis.search.ThreadNumber;
import game.IPlayer;
import game.TwoPlayers;
import gui.FixedDurationGameLoop;
import gui.GameImage;
import gui.GamePanel;
import main.BoardGameEngineMain;

@SuppressWarnings("serial")
public class AnalysisPanel extends JPanel {
	private final FixedDurationGameLoop gameLoop;
	private IAnalysisState analysisState;

	public AnalysisPanel() {
		analysisState = new InfiniteAnalysisState(TwoPlayers.PLAYER_1);
		setLayout(new BorderLayout());
		BoardGameEngineMain.initComponent(this);
		GameImage analysisImage = new GameImage();
		GamePanel analysisPanel = new GamePanel(analysisImage);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				analysisImage.checkResized(getWidth(), getHeight());
				analysisState.componentResized(getWidth(), getHeight());
			}
		});

		gameLoop = new FixedDurationGameLoop(() -> {
			analysisState.drawOn(analysisImage.getGraphics());
			analysisPanel.repaint();
		});

		add(analysisState.getTopPanel(), BorderLayout.NORTH);
		add(analysisPanel, BorderLayout.CENTER);
		new Thread(() -> gameLoop.runLoop(), "Analysis_Draw_Thread_" + ThreadNumber.getThreadNum(getClass())).start();
	}

	public void stopDrawThread() {
		gameLoop.stop();
		analysisState.stopAnalysis();
	}

	public void gameEnded() {
		analysisState.stopAnalysis();
	}

	public void playerChanged(IPlayer player, int playerNum) {
		analysisState.stopAnalysis();
		IAnalysisState newAnalysisState;
		if (player instanceof ComputerPlayer) {
			newAnalysisState = new ComputerPlayerObservationState((ComputerPlayer) player, playerNum);
		} else {
			newAnalysisState = new InfiniteAnalysisState(playerNum);
		}

		newAnalysisState.componentResized(getWidth(), getHeight());

		remove(analysisState.getTopPanel());
		add(newAnalysisState.getTopPanel(), BorderLayout.NORTH);

		analysisState = newAnalysisState;
	}
}
