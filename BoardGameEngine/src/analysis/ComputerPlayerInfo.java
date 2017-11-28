package analysis;

import java.util.function.Supplier;

import analysis.strategy.IDepthBasedStrategy;
import game.IPosition;

public class ComputerPlayerInfo<M, P extends IPosition<M, P>> {
	public String strategyName;
	public Supplier<IDepthBasedStrategy<M, P>> strategySupplier;
	public int numWorkers;
	public long msPerMove;
	public boolean infiniteTimeOnly;
	public int maxWorkers;

	public ComputerPlayerInfo(String strategyName, Supplier<IDepthBasedStrategy<M, P>> strategySupplier, int numWorkers, long msPerMove, int maxWorkers) {
		setValues(strategyName, strategySupplier, numWorkers, msPerMove);
		this.maxWorkers = maxWorkers;
	}

	public void setValues(String strategyName, Supplier<IDepthBasedStrategy<M, P>> strategySupplier, int numWorkers, long msPerMove) {
		this.strategyName = strategyName;
		this.strategySupplier = strategySupplier;
		this.numWorkers = numWorkers;
		this.msPerMove = msPerMove;
		this.infiniteTimeOnly = msPerMove == Long.MAX_VALUE;
	}

	public static String getComputerName(String strategyName, int numWorkers, long msPerMove) {
		return new StringBuilder(strategyName).append("(").append(numWorkers).append(", ").append(msPerMove == Long.MAX_VALUE ? "Inf" : msPerMove).append(")").toString();
	}

	@Override
	public String toString() {
		return getComputerName(strategyName, numWorkers, msPerMove);
	}
}
