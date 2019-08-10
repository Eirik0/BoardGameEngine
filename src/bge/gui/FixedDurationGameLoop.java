package bge.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FixedDurationGameLoop {
    private static final double TARGET_FPS = 60;

    public static final long NANOS_PER_SECOND = 1000000000; // in nanoseconds
    public static final long NANOS_PER_MILLISECOND = 1000000;

    private static final FixedDurationGameLoop instance = new FixedDurationGameLoop();

    private Map<String, Runnable> runnableMap = new HashMap<>();

    private FixedDurationGameLoop() {
    }

    public static void startLoop() {
        new Thread(() -> instance.runLoop(), "Game_Loop_Thread").start();
    }

    public void runLoop() {
        long loopStart;
        for (;;) {
            loopStart = System.nanoTime();
            Collection<Runnable> runnables;
            runnables = runnableMap.values();
            for (Runnable runnable : runnables) {
                runnable.run();
            }
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

    private synchronized void addRunnableToMap(String name, Runnable runnable) {
        HashMap<String, Runnable> newRunnableMap = new HashMap<>(runnableMap);
        newRunnableMap.put(name, runnable);
        runnableMap = newRunnableMap;
    }

    private synchronized void removeRunnableFromMap(String name) {
        HashMap<String, Runnable> newRunnableMap = new HashMap<>(runnableMap);
        newRunnableMap.remove(name);
        runnableMap = newRunnableMap;
    }

    public static void addRunnable(String name, Runnable runnable) {
        instance.addRunnableToMap(name, runnable);
    }

    public static void removeRunnable(String name) {
        instance.removeRunnableFromMap(name);
    }
}
