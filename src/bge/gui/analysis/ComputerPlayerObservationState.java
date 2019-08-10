package bge.gui.analysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import bge.analysis.ComputerPlayer;
import bge.game.IPosition;
import bge.main.BoardGameEngineMain;

public class ComputerPlayerObservationState<M> implements IAnalysisState<M> {
    private final JPanel titlePanel;
    private final ComputerPlayerObserver observer;

    public ComputerPlayerObservationState(ComputerPlayer computerPlayer, int playerNum) {
        JLabel nameLabel = BoardGameEngineMain.initComponent(new JLabel(computerPlayer.toString()));
        JLabel depthLabel = BoardGameEngineMain.initComponent(new JLabel(String.format("depth = %-3d", Integer.valueOf(0))));

        observer = new ComputerPlayerObserver(computerPlayer, playerNum, name -> nameLabel.setText(name), depth -> depthLabel.setText(depth));

        titlePanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));

        JPanel titleLabelPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
        titleLabelPanel.add(nameLabel);

        JPanel depthLabelPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.TRAILING)));
        depthLabelPanel.add(depthLabel);

        titlePanel.add(titleLabelPanel, BorderLayout.WEST);
        titlePanel.add(depthLabelPanel, BorderLayout.EAST);
    }

    @Override
    public void checkResized(int width, int height) {
        observer.checkResized(width, height);
    }

    @Override
    public int getWidth() {
        return observer.getWidth();
    }

    @Override
    public int getHeight() {
        return observer.getHeight();
    }

    @Override
    public void handleUserInput(UserInput input) {
        // Do nothing
    }

    @Override
    public void drawOn(Graphics2D graphics) {
        observer.drawOn(graphics);
    }

    @Override
    public void setOnResize(Runnable onResize) {
        observer.setOnResize(onResize);
    }

    @Override
    public void setPosition(IPosition<M> position) {
        // do nothing
    }

    @Override
    public synchronized void stopAnalysis() {
        observer.stopObserving();
    }

    @Override
    public JPanel getTopPanel() {
        return titlePanel;
    }
}
