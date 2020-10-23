package eu.javaspecialists.concurrent.playground.phaser;

import java.util.concurrent.*;

public class ScrumMasterPhaserWithAxing extends TeamGlorious {
  public static void main(String... args) throws InterruptedException {
    var phaser = new Phaser(5);
    var pool = Executors.newCachedThreadPool();

    for (int i = 0; i < 5; i++) {
      pool.submit((Runnable)() -> {
        try {
          while(true) {
            work();
            phaser.arriveAndAwaitAdvance();
            meet();
            axeMaybe();
          }
        } catch (CancellationException e) {
          message("cancelled");
          phaser.arriveAndDeregister();
        }
      });
    }

    Thread.sleep(20_000);
    pool.shutdownNow();
  }
}
