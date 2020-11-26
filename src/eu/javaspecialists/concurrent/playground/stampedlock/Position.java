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

ReentrantLock:
Best values:
        moveBy()        24,961,088
        distanceFromOrigin()         20,602,797
Worst values:
        moveBy()        16,777,996
        distanceFromOrigin()         14,501,826

fair ReentrantLock:
Best values:
        moveBy()        160,823
        distanceFromOrigin()         163,312
Worst values:
        moveBy()        126,777
        distanceFromOrigin()         126,797


ReentrantReadWriteLock:
Best values:
        moveBy()        21,003,774
        distanceFromOrigin()         6,151,460
Worst values:
        moveBy()        15,443,790
        distanceFromOrigin()         2,762,214


StampedLock Java 8 idiom:
Best values:
        moveBy()        33,242,428
        distanceFromOrigin()         156,611,227
Worst values:
        moveBy()        16,789,723
        distanceFromOrigin()         81,814,448


 */

import java.util.concurrent.locks.*;

// TODO: Refactor to use ReentrantLock, then ReentrantReadWriteLock, then StampedLock
public class Position {
    private final StampedLock sl = new StampedLock();
    private double x, y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moveBy(double deltaX, double deltaY) {
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    public double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead();
        double currentX = x, currentY = y;
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}

