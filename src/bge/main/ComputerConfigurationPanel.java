package bge.main;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bge.analysis.ComputerPlayerInfo;
import bge.gui.GameRegistry;

@SuppressWarnings("serial")
public class ComputerConfigurationPanel extends JPanel {
    private final String gameName;
    private final ComputerPlayerInfo<?, ?> computerPlayerInfo;
    private final boolean infiniteTimeOnly;

    private final JComboBox<String> strategyComboBox;
    private final JTextField numWorkersField;
    private JTextField msPerMoveField;

    public ComputerConfigurationPanel(String gameName, ComputerPlayerInfo<?, ?> computerPlayerInfo, boolean infiniteTimeOnly) {
        this.gameName = gameName;
        this.computerPlayerInfo = computerPlayerInfo;
        this.infiniteTimeOnly = infiniteTimeOnly;

        setLayout(new FlowLayout(FlowLayout.LEADING));
        BoardGameEngineMain.initComponent(this);

        String[] strategyNames = GameRegistry.getStrategyNames(gameName).toArray(new String[0]);
        strategyComboBox = BoardGameEngineMain.initComponent(new JComboBox<>(strategyNames));

        numWorkersField = BoardGameEngineMain.initComponent(new JTextField(Integer.toString(computerPlayerInfo.numWorkers), 3));
        numWorkersField.setFocusable(true);

        if (infiniteTimeOnly) {
            msPerMoveField = BoardGameEngineMain.initComponent(new JTextField("Inf", 5));
        } else {
            msPerMoveField = BoardGameEngineMain.initComponent(new JTextField(Long.toString(computerPlayerInfo.msPerMove), 10));
            msPerMoveField.setFocusable(true);
        }

        if (strategyNames.length > 1) {
            add(strategyComboBox);
            add(Box.createHorizontalStrut(10));
        }
        add(BoardGameEngineMain.initComponent(new JLabel("Threads:")));
        add(numWorkersField);
        if (!infiniteTimeOnly) {
            add(Box.createHorizontalStrut(10));
            add(BoardGameEngineMain.initComponent(new JLabel("ms per move:")));
            add(msPerMoveField);
        }
    }

    public void updateComputerPlayerInfo() {
        int numWorkers = computerPlayerInfo.numWorkers;
        try {
            numWorkers = Integer.parseInt(numWorkersField.getText());
            numWorkers = Math.min(Math.max(1, numWorkers), computerPlayerInfo.maxWorkers);
        } catch (Exception ex) {
            numWorkersField.setText(Integer.toString(computerPlayerInfo.numWorkers));
        }
        long msPerMove = computerPlayerInfo.msPerMove;
        if (!infiniteTimeOnly) {
            try {
                msPerMove = Integer.parseInt(msPerMoveField.getText());
                msPerMove = Math.max(100, msPerMove);
            } catch (Exception ex) {
                msPerMoveField.setText(Long.toString(computerPlayerInfo.msPerMove));
            }
        }
        String strategyName = strategyComboBox.getItemAt(strategyComboBox.getSelectedIndex());
        GameRegistry.updateComputerPlayerInfo(computerPlayerInfo, gameName, strategyName, numWorkers, msPerMove);
    }

    public ComputerPlayerInfo<?, ?> getComputerPlayerInfo() {
        return computerPlayerInfo;
    }
}
