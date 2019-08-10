package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class StartStopButton extends JButton {
    private final AtomicBoolean stopped = new AtomicBoolean(true);
    private final String startText;
    private final String stopText;

    public StartStopButton(String startText, String stopText, Runnable startRunnable, Runnable stopRunnable, List<JButton> buttonsToDisable) {
        super(startText);
        this.startText = startText;
        this.stopText = stopText;
        BoardGameEngineMain.initComponent(this);
        List<JButton> buttons = new ArrayList<>(buttonsToDisable);
        buttons.add(this);
        addActionListener(PlayerControllerPanel.createEnableDisableRunnableWrapper(() -> {
            if (stopped.getAndSet(!stopped.get())) {
                startRunnable.run();
            } else {
                stopRunnable.run();
            }
        }, buttons));
    }

    public void notifyStarted() {
        stopped.set(false);
        setText(stopText);
    }

    public void notifyStopped() {
        stopped.set(true);
        setText(startText);
    }
}
