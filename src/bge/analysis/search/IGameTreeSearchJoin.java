package bge.analysis.search;

import bge.analysis.AnalysisResult;

@FunctionalInterface
public interface IGameTreeSearchJoin<M> {
    public void accept(boolean searchCanceled, MoveWithResult<M> moveWithResult);

    public default AnalysisResult<M> getPartialResult() {
        return null;
    }

    public default IGameTreeSearchJoin<M> getParent() {
        return null;
    }
}
