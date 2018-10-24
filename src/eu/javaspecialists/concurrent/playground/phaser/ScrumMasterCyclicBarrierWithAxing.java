package eu.javaspecialists.concurrent.playground.phaser;

import java.util.concurrent.*;

public class ScrumMasterCyclicBarrierWithAxing extends TeamGlorious {
  public static void main(String... args) throws InterruptedException {
    var whistle = new CyclicBarrier(5);
    var team = Executors.newCachedThreadPool();
    for (int i = 0; i < 5; i++) {
      Runnable worker = () -> {
        try {
          while (true) {
            work();
            try {
              whistle.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
              throw new CancellationException("interrupted");
            } catch (BrokenBarrierException e) {
              message("broken barrier");
            } catch (TimeoutException e) {
              message("timed out");
            }
            meet();
            axeMaybe();
          }
        } catch (CancellationException e) {
          message("done (" + e.getMessage() + ")");
        }
      };
      team.submit(worker);
    }
    Thread.sleep(20_000);
    team.shutdownNow();
  }
}
