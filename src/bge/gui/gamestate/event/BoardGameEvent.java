package bge.gui.gamestate.event;

import bge.igame.PositionChangedInfo;

public interface BoardGameEvent {
    public static class GameStartEvent implements BoardGameEvent {
    }

    public static class PositionChangedEvent<M> implements BoardGameEvent {
        public final PositionChangedInfo<M> changeInfo;

        public PositionChangedEvent(PositionChangedInfo<M> changeInfo) {
            this.changeInfo = changeInfo;
        }
    }

    public static class GamePausedEvent implements BoardGameEvent {
        public boolean gameOver;

        public GamePausedEvent(boolean gameOver) {
            this.gameOver = gameOver;
        }
    }
}
