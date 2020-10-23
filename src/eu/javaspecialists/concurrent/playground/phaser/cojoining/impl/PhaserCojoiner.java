package eu.javaspecialists.concurrent.playground.phaser.cojoining.impl;

import eu.javaspecialists.concurrent.playground.phaser.cojoining.*;

import java.util.concurrent.*;

public class PhaserCojoiner implements Cojoiner {
  private final Phaser phaser = new Phaser(
      Constants.PARTIES + 1
  );

  public void runWaiter() {
    phaser.arriveAndAwaitAdvance();
  }

  public void runSignaller() {
    phaser.arriveAndDeregister();
  }
}