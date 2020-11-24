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

/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.phaser.loom;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class PhaserLimits {
  public static void main(String... args) throws InterruptedException {
    Supplier<ExecutorService> poolSupplier = Executors::newCachedThreadPool;
//    Supplier<ExecutorService> poolSupplier = Executors::newVirtualThreadExecutor;

    ExecutorService pool = poolSupplier.get();
    int COUNT = 10_000_000;
    Phaser root = new Phaser();
    LongAdder done = new LongAdder();
    for (int i = 0; i < COUNT / 10000; i++) {
      Phaser child = new Phaser(root, 10000);
      System.out.println("child = " + child);
      for (int j = 0; j < 10000; j++) {
        pool.submit(() -> {
          child.arriveAndAwaitAdvance();
          done.increment();
        });
      }
    }
    System.out.println("root = " + root);
    while (done.intValue() != COUNT) {
      Thread.sleep(1000);
      System.out.println(root + " done=" + done.intValue());
    }
    System.out.println("Done");
    pool.shutdown();
  }
}
