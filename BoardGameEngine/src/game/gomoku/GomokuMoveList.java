package game.gomoku;

import game.ArrayMoveList;
import game.Coordinate;
import game.IPosition;
import game.MoveList;
import game.TwoPlayers;

public class GomokuMoveList implements MoveList<Coordinate> {
	private final ArrayMoveList<Coordinate> arrayMoveList;

	public GomokuMoveList(int capacity) {
		arrayMoveList = new ArrayMoveList<>(capacity);
	}

	@Override
	public <P extends IPosition<Coordinate, P>> void addDynamicMove(Coordinate move, P position) {
		if (hasNeighbors(((GomokuPosition) position).board, move.x, move.y)) {
			arrayMoveList.addDynamicMove(move, position);
		}
	}

	@Override
	public <P extends IPosition<Coordinate, P>> void addQuietMove(Coordinate move, P position) {
		if (hasNeighbors(((GomokuPosition) position).board, move.x, move.y)) {
			arrayMoveList.addQuietMove(move, position);
		}
	}

	@Override
	public <P extends IPosition<Coordinate, P>> void addAllQuietMoves(Coordinate[] moves, P position) {
		int i = 0;
		while (i < moves.length) {
			addQuietMove(moves[i], position);
			++i;
		}
	}

	private static boolean hasNeighbors(int[][] board, int x, int y) {
		return (x == 9 && y == 9) ||
				(y > 0 && x > 0 && board[y - 1][x - 1] != TwoPlayers.UNPLAYED) ||
				(y > 0 && board[y - 1][x] != TwoPlayers.UNPLAYED) ||
				(y > 0 && x < GomokuPosition.BOARD_WIDTH - 1 && board[y - 1][x + 1] != TwoPlayers.UNPLAYED) ||
				(x > 0 && board[y][x - 1] != TwoPlayers.UNPLAYED) ||
				(x < GomokuPosition.BOARD_WIDTH - 1 && board[y][x + 1] != TwoPlayers.UNPLAYED) ||
				(y < GomokuPosition.BOARD_WIDTH - 1 && x > 0 && board[y + 1][x - 1] != TwoPlayers.UNPLAYED) ||
				(y < GomokuPosition.BOARD_WIDTH - 1 && board[y + 1][x] != TwoPlayers.UNPLAYED) ||
				(y < GomokuPosition.BOARD_WIDTH - 1 && x < GomokuPosition.BOARD_WIDTH - 1 && board[y + 1][x + 1] != TwoPlayers.UNPLAYED);
	}

	@Override
	public Coordinate get(int index) {
		return arrayMoveList.get(index);
	}

	@Override
	public boolean contains(Coordinate move) {
		return arrayMoveList.contains(move);
	}

	@Override
	public int size() {
		return arrayMoveList.size();
	}

	@Override
	public int numDynamicMoves() {
		return arrayMoveList.numDynamicMoves();
	}

	@Override
	public MoveList<Coordinate> subList(int beginIndex) {
		return arrayMoveList.subList(beginIndex);
	}

	@Override
	public void clear() {
		arrayMoveList.clear();
	}
}
