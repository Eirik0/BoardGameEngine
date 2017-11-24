package game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArrayMoveListTest {
	@Test
	public void testAddOne() {
		MoveList<String> moveList = new ArrayMoveList<>(1);
		moveList.add("test");
		assertEquals(1, moveList.size());
		assertEquals("test", moveList.get(0));
		assertTrue(moveList.contains("test"));
	}

	@Test
	public void testAddMany() {
		MoveList<String> moveList = new ArrayMoveList<>(2);
		moveList.addAll(new String[] { "test1", "test2" });
		assertEquals(2, moveList.size());
		assertEquals("test1", moveList.get(0));
		assertEquals("test2", moveList.get(1));
		assertTrue(moveList.contains("test1"));
		assertTrue(moveList.contains("test2"));
	}

	@Test
	public void testAddOneThenMany() {
		MoveList<String> moveList = new ArrayMoveList<>(3);
		moveList.add("test0");
		moveList.addAll(new String[] { "test1", "test2" });
		assertEquals(3, moveList.size());
		assertEquals("test0", moveList.get(0));
		assertEquals("test1", moveList.get(1));
		assertEquals("test2", moveList.get(2));
		assertTrue(moveList.contains("test0"));
		assertTrue(moveList.contains("test1"));
		assertTrue(moveList.contains("test2"));
	}
}
