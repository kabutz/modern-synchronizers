/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.phaser.cojoining.impl;

import eu.javaspecialists.concurrent.playground.phaser.cojoining.*;

public class WaitNotifyCojoiner implements Cojoiner {
    private boolean ready = false;
    public synchronized void runWaiter() {
        boolean interrupted = Thread.interrupted();
        while(!ready) {
            try {
                wait();
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) Thread.currentThread().interrupt(); // self-interrupt
    }

    public synchronized void runSignaller() {
        ready = true;
        notifyAll();
    }
}