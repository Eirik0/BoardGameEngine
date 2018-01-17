package analysis.montecarlo;

import analysis.AnalysisResult;

public class MonteCarloStatistics {
	public static final double WIN = 1;
	public static final double DRAW = 0;
	public static final double LOSS = -1;

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
			numWon = 1;
		} else if (AnalysisResult.LOSS == score) {
			numLost = 1;
		} else {
			numDrawn = 1;
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

	public void setDecided() {
		isDecided = true;
		if (numWon > 0) {
			numWon = nodesEvaluated;
			numDrawn = 0;
			numLost = 0;
		} else if (numDrawn > 0) {
			numDrawn = nodesEvaluated;
			numLost = 0;
		} else {
			numLost = nodesEvaluated;
		}
	}

	public void setResult(MonteCarloStatistics statistics) {
		nodesEvaluated = statistics.nodesEvaluated;
		numWon = statistics.numWon;
		numDrawn = statistics.numDrawn;
		numLost = statistics.numLost;
		isDecided = statistics.isDecided;
	}

	public boolean isWin(int currentPlayer) {
		return isDecided && ((player == currentPlayer && numWon == nodesEvaluated) || (player != currentPlayer && numLost == nodesEvaluated));
	}

	public double getExpectedValue(int parentNodesEvaluated) {
		return getMeanValue() + getUncertainty(parentNodesEvaluated);
	}

	public double getUncertainty(int parentNodesEvaluated) {
		return isDecided || parentNodesEvaluated == 0 ? 0.0 : Math.sqrt(2 * Math.log(parentNodesEvaluated) / nodesEvaluated);
	}

	public double getMeanValue() {
		if (isDecided) {
			return numWon > 0 ? WIN : numDrawn > DRAW ? 0.0 : LOSS;
		}
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
