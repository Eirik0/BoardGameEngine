package analysis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import game.IPosition;

public class TreeSearchRoot<M, P extends IPosition<M, P>> {
	private final List<GameTreeSearch<M, P>> branches;
	private boolean[] max;

	public TreeSearchRoot() {
		branches = Collections.emptyList();
		max = new boolean[] {};
	}

	public TreeSearchRoot(GameTreeSearch<M, P> rootTreeSearch) {
		// Always fork once so we can keep track of the searches in progress
		if (rootTreeSearch.getPlies() > 0 && rootTreeSearch.getRemainingBranches() > 0) {
			branches = rootTreeSearch.fork();
			max = new boolean[branches.size()];
			int i = 0;
			do {
				max[i] = rootTreeSearch.player == branches.get(i).player;
				++i;
			} while (i < max.length);
		} else {
			branches = Collections.singletonList(rootTreeSearch);
			max = new boolean[] { true };
		}
	}

	public List<GameTreeSearch<M, P>> getBranches() {
		return branches;
	}

	public List<MoveWithScore<M>> getPartialResult() {
		List<MoveWithScore<M>> movesWithScore = new ArrayList<>();
		int i = 0;
		while (i < branches.size()) {
			GameTreeSearch<M, P> branch = branches.get(i);
			AnalysisResult<M> partialResult = branch.getResult();
			if (partialResult != null && partialResult.isSeachComplete()) {
				MoveWithScore<M> maxMoveWithScore = partialResult.getMax();
				if (maxMoveWithScore != null) {
					movesWithScore.add(new MoveWithScore<>(branch.parentMove, max[i] ? maxMoveWithScore.score : -maxMoveWithScore.score));
				}
			}
			++i;
		}
		return movesWithScore;
	}
}
