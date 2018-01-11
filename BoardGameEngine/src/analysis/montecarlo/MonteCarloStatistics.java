package analysis.montecarlo;

import analysis.AnalysisResult;

public class MonteCarloStatistics {
	public final int player;

	int nodesEvaluated = 0;
	int numWon = 0;
	int numDrawn = 0;
	int numLost = 0;

	boolean isDecided = false;

	public MonteCarloStatistics(int player) {
		this.player = player;
	}

	public MonteCarloStatistics(int player, double score) {
		this.player = player;
		nodesEvaluated = 1;
		if (AnalysisResult.WIN == score) {
			++numWon;
		} else if (AnalysisResult.LOSS == score) {
			++numLost;
		} else {
			++numDrawn;
		}
		isDecided = true;
	}

	public void addScore(double score) {
		++nodesEvaluated;
		if (AnalysisResult.WIN == score) {
			++numWon;
		} else if (AnalysisResult.LOSS == score) {
			++numLost;
		} else {
			++numDrawn;
		}
	}

	public void updateWith(MonteCarloStatistics result) {
		nodesEvaluated += result.nodesEvaluated;
		numDrawn += result.numDrawn;
		if (player == result.player) {
			numWon += result.numWon;
			numLost += result.numLost;
		} else {
			numWon += result.numLost;
			numLost += result.numWon;
		}
	}

	public void setResult(MonteCarloStatistics result) {
		nodesEvaluated = result.nodesEvaluated;
		numDrawn = result.numDrawn;
		isDecided = result.isDecided;
		if (player == result.player) {
			numWon = result.numWon;
			numLost = result.numLost;
		} else {
			numWon = result.numLost;
			numLost = result.numWon;
		}
	}

	public void clear() {
		nodesEvaluated = 0;
		numWon = 0;
		numDrawn = 0;
		numLost = 0;
		isDecided = false;
	}

	public boolean isWin(int currentPlayer) {
		return isDecided && ((player == currentPlayer && numWon == nodesEvaluated) || (player != currentPlayer && numLost == nodesEvaluated));
	}

	public double getExpectedValue(int parentNodesEvaluated) {
		return getMeanValue() + getUncertainty(parentNodesEvaluated);
	}

	public double getUncertainty(int parentNodesEvaluated) {
		return isDecided ? 0.0 : Math.sqrt(2 * Math.log(parentNodesEvaluated) / nodesEvaluated);
	}

	public double getMeanValue() {
		return nodesEvaluated == 0 ? 0.0 : (double) (numWon - numLost) / nodesEvaluated;
	}

	@Override
	public String toString() {
		return player + ": " + numWon + " won, " + numDrawn + " drawn, " + numLost + " lost, " + nodesEvaluated + " total";
	}

	public String toString(int parentNodesEvaluated) {
		return toString() + "; " + String.format("%.2f +/- %.2f", Double.valueOf(getMeanValue()), Double.valueOf(getUncertainty(parentNodesEvaluated)));
	}
}
