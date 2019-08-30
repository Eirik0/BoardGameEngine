package bge.analysis;

import bge.igame.IPosition;

public interface ITreeSearcher<M, P extends IPosition<M>> {
    void searchForever(P position, boolean escapeEarly);

    boolean isSearching();

    void stopSearch(boolean gameOver);

    AnalysisResult<M> getResult();

    void clearResult();
}
