/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.stampedlock;


/*
synchronized:
Best values:
        moveBy()        6,783,490
        distanceFromOrigin()         4,925,671
Worst values:
        moveBy()        4,115,273
        distanceFromOrigin()         3,740,724

 */

import java.util.concurrent.locks.*;

// TODO: Refactor to use ReentrantLock, then ReentrantReadWriteLock, then StampedLock
public class Position {
    private final Lock lock = new ReentrantLock();
    private double x, y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moveBy(double deltaX, double deltaY) {
        lock.lock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlock();
        }
    }

    public double distanceFromOrigin() {
        lock.lock();
        try {
            return Math.sqrt(x * x + y * y);
        } finally {
            lock.unlock();
        }
    }
}

