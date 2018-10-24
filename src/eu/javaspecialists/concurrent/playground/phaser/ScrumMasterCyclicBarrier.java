package eu.javaspecialists.concurrent.playground.phaser;

import java.util.concurrent.*;

public class ScrumMasterCyclicBarrier extends TeamGlorious {
  public static void main(String... args) throws InterruptedException {
    var whistle = new CyclicBarrier(5);
    var team = Executors.newCachedThreadPool();
    for (int i = 0; i < 5; i++) {
      Runnable worker = () -> {
        try {
          while (true) {
            work();
            try {
              whistle.await();
            } catch (InterruptedException e) {
              throw new CancellationException("interrupted");
            } catch (BrokenBarrierException e) {
              message("broken barrier");
            }
            meet();
          }
        } catch (CancellationException e) {
          message("done (" + e.getMessage() + ")");
        }
      };
      team.submit(worker);
    }
    Thread.sleep(2_000);
    team.shutdownNow();
  }
}
