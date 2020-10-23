package eu.javaspecialists.concurrent.playground.phaser.cojoining.impl;

import eu.javaspecialists.concurrent.playground.phaser.cojoining.*;

public class VolatileSpinCojoiner implements Cojoiner {
  private volatile boolean ready = false;

  public void runWaiter() {
    while (!ready) Thread.onSpinWait();
  }

  public void runSignaller() {
    ready = true;
  }
}