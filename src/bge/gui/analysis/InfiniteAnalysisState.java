package bge.gui.analysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JPanel;

import bge.analysis.ComputerPlayer;
import bge.analysis.ComputerPlayerInfo;
import bge.game.IPosition;
import bge.gui.GameRegistry;
import bge.main.BoardGameEngineMain;
import bge.main.ComputerConfigurationPanel;
import bge.main.StartStopButton;
import gt.async.ThreadNumber;

public class InfiniteAnalysisState<M> implements IAnalysisState<M> {
    private IPosition<M> position;

    private ComputerPlayerObserver observer = new ComputerPlayerObserver();
    private ComputerPlayer computerPlayer;

    volatile boolean isRunning = false;
    private final AtomicBoolean keepRunning = new AtomicBoolean(false);

    private final JPanel optionsPanel;
    private final StartStopButton analyzeButton;
    private final JLabel depthLabel;

    public InfiniteAnalysisState(String gameName, IPosition<M> position, ComputerPlayerInfo<M, IPosition<M>> computerPlayerInfo) {
        this.position = position;

        optionsPanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));

        ComputerConfigurationPanel computerConfiurationPanel = new ComputerConfigurationPanel(gameName, computerPlayerInfo, true);

        depthLabel = BoardGameEngineMain.initComponent(new JLabel(String.format("depth = %-3d", Integer.valueOf(0))));

        analyzeButton = new StartStopButton("Analyze", "   Stop   ", () -> {
            if (isRunning) {
                setPosition(this.position);
            } else {
                computerConfiurationPanel.updateComputerPlayerInfo();
                computerPlayer = (ComputerPlayer) GameRegistry.getPlayer(gameName, ComputerPlayer.NAME, computerPlayerInfo);

                int oldWidth = observer.getWidth();
                int oldHeight = observer.getHeight();
                Runnable oldOnResize = observer.getOnResize();

                observer = new ComputerPlayerObserver(computerPlayer, this.position.getCurrentPlayer(), name -> {
                }, depth -> depthLabel.setText(depth));

                observer.checkResized(oldWidth, oldHeight);
                observer.setOnResize(oldOnResize);

                startAnalysisThread();
            }
        }, () -> stopAnalysis(), Collections.emptyList());

        JPanel bottomCenterPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
        bottomCenterPanel.add(analyzeButton);

        JPanel bottomPanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));
        bottomPanel.add(bottomCenterPanel, BorderLayout.CENTER);
        bottomPanel.add(depthLabel, BorderLayout.EAST);

        optionsPanel.add(computerConfiurationPanel, BorderLayout.CENTER);
        optionsPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startAnalysisThread() {
        analyzeButton.notifyStarted();
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
                analyzeButton.notifyStopped();
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
    public void setOnResize(Runnable onResize) {
        observer.setOnResize(onResize);
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
    public synchronized void setPosition(IPosition<M> position) {
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
        analyzeButton.notifyStopped();
        if (computerPlayer != null) {
            computerPlayer.notifyGameEnded();
        }
        observer.stopObserving();
        waitForRunningToBe(false);
    }

    @Override
    public JPanel getTopPanel() {
        return optionsPanel;
    }
}
