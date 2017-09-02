package analysis.search;

import analysis.AnalysisResult;

public class MoveWithResult<M> {
	public final M move;
	public final AnalysisResult<M> result;

	private boolean isValid = true;

	public MoveWithResult(M move, AnalysisResult<M> result) {
		this(move, result, true);
	}

	public MoveWithResult(M move, AnalysisResult<M> result, boolean isValid) {
		this.move = move;
		this.result = result;
		this.isValid = isValid;
	}

	public boolean isValid() {
		return isValid;
	}

	public void invalidate() {
		isValid = false;
	}
}
