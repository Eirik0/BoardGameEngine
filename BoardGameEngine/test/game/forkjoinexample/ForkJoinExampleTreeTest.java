package game.forkjoinexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

public class ForkJoinExampleTreeTest {
	@Test
	public void testCreateTreeOfDepthZero() {
		assertNull(ForkJoinExampleTree.createTree(0, 2, 0));
	}

	@Test
	public void testCreateTreeOfDepthOne() {
		ForkJoinExampleNode createTreeOfDepth = ForkJoinExampleTree.createTree(1, 2, 0);
		assertEquals(0, createTreeOfDepth.getChildren().size());
	}

	@Test
	public void testCreateTreeOfDepthTwo() {
		ForkJoinExampleNode createTreeOfDepth = ForkJoinExampleTree.createTree(2, 2, 0);
		List<ForkJoinExampleNode> children = createTreeOfDepth.getChildren();
		assertEquals(2, children.size());
		assertEquals(0, children.get(0).getChildren().size());
		assertEquals(0, children.get(1).getChildren().size());
	}
}
