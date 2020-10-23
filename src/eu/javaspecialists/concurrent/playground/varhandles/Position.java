package eu.javaspecialists.concurrent.playground.varhandles;

import java.lang.invoke.*;

public class Position {
  private volatile double[] xy;

  public Position(double x, double y) {
    this.xy = new double[]{x, y};
  }

  public void moveBy(double deltaX, double deltaY) {
    double[] current, next = new double[2];
    do {
      current = xy;
      next[0] = xy[0] + deltaX;
      next[1] = xy[1] + deltaY;
    } while(!XY.compareAndSet(this, current, next));
  }

  public double distanceFromOrigin() {
    double[] currentXY = xy;
    return Math.hypot(currentXY[0], currentXY[1]);
  }
  private final static VarHandle XY;
  static {
    try {
      XY = MethodHandles.lookup().findVarHandle(
          Position.class, "xy", double[].class
      );
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
  }
}
