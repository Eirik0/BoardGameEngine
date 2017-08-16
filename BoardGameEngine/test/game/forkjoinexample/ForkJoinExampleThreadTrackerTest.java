package game.forkjoinexample;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class ForkJoinExampleThreadTrackerTest {
	@Test
	public void testInit() {
		ForkJoinExampleThreadTracker.init(new ForkJoinExampleTree(3, 2));
		List<List<ForkJoinExampleNode>> nodesByBredth = ForkJoinExampleThreadTracker.getNodesByBredth();
		assertEquals(3, nodesByBredth.size());
		assertEquals(1, nodesByBredth.get(0).size());
		assertEquals(2, nodesByBredth.get(1).size());
		assertEquals(4, nodesByBredth.get(2).size());
	}
}
