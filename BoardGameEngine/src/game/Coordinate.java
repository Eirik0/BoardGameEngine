package game;

public class Coordinate {
	public final int x;
	public final int y;

	public static Coordinate valueOf(int x, int y) {
		return CoordinateCache.getCoordinate(x, y);
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

	static class CoordinateCache {
		static Coordinate[][] coordinates = new Coordinate[19][19];

		static Coordinate getCoordinate(int x, int y) {
			Coordinate coordinate = coordinates[x][y];
			if (coordinate == null) {
				coordinate = new Coordinate(x, y);
				coordinates[x][y] = coordinate;
			}
			return coordinate;
		}
	}
}
