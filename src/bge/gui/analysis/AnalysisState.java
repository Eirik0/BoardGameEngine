package bge.gui.analysis;

import java.awt.Color;
import java.util.Collections;

import bge.gui.gamestate.PlayerOptionsPanel;
import bge.igame.IPosition;
import bge.igame.PositionChangedInfo;
import bge.igame.player.ComputerPlayer;
import bge.igame.player.ComputerPlayerResult;
import bge.igame.player.PlayerInfo;
import bge.igame.player.PlayerOptions;
import bge.main.GameRegistry;
import gt.async.ThreadWorker;
import gt.component.ComponentCreator;
import gt.component.IMouseTracker;
import gt.ecomponent.EComponentPanel;
import gt.ecomponent.EComponentPanelBuilder;
import gt.ecomponent.ETextLabel;
import gt.ecomponent.button.ECheckBox;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.location.EGluedLocation;
import gt.ecomponent.location.GlueSide;
import gt.ecomponent.location.SizedComponentLocationAdapter;
import gt.ecomponent.scrollbar.EScrollPane;
import gt.gameentity.DurationTimer;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gameentity.Sized;
import gt.gameloop.TimeConstants;
import gt.gamestate.GameState;
import gt.gamestate.UserInput;

public class AnalysisState implements GameState, Sized {
    private static enum AnalysisMode {
        STOPPED, ANALYZING, OBSERVING
    }

    public static final int TITLE_HEIGHT = 30;

    private String gameName;
    private final IMouseTracker mouseTracker;
    private final IGameImageDrawer imageDrawer;

    double width;
    double height;

    private final EComponentPanel componentPanel;
    private final EComponentLocation spLoc;
    private final EScrollPane scrollPane;

    private final AnalysisViewport view;

    private final PlayerOptions playerOptions;
    private final EComponentLocation optionsPanelLocation;
    private PlayerOptionsPanel playerOptionsPanel;
    private ECheckBox analyzePauseButton;

    private final DurationTimer analysisRefreshTimer = new DurationTimer(TimeConstants.NANOS_PER_SECOND / 10);

    private final ThreadWorker analysisWorker = new ThreadWorker("Analysis_Observer");

    private AnalysisMode mode = AnalysisMode.STOPPED;

    private ComputerPlayer analysisPlayer = null;
    private IPosition<?> currentPosition = null;

    private String analysisMsg = "";

    public AnalysisState(String gameName, IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        this.gameName = gameName;
        this.mouseTracker = mouseTracker;
        this.imageDrawer = imageDrawer;
        SizedComponentLocationAdapter cl = new SizedComponentLocationAdapter(this, 0, 0);
        spLoc = cl.createPaddedLocation(1, TITLE_HEIGHT + 120 + 25, 1, 1);
        view = new AnalysisViewport(spLoc);
        scrollPane = new EScrollPane(spLoc, view, imageDrawer);
        EGluedLocation headerLocation = cl.createGluedLocation(GlueSide.TOP, 0, 0, 0, TITLE_HEIGHT - 1);
        analyzePauseButton = new ECheckBox(headerLocation.createGluedLocation(GlueSide.RIGHT, -100, 3, -3, -3), "Pause", "Analyze", false,
                selected -> {
                    if (mode == AnalysisMode.OBSERVING) {
                        analyzePauseButton.setSelected(true);
                        return;
                    }
                    if (selected) {
                        if (currentPosition == null) {
                            mode = AnalysisMode.STOPPED;
                            return;
                        }
                        createNewAnalysisPlayer();
                        analyze();
                    } else {
                        if (analysisPlayer != null) {
                            analysisPlayer.notifyGameEnded();
                        }
                        mode = AnalysisMode.STOPPED;
                    }
                });
        componentPanel = new EComponentPanelBuilder(mouseTracker)
                .add(0, new ETextLabel(headerLocation.createGluedLocation(GlueSide.LEFT, 0, 0, 100, 0), "Analysis", true))
                .add(0, analyzePauseButton)
                .add(0, scrollPane)
                .build();
        optionsPanelLocation = cl.createGluedLocation(GlueSide.TOP, 5, TITLE_HEIGHT + 5, -5, TITLE_HEIGHT + 120);
        playerOptions = GameRegistry.getPlayerOptions(gameName, ComputerPlayer.NAME);
        playerOptionsPanel = new PlayerOptionsPanel(optionsPanelLocation, mouseTracker, imageDrawer, playerOptions,
                Collections.singleton(PlayerInfo.KEY_MS_PER_MOVE));
    }

    private void createNewAnalysisPlayer() {
        if (analysisPlayer != null) {
            analysisPlayer.notifyGameEnded();
        }
        PlayerInfo playerInfo = playerOptionsPanel.getPlayerInfo();
        playerInfo.setOption(PlayerInfo.KEY_MS_PER_MOVE, Integer.valueOf(Integer.MAX_VALUE));
        analysisPlayer = playerInfo.newComputerPlayer(gameName);
    }

    private void analyze() {
        analysisWorker.workOn(() -> {
            analysisPlayer.getMove(currentPosition);
            analysisPlayer.notifyGameEnded();
        });
        analyzePauseButton.setSelected(true);
        mode = AnalysisMode.ANALYZING;
    }

    public <M> void positionChanged(PositionChangedInfo<M> changeInfo) {
        currentPosition = changeInfo.position.createCopy();
        boolean willObserve = changeInfo.currentPlayer instanceof ComputerPlayer;
        if (mode == AnalysisMode.OBSERVING) {
            if (willObserve) {
                analysisPlayer = (ComputerPlayer) changeInfo.currentPlayer;
            } else {
                playerOptionsPanel = new PlayerOptionsPanel(optionsPanelLocation, mouseTracker, imageDrawer, playerOptions,
                        Collections.singleton(PlayerInfo.KEY_MS_PER_MOVE));
                createNewAnalysisPlayer();
                mode = AnalysisMode.STOPPED;
            }
            analyzePauseButton.setSelected(true);
        } else if (mode == AnalysisMode.ANALYZING) {
            if (willObserve) {
                analysisPlayer.notifyGameEnded();
                analysisPlayer = (ComputerPlayer) changeInfo.currentPlayer;
                mode = AnalysisMode.OBSERVING;
            } else {
                analysisPlayer.stopSearch(false);
                analyze();
            }
            analyzePauseButton.setSelected(true);
        } else if (mode == AnalysisMode.STOPPED && willObserve) {
            analysisPlayer = (ComputerPlayer) changeInfo.currentPlayer;
            mode = AnalysisMode.OBSERVING;
            analyzePauseButton.setSelected(true);
        }
        scrollPane.setSize(spLoc.getWidth(), spLoc.getHeight());
    }

    public void gamePaused(boolean back) {
        if (mode == AnalysisMode.ANALYZING) {
            if (analysisPlayer != null) {
                analysisPlayer.notifyGameEnded();
            }
        }
        analyzePauseButton.setSelected(false);
        mode = AnalysisMode.STOPPED;
        if (back) {
            analysisWorker.joinThread();
        }
    }

    @Override
    public void update(double dt) {
        playerOptionsPanel.update(dt);
        componentPanel.update(dt);
        analysisRefreshTimer.update(dt);
        if (analysisRefreshTimer.getPercentComplete() >= 1 && (mode == AnalysisMode.ANALYZING || mode == AnalysisMode.OBSERVING)) {
            if (analysisPlayer != null) {
                ComputerPlayerResult currentResult = analysisPlayer.getCurrentResult();
                analysisMsg = "depth = " + currentResult.depth;
                view.setAnalyzedMoves(currentResult.moves);
                scrollPane.setSize(spLoc.getWidth(), spLoc.getHeight());
            }
            analysisRefreshTimer.reset();
        }
    }

    @Override
    public void drawOn(IGraphics graphics) {
        graphics.fillRect(0, TITLE_HEIGHT + 120, width, TITLE_HEIGHT + 120 + 25, ComponentCreator.backgroundColor());
        graphics.setColor(ComponentCreator.foregroundColor());
        graphics.drawCenteredYString(analysisMsg, 5, TITLE_HEIGHT + 120 + 12.5);
        playerOptionsPanel.drawOn(graphics);
        componentPanel.drawOn(graphics);
        graphics.drawRect(0, 0, width, height, Color.RED);
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        scrollPane.setSize(spLoc.getWidth(), spLoc.getHeight());
        playerOptionsPanel = new PlayerOptionsPanel(optionsPanelLocation, mouseTracker, imageDrawer, playerOptions,
                Collections.singleton(PlayerInfo.KEY_MS_PER_MOVE));
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void handleUserInput(UserInput input) {
        playerOptionsPanel.handleUserInput(input);
        componentPanel.handleUserInput(input);
    }
}
