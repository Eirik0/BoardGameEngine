package bge.game.photosynthesis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import bge.game.Coordinate;
import bge.game.MoveList;
import bge.game.photosynthesis.PhotosynthesisPosition.PlayerBoard;
import bge.game.photosynthesis.PhotosynthesisPosition.Setup;
import bge.game.photosynthesis.PhotosynthesisPosition.Tile;
import bge.gui.DrawingMethods;
import bge.gui.GameGuiManager;
import bge.gui.GuiPlayer;
import bge.gui.gamestate.GameState.UserInput;
import bge.gui.gamestate.IGameRenderer;
import bge.gui.gamestate.IPositionObserver;
import bge.main.BoardGameEngineMain;
import gt.gameentity.GridSizer;

public class PhotosynthesisGameRenderer implements IGameRenderer<IPhotosynthesisMove, PhotosynthesisPosition>,
        IPositionObserver<IPhotosynthesisMove, PhotosynthesisPosition> {
    private static enum SpecialMove {
        BUY_SEED, BUY_SMALL, BUY_MEDIUM, BUY_LARGE, END
    }

    private static final Color[] PLAYER_COLORS = new Color[] { Color.RED, Color.BLUE, Color.WHITE, Color.BLACK };

    private GridSizer sizer;
    private HexGrid hexGrid;

    private final GuiPlayerBoard[] playerBoards = new GuiPlayerBoard[4];

    private Map<Coordinate, Map<Coordinate, IPhotosynthesisMove>> moveMap = new HashMap<>();
    private Map<SpecialMove, IPhotosynthesisMove> specialMoveMap = new HashMap<>();

    public PhotosynthesisGameRenderer() {
    }

    @Override
    public void initializeAndDrawBoard(Graphics2D g) {
        int imageWidth = GameGuiManager.getComponentWidth();
        int imageHeight = GameGuiManager.getComponentHeight();

        sizer = new GridSizer(imageWidth, imageHeight, 11, 11);
        hexGrid = new HexGrid(sizer.getCenterX(2), sizer.getCenterY(3), sizer.cellSize / 2);

        fillRect(g, 0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);
        int green = 255 / 2;
        int red = 255 / 2;
        for (Coordinate[] coordinates : PhotosynthesisPosition.ALL_TILES) {
            g.setColor(new Color(red, green, 0));
            for (Coordinate coordinate : coordinates) {
                double cx = hexGrid.centerX(coordinate.x, coordinate.y);
                double cy = hexGrid.centerY(coordinate.x, coordinate.y);
                fillCircle(g, cx, cy, sizer.cellSize / 2);
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
            drawRect(g, x0, y0, width - 1, height - 1, Color.RED);
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
    public void drawPosition(Graphics2D g, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves, IPhotosynthesisMove lastMove) {
        // TODO draw sun
        int currentPlayer = position.currentPlayer;
        GuiPlayerBoard cpb = playerBoards[currentPlayer];
        drawRect(g, cpb.x0, cpb.y0, cpb.width, cpb.height, Color.GREEN);

        Tile[][] grid = position.mainBoard.grid;
        for (int a = 0; a < grid.length; ++a) {
            Tile[] tiles = grid[a];
            for (int b = 0; b < tiles.length; ++b) {
                Tile tile = tiles[b];
                if (tile == null) {
                    continue;
                }
                double x = hexGrid.centerX(a, b);
                double y = hexGrid.centerY(a, b);
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
            g.setColor(Color.YELLOW);
            fillCircle(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth / 2, compWidth / 2);
            g.setColor(Color.BLUE);
            drawCenteredString(g, Integer.toString(pb.lightPoints), gpb.x0 + compWidth / 2, gpb.y0 + compWidth / 2);
            // victory points
            g.setColor(Color.GREEN);
            drawCircle(g, gpb.x0 + gpb.width - compWidth / 2, gpb.y0 + compWidth / 2, compWidth / 2);
            drawCenteredString(g, Integer.toString(pb.victoryPoints), gpb.x0 + gpb.width - compWidth / 2, gpb.y0 + compWidth / 2);
            // seeds
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 1.5, compWidth / 2, 0, i);
            DrawingMethods.drawCenteredStringS(g, Integer.toString(PhotosynthesisPosition.PRICES[0][pb.buy[0] - 1]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 1.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 2.5, compWidth / 2, 1, i);
            DrawingMethods.drawCenteredStringS(g, Integer.toString(PhotosynthesisPosition.PRICES[1][pb.buy[1] - 1]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 2.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 3.5, compWidth / 2, 2, i);
            DrawingMethods.drawCenteredStringS(g, Integer.toString(PhotosynthesisPosition.PRICES[2][pb.buy[2] - 1]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 3.5);
            drawTree(g, gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 4.5, compWidth / 2, 3, i);
            DrawingMethods.drawCenteredStringS(g, Integer.toString(PhotosynthesisPosition.PRICES[3][pb.buy[3] - 1]),
                    gpb.x0 + compWidth / 2, gpb.y0 + compWidth * 4.5);
            g.setColor(PLAYER_COLORS[i]);
            drawCenteredYString(g, treeString(pb.buy[0], 4, pb.available[0]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 1.5);
            drawCenteredYString(g, treeString(pb.buy[1], 4, pb.available[1]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 2.5);
            drawCenteredYString(g, treeString(pb.buy[2], 3, pb.available[2]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 3.5);
            drawCenteredYString(g, treeString(pb.buy[3], 2, pb.available[3]), gpb.x0 + compWidth * 1.5, gpb.y0 + compWidth * 4.5);
            // end button
            if (i == position.currentPlayer) {
                drawCenteredString(g, "End", gpb.x0 + gpb.width / 2, gpb.y0 + compWidth * 5.5);
            }
        }

        if (GuiPlayer.HUMAN.isRequestingMove()) {
            Coordinate coordinate = maybeGetCoordinate(GameGuiManager.getMouseX(), GameGuiManager.getMouseY());
            if (coordinate != null && moveMap.containsKey(coordinate)) {
                double cx = hexGrid.centerX(coordinate.x, coordinate.y);
                double cy = hexGrid.centerY(coordinate.x, coordinate.y);
                g.setColor(PLAYER_COLORS[currentPlayer]);
                drawCircle(g, cx, cy, sizer.cellSize / 2 - 1);
            }
        }
    }

    private static void drawTree(Graphics2D g, double cx, double cy, double r, int level, int player) {
        g.setColor(PLAYER_COLORS[player]);
        switch (level) {
        case 0: // seed
            DrawingMethods.fillRectS(g, cx - r / 4, cy - r / 4, r / 2, r / 2);
            break;
        case 1: // small
            r *= 0.7;
        case 2: // med
            r *= 0.7;
        case 3: // large
            DrawingMethods.fillCircleS(g, cx, cy, r - 1);
            break;
        default:
            throw new IllegalStateException("Unknown tree level " + level);
        }
        g.setColor(Color.GREEN);
    }

    private static String treeString(int buy, int total, int available) {
        return Integer.toString(buy) + "/" + Integer.toString(total) + " (" + Integer.toString(available) + ")";
    }

    private Coordinate maybeGetCoordinate(int mouseX, int mouseY) {
        if (!GameGuiManager.isMouseEntered()) {
            return null;
        }
        for (Coordinate[] coordinates : PhotosynthesisPosition.ALL_TILES) {
            for (Coordinate coordinate : coordinates) {
                double cx = hexGrid.centerX(coordinate.x, coordinate.y);
                double cy = hexGrid.centerY(coordinate.x, coordinate.y);
                double dx = mouseX - cx;
                double dy = mouseY - cy;
                if (dx * dx + dy * dy < sizer.cellSize * sizer.cellSize / 4) {
                    return coordinate;
                }
            }
        }
        return null;
    }

    @Override
    public IPhotosynthesisMove maybeGetUserMove(UserInput input, PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves) {
        // TODO Auto-generated method stub
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
