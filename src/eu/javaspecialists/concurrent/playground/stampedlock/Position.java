/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.stampedlock;


// TODO: Refactor to use ReentrantLock, then ReentrantReadWriteLock, then StampedLock
public class Position {
    private double x, y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void moveBy(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public synchronized double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y);
    }
}

