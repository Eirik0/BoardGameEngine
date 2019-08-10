package bge.game;

public class Coordinate {
    private static final int BOARD_SHIFT = 5;
    private static final int MAX_BOARD_WIDTH = 1 << BOARD_SHIFT; // = 2 ^ BOARD_SHIFT

    private static final Coordinate[] coordinates = new Coordinate[MAX_BOARD_WIDTH * MAX_BOARD_WIDTH];

    public final int x;
    public final int y;

    public static Coordinate valueOf(int x, int y) {
        int index = (y << BOARD_SHIFT) + x;
        Coordinate coordinate = coordinates[index];
        if (coordinate == null) {
            coordinate = new Coordinate(x, y);
            coordinates[index] = coordinate;
        }
        return coordinate;
    }

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime + x) + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinate other = (Coordinate) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
