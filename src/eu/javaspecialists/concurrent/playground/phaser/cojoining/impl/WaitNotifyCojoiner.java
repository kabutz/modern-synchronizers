/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * © 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.phaser.cojoining.impl;

import eu.javaspecialists.concurrent.playground.phaser.cojoining.*;

public class WaitNotifyCojoiner implements Cojoiner {
  private final Object monitor = new Object();
  private boolean ready = false;

  public void runWaiter() {
    synchronized (monitor) {
      boolean interrupted = Thread.interrupted();
      while (!ready) {
        try {
          monitor.wait();
        } catch (InterruptedException e) {
          interrupted = true;
        }
      }
      if (interrupted) Thread.currentThread().interrupt(); // self-interrupt
    }
  }

  public void runSignaller() {
    synchronized (monitor) {
      ready = true;
      monitor.notifyAll();
    }
  }
}