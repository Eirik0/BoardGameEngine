package gui;

public class FixedDurationGameLoop {
	private static final double TARGET_FPS = 60;

	public static final long NANOS_PER_SECOND = 1000000000; // in nanoseconds
	public static final long NANOS_PER_MILLISECOND = 1000000;

	private final GamePanel component;

	private final GameImage gameImage;

	public FixedDurationGameLoop(GamePanel component, GameImage gameImage) {
		this.component = component;
		this.gameImage = gameImage;
	}

	public void runLoop() {
		long loopStart;
		for (;;) {
			loopStart = System.nanoTime();
			GameGuiManager.getGameState().drawOn(gameImage.getGraphics());
			component.repaintAndWait();
			double timeToSleep = NANOS_PER_SECOND / TARGET_FPS - (System.nanoTime() - loopStart);
			if (timeToSleep > 0) {
				try {
					Thread.sleep(Math.round(timeToSleep / NANOS_PER_MILLISECOND));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
