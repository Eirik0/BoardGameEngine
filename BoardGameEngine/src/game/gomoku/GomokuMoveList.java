package game.gomoku;

import game.ArrayMoveList;
import game.Coordinate;
import game.IPosition;
import game.MoveList;
import game.TwoPlayers;

public class GomokuMoveList implements MoveList<Coordinate> {
	ArrayMoveList<Coordinate> arrayMoveList;

	public GomokuMoveList(Integer capacity) {
		arrayMoveList = new ArrayMoveList<>(capacity);
	}

	@Override
	public <P extends IPosition<Coordinate, P>> void add(Coordinate move, P position) {
		int[][] board = ((GomokuPosition) position).board;
		int x = move.x;
		int y = move.y;
		if ((x == 9 && y == 9) ||
				(y > 0 && x > 0 && board[y - 1][x - 1] != TwoPlayers.UNPLAYED) ||
				(y > 0 && board[y - 1][x] != TwoPlayers.UNPLAYED) ||
				(y > 0 && x < GomokuPosition.BOARD_WIDTH - 1 && board[y - 1][x + 1] != TwoPlayers.UNPLAYED) ||
				(x > 0 && board[y][x - 1] != TwoPlayers.UNPLAYED) ||
				(x < GomokuPosition.BOARD_WIDTH - 1 && board[y][x + 1] != TwoPlayers.UNPLAYED) ||
				(y < GomokuPosition.BOARD_WIDTH - 1 && x > 0 && board[y + 1][x - 1] != TwoPlayers.UNPLAYED) ||
				(y < GomokuPosition.BOARD_WIDTH - 1 && board[y + 1][x] != TwoPlayers.UNPLAYED) ||
				(y < GomokuPosition.BOARD_WIDTH - 1 && x < GomokuPosition.BOARD_WIDTH - 1 && board[y + 1][x + 1] != TwoPlayers.UNPLAYED)) {
			arrayMoveList.add(move, position);
		}
	}

	@Override
	public <P extends IPosition<Coordinate, P>> void addAll(Coordinate[] moves, P position) {
		for (Coordinate move : moves) {
			add(move, position);
		}
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
	public MoveList<Coordinate> subList(int beginIndex) {
		return arrayMoveList.subList(beginIndex);
	}

	@Override
	public void clear() {
		arrayMoveList.clear();
	}
}
