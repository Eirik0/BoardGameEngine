package bge.gui.gamestate;

import java.awt.Color;
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
import bge.igame.player.PlayerOptions;
import bge.main.GameRegistry;
import gt.component.ComponentCreator;
import gt.component.IMouseTracker;
import gt.ecomponent.ComponentMouseTracker;
import gt.ecomponent.EComponentPanel;
import gt.ecomponent.EComponentPanelBuilder;
import gt.ecomponent.ETextLabel;
import gt.ecomponent.button.EButton;
import gt.ecomponent.button.ECheckBox;
import gt.ecomponent.list.EComboBox;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.location.EGluedLocation;
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
    private static final int CONTROLLER_PANEL_HEIGHT = 170;
    private static final int PLAYER_SELECTION_WIDTH = 200;

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
    private final PlayerOptionsPanel[] playerOptionsPanels;

    public BoardGameState(GameStateManager gameStateManager, IGame<M, IPosition<M>> game) {
        EComponentLocation stateLocation = new SizedComponentLocationAdapter(this, 0, 0);
        moveHistoryLocation = stateLocation.createGluedLocation(GlueSide.LEFT, 0, CONTROLLER_PANEL_HEIGHT, 299, 0);
        gameLocation = stateLocation.createPaddedLocation(300, CONTROLLER_PANEL_HEIGHT, 200, 0);
        analysisLocation = stateLocation.createGluedLocation(GlueSide.RIGHT, -199, CONTROLLER_PANEL_HEIGHT, 0, 0);
        // Create images
        imageDrawer = gameStateManager.getImageDrawer();
        moveHistoryImage = imageDrawer.newGameImage(moveHistoryLocation.getWidth(), moveHistoryLocation.getHeight());
        gameImage = imageDrawer.newGameImage(gameLocation.getWidth(), gameLocation.getHeight());
        analysisImage = imageDrawer.newGameImage(analysisLocation.getWidth(), analysisLocation.getHeight());

        // Game Runner
        GameObserver<M> gameObserver = new GameObserver<>();
        MoveListFactory<M> moveListFactory = GameRegistry.getMoveListFactory(game.getName());
        MoveHistory<M> moveHistory = new MoveHistory<>(game);
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
        EComponentLocation bpl = cpl.createGluedLocation(GlueSide.TOP, 0, 0, 0, 44);

        IMouseTracker mouseTracker = gameStateManager.getMouseTracker();
        EComponentPanelBuilder panelBuilder = new EComponentPanelBuilder(mouseTracker);

        // "Game" ... [New Game] [Pause/Play] [Back]
        EComponentLocation gameLabelLocation = bpl.createGluedLocation(GlueSide.LEFT, 10, 10, 109, -10);
        EComponentLocation newGameButtonLocation = bpl.createGluedLocation(GlueSide.RIGHT, -239, 10, -170, -10);
        EComponentLocation playPauseButtonLocation = bpl.createGluedLocation(GlueSide.RIGHT, -159, 10, -90, -10);
        EComponentLocation backPauseButtonLocation = bpl.createGluedLocation(GlueSide.RIGHT, -79, 10, -10, -10);

        selectedPlayersIndexes = new int[game.getNumberOfPlayers()];
        playerOptionsPanels = new PlayerOptionsPanel[game.getNumberOfPlayers()];
        panelBuilder
                .add(1, new ETextLabel(gameLabelLocation, game.getName(), false))
                .add(1, EButton.createTextButton(newGameButtonLocation, "New Game", () -> {
                }))
                .add(1, new ECheckBox(playPauseButtonLocation, "Pause", "Play", false, play -> {
                    if (play) {
                        List<IPlayer> players = new ArrayList<>();
                        for (int i = 0; i < game.getNumberOfPlayers(); ++i) {
                            String playerName = GameRegistry.getPlayerNames(game.getName())[selectedPlayersIndexes[i]];
                            if (GuiPlayer.NAME.equals(playerName)) {
                                players.add(GuiPlayer.HUMAN);
                            } else {
                                players.add(playerOptionsPanels[i].getPlayerInfo().newComputerPlayer(game.getName()));
                            }
                        }
                        gameRunner.setPlayersAndResume(players);
                    } else {
                        gameRunner.pauseGame(false);
                    }
                }))
                .add(1, EButton.createTextButton(backPauseButtonLocation, "Back", () -> {
                }));

        // ... [Player v] "v." ... [Player v] ...
        String[] playerNames = GameRegistry.getPlayerNames(game.getName());
        for (int i = 0; i < game.getNumberOfPlayers(); ++i) {
            final int playerIndex = i;
            int x0 = 200 + i * (PLAYER_SELECTION_WIDTH + 25);
            EComponentLocation boxLoc = cpl.createRelativeLocation(x0, 10, x0 + PLAYER_SELECTION_WIDTH - 1, 34);
            EGluedLocation optionsPanelLocation = boxLoc.createGluedLocation(GlueSide.BOTTOM, 0, 5, 0, 120);
            panelBuilder.add(1, new EComboBox(boxLoc, gameStateManager.getImageDrawer(), playerNames, 2, 0, index -> {
                selectedPlayersIndexes[playerIndex] = index;
                PlayerOptions playerOptions = GameRegistry.getPlayerOptions(game.getName(), playerNames[index]);
                playerOptionsPanels[playerIndex] = new PlayerOptionsPanel(optionsPanelLocation, mouseTracker, imageDrawer, playerOptions);
            }));
            if (i < game.getNumberOfPlayers() - 1) {
                EComponentLocation labelLoc = cpl.createRelativeLocation(x0 + PLAYER_SELECTION_WIDTH, 10, x0 + PLAYER_SELECTION_WIDTH + 25 - 1, 34);
                panelBuilder.add(1, new ETextLabel(labelLoc, "v.", false));
            }
            PlayerOptions playerOptions = GameRegistry.getPlayerOptions(game.getName(), playerNames[0]);
            playerOptionsPanels[i] = new PlayerOptionsPanel(optionsPanelLocation, mouseTracker, imageDrawer, playerOptions);
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
        for (PlayerOptionsPanel playerOptionsPanel : playerOptionsPanels) {
            playerOptionsPanel.update(dt);
        }
    }

    @Override
    public void drawOn(IGraphics g) {
        moveHistoryState.drawOn(moveHistoryImage.getGraphics());
        gameRunningState.drawOn(gameImage.getGraphics());
        analysisState.drawOn(analysisImage.getGraphics());
        imageDrawer.drawImage(g, moveHistoryImage, 0, CONTROLLER_PANEL_HEIGHT);
        imageDrawer.drawImage(g, gameImage, moveHistoryImage.getWidth(), CONTROLLER_PANEL_HEIGHT);
        imageDrawer.drawImage(g, analysisImage, moveHistoryImage.getWidth() + gameImage.getWidth(), CONTROLLER_PANEL_HEIGHT);
        g.fillRect(0, 0, width, CONTROLLER_PANEL_HEIGHT, ComponentCreator.backgroundColor());
        g.drawRect(0, 0, width, CONTROLLER_PANEL_HEIGHT, Color.GREEN);
        for (PlayerOptionsPanel playerOptionsPanel : playerOptionsPanels) {
            playerOptionsPanel.drawOn(g);
        }
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
        for (PlayerOptionsPanel playerOptionsPanel : playerOptionsPanels) {
            playerOptionsPanel.handleUserInput(input);
        }
    }
}
