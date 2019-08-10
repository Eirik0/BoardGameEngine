package analysis.search;

import analysis.AnalysisResult;

public class MoveWithResult<M> {
	public final M move;
	public final AnalysisResult<M> result;

	public MoveWithResult(M move, AnalysisResult<M> result) {
		this.move = move;
		this.result = result;
	}
}
