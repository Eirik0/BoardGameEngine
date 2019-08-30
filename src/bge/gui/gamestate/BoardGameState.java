package bge.gui.gamestate;

import java.util.ArrayList;
import java.util.List;

import bge.gui.analysis.AnalysisGameState;
import bge.gui.movehistory.MoveHistoryState;
import bge.igame.GameObserver;
import bge.igame.GameRunner;
import bge.igame.IGame;
import bge.igame.IPosition;
import bge.igame.MoveHistory;
import bge.igame.MoveListFactory;
import bge.igame.player.GuiPlayer;
import bge.igame.player.IPlayer;
import bge.main.GameRegistry;
import gt.component.ComponentCreator;
import gt.component.IMouseTracker;
import gt.ecomponent.ComponentMouseTracker;
import gt.ecomponent.EBackground;
import gt.ecomponent.EComponentPanel;
import gt.ecomponent.EComponentPanelBuilder;
import gt.ecomponent.ETextLabel;
import gt.ecomponent.button.EButton;
import gt.ecomponent.button.ECheckBox;
import gt.ecomponent.list.EComboBox;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.location.GlueSide;
import gt.ecomponent.location.SizedComponentLocationAdapter;
import gt.gameentity.IGameImage;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gameentity.Sized;
import gt.gamestate.GameState;
import gt.gamestate.GameStateManager;
import gt.gamestate.UserInput;

public class BoardGameState<M> implements GameState, Sized {
    private static final int CONTROLLER_PANEL_HEIGHT = 50;

    private double width;
    private double height;

    private final IGameImageDrawer imageDrawer;
    private final IGameImage moveHistoryImage;
    private final IGameImage gameImage;
    private final IGameImage analysisImage;

    private final EComponentPanel controllerPanel;

    private final EComponentLocation moveHistoryLocation;
    private final EComponentLocation gameLocation;
    private final EComponentLocation analysisLocation;

    private final GameState moveHistoryState;
    private final GameState gameRunningState;
    private final GameState analysisState;

    int[] selectedPlayersIndexes;

    public BoardGameState(GameStateManager gameStateManager, IGame<M, IPosition<M>> game) {
        EComponentLocation stateLocation = new SizedComponentLocationAdapter(this, 0, 0);
        moveHistoryLocation = stateLocation.createGluedLocation(GlueSide.LEFT, 0, CONTROLLER_PANEL_HEIGHT, 199, 0);
        gameLocation = stateLocation.createPaddedLocation(200, CONTROLLER_PANEL_HEIGHT, 200, 0);
        analysisLocation = stateLocation.createGluedLocation(GlueSide.RIGHT, -199, CONTROLLER_PANEL_HEIGHT, 0, 0);
        // Create images
        imageDrawer = gameStateManager.getImageDrawer();
        moveHistoryImage = imageDrawer.newGameImage(moveHistoryLocation.getWidth(), moveHistoryLocation.getHeight());
        gameImage = imageDrawer.newGameImage(gameLocation.getWidth(), gameLocation.getHeight());
        analysisImage = imageDrawer.newGameImage(analysisLocation.getWidth(), analysisLocation.getHeight());

        // Game Runner
        GameObserver<M> gameObserver = new GameObserver<>();
        MoveListFactory<M> moveListFactory = GameRegistry.getMoveListFactory(game.getName());
        MoveHistory<M> moveHistory = new MoveHistory<>(game.getNumberOfPlayers());
        GameRunner<M, IPosition<M>> gameRunner = new GameRunner<>(game, moveHistory, gameObserver, moveListFactory);
        gameObserver.setPositionChangedAction(positionChangedInfo -> {
            //            moveHistoryPanel.setMoveHistory(positionChangedInfo.moveHistory);
            //            analysisPanel.positionChanged(positionChangedInfo);
        });

        //        gameObserver.setGameRunningAction(() -> playerControllerPanel.gameStarted());

        gameObserver.setGamePausedAction(gameEnded -> {
            //            playerControllerPanel.gamePaused();
            //            if (gameEnded.booleanValue()) {
            //                analysisPanel.gameEnded();
            //            }
        });

        // Top "Controller" panel
        EComponentLocation cpl = stateLocation.createGluedLocation(GlueSide.TOP, 0, 0, 0, CONTROLLER_PANEL_HEIGHT - 1);

        IMouseTracker mouseTracker = gameStateManager.getMouseTracker();
        EComponentPanelBuilder panelBuilder = new EComponentPanelBuilder(mouseTracker)
                .add(0, new EBackground(cpl, ComponentCreator.backgroundColor()));

        // "Game" ... [New Game] [Pause/Play] [Back]
        EComponentLocation gameLabelLocation = cpl.createRelativeLocation(10, 10, 109, CONTROLLER_PANEL_HEIGHT - 1);
        EComponentLocation newGameButtonLocation = cpl.createGluedLocation(GlueSide.RIGHT, -239, 10, -170, -10);
        EComponentLocation playPauseButtonLocation = cpl.createGluedLocation(GlueSide.RIGHT, -159, 10, -90, -10);
        EComponentLocation backPauseButtonLocation = cpl.createGluedLocation(GlueSide.RIGHT, -79, 10, -10, -10);

        panelBuilder
                .add(1, new ETextLabel(gameLabelLocation, game.getName(), false))
                .add(1, EButton.createTextButton(newGameButtonLocation, "New Game", () -> {
                }))
                .add(1, new ECheckBox(playPauseButtonLocation, "Pause", "Play", false, play -> {
                    if (play) {
                        List<IPlayer> players = new ArrayList<>();
                        for (int i = 0; i < game.getNumberOfPlayers(); ++i) {
                            String playerName = GameRegistry.getPlayerNames(game.getName())[i];
                            players.add(GuiPlayer.HUMAN);
                        }
                        gameRunner.setPlayersAndResume(players);
                    } else {
                        gameRunner.pauseGame(false);
                    }
                }))
                .add(1, EButton.createTextButton(backPauseButtonLocation, "Back", () -> {
                }));

        // ... [Player v] "v." ... [Player v] ...
        selectedPlayersIndexes = new int[game.getNumberOfPlayers()];
        String[] playerNames = GameRegistry.getPlayerNames(game.getName());
        for (int i = 0; i < game.getNumberOfPlayers(); ++i) {
            final int playerIndex = i;
            int x0 = 200 + i * 125;
            EComponentLocation boxLoc = cpl.createRelativeLocation(x0, 10, x0 + 99, 34);
            panelBuilder.add(1, new EComboBox(boxLoc, gameStateManager.getImageDrawer(), playerNames, 2, 0, index -> {
                selectedPlayersIndexes[playerIndex] = index;
            }));
            if (i < game.getNumberOfPlayers() - 1) {
                EComponentLocation labelLoc = cpl.createRelativeLocation(x0 + 100, 10, x0 + 124, 34);
                panelBuilder.add(1, new ETextLabel(labelLoc, "v.", false));
            }
        }
        controllerPanel = panelBuilder.build();

        moveHistoryState = new MoveHistoryState<>(moveHistory, new ComponentMouseTracker(mouseTracker, moveHistoryLocation), imageDrawer);

        gameRunningState = new GameRunningState<>(gameStateManager, gameRunner,
                GameRegistry.getGameRenderer(game.getName(), new ComponentMouseTracker(mouseTracker, gameLocation), gameStateManager.getImageDrawer()));

        analysisState = new AnalysisGameState();
    }

    @Override
    public void update(double dt) {
        controllerPanel.update(dt);
        moveHistoryState.update(dt);
        gameRunningState.update(dt);
        analysisState.update(dt);
    }

    @Override
    public void drawOn(IGraphics g) {
        moveHistoryState.drawOn(moveHistoryImage.getGraphics());
        gameRunningState.drawOn(gameImage.getGraphics());
        analysisState.drawOn(analysisImage.getGraphics());
        imageDrawer.drawImage(g, moveHistoryImage, 0, CONTROLLER_PANEL_HEIGHT);
        imageDrawer.drawImage(g, gameImage, moveHistoryImage.getWidth(), CONTROLLER_PANEL_HEIGHT);
        imageDrawer.drawImage(g, analysisImage, moveHistoryImage.getWidth() + gameImage.getWidth(), CONTROLLER_PANEL_HEIGHT);
        controllerPanel.drawOn(g);
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;

        moveHistoryImage.setSize(moveHistoryLocation.getWidth(), moveHistoryLocation.getHeight());
        gameImage.setSize(gameLocation.getWidth(), gameLocation.getHeight());
        analysisImage.setSize(analysisLocation.getWidth(), analysisLocation.getHeight());

        moveHistoryState.setSize(moveHistoryLocation.getWidth(), moveHistoryLocation.getHeight());
        gameRunningState.setSize(gameLocation.getWidth(), gameLocation.getHeight());
        analysisState.setSize(analysisLocation.getWidth(), analysisLocation.getHeight());
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
        controllerPanel.handleUserInput(input);
        moveHistoryState.handleUserInput(input);
        gameRunningState.handleUserInput(input);
        analysisState.handleUserInput(input);
    }
}
