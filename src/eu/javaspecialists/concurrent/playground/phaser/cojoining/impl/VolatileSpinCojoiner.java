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

public class VolatileSpinCojoiner implements Cojoiner {
    private volatile boolean ready = false;
    public void runWaiter() {
        while(!ready) Thread.onSpinWait();
    }

    public void runSignaller() {
        ready = true;
    }
}