package analysis;

import game.IPosition;

public interface ITreeSearcher<M, P extends IPosition<M>> {
	public void searchForever(P position, boolean escapeEarly);

	public boolean isSearching();

	public void stopSearch(boolean gameOver);

	public AnalysisResult<M> getResult();

	public void clearResult();
}
