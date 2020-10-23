package eu.javaspecialists.concurrent.playground.varhandles;

import java.lang.invoke.*;
import java.util.concurrent.atomic.*;

/*

Best values:
	moveBy()        87,051,311
	distanceFromOrigin()         55,428,860
Worst values:
	moveBy()        71,838,260
	distanceFromOrigin()         49,331,756

 */
public class Position {
  private final AtomicReference<double[]> xy;

  public Position(double x, double y) {
    xy = new AtomicReference<>(new double[]{x, y});
  }

//    public void moveBy(double deltaX, double deltaY) {
//        double[] current, latest = xy, next = new double[2];
//        do {
//            current = latest;
//            next[0] = current[0] + deltaX;
//            next[1] = current[1] + deltaY;
//        } while((latest = (double[]) XY.compareAndExchange(this, current, next)) != current);
//    }

  public void moveBy(double deltaX, double deltaY) {
    double[] current, next = new double[2];
    do {
      current = xy.get();
      next[0] = current[0] + deltaX;
      next[1] = current[1] + deltaY;
    } while (!xy.compareAndSet(current, next));
  }

  public double distanceFromOrigin() {
    double[] current = xy.get();
    return Math.hypot(current[0], current[1]);
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
