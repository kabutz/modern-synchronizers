package eu.javaspecialists.concurrent.playground.phaser.cojoining.impl;

import eu.javaspecialists.concurrent.playground.phaser.cojoining.*;

public class WaitNotifyCojoiner implements Cojoiner {
  private boolean ready = false;

  public synchronized void runWaiter() {
    boolean interrupted = Thread.interrupted();
    while (!ready) {
      try {
        wait();
      } catch (InterruptedException e) {
        interrupted = true;
      }
    }
    if (interrupted) Thread.currentThread().interrupt();
  }


  public synchronized void runSignaller() {
    ready = true;
    notifyAll();
  }
}