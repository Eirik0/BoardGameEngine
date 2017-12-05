package gui.analysis;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import analysis.ComputerPlayer;
import analysis.ComputerPlayerInfo;
import analysis.search.ThreadNumber;
import game.IPlayer;
import game.IPosition;
import game.TwoPlayers;
import gui.FixedDurationGameLoop;
import gui.GameImage;
import gui.GamePanel;
import gui.GameRegistry;
import main.BoardGameEngineMain;

@SuppressWarnings("serial")
public class AnalysisPanel<M, P extends IPosition<M, P>> extends JPanel {
	private final String gameName;
	private final ComputerPlayerInfo<M, P> computerPlayerInfo;
	private final FixedDurationGameLoop gameLoop;

	private IAnalysisState<M, P> analysisState;

	private P position;

	public AnalysisPanel(String gameName) {
		this.gameName = gameName;

		setLayout(new BorderLayout());
		BoardGameEngineMain.initComponent(this);

		computerPlayerInfo = GameRegistry.newDefaultComputerPlayerInfo(gameName);
		GameRegistry.updateComputerPlayerInfo(computerPlayerInfo, gameName, computerPlayerInfo.strategyName, computerPlayerInfo.numWorkers, Long.MAX_VALUE);

		analysisState = new InfiniteAnalysisState<>(gameName, position, computerPlayerInfo, TwoPlayers.PLAYER_1);

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

	public void gamePaused(int playerNum) {
		analysisState.stopAnalysis();
		setAnalysisState(new InfiniteAnalysisState<>(gameName, position, computerPlayerInfo, playerNum));
	}

	public void playerChanged(IPlayer player, int playerNum) {
		analysisState.stopAnalysis();
		IAnalysisState<M, P> newAnalysisState;
		if (player instanceof ComputerPlayer) {
			newAnalysisState = new ComputerPlayerObservationState<>((ComputerPlayer) player, playerNum);
		} else {
			newAnalysisState = new InfiniteAnalysisState<>(gameName, position, computerPlayerInfo, playerNum);
		}

		setAnalysisState(newAnalysisState);
	}

	private void setAnalysisState(IAnalysisState<M, P> newAnalysisState) {
		newAnalysisState.componentResized(getWidth(), getHeight());

		remove(analysisState.getTopPanel());
		add(newAnalysisState.getTopPanel(), BorderLayout.NORTH);
		revalidate();

		analysisState = newAnalysisState;
	}

	@SuppressWarnings("unchecked")
	public void positionChanged(Object positionObj) {
		P position = (P) positionObj;
		this.position = position;
		analysisState.setPosition(position);
	}
}
