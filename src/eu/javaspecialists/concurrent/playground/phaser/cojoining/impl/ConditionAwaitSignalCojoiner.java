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

import java.util.concurrent.locks.*;

public class ConditionAwaitSignalCojoiner implements Cojoiner {
    private final Lock lock = new ReentrantLock();
    private final Condition readyCondition = lock.newCondition();
    private boolean ready = false;

    public void runWaiter() {
        lock.lock();
        try {
            while (!ready) readyCondition.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }
    }

    public void runSignaller() {
        lock.lock();
        try {
            ready = true;
            readyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}