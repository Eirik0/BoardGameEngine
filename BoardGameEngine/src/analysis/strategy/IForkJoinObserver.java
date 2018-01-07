package analysis.strategy;

import analysis.AnalysisResult;
import game.MoveList;

public interface ForkJoinObserver<M> {
	public void notifyPlyStarted(AnalysisResult<M> lastResult);

	public void notifyForked(M parentMove, MoveList<M> unanalyzedMoves);

	public void notifyPlyComplete(boolean searchStopped);
}
