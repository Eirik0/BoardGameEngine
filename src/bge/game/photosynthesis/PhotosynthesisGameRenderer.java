package bge.game.photosynthesis;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import bge.game.photosynthesis.PhotosynthesisPosition.PlayerBoard;
import bge.game.photosynthesis.PhotosynthesisPosition.Setup;
import bge.game.photosynthesis.PhotosynthesisPosition.Tile;
import bge.gui.gamestate.IGameRenderer;
import bge.gui.gamestate.IPositionObserver;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import bge.igame.player.GuiPlayer;
import bge.main.BoardGameEngineMain;
import gt.component.IMouseTracker;
import gt.gameentity.GridSizer;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;

public class PhotosynthesisGameRenderer implements IGameRenderer<IPhotosynthesisMove, PhotosynthesisPosition>,
        IPositionObserver<IPhotosynthesisMove, PhotosynthesisPosition> {
    private static final Coordinate[] SUN_POSITIONS = new Coordinate[] {
            Coordinate.valueOf(0, 0),
            Coordinate.valueOf(3, 0),
            Coordinate.valueOf(6, 3),
            Coordinate.valueOf(6, 6),
            Coordinate.valueOf(3, 6),
            Coordinate.valueOf(0, 3),
    };

    private static enum SpecialMove {
        BUY_SEED, BUY_SMALL, BUY_MEDIUM, BUY_LARGE, END
    }

    private static final Color[] PLAYER_COLORS = new Color[] { Color.RED, Color.BLUE, Color.WHITE, Color.MAGENTA };

    private final IMouseTracker mouseTracker;
    private GridSizer sizer;
    private HexGrid hexGrid;

    private final GuiPlayerBoard[] playerBoards = new GuiPlayerBoard[4];

    private Map<Coordinate, Map<Coordinate, IPhotosynthesisMove>> moveMap = new HashMap<>();
    private Map<SpecialMove, IPhotosynthesisMove> specialMoveMap = new HashMap<>();

    public PhotosynthesisGameRenderer(IMouseTracker mouseTracker) {
        this.mouseTracker = mouseTracker;
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        sizer = new GridSizer(imageWidth, imageHeight, 11, 11);
        hexGrid = new HexGrid(sizer.getCenterX(2), sizer.getCenterY(3), sizer.cellSize / 2);

        g.fillRect(0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);
        int green = 255 / 2;
        int red = 255 / 2;
        for (Coordinate[] coordinates : PhotosynthesisPosition.ALL_TILES) {
            g.setColor(new Color(red, green, 0));
            for (Coordinate coordinate : coordinates) {
                double cx = hexGrid.centerX(coordinate.x, coordinate.y);
                double cy = hexGrid.centerY(coordinate.x, coordinate.y);
                g.fillCircle(cx, cy, sizer.cellSize / 2);
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
        Map<SpecialMove, IPhotosynthesisMove> newSpecialMoveMap = new HashMap<>();
        int i = 0;
        while (i < possibleMoves.size()) {
            IPhotosynthesisMove move = possibleMoves.get(i);
            if (move instanceof Setup) {
                Setup setupMove = (Setup) move;
                Coordinate coordinate = setupMove.coordinate;
                Map<Coordinate, IPhotosynthesisMove> map = newMoveMap.get(coordinate);
                if (map == null) {
                    map = new HashMap<>();
                    newMoveMap.put(coordinate, map);
                }
                map.put(coordinate, move);
            }
            ++i;
        }
        moveMap = newMoveMap;
        specialMoveMap = newSpecialMoveMap;
    }

    @Override
    public void drawPosition(IGraphics g, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves, IPhotosynthesisMove lastMove) {
        Coordinate sunPos = SUN_POSITIONS[(4 * 6 * 3 + 12 - position.playerRoundsRemaining) % 6];
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
                double cx = hexGrid.centerX(a, b);
                double cy = hexGrid.centerY(a, b);
                drawTree(g, cx, cy, sizer.cellSize / 3, tile.level, tile.player);
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
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 1.5, compWidth / 2, 0, i);
            int pbBuySeed = pb.buy[0] - 1;
            g.drawCenteredString(pbBuySeed < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[0][pbBuySeed]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 1.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 2.5, compWidth / 2, 1, i);
            int pbBuySmall = pb.buy[1] - 1;
            g.drawCenteredString(pbBuySmall < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[1][pbBuySmall]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 2.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 3.5, compWidth / 2, 2, i);
            int pbBuyMed = pb.buy[2] - 1;
            g.drawCenteredString(pbBuyMed < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[2][pbBuyMed]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 3.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 4.5, compWidth / 2, 3, i);
            int pbBuyLarge = pb.buy[3] - 1;
            g.drawCenteredString(pbBuyLarge < 0 ? "X" : Integer.toString(PhotosynthesisPosition.PRICES[3][pbBuyLarge]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 4.5);
            g.setColor(PLAYER_COLORS[i]);
            g.drawCenteredYString(treeString(pb.buy[0], 4, pb.available[0]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 1.5);
            g.drawCenteredYString(treeString(pb.buy[1], 4, pb.available[1]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 2.5);
            g.drawCenteredYString(treeString(pb.buy[2], 3, pb.available[2]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 3.5);
            g.drawCenteredYString(treeString(pb.buy[3], 2, pb.available[3]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 4.5);
            // end button
            if (i == position.currentPlayer) {
                g.drawCenteredString("End", gpb.x0 + gpb.width / 2, gpb.y0 + compWidth * 5.5);
            }
        }

        if (GuiPlayer.HUMAN.isRequestingMove()) {
            Coordinate coordinate = maybeGetCoordinate();
            if (coordinate != null && moveMap.containsKey(coordinate)) {
                double cx = hexGrid.centerX(coordinate.x, coordinate.y);
                double cy = hexGrid.centerY(coordinate.x, coordinate.y);
                g.setColor(PLAYER_COLORS[currentPlayer]);
                g.drawCircle(cx, cy, sizer.cellSize / 2 - 1);
            }
        }
    }

    private static void drawTree(IGraphics g, double cx, double cy, double r, int level, int player) {
        g.setColor(PLAYER_COLORS[player]);
        switch (level) {
        case 0: // seed
            g.fillRect(cx - r / 4, cy - r / 4, r / 2, r / 2);
            break;
        case 1: // small
            r *= 0.7;
        case 2: // med
            r *= 0.7;
        case 3: // large
            g.fillCircle(cx, cy, r - 1);
            break;
        default:
            throw new IllegalStateException("Unknown tree level " + level);
        }
        g.setColor(Color.GREEN);
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
            Coordinate coordinate = maybeGetCoordinate();
            Map<Coordinate, IPhotosynthesisMove> map = moveMap.get(coordinate);
            if (map != null) {
                IPhotosynthesisMove move = map.get(coordinate);
                if (move != null) {
                    return move;
                }
            }

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
    }

    private static class GuiPlayerBoard {
        private final double x0;
        private final double y0;
        private final double width;
        private final double height;

        public GuiPlayerBoard(double x0, double y0, double width, double height) {
            this.x0 = x0;
            this.y0 = y0;
            this.width = width;
            this.height = height;
        }
    }
}
