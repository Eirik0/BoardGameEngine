package analysis.search;

import java.util.HashSet;
import java.util.Set;

import analysis.MoveWithScore;
import game.IPosition;
import game.MoveList;

public class SearchMoveList<M> implements MoveList<M> {
	private final MoveList<M> moveList;
	private final Set<M> decidedMoves;

	public SearchMoveList(MoveList<M> moveList, Set<MoveWithScore<M>> decidedMoves) {
		this.moveList = moveList;
		this.decidedMoves = new HashSet<>();
		for (MoveWithScore<M> moveWithScore : decidedMoves) {
			decidedMoves.add(moveWithScore);
		}
	}

	@Override
	public <P extends IPosition<M, P>> void setQuietMoves(M[] moves, P position) {
		moveList.setQuietMoves(moves, position);
	}

	@Override
	public <P extends IPosition<M, P>> void addDynamicMove(M move, P position) {
		if (!decidedMoves.contains(move)) {
			moveList.addDynamicMove(move, position);
		}
	}

	@Override
	public <P extends IPosition<M, P>> void addQuietMove(M move, P position) {
		if (!decidedMoves.contains(move)) {
			moveList.addQuietMove(move, position);
		}
	}

	@Override
	public M get(int index) {
		return moveList.get(index);
	}

	@Override
	public boolean contains(M move) {
		return moveList.contains(move);
	}

	@Override
	public int size() {
		return moveList.size();
	}

	@Override
	public int numDynamicMoves() {
		return moveList.numDynamicMoves();
	}

	@Override
	public MoveList<M> subList(int beginIndex) {
		return moveList.subList(beginIndex);
	}

	@Override
	public void clear() {
		moveList.clear();
	}
}
