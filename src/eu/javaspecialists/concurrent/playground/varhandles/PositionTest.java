/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.varhandles;

import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class PositionTest {
    private static final ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();

    public static final int REPEATS = 10;
    private static final int WRITERS = Math.max(Runtime.getRuntime().availableProcessors() / 6, 1);
    private static final int READERS = Math.max(Runtime.getRuntime().availableProcessors() / 2 - WRITERS, 1);

    static {
        System.out.println("REPEATS = " + REPEATS);
        System.out.println("WRITERS = " + WRITERS);
        System.out.println("READERS = " + READERS);
    }

    private static final LongAccumulator bestMoveThread = new LongAccumulator(Long::max, 0);
    private static final LongAccumulator bestDistanceThread = new LongAccumulator(Long::max, 0);
    private static final LongAccumulator worstMoveThread = new LongAccumulator(Long::min, Long.MAX_VALUE);
    private static final LongAccumulator worstDistanceThread = new LongAccumulator(Long::min, Long.MAX_VALUE);

    public static void main(String... args) throws InterruptedException {
        System.out.println("c/e = cpu time / elapsed time");
        System.out.println("s/e = system cpu time / elapsed time");
        System.out.println("u/e = user cpu time / elapsed time");
        for (int i = 0; i < REPEATS; i++) {
            new PositionTest().test();
        }

        System.out.println();
        System.out.println("Best values:");
        System.out.printf(Locale.US, "\tmoveBy()        %,d%n", bestMoveThread.longValue());
        System.out.printf(Locale.US, "\tdistanceFromOrigin()         %,d%n", bestDistanceThread.longValue());
        System.out.println("Worst values:");
        System.out.printf(Locale.US, "\tmoveBy()        %,d%n", worstMoveThread.longValue());
        System.out.printf(Locale.US, "\tdistanceFromOrigin()         %,d%n", worstDistanceThread.longValue());
    }

    private void test() throws InterruptedException {
        Position position = new Position(0, 0);
        AtomicBoolean testing = new AtomicBoolean(true);

        ExecutorService pool = Executors.newCachedThreadPool();

        for (int writer = 1; writer <= WRITERS; writer++) {
            int finalWriter = writer;
            pool.submit(() -> {
                double[] moves = ThreadLocalRandom.current().doubles(1024, -100, +100).toArray();
                long time = System.currentTimeMillis();
                long userTime = tmbean.getCurrentThreadUserTime();
                long cpuTime = tmbean.getCurrentThreadCpuTime();
                long count = 0;
                int pos = 0;
                while (testing.get()) {
                    position.moveBy(moves[pos++ & 1023], moves[pos++ & 1023]);
                    count++;
                }
                bestMoveThread.accumulate(count);
                worstMoveThread.accumulate(count);
                time = System.currentTimeMillis() - time;
                userTime = tmbean.getCurrentThreadUserTime() - userTime;
                cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
                System.out.printf(Locale.US, "move" + finalWriter + "() called %,d times, c/e=%d%%, u/e=%d%%, s/e=%d%%%n",
                        count, (cpuTime / time) / 10_000, userTime / time / 10_000, (cpuTime - userTime) / time / 10_000);
            });
        }

        for (int reader = 1; reader <= READERS; reader++) {
            int finalReader = reader;
            pool.submit(() -> {
                long time = System.currentTimeMillis();
                long userTime = tmbean.getCurrentThreadUserTime();
                long cpuTime = tmbean.getCurrentThreadCpuTime();
                long count = 0;
                double totalDistance = 0;
                while (testing.get()) {
                    totalDistance += position.distanceFromOrigin();
                    count++;
                }
                bestDistanceThread.accumulate(count);
                worstDistanceThread.accumulate(count);
                time = System.currentTimeMillis() - time;
                userTime = tmbean.getCurrentThreadUserTime() - userTime;
                cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
                System.out.printf(Locale.US, "distanceFromOrigin" + finalReader + "() called %,d times, c/e=%d%%, u/e=%d%%, s/e=%d%%%n",
                        count, (cpuTime / time) / 10_000, userTime / time / 10_000, (cpuTime - userTime) / time / 10_000);
            });
        }

        Thread.sleep(3000);
        testing.set(false);

        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("Waiting for pool to shut down ...");
        }
        System.out.println();
    }
}
