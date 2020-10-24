/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * © 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.concurrent.locks.*;

/*
With synchronized Position:
Best values:
	moveBy()        29,378,651
	distanceFromOrigin()         37,476,220
Worst values:
	moveBy()        10,891,372
	distanceFromOrigin()         29,554,318

With pessimistic read lock on StampedLock:
Best values:
	moveBy()        21,352,588
	distanceFromOrigin()         17,865,360
Worst values:
	moveBy()        18,183,125
	distanceFromOrigin()         12,796,374
 */
public class Position {
  private final StampedLock sl = new StampedLock();
  private double x, y;

  public Position(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void moveBy(double deltaX, double deltaY) {
    // pessimistic exclusive lock
    long stamp = sl.writeLock();
    try {
      x += deltaX;
      y += deltaY;
    } finally {
      sl.unlockWrite(stamp);
    }
  }

  public double distanceFromOrigin() {
    // pessimistic non-exclusive lock
    long stamp = sl.readLock();
    try {
      return Math.hypot(x, y);
    } finally {
      sl.unlockRead(stamp);
    }
  }
}

