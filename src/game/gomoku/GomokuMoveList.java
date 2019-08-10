package game.gomoku;

import game.ArrayMoveList;
import game.IPosition;
import game.MoveList;
import game.TwoPlayers;

public class GomokuMoveList implements MoveList<Integer> {
	private final ArrayMoveList<Integer> arrayMoveList;

	public GomokuMoveList(int capacity) {
		arrayMoveList = new ArrayMoveList<>(capacity);
	}

	@Override
	public void addDynamicMove(Integer move, IPosition<Integer> position) {
		if (hasNeighbors(((GomokuPosition) position).board, move.intValue())) {
			arrayMoveList.addDynamicMove(move, position);
		}
	}

	@Override
	public void addAllDynamicMoves(Integer[] moves, IPosition<Integer> position) {
		int i = 0;
		while (i < moves.length) {
			addDynamicMove(moves[i], position);
			++i;
		}
	}

	@Override
	public void addQuietMove(Integer move, IPosition<Integer> position) {
		if (hasNeighbors(((GomokuPosition) position).board, move.intValue())) {
			arrayMoveList.addQuietMove(move, position);
		}
	}

	@Override
	public void addAllQuietMoves(Integer[] moves, IPosition<Integer> position) {
		int i = 0;
		while (i < moves.length) {
			addQuietMove(moves[i], position);
			++i;
		}
	}

	private static boolean hasNeighbors(int[] board, int move) {
		return (move == 210) ||
				(board[move + GomokuUtilities.DIRECTIONS[0]] != TwoPlayers.UNPLAYED) ||
				(board[move + GomokuUtilities.DIRECTIONS[1]] != TwoPlayers.UNPLAYED) ||
				(board[move + GomokuUtilities.DIRECTIONS[2]] != TwoPlayers.UNPLAYED) ||
				(board[move + GomokuUtilities.DIRECTIONS[3]] != TwoPlayers.UNPLAYED) ||
				(board[move + GomokuUtilities.DIRECTIONS[4]] != TwoPlayers.UNPLAYED) ||
				(board[move + GomokuUtilities.DIRECTIONS[5]] != TwoPlayers.UNPLAYED) ||
				(board[move + GomokuUtilities.DIRECTIONS[6]] != TwoPlayers.UNPLAYED) ||
				(board[move + GomokuUtilities.DIRECTIONS[7]] != TwoPlayers.UNPLAYED);
	}

	@Override
	public Integer get(int index) {
		return arrayMoveList.get(index);
	}

	@Override
	public boolean contains(Integer move) {
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
	public MoveList<Integer> subList(int beginIndex) {
		return arrayMoveList.subList(beginIndex);
	}

	@Override
	public void clear() {
		arrayMoveList.clear();
	}
}
