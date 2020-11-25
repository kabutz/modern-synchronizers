/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.phaser.loom;

import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class ThreadSleepLengthVariance {
    private static final int NUMBER_OF_THREADS = 10_000_000;

    public static void main(String... args) throws InterruptedException {
        Supplier<ExecutorService> poolSupplier = Executors::newCachedThreadPool;
//    Supplier<ExecutorService> poolSupplier = Executors::newVirtualThreadExecutor;

        CountDownLatch startingGun = new CountDownLatch(NUMBER_OF_THREADS + 1);
        Collection<Long> times = new ConcurrentLinkedQueue<>();
        ExecutorService pool = poolSupplier.get();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            pool.submit(() -> {
                try {
                    startingGun.countDown();
                    startingGun.await();
                    long time = System.nanoTime();
                    Thread.sleep(1000);
                    time = (System.nanoTime() - time) / 1_000_000;
                    times.add(time);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
            if (i - startingGun.getCount() > 10_000) {
                System.out.print("Waiting for virtual thread creation to catch up");
                System.out.println("i = " + i);
                while (i - startingGun.getCount() > 1000) {
                    System.out.println("startingGun = " + startingGun);
                    Thread.sleep(1000);
                    System.out.print('.');
                }
                System.out.println();
            }
        }
        startingGun.countDown();
        System.out.println("Waiting for us all to wake up ...");

        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("Waiting for pool ...");
        }
        if (times.size() != NUMBER_OF_THREADS) throw new AssertionError();
        System.out.println(times.stream().mapToLong(Long::longValue).summaryStatistics());
        ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
        System.out.println("tmb.getPeakThreadCount() = " + tmb.getPeakThreadCount());
    }
}