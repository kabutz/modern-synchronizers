/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.stampedlock;

import java.lang.invoke.*;
import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class IntListTest {
  private static final ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();

  public static final int REPEATS = 10;
  private static final int ADDREMOVERS = 1;
  private static final int GETTERS = Math.max(Runtime.getRuntime().availableProcessors() / 4, 1);
  private static final int SIZERS = Math.max(Runtime.getRuntime().availableProcessors() / 6, 1);

  static {
    System.out.println("REPEATS = " + REPEATS);
    System.out.println("ADDREMOVERS = " + ADDREMOVERS);
    System.out.println("GETTERS = " + GETTERS);
    System.out.println("SIZERS = " + SIZERS);
  }

  private final static VarHandle SIZETEST;
  private final static VarHandle ELEMENTTEST;
  private final static VarHandle ADDTEST;
  private final static VarHandle REMOVETEST;

  static {
    try {
      SIZETEST = MethodHandles.lookup().findVarHandle(IntListTest.class, "sizeTest", int.class);
      ELEMENTTEST = MethodHandles.lookup().findVarHandle(IntListTest.class, "elementTest", int.class);
      ADDTEST = MethodHandles.lookup().findVarHandle(IntListTest.class, "addTest", int.class);
      REMOVETEST = MethodHandles.lookup().findVarHandle(IntListTest.class, "removeTest", int.class);
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
  }

  private int sizeTest = 0;
  private int elementTest = 0;
  private int addTest = 0;
  private int removeTest = 0;

  private static final LongAccumulator bestSizeThread = new LongAccumulator(Long::max, 0);
  private static final LongAccumulator bestGetThread = new LongAccumulator(Long::max, 0);
  private static final LongAccumulator bestAddRemoveThread = new LongAccumulator(Long::max, 0);
  private static final LongAccumulator worstSizeThread = new LongAccumulator(Long::min, Long.MAX_VALUE);
  private static final LongAccumulator worstGetThread = new LongAccumulator(Long::min, Long.MAX_VALUE);
  private static final LongAccumulator worstAddRemoveThread = new LongAccumulator(Long::min, Long.MAX_VALUE);

  public static void main(String... args) throws InterruptedException {
    System.out.println("c/e = cpu time / elapsed time");
    System.out.println("s/e = system cpu time / elapsed time");
    System.out.println("u/e = user cpu time / elapsed time");
    for (int i = 0; i < REPEATS; i++) {
      new IntListTest().test();
    }

    System.out.println();
    System.out.println("Best values:");
    System.out.printf(Locale.US, "\tsize()        %,d%n", bestSizeThread.longValue());
    System.out.printf(Locale.US, "\tget()         %,d%n", bestGetThread.longValue());
    System.out.printf(Locale.US, "\tadd/remove()  %,d%n", bestAddRemoveThread.longValue());
    System.out.println("Worst values:");
    System.out.printf(Locale.US, "\tsize()        %,d%n", worstSizeThread.longValue());
    System.out.printf(Locale.US, "\tget()         %,d%n", worstGetThread.longValue());
    System.out.printf(Locale.US, "\tadd/remove()  %,d%n", worstAddRemoveThread.longValue());
  }

  private void test() throws InterruptedException {
    IntList list = new IntList();
    AtomicBoolean testing = new AtomicBoolean(true);

    ExecutorService pool = Executors.newCachedThreadPool();

    for (int threads = 0; threads < ADDREMOVERS; threads++) {
      pool.submit(() -> {
        long time = System.currentTimeMillis();
        long userTime = tmbean.getCurrentThreadUserTime();
        long cpuTime = tmbean.getCurrentThreadCpuTime();
        long count = 0;
        for (int i = 1; i < 50; i++) {
          boolean add = list.add(i);
          ADDTEST.setRelease(IntListTest.this, add ? 1 : 0);
          count++;
        }

        while (testing.get()) {
          for (int i = 1; i < 500; i++) {
            boolean add = list.add(i);
            ADDTEST.setRelease(IntListTest.this, add ? 1 : 0);
            count++;
          }
          for (int i = 1; i < 500; i++) {
            int remove = list.remove(7);
            REMOVETEST.setRelease(IntListTest.this, remove);
            count++;
          }
        }
        bestAddRemoveThread.accumulate(count);
        worstAddRemoveThread.accumulate(count);
        time = System.currentTimeMillis() - time;
        userTime = tmbean.getCurrentThreadUserTime() - userTime;
        cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
        System.out.printf(Locale.US, "add()/remove() called %,d times, c/e=%d%%, u/e=%d%%, s/e=%d%%%n",
                count, (cpuTime / time) / 10_000, userTime / time / 10_000, (cpuTime - userTime) / time / 10_000);
      });
    }

    for (int threads = 0; threads < GETTERS; threads++) {
      int finalThreads = threads;
      pool.submit(() -> {
        long time = System.currentTimeMillis();
        long userTime = tmbean.getCurrentThreadUserTime();
        long cpuTime = tmbean.getCurrentThreadCpuTime();
        long count = 0;
        while (list.size() < 50) ; // wait
        while (testing.get()) {
          int element = list.get(10);
          ELEMENTTEST.setRelease(IntListTest.this, element);
          count++;
        }
        bestGetThread.accumulate(count);
        worstGetThread.accumulate(count);
        time = System.currentTimeMillis() - time;
        userTime = tmbean.getCurrentThreadUserTime() - userTime;
        cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
        System.out.printf(Locale.US, "%d get() called %,d times, c/e=%d%%, u/e=%d%%, s/e=%d%%%n",
                finalThreads + 1,
                count, (cpuTime / time) / 10_000, userTime / time / 10_000, (cpuTime - userTime) / time / 10_000);
      });
    }

    for (int threads = 0; threads < SIZERS; threads++) {
      int finalThreads = threads;
      pool.submit(() -> {
        long time = System.currentTimeMillis();
        long userTime = tmbean.getCurrentThreadUserTime();
        long cpuTime = tmbean.getCurrentThreadCpuTime();
        long count = 0;
        while (testing.get()) {
          int size = list.size();
          SIZETEST.setRelease(IntListTest.this, size);
          count++;
        }
        bestSizeThread.accumulate(count);
        worstSizeThread.accumulate(count);
        time = System.currentTimeMillis() - time;
        userTime = tmbean.getCurrentThreadUserTime() - userTime;
        cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
        System.out.printf(Locale.US, "%d size() called %,d times, c/e=%d%%, u/e=%d%%, s/e=%d%%%n",
                finalThreads + 1,
                count, (cpuTime / time) / 10_000, userTime / time / 10_000, (cpuTime - userTime) / time / 10_000);
      });
    }
    pool.submit(() -> {
      while (testing.get()) {
        list.trimToSize();
        try {
          Thread.sleep(5);
        } catch (InterruptedException e) {
          return;
        }
      }
    });
    Thread.sleep(3000);
    testing.set(false);
    pool.shutdown();
    while(!pool.awaitTermination(1, TimeUnit.SECONDS)) {
      System.out.println("Waiting for pool to shut down ...");
    }
    System.out.println();
  }
}

