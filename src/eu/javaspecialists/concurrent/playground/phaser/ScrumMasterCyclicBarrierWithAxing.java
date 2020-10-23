package eu.javaspecialists.concurrent.playground.phaser;

import java.util.concurrent.*;

public class ScrumMasterCyclicBarrierWithAxing extends TeamGlorious {
  public static void main(String... args) throws InterruptedException {
    var barrier = new CyclicBarrier(5);
    var pool = Executors.newCachedThreadPool();

    for (int i = 0; i < 5; i++) {
      pool.submit((Runnable)() -> {
        try {
          while(true) {
            work();
            try {
              barrier.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
              message("interrupted - exiting");
              return;
            } catch (BrokenBarrierException e) {
              message("broken barrier");
            } catch (TimeoutException e) {
              message("timeout");
            }
            meet();
            axeMaybe();
          }
        } catch (CancellationException e) {
          message("cancelled");
        }
      });
    }

    Thread.sleep(20_000);
    pool.shutdownNow();
  }
}
