package bge.game.photosynthesis;

import java.awt.Color;
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
import gt.gameentity.GridSizer;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;
import gt.util.DoublePair;

public class PhotosynthesisGameRenderer implements IGameRenderer<IPhotosynthesisMove, PhotosynthesisPosition>,
        IPositionObserver<IPhotosynthesisMove, PhotosynthesisPosition> {
    public static final Color[] PLAYER_COLORS = new Color[] { Color.RED, Color.BLUE, Color.WHITE, Color.MAGENTA };

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

    private final GuiPlayerBoard[] playerBoards = new GuiPlayerBoard[4];

    private Coordinate mousePressedCoordinate;

    // The allowed buy moves this turn
    private IPhotosynthesisMove[] allowedBuyMoves;
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
        double x0 = 0;
        double y0 = sizer.getCenterY(7);
        double width = imageWidth / 4.0;
        double height = imageHeight - y0;
        for (int i = 0; i < 4; ++i) {
            playerBoards[i] = new GuiPlayerBoard(x0, y0, width - 1, height - 1);
            g.drawRect(x0, y0, width - 1, height - 1, Color.RED);
            x0 += width;
        }
    }

    @Override
    public void notifyPositionChanged(PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves) {
        Map<Coordinate, Map<Coordinate, IPhotosynthesisMove>> newMoveMap = new HashMap<>();
        IPhotosynthesisMove[] newAllowedBuyMoves = new IPhotosynthesisMove[4];

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
    }

    @Override
    public void drawPosition(IGraphics g, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves, IPhotosynthesisMove lastMove) {
        Coordinate sunPos = SUN_POSITIONS[position.getSunPosition()];
        g.setColor(Color.YELLOW);
        g.drawCircle(hexGrid.centerX(sunPos.x, sunPos.y), hexGrid.centerY(sunPos.x, sunPos.y), sizer.cellSize * 3);
        g.drawCenteredYString("Rounds remaining: " + position.playerRoundsRemaining, 5, 20);
        int currentPlayer = position.currentPlayer;
        GuiPlayerBoard cpb = playerBoards[currentPlayer];
        g.drawRect(cpb.x0, cpb.y0, cpb.width, cpb.height, Color.GREEN);

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
                drawTree(g, hexGrid.centerX(a, b), hexGrid.centerY(a, b), tile.level, tile.player);
            }
        }

        for (int i = 0; i < position.numPlayers; ++i) {
            PlayerBoard pb = position.playerBoards[i];
            GuiPlayerBoard gpb = playerBoards[i];
            double compWidth = gpb.height / 6.0;
            // light points
            g.setFont(BoardGameEngineMain.DEFAULT_SMALL_FONT);
            g.setColor(Color.YELLOW);
            g.fillCircle(gpb.x0 + compWidth / 2, gpb.y0 + compWidth / 2, compWidth / 2);
            g.setColor(Color.BLUE);
            g.drawCenteredString(Integer.toString(pb.lightPoints), gpb.x0 + compWidth / 2, gpb.y0 + compWidth / 2);
            // victory points
            g.setColor(Color.GREEN);
            g.drawCircle(gpb.x0 + gpb.width - compWidth / 2, gpb.y0 + compWidth / 2, compWidth / 2);
            g.drawCenteredString(Integer.toString(pb.victoryPoints), gpb.x0 + gpb.width - compWidth / 2, gpb.y0 + compWidth / 2);
            // seeds
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 1.5, 0, i);
            int pbBuySeed = pb.buy[0] - 1;
            g.drawCenteredString(pbBuySeed < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[0][pbBuySeed]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 1.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 2.5, 1, i);
            int pbBuySmall = pb.buy[1] - 1;
            g.drawCenteredString(pbBuySmall < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[1][pbBuySmall]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 2.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 3.5, 2, i);
            int pbBuyMed = pb.buy[2] - 1;
            g.drawCenteredString(pbBuyMed < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[2][pbBuyMed]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 3.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 4.5, 3, i);
            int pbBuyLarge = pb.buy[3] - 1;
            g.drawCenteredString(pbBuyLarge < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[3][pbBuyLarge]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 4.5);
            g.setColor(PLAYER_COLORS[i]);

            g.drawRect(gpb.x0, gpb.y0, compWidth, compWidth);
            gpb.setBuyableLocations(new EComponentLocation[] {
                    new EFixedLocation(gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth, gpb.x0 + gpb.width, gpb.y0 + compWidth * 2),
                    new EFixedLocation(gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 2, gpb.x0 + gpb.width, gpb.y0 + compWidth * 3),
                    new EFixedLocation(gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 3, gpb.x0 + gpb.width, gpb.y0 + compWidth * 4),
                    new EFixedLocation(gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 4, gpb.x0 + gpb.width, gpb.y0 + compWidth * 5)
            });

            g.drawCenteredYString(treeString(pb.buy[0], 4, pb.available[0]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 1.5);
            g.drawCenteredYString(treeString(pb.buy[1], 4, pb.available[1]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 2.5);
            g.drawCenteredYString(treeString(pb.buy[2], 3, pb.available[2]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 3.5);
            g.drawCenteredYString(treeString(pb.buy[3], 2, pb.available[3]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 4.5);

            // end button
            if (i == position.currentPlayer) {
                gpb.setEndTurnLocation(new EFixedLocation(gpb.x0, gpb.y0 + compWidth * 5, gpb.x0 + gpb.width, gpb.y0 + compWidth * 6));
                g.drawCenteredString("End", gpb.x0 + gpb.width / 2, gpb.y0 + compWidth * 5.5);
            }
        }

        drawLastMove(g, lastMove);

        maybeDrawMouse(g, currentPlayer);
    }

    private void drawLastMove(IGraphics g, IPhotosynthesisMove move) {
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
            // TODO
        } else if (move instanceof EndTurn) {
            // TODO
        }
    }

    private void maybeDrawMouse(IGraphics g, int currentPlayer) {
        if (!GuiPlayer.HUMAN.isRequestingMove()) {
            return;
        }
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

    private void drawTree(IGraphics g, double cx, double cy, int level, int player) {
        double x0 = cx - sizer.cellSize / 2;
        double y0 = cy - sizer.cellSize / 2;
        imageDrawer.drawImage(g, PhotosynthesisPieceImages.getInstance().getPieceImage(level, player), x0, y0, sizer.cellSize, sizer.cellSize);
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

                if (currentPlayerBoard.endTurnLocation.containsPoint(mouseX, mouseY)) {
                    return endTurn;
                }
            } else if (mousePressedCoordinate != null) {
                Map<Coordinate, IPhotosynthesisMove> map = moveMap.get(mousePressedCoordinate);
                Coordinate print = mouseReleasedCoordinate;
                mousePressedCoordinate = null;
                if (map != null) {
                    IPhotosynthesisMove move = map.get(mouseReleasedCoordinate);
                    if (move != null) {
                        System.out.println(print + "->" + mouseReleasedCoordinate);
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

    private static class GuiPlayerBoard {
        private final double x0;
        private final double y0;
        private final double width;
        private final double height;

        private EComponentLocation[] buyableLocations;

        private EComponentLocation endTurnLocation;

        public GuiPlayerBoard(double x0, double y0, double width, double height) {
            this.x0 = x0;
            this.y0 = y0;
            this.width = width;
            this.height = height;
        }

        public void setBuyableLocations(EComponentLocation[] buyableLocations) {
            this.buyableLocations = buyableLocations;
        }

        public void setEndTurnLocation(EComponentLocation endTurnLocation) {
            this.endTurnLocation = endTurnLocation;
        }
    }
}
