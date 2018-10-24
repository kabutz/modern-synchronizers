package eu.javaspecialists.concurrent.playground.stampedlock;

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
    return Math.hypot(x, y);
  }
}

