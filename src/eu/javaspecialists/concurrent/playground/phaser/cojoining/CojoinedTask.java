package eu.javaspecialists.concurrent.playground.phaser.cojoining;

public class CojoinedTask implements Runnable {
  private volatile long startTime;
  private final Runnable joiner;
  private final Runnable task;

  public CojoinedTask(Runnable joiner, Runnable task) {
    this.task = task;
    this.joiner = joiner;
  }

  public void run() {
    joiner.run();
    startTime = System.nanoTime();
    task.run();
  }

  public long getStartTime() {
    return startTime;
  }
}
