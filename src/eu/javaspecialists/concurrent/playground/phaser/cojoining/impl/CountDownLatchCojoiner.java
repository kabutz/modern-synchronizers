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

import java.util.concurrent.*;

public class CountDownLatchCojoiner implements Cojoiner {
    private final CountDownLatch latch = new CountDownLatch(Constants.PARTIES + 1);

    public void runWaiter() {
        latch.countDown();
        boolean interrupted = Thread.interrupted();
        while (true) {
            try {
                latch.await();
                if (interrupted) Thread.currentThread().interrupt();
                return;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
    }

    public void runSignaller() {
        latch.countDown();
    }
}
