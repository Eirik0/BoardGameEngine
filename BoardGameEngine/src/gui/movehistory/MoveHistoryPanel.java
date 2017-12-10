package gui.movehistory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import game.GameRunner;
import game.IPosition;
import game.MoveHistory;
import gui.GameMouseAdapter;
import gui.GamePanel;
import main.BoardGameEngineMain;

@SuppressWarnings("serial")
public class MoveHistoryPanel<M, P extends IPosition<M, P>> extends JPanel {
	private static final String NAME = "Move History";

	private final MoveHistoryState<M, P> moveHistoryState;
	private final GamePanel moveHistoryPanel;

	public MoveHistoryPanel() {
		setLayout(new BorderLayout());
		BoardGameEngineMain.initComponent(this);

		moveHistoryState = new MoveHistoryState<>();

		moveHistoryPanel = new GamePanel(g -> drawOn(g), (width, height) -> resized(width.intValue(), height.intValue()));
		GameMouseAdapter mouseAdapter = new GameMouseAdapter(moveHistoryState.mouseTracker);
		moveHistoryPanel.addMouseMotionListener(mouseAdapter);
		moveHistoryPanel.addMouseListener(mouseAdapter);

		JPanel topPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
		topPanel.add(BoardGameEngineMain.initComponent(new JLabel("Move History")), BorderLayout.NORTH);

		add(topPanel, BorderLayout.NORTH);
		add(moveHistoryPanel, BorderLayout.CENTER);
	}

	public void setGameRunner(GameRunner<M, P> gameRunner) {
		moveHistoryState.setGameRunner(gameRunner);
	}

	public void setMoveHistory(MoveHistory<M, P> moveHistory) {
		moveHistoryState.setMoveHistory(moveHistory);
	}

	private void drawOn(Graphics2D g) {
		moveHistoryState.drawOn(g);
	}

	private void resized(int width, int height) {
		moveHistoryState.componentResized(width, height);
	}

	public void startDrawing() {
		moveHistoryPanel.addToGameLoop(NAME);
	}

	public void stopDrawing() {
		moveHistoryPanel.removeFromGameLoop(NAME);
	}
}
