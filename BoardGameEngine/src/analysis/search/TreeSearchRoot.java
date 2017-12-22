package analysis.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import analysis.AnalysisResult;
import analysis.AnalyzedMove;
import analysis.MoveAnalysis;
import game.IPosition;

public class TreeSearchRoot<M, P extends IPosition<M>> {
	private final List<GameTreeSearch<M, P>> branches;
	private boolean[] max;

	public TreeSearchRoot() {
		branches = Collections.emptyList();
		max = new boolean[] {};
	}

	public TreeSearchRoot(GameTreeSearch<M, P> rootTreeSearch) {
		// Always fork once so we can keep track of the searches in progress
		if (rootTreeSearch.isForkable()) {
			branches = rootTreeSearch.fork();
			max = new boolean[branches.size()];
			int i = 0;
			do {
				max[i] = rootTreeSearch.getPlayer() == branches.get(i).getPlayer();
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

	public Map<M, MoveAnalysis> getPartialResult() {
		Map<M, MoveAnalysis> movesWithScore = new HashMap<>();
		int i = 0;
		while (i < branches.size()) {
			GameTreeSearch<M, P> branch = branches.get(i);
			AnalysisResult<M> partialResult = branch.getResult();
			if (partialResult != null && partialResult.isSearchComplete()) {
				AnalyzedMove<M> maxMoveWithScore = partialResult.getBestMove(max[i]);
				if (maxMoveWithScore != null) {
					movesWithScore.put(branch.getParentMove(), maxMoveWithScore.analysis);
				}
			}
			++i;
		}
		return movesWithScore;
	}
}
