package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.concurrent.locks.*;

public class Position {
  private final StampedLock sl = new StampedLock();
  private double x, y;

  public Position(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void moveBy(double deltaX, double deltaY) {
    var stamp = sl.writeLock();
    try {
      x += deltaX;
      y += deltaY;
    } finally {
      sl.unlockWrite(stamp);
    }
  }

  static long numberOfDistanceCalls;
  static long numberOfOptimisticFailures;

  public double distanceFromOrigin() {
    numberOfDistanceCalls++;
    double currentX, currentY;
    out:
    {
      for (int i = 0; i < 5; i++) {
        var stamp = sl.tryOptimisticRead();
        currentX = x;
        currentY = y;
        if (sl.validate(stamp)) break out;
      }
      numberOfOptimisticFailures++;
      var stamp = sl.readLock();
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

