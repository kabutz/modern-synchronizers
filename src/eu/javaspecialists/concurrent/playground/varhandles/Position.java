/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.varhandles;

import java.lang.invoke.*;

/*

Best values:
        moveBy()        9,299,325
        distanceFromOrigin()         120,999,015
Worst values:
        moveBy()        4,994,981
        distanceFromOrigin()         29,764,736

 */
public class Position {
    private volatile double[] xy;

    public Position(double x, double y) {
        this.xy = new double[]{x, y};
    }

    public void moveBy(double deltaX, double deltaY) {
        double[] current, next = new double[2];
        do {
            current = xy;
            next[0] = current[0] + deltaX;
            next[1] = current[1] + deltaY;
        } while (!XY.compareAndSet(this, current, next));
    }

    public double distanceFromOrigin() {
        double[] current = xy;
        double x = current[0], y = current[1];
        return Math.sqrt(x * x + y * y);
    }

    private static final VarHandle XY;

    static {
        try {
            XY = MethodHandles.lookup().findVarHandle(Position.class, "xy", double[].class);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }
}
