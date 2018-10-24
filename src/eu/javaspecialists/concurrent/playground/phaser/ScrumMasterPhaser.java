package eu.javaspecialists.concurrent.playground.phaser;

import java.util.concurrent.*;

public class ScrumMasterPhaser extends TeamGlorious {
  public static void main(String... args) throws InterruptedException {
    var whistle = new Phaser(5);
    var team = Executors.newCachedThreadPool();
    for (int i = 0; i < 5; i++) {
      Runnable worker = () -> {
        try {
          while (true) {
            work();
            whistle.arriveAndAwaitAdvance();
            meet();
          }
        } catch (CancellationException e) {
          message("done (" + e.getMessage() + ")");
          whistle.arriveAndDeregister();
        }
      };
      team.submit(worker);
    }
    Thread.sleep(2_000);
    team.shutdownNow();
  }
}
