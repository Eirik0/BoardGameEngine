package bge.igame.player;

import java.util.Collections;

import bge.analysis.AnalysisResult;
import bge.analysis.StrategyResult;
import bge.igame.IPosition;
import bge.strategy.IStrategy;
import bge.strategy.InterruptableStrategy;
import bge.strategy.ObservableStrategy;
import bge.strategy.UpdatableStrategy;

public class ComputerPlayer implements IPlayer {
    public static final String NAME = "Computer";

    private final PlayerInfo playerInfo;
    private final IStrategy<?> strategy;

    public ComputerPlayer(String gameName, PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        strategy = playerInfo.newStrategy(gameName);
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> M getMove(IPosition<M> position) {
        return ((IStrategy<M>) strategy).getMove(position);
    }

    @Override
    public void notifyTurnEnded() {
        if (strategy instanceof InterruptableStrategy) {
            ((InterruptableStrategy) strategy).pauseSearch();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> void notifyMoveMade(M move) {
        if (strategy instanceof UpdatableStrategy<?>) {
            ((UpdatableStrategy<M>) strategy).moveMade(move);
        }
    }

    @Override
    public synchronized void notifyGameEnded() {
        if (strategy instanceof InterruptableStrategy) {
            ((InterruptableStrategy) strategy).stopSearch();
        }
    }

    public StrategyResult getCurrentResult() {
        if (strategy instanceof ObservableStrategy) {
            return ((ObservableStrategy) strategy).getCurrentResult();
        }
        return new StrategyResult(new AnalysisResult<>(0), Collections.emptyList(), 0); // TODO (re)consider this
    }
}
