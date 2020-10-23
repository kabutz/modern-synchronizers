package eu.javaspecialists.concurrent.playground.phaser;

import java.util.concurrent.locks.*;

public class InterruptExample {
  public static void main(String... args) throws InterruptedException {
    Lock lock = new ReentrantLock();
    lock.lock();
    Thread t1 = new Thread(() -> {
      lock.lock();
    });
    t1.start();
    for (int i = 0; i < 10; i++) {
      System.out.println("Here now");
      t1.join(1000);
      t1.interrupt();
    }
  }
}
