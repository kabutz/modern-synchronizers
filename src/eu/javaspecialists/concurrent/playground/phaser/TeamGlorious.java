package eu.javaspecialists.concurrent.playground.phaser;

import java.util.*;
import java.util.concurrent.*;

public class TeamGlorious {
  private final static Queue<String> names = new ConcurrentLinkedQueue<>(
      Arrays.asList("Ioannis", "Marc", "Rabea", "Dmitry", "Steve")
  );
  protected final static ThreadLocal<String> name = ThreadLocal.withInitial(names::poll);

  public static void work() {
    message("working");
    busyFor(ThreadLocalRandom.current().nextInt(500, 3000), TimeUnit.MILLISECONDS);
    message("ready for SCRUMMMMMM");
  }

  public static void meet() {
    message("heads down for scrum");
    busyFor(1, TimeUnit.SECONDS);
  }

  public static void axeMaybe() {
    if (ThreadLocalRandom.current().nextDouble() > 0.7) {
      message("getting axed");
      throw new CancellationException("axed");
    }
  }

  public static String getName() {
    return name.get();
  }

  public static void message(String txt) {
    System.out.println(getName() + " " + txt);
  }

  private static void busyFor(int time, TimeUnit unit) {
    try {
      unit.sleep(time);
    } catch (InterruptedException e) {
      throw new CancellationException("interrupted");
    }
  }
}