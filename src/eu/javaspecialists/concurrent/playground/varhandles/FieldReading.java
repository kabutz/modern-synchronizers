package eu.javaspecialists.concurrent.playground.varhandles;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.concurrent.*;

public class FieldReading {
  private int val;

  public void reset() {
    val = ThreadLocalRandom.current().nextInt(1, 42);
  }

  public void increaseBy50Percent() {
    val *= 1.5;
  }

  public void increaseBy50PercentVarHandle() {
    int current = (int) VAL.get(this);
    current *= 1.5;
    VAL.set(this, current);
  }

  public void increaseBy50PercentReflection() {
    increaseBy50PercentReflection(VAL_FIELD);
  }

  public void increaseBy50PercentReflectionAccessible() {
    increaseBy50PercentReflection(VAL_FIELD_ACCESSIBLE);
  }

  private void increaseBy50PercentReflection(Field field) {
    try {
      int current = (int) field.getInt(this);
      current *= 1.5;
      field.set(this, current);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private final static VarHandle VAL;
  private final static Field VAL_FIELD;
  private final static Field VAL_FIELD_ACCESSIBLE;

  static {
    try {
      VAL = MethodHandles.lookup().findVarHandle(
          FieldReading.class, "val", int.class);
      VAL_FIELD = FieldReading.class.getDeclaredField("val");
      VAL_FIELD_ACCESSIBLE = FieldReading.class.getDeclaredField("val");
      VAL_FIELD_ACCESSIBLE.setAccessible(true);
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
  }
}
