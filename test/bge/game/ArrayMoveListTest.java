package bge.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArrayMoveListTest {
    @Test
    public void testAddOne() {
        MoveList<String> moveList = new ArrayMoveList<>(1);
        moveList.addQuietMove("test", null);
        assertEquals(1, moveList.size());
        assertEquals("test", moveList.get(0));
        assertTrue(moveList.contains("test"));
    }

    @Test
    public void testSet() {
        MoveList<String> moveList = new ArrayMoveList<>(2);
        moveList.addAllQuietMoves(new String[] { "test1", "test2" }, null);
        assertEquals(2, moveList.size());
        assertEquals("test1", moveList.get(0));
        assertEquals("test2", moveList.get(1));
        assertTrue(moveList.contains("test1"));
        assertTrue(moveList.contains("test2"));
    }

    @Test
    public void testAddOneThenAddAll() {
        MoveList<String> moveList = new ArrayMoveList<>(3);
        moveList.addQuietMove("test0", null);
        moveList.addAllQuietMoves(new String[] { "test1", "test2" }, null);
        assertEquals(3, moveList.size());
        assertEquals("test0", moveList.get(0));
        assertEquals("test1", moveList.get(1));
        assertEquals("test2", moveList.get(2));
        assertTrue(moveList.contains("test0"));
        assertTrue(moveList.contains("test1"));
        assertTrue(moveList.contains("test2"));
    }

    private static void testSublist(int beginIndex, int expectedDynamic, int expectedSize) {
        ArrayMoveList<String> moveList = new ArrayMoveList<>(6);
        moveList.addQuietMove("1", null);
        moveList.addDynamicMove("2", null);
        moveList.addQuietMove("3", null);
        moveList.addDynamicMove("4", null);
        moveList.addQuietMove("5", null);
        moveList.addDynamicMove("6", null);
        assertEquals(3, moveList.numDynamicMoves());
        assertEquals(6, moveList.size());
        MoveList<String> subList = moveList.subList(beginIndex);
        assertEquals(expectedDynamic, subList.numDynamicMoves());
        assertEquals(expectedSize, subList.size());
    }

    @Test
    public void testSublist_FullList() {
        testSublist(0, 3, 6);
    }

    @Test
    public void testSublist_OneDynamic() {
        testSublist(2, 1, 4);
    }

    @Test
    public void testSublist_AllQuiet() {
        testSublist(3, 0, 3);
    }

    @Test
    public void testSublist_OneQuiet() {
        testSublist(5, 0, 1);
    }
}
