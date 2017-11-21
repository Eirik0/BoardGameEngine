package gui;

public class FixedDurationGameLoop {
	private static final double TARGET_FPS = 60;

	public static final long NANOS_PER_SECOND = 1000000000; // in nanoseconds
	public static final long NANOS_PER_MILLISECOND = 1000000;

	private final Runnable loopRunnable;

	private boolean keepRunning = true;

	public FixedDurationGameLoop(Runnable loopRunnable) {
		this.loopRunnable = loopRunnable;
	}

	public void runLoop() {
		long loopStart;
		while (keepRunning) {
			loopStart = System.nanoTime();
			loopRunnable.run();
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

	public void stop() {
		keepRunning = false;
	}
}
