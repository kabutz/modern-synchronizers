/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.phaser.cojoining;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public class CojoinedTasksTester {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final static LongAdder totalTests = new LongAdder();

    static {
        if (Constants.PARTIES < 2)
            throw new AssertionError("Need at least 4 cores for this test");
    }

    public static void shutdown() {
        pool.shutdown();
    }

    public static void test(Supplier<Cojoiner> supplier) {
        var total = new LongAdder();
        var max = new LongAccumulator(Long::max, 0);
        for (int i = 0; i < 20_000; i++) {
            var cojoiner = supplier.get();
            test(cojoiner::runWaiter, cojoiner::runSignaller, total, max);
        }
        System.out.printf(Locale.US, "%s: max = %,d, total=%,d%n",
                supplier.get().getClass().getSimpleName(),
                max.longValue(), total.longValue());
    }

    private static void test(Runnable waiter, Runnable signaller,
                             LongAdder total, LongAccumulator max) {
        CojoinedTask[] tasks = IntStream.range(0, Constants.PARTIES)
                .mapToObj(i -> new CojoinedTask(waiter, totalTests::increment))
                .toArray(CojoinedTask[]::new);
        List<? extends Future<?>> futures =
                Stream.of(tasks)
                        .map(pool::submit)
                        .collect(Collectors.toList());
        signaller.run();
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } catch (ExecutionException e) {
                throw new IllegalStateException(e.getCause());
            }
        });
        long min = Stream.of(tasks).mapToLong(CojoinedTask::getStartTime)
                .min().getAsLong();
        for (CojoinedTask task : tasks) {
            long diff = task.getStartTime() - min;
            max.accumulate(diff);
            total.add(diff);
        }
    }
}
