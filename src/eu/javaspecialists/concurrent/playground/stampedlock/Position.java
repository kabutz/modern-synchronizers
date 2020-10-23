package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.concurrent.locks.*;

public class Position {
  public static final int RETRY_COUNT = 5;
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
    double currentX, currentY;
    out:
    {
      for (int i = 0; i < RETRY_COUNT; i++) {
        long stamp = sl.tryOptimisticRead();
        currentX = x;
        currentY = y;
        if (sl.validate(stamp)) {
          break out;
        }
      }
      long stamp = sl.readLock();
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

