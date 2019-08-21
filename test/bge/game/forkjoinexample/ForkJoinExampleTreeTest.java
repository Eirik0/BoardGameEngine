package bge.game.forkjoinexample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import bge.game.TwoPlayers;

public class ForkJoinExampleTreeTest {
    @Test
    public void testCreateTreeOfDepthZero() {
        assertNull(ForkJoinExampleTree.createTree(0, 2, true, 0));
    }

    @Test
    public void testCreateTreeOfDepthOne() {
        ForkJoinExampleNode createTreeOfDepth = ForkJoinExampleTree.createTree(1, 2, true, 0);
        assertEquals(0, createTreeOfDepth.getChildren().length);
        assertEquals(TwoPlayers.PLAYER_1, createTreeOfDepth.getPlayer());
    }

    @Test
    public void testCreateTreeOfDepthTwo() {
        ForkJoinExampleNode createTreeOfDepth = ForkJoinExampleTree.createTree(2, 2, true, 0);
        ForkJoinExampleNode[] children = createTreeOfDepth.getChildren();
        assertEquals(2, children.length);
        assertEquals(0, children[0].getChildren().length);
        assertEquals(0, children[1].getChildren().length);
        assertEquals(TwoPlayers.PLAYER_1, createTreeOfDepth.getPlayer());
    }

    @Test
    public void testCreateTreeOfDepthThree() {
        ForkJoinExampleNode createTreeOfDepth = ForkJoinExampleTree.createTree(1, 2, true, 0);
        assertEquals(TwoPlayers.PLAYER_1, createTreeOfDepth.getPlayer());
    }
}
