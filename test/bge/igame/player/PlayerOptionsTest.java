package bge.igame.player;

import org.junit.jupiter.api.Test;

import bge.game.tictactoe.TicTacToeGame;
import bge.game.tictactoe.TicTacToeGameRenderer;
import bge.game.tictactoe.TicTacToePositionEvaluator;
import bge.main.BoardGameEngineMain;
import bge.main.GameRegistry;;

public class PlayerOptionsTest {
    @Test
    public void testDefaultOptions() {
        BoardGameEngineMain.registerGames();
        TicTacToeGame game = new TicTacToeGame();
        GameRegistry.registerGame(game, (mouseTracker, imageDrawer) -> new TicTacToeGameRenderer(mouseTracker))
                .addPlayer(ComputerPlayer.NAME)
                .addPositionEvaluator("Evaluator1", new TicTacToePositionEvaluator());

    }
}
