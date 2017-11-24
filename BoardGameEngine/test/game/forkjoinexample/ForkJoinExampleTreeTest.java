package game.forkjoinexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ForkJoinExampleTreeTest {
	@Test
	public void testCreateTreeOfDepthZero() {
		assertNull(ForkJoinExampleTree.createTree(0, 2, 0));
	}

	@Test
	public void testCreateTreeOfDepthOne() {
		ForkJoinExampleNode createTreeOfDepth = ForkJoinExampleTree.createTree(1, 2, 0);
		assertEquals(0, createTreeOfDepth.getChildren().length);
	}

	@Test
	public void testCreateTreeOfDepthTwo() {
		ForkJoinExampleNode createTreeOfDepth = ForkJoinExampleTree.createTree(2, 2, 0);
		ForkJoinExampleNode[] children = createTreeOfDepth.getChildren();
		assertEquals(2, children.length);
		assertEquals(0, children[0].getChildren().length);
		assertEquals(0, children[1].getChildren().length);
	}
}
