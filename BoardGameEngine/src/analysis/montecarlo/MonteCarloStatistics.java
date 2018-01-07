package analysis.montecarlo;

import analysis.AnalysisResult;

public class MonteCarloStatistics {
	public final int player;

	int nodesEvaluated = 0;
	int numWon = 0;
	int numDrawn = 0;
	int numLost = 0;

	public MonteCarloStatistics(int player) {
		this.player = player;
	}

	public void addScore(double score) {
		if (AnalysisResult.WIN == score) {
			++numWon;
		} else if (AnalysisResult.LOSS == score) {
			++numLost;
		} else {
			++numDrawn;
		}
	}

	public void updateWith(MonteCarloStatistics statistics) {
		nodesEvaluated += statistics.nodesEvaluated;
		numDrawn += statistics.numDrawn;
		if (player == statistics.player) {
			numWon += statistics.numWon;
			numLost += statistics.numLost;
		} else {
			numWon += statistics.numLost;
			numLost += statistics.numWon;
		}
	}

	public double getExpectedValue(int parentNodesEvaluated) {
		return getMeanValue() + getUncertainty(parentNodesEvaluated);
	}

	private double getUncertainty(int parentNodesEvaluated) {
		return Math.sqrt(2 * Math.log(parentNodesEvaluated) / nodesEvaluated);
	}

	public double getMeanValue() {
		return (numWon + numDrawn * 0.5) / nodesEvaluated;
	}

	@Override
	public String toString() {
		return player + ": " + numWon + " won, " + numDrawn + " drawn, " + numLost + " lost, " + nodesEvaluated + " total";
	}

	public String toString(int parentNodesEvaluated) {
		return toString() + "; " + String.format("%.2f +/- %.2f", Double.valueOf(getMeanValue()), Double.valueOf(getUncertainty(parentNodesEvaluated)));
	}
}
