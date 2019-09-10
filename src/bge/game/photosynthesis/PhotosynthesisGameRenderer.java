package bge.game.photosynthesis;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import bge.game.photosynthesis.PhotosynthesisPosition.Buy;
import bge.game.photosynthesis.PhotosynthesisPosition.EndTurn;
import bge.game.photosynthesis.PhotosynthesisPosition.PlayerBoard;
import bge.game.photosynthesis.PhotosynthesisPosition.Seed;
import bge.game.photosynthesis.PhotosynthesisPosition.Setup;
import bge.game.photosynthesis.PhotosynthesisPosition.Tile;
import bge.game.photosynthesis.PhotosynthesisPosition.Upgrade;
import bge.gui.gamestate.IGameRenderer;
import bge.gui.gamestate.IPositionObserver;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import bge.igame.player.GuiPlayer;
import bge.main.BoardGameEngineMain;
import gt.component.ComponentCreator;
import gt.component.IMouseTracker;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.location.EFixedLocation;
import gt.ecomponent.location.EGluedLocation;
import gt.ecomponent.location.GlueSide;
import gt.gameentity.Drawable;
import gt.gameentity.DrawingMethods;
import gt.gameentity.GridSizer;
import gt.gameentity.IGameImage;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;
import gt.util.DoublePair;

public class PhotosynthesisGameRenderer implements IGameRenderer<IPhotosynthesisMove, PhotosynthesisPosition>,
        IPositionObserver<IPhotosynthesisMove, PhotosynthesisPosition> {
    private static final Font PHOTO_FONT = BoardGameEngineMain.DEFAULT_SMALL_FONT.deriveFont(Font.BOLD, 14f);

    public static final Color[] PLAYER_COLORS = new Color[] { Color.RED, Color.BLUE, Color.ORANGE, Color.MAGENTA };

    private static final Coordinate[] SUN_POSITIONS = new Coordinate[] {
            Coordinate.valueOf(0, 0),
            Coordinate.valueOf(3, 0),
            Coordinate.valueOf(6, 3),
            Coordinate.valueOf(6, 6),
            Coordinate.valueOf(3, 6),
            Coordinate.valueOf(0, 3),
    };

    private final IMouseTracker mouseTracker;
    private final IGameImageDrawer imageDrawer;

    private GridSizer sizer;
    private HexGrid hexGrid;

    private EComponentLocation endTurnLocation;

    private final GuiPlayerBoard[] playerBoards = new GuiPlayerBoard[4];

    private Coordinate mousePressedCoordinate;

    int[][] shadowMap;

    // The allowed buy moves this turn
    private Buy[] allowedBuyMoves;
    // The end turn move
    private IPhotosynthesisMove endTurn;
    private Map<Coordinate, Map<Coordinate, IPhotosynthesisMove>> moveMap = new HashMap<>();

    public PhotosynthesisGameRenderer(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        this.mouseTracker = mouseTracker;
        this.imageDrawer = imageDrawer;
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        sizer = new GridSizer(imageWidth, imageHeight, 11, 11);
        hexGrid = new HexGrid(sizer.getCenterX(2), sizer.getCenterY(3), sizer.cellSize / 2);
        g.fillRect(0, 0, imageWidth, imageHeight, ComponentCreator.backgroundColor());
        double endTurnX0 = imageWidth - 80;
        endTurnLocation = new EFixedLocation(endTurnX0, 10, endTurnX0 + 70, 40);
        drawEndTurnButton(g, Color.RED);
        double x0 = 0;
        double y0 = sizer.getCenterY(7);
        double width = imageWidth / 4.0;
        double height = imageHeight - y0;
        for (int i = 0; i < 4; ++i) {
            playerBoards[i] = new GuiPlayerBoard(new EFixedLocation(x0, y0, x0 + width - 1, y0 + height - 1), imageDrawer, i);
            x0 += width;
        }
    }

    private void drawEndTurnButton(IGraphics g, Color color) {
        g.setColor(Color.BLACK);
        g.fillRect(endTurnLocation.getX0() - 2, endTurnLocation.getY0() - 2, endTurnLocation.getWidth() + 4, endTurnLocation.getHeight() + 4);
        g.setColor(color);
        g.drawRect(endTurnLocation.getX0(), endTurnLocation.getY0(), endTurnLocation.getWidth(), endTurnLocation.getHeight());
        g.setFont(PHOTO_FONT);
        g.drawCenteredString("End Turn", endTurnLocation.getCenterX(), endTurnLocation.getCenterY());
    }

    @Override
    public void notifyPositionChanged(PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves) {
        Map<Coordinate, Map<Coordinate, IPhotosynthesisMove>> newMoveMap = new HashMap<>();
        Buy[] newAllowedBuyMoves = new Buy[4];

        int i = 0;
        while (i < possibleMoves.size()) {
            IPhotosynthesisMove move = possibleMoves.get(i);

            BiConsumer<Coordinate, Coordinate> putCoord = (a, b) -> {
                Map<Coordinate, IPhotosynthesisMove> map = newMoveMap.get(a);
                if (map == null) {
                    map = new HashMap<>();
                    newMoveMap.put(a, map);
                }

                map.put(b, move);
            };

            if (move instanceof Setup) {
                Setup setupMove = (Setup) move;
                Coordinate coordinate = setupMove.coordinate;

                putCoord.accept(coordinate, coordinate);
            } else if (move instanceof Upgrade) {
                Upgrade upgradeMove = (Upgrade) move;
                Coordinate coordinate = upgradeMove.coordinate;

                putCoord.accept(coordinate, coordinate);
            } else if (move instanceof Seed) {
                Seed seedMove = (Seed) move;

                putCoord.accept(seedMove.source, seedMove.dest);
            } else if (move instanceof Buy) {
                Buy buyMove = (Buy) move;

                newAllowedBuyMoves[buyMove.buyColumn] = buyMove;
            } else if (move instanceof EndTurn) {
                endTurn = move;
            }
            ++i;
        }

        moveMap = newMoveMap;
        allowedBuyMoves = newAllowedBuyMoves;
        shadowMap = position.getShadowMap();
    }

    @Override
    public void drawPosition(IGraphics g, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves, IPhotosynthesisMove lastMove) {
        drawBoard(g, position);

        // Draw player boards
        g.setFont(PHOTO_FONT);
        for (int i = 0; i < position.numPlayers; ++i) {
            PlayerBoard pb = position.playerBoards[i];
            GuiPlayerBoard gpb = playerBoards[i];
            Color playerColor = PLAYER_COLORS[i];
            gpb.drawOn(g);
            // light points
            g.setColor(Color.YELLOW);
            g.drawCenteredString(Integer.toString(pb.lightPoints), gpb.lightPointsLocation.getCenterX(), gpb.lightPointsLocation.getCenterY());
            // victory points
            g.setColor(Color.GREEN);
            g.drawCenteredString(Integer.toString(pb.victoryPoints), gpb.victoryPointsLocation.getCenterX(), gpb.victoryPointsLocation.getCenterY());
            // seeds
            for (int j = 0; j < pb.buy.length; ++j) {
                EComponentLocation loc = gpb.buyableLocations[j];
                g.setColor(new Color(255 - playerColor.getRed(), 255 - playerColor.getGreen(), 255 - playerColor.getBlue()));
                int pbBuy = pb.buy[j] - 1;
                g.drawCenteredString(pbBuy < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[0][pbBuy]),
                        loc.getX0() + loc.getHeight() / 2,
                        loc.getY0() + loc.getHeight() / 2);
                g.setColor(playerColor);
                g.drawCenteredString(treeString(pb.buy[j], 4, pb.available[j]),
                        loc.getX0() + (loc.getWidth() - loc.getHeight()) / 2,
                        loc.getY0() + loc.getHeight() / 2);
            }
        }

        // Highlight current player board
        int currentPlayer = position.currentPlayer;
        EComponentLocation cpbCL = playerBoards[currentPlayer].cl;
        g.drawRect(cpbCL.getX0(), cpbCL.getY0(), cpbCL.getWidth(), cpbCL.getHeight(), Color.GREEN);
        maybeHighlightUserMove(g, position);

        // Draw trees
        Tile[][] grid = position.mainBoard.grid;
        for (int a = 0; a < grid.length; ++a) {
            Tile[] tiles = grid[a];
            for (int b = 0; b < tiles.length; ++b) {
                Tile tile = tiles[b];
                if (tile == null) {
                    continue;
                }
                if (tile.player == -1) {
                    continue;
                }

                double cx = hexGrid.centerX(a, b);
                double cy = hexGrid.centerY(a, b);
                if (tile.lastTouchedPlayerRoundsRemaining == position.playerRoundsRemaining) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillCircle(cx, cy, sizer.cellSize / 2);
                }
                boolean shadow = (tile.level == 0 && shadowMap[a][b] > 0) || (tile.level > 0 && tile.level <= shadowMap[a][b]);
                drawTree(g, cx, cy, tile.level, tile.player, shadow);
            }
        }

        drawLastMove(g, lastMove, currentPlayer);
    }

    private void drawBoard(IGraphics g, PhotosynthesisPosition position) {
        Coordinate sunPos = SUN_POSITIONS[position.getSunPosition()];
        double fadePercent = 1 - (double) position.playerRoundsRemaining / (position.numPlayers * 18);
        g.setColor(DrawingMethods.fadeToColor(Color.YELLOW, Color.DARK_GRAY, fadePercent));
        g.fillCircle(hexGrid.centerX(sunPos.x, sunPos.y), hexGrid.centerY(sunPos.x, sunPos.y), sizer.cellSize * 3);
        g.drawCenteredYString("Rounds remaining: " + position.playerRoundsRemaining, 5, 20);
        g.fillCircle(hexGrid.centerX(3, 3), hexGrid.centerY(3, 3), sizer.cellSize * 3.25, new Color(3, 16, 79));

        int green = 255 / 2;
        int red = 255 / 2;
        for (Coordinate[] coordinates : PhotosynthesisPosition.ALL_TILES) {
            g.setColor(new Color(red, green, 0));
            for (Coordinate coordinate : coordinates) {
                g.fillCircle(hexGrid.centerX(coordinate.x, coordinate.y), hexGrid.centerY(coordinate.x, coordinate.y), sizer.cellSize / 2);
            }
            green += 32;
            red -= 32;
        }
    }

    private void drawLastMove(IGraphics g, IPhotosynthesisMove move, int currentPlayer) {
        g.setColor(Color.YELLOW);
        if (move instanceof Setup) {
            Setup setupMove = (Setup) move;
            Coordinate coordinate = setupMove.coordinate;
            DoublePair centerXY = hexGrid.centerXY(coordinate);
            g.drawCircle(centerXY.getFirst(), centerXY.getSecond(), sizer.cellSize * .33);
        } else if (move instanceof Upgrade) {
            Upgrade upgradeMove = (Upgrade) move;
            Coordinate coordinate = upgradeMove.coordinate;
            DoublePair centerXY = hexGrid.centerXY(coordinate);
            g.drawCircle(centerXY.getFirst(), centerXY.getSecond(), sizer.cellSize * .33);
        } else if (move instanceof Seed) {
            Seed seedMove = (Seed) move;
            DoublePair fromXY = hexGrid.centerXY(seedMove.source);
            DoublePair toXY = hexGrid.centerXY(seedMove.dest);
            g.drawCircle(fromXY.getFirst(), fromXY.getSecond(), sizer.cellSize * .33);
            g.drawCircle(toXY.getFirst(), toXY.getSecond(), sizer.cellSize * .33);
            g.drawLine(fromXY.getFirst(), fromXY.getSecond(), toXY.getFirst(), toXY.getSecond());
        } else if (move instanceof Buy) {
            Buy buyMove = (Buy) move;
            EComponentLocation buyLocation = playerBoards[currentPlayer].buyableLocations[buyMove.buyColumn];
            g.drawRect(buyLocation.getX0() + 5, buyLocation.getY0() + 5, buyLocation.getWidth() - 10, buyLocation.getHeight() - 10);
        } else if (move instanceof EndTurn) {
            g.drawRect(endTurnLocation.getX0() + 5, endTurnLocation.getY0() + 5, endTurnLocation.getWidth() - 10, endTurnLocation.getHeight() - 10);
        }
    }

    private static boolean circleContains(double screenX, double screenY, double cx, double cy, double r) {
        return (cx - screenX) * (cx - screenX) + (cy - screenY) * (cy - screenY) <= r * r;
    }

    private void maybeHighlightUserMove(IGraphics g, PhotosynthesisPosition position) {
        // Highlight sun
        DoublePair sunXY = hexGrid.centerXY(SUN_POSITIONS[position.getSunPosition()]);
        double boardCenterX = hexGrid.centerX(3, 3);
        double boardCenterY = hexGrid.centerY(3, 3);
        if (circleContains(mouseTracker.mouseX(), mouseTracker.mouseY(), sunXY.getFirst(), sunXY.getSecond(), sizer.cellSize * 3)
                && !circleContains(mouseTracker.mouseX(), mouseTracker.mouseY(), boardCenterX, boardCenterY, sizer.cellSize * 3.5)) {
            for (Coordinate coordiante : PhotosynthesisPosition.ALL_COORDS) {
                int shadowLevel = shadowMap[coordiante.x][coordiante.y];
                if (shadowLevel > 0) {
                    g.setColor(DrawingMethods.fadeToColor(Color.WHITE, Color.BLACK, (shadowLevel + 1) / 4.0));
                    DoublePair centerXY = hexGrid.centerXY(coordiante);
                    g.fillCircle(centerXY.getFirst(), centerXY.getSecond(), sizer.cellSize * 0.33);
                }
            }
        }
        if (!GuiPlayer.HUMAN.isRequestingMove()) {
            return;
        }
        int currentPlayer = position.getCurrentPlayer();
        g.setColor(PLAYER_COLORS[currentPlayer]);
        Coordinate currentCoordinate = maybeGetCoordinate();
        if (mousePressedCoordinate != null) {
            Map<Coordinate, IPhotosynthesisMove> toMoveMap = moveMap.get(mousePressedCoordinate);
            if (toMoveMap == null) {
                return;
            }
            if (currentCoordinate == null || currentCoordinate.equals(mousePressedCoordinate)) {
                drawMovesFrom(g, mousePressedCoordinate, toMoveMap.keySet());
            } else if (toMoveMap.containsKey(currentCoordinate)) {
                DoublePair fromXY = hexGrid.centerXY(mousePressedCoordinate);
                DoublePair toXY = hexGrid.centerXY(currentCoordinate);
                g.drawLine(fromXY.getFirst(), fromXY.getSecond(), toXY.getFirst(), toXY.getSecond());
                g.drawCircle(toXY.getFirst(), toXY.getSecond(), sizer.cellSize / 2 - 1);
            }
        } else if (currentCoordinate != null && moveMap.containsKey(currentCoordinate)) {
            drawMovesFrom(g, currentCoordinate, moveMap.get(currentCoordinate).keySet());
        }
        if (endTurnLocation.containsPoint(mouseTracker.mouseX(), mouseTracker.mouseY())) {
            drawEndTurnButton(g, Color.GREEN);
        }
        for (Buy buy : allowedBuyMoves) {
            if (buy == null) {
                continue;
            }
            EComponentLocation buyLocation = playerBoards[currentPlayer].buyableLocations[buy.buyColumn];
            if (buyLocation.containsPoint(mouseTracker.mouseX(), mouseTracker.mouseY())) {
                g.setColor(Color.GREEN);
                g.drawRect(buyLocation.getX0(), buyLocation.getY0(), buyLocation.getWidth(), buyLocation.getHeight());
            }
        }
    }

    private void drawMovesFrom(IGraphics g, Coordinate from, Collection<Coordinate> tos) {
        DoublePair fromXY = hexGrid.centerXY(from);
        g.drawCircle(fromXY.getFirst(), fromXY.getSecond(), sizer.cellSize / 2 - 1);
        for (Coordinate to : tos) {
            DoublePair toXY = hexGrid.centerXY(to);
            g.drawLine(fromXY.getFirst(), fromXY.getSecond(), toXY.getFirst(), toXY.getSecond());
            g.drawCircle(toXY.getFirst(), toXY.getSecond(), sizer.cellSize / 2 - 1);
        }
    }

    private void drawTree(IGraphics g, double cx, double cy, int level, int player, boolean shadow) {
        double x0 = cx - sizer.cellSize / 2;
        double y0 = cy - sizer.cellSize / 2;
        IGameImage pieceImage = PhotosynthesisPieceImages.getInstance().getPieceImage(level, player, shadow);
        imageDrawer.drawImage(g, pieceImage, x0, y0, sizer.cellSize, sizer.cellSize);
    }

    private static String treeString(int buy, int total, int available) {
        return Integer.toString(buy) + "/" + Integer.toString(total) + " (" + Integer.toString(available) + ")";
    }

    private Coordinate maybeGetCoordinate() {
        if (!mouseTracker.isMouseEntered()) {
            return null;
        }
        for (Coordinate[] coordinates : PhotosynthesisPosition.ALL_TILES) {
            for (Coordinate coordinate : coordinates) {
                double cx = hexGrid.centerX(coordinate.x, coordinate.y);
                double cy = hexGrid.centerY(coordinate.x, coordinate.y);
                double dx = mouseTracker.mouseX() - cx;
                double dy = mouseTracker.mouseY() - cy;
                if (dx * dx + dy * dy < sizer.cellSize * sizer.cellSize / 4) {
                    return coordinate;
                }
            }
        }
        return null;
    }

    @Override
    public IPhotosynthesisMove maybeGetUserMove(UserInput input, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves) {
        if (input == UserInput.LEFT_BUTTON_RELEASED) {
            Coordinate mouseReleasedCoordinate = maybeGetCoordinate();

            if (mouseReleasedCoordinate == null) {
                mousePressedCoordinate = null;

                GuiPlayerBoard currentPlayerBoard = playerBoards[position.currentPlayer];
                int mouseX = mouseTracker.mouseX();
                int mouseY = mouseTracker.mouseY();

                for (int i = 0; i < currentPlayerBoard.buyableLocations.length; i++) {
                    if (currentPlayerBoard.buyableLocations[i].containsPoint(mouseTracker.mouseX(), mouseTracker.mouseY())
                            && allowedBuyMoves[i] != null) {
                        return allowedBuyMoves[i];
                    }
                }

                if (endTurnLocation.containsPoint(mouseX, mouseY)) {
                    return endTurn;
                }
            } else if (mousePressedCoordinate != null) {
                Map<Coordinate, IPhotosynthesisMove> map = moveMap.get(mousePressedCoordinate);
                mousePressedCoordinate = null;
                if (map != null) {
                    IPhotosynthesisMove move = map.get(mouseReleasedCoordinate);
                    if (move != null) {
                        return move;
                    }
                }
            }
        } else if (input == UserInput.LEFT_BUTTON_PRESSED) {
            mousePressedCoordinate = maybeGetCoordinate();
        }

        return null;
    }

    private static class HexGrid {
        private final double x0;
        private final double y0;
        private final double radius;

        public HexGrid(double x0, double y0, double radius) {
            this.x0 = x0;
            this.y0 = y0;
            this.radius = radius;
        }

        public double centerX(int a, int b) {
            return x0 + (a + b) * radius;
        }

        public double centerY(int a, int b) {
            return y0 + (b - a) * radius * Math.sqrt(3);
        }

        public DoublePair centerXY(Coordinate coord) {
            return new DoublePair(centerX(coord.x, coord.y), centerY(coord.x, coord.y));
        }
    }

    private static class GuiPlayerBoard implements Drawable {
        private final EComponentLocation cl;
        private final IGameImageDrawer imageDrawer;

        private final int playerNum;

        private final EComponentLocation[] buyableLocations = new EComponentLocation[4];
        private final EComponentLocation lightPointsLocation;
        private final EComponentLocation victoryPointsLocation;

        public GuiPlayerBoard(EComponentLocation cl, IGameImageDrawer imageDrawer, int playerNum) {
            this.cl = cl;
            this.imageDrawer = imageDrawer;
            this.playerNum = playerNum;
            double y0 = cl.getHeight() / 5;
            for (int i = 0; i < buyableLocations.length; ++i) {
                buyableLocations[i] = cl.createGluedLocation(GlueSide.TOP, 0, y0, 0, y0 + cl.getWidth() / 5 - 1);
                y0 += cl.getHeight() / 5;
            }
            EGluedLocation topLocation = cl.createGluedLocation(GlueSide.TOP, 0, 0, 0, cl.getWidth() / 5 - 1);
            lightPointsLocation = topLocation.createGluedLocation(GlueSide.LEFT, 0, 0, cl.getHeight() / 5 - 1, 0);
            victoryPointsLocation = topLocation.createGluedLocation(GlueSide.RIGHT, -cl.getHeight() / 5 + 1, 0, 0, 0);
        }

        @Override
        public void drawOn(IGraphics g) {
            g.fillRect(cl.getX0(), cl.getY0(), cl.getWidth(), cl.getHeight(), ComponentCreator.backgroundColor());
            g.setColor(Color.RED);
            g.drawRect(cl.getX0(), cl.getY0(), cl.getWidth(), cl.getHeight());
            for (int i = 0; i < buyableLocations.length; ++i) {
                double x0 = buyableLocations[i].getX0() + 2;
                double y0 = buyableLocations[i].getY0() + 2;
                double height = buyableLocations[i].getHeight() - 4;
                imageDrawer.drawImage(g, PhotosynthesisPieceImages.getInstance().getPieceImage(i, playerNum, false), x0, y0, height, height);
            }
            g.setColor(PLAYER_COLORS[playerNum]);
            g.drawRect(lightPointsLocation.getX0(), lightPointsLocation.getY0(), lightPointsLocation.getWidth(), lightPointsLocation.getHeight());
            g.drawCircle(lightPointsLocation.getCenterX(), lightPointsLocation.getCenterY(), lightPointsLocation.getWidth() / 2, Color.YELLOW);
            g.drawCircle(victoryPointsLocation.getCenterX(), victoryPointsLocation.getCenterY(), victoryPointsLocation.getWidth() / 2, Color.BLUE);
        }
    }
}
