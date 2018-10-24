package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.concurrent.locks.*;

/*
Best values:
	moveBy()        44,321,520
	distanceFromOrigin()         33,075,768
Worst values:
	moveBy()        26,673,958
	distanceFromOrigin()         11,732,486

 */
public class Position {
  private double x, y;
  private final StampedLock sl = new StampedLock();

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

  public  double distanceFromOrigin() {
    long stamp = sl.tryOptimisticRead();
    var currentX = x;
    var currentY = y;
    if (!sl.validate(stamp)) {
      stamp = sl.readLock();
      try {
        currentX = x;
        currentY = y;
      } finally {
        sl.unlockRead(stamp);
      }
    }
    return Math.hypot(currentX, currentY);
  }
}

