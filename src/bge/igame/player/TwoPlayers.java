package bge.igame.player;

public class TwoPlayers {
    public static final int NUMBER_OF_PLAYERS = 2;

    public static final int UNPLAYED = 0;

    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int BOTH_PLAYERS = 3;

    public static int otherPlayer(int player) {
        return player == 1 ? 2 : 1;
    }
}
