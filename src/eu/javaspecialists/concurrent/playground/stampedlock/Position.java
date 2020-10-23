package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.concurrent.locks.*;

/*
StampedLock
Best values:
	moveBy()        65,058,211
	distanceFromOrigin()         11,727,123
Worst values:
	moveBy()        35,240,258
	distanceFromOrigin()         6,243,450

Best values:
	moveBy()        60,895,170
	distanceFromOrigin()         12,106,608
Worst values:
	moveBy()        42,777,855
	distanceFromOrigin()         6,510,150

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

  public double distanceFromOrigin() {
    double currentX, currentY;
    out:
    {
      for (int i = 0; i < 5; i++) {
        long stamp = sl.tryOptimisticRead(); // assume not 0
        currentX = x;
        currentY = y;
        if (sl.validate(stamp)) break out;
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

