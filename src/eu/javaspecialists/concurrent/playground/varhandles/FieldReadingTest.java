package eu.javaspecialists.concurrent.playground.varhandles;

public class FieldReadingTest {
  public static void main(String... args) throws IllegalAccessException {
    for (int i = 0; i < 30; i++) {
      test();
    }
  }

  private static void test() {
    FieldReading fr = new FieldReading();
    test(fr, "normal", fr::increaseBy50Percent);
    test(fr, "VarHandle", fr::increaseBy50PercentVarHandle);
    test(fr, "Reflection", fr::increaseBy50PercentReflection);
    test(fr, "Reflection Accessible", fr::increaseBy50PercentReflectionAccessible);
  }

  private static void test(FieldReading fr, String description, Runnable increase) {
    long time = System.nanoTime();
    try {
      for (int i = 0; i < 1_000; i++) {
        fr.reset();
        for (int j = 0; j < 100_000; j++) {
          increase.run();
        }
      }
    } finally {
      time = System.nanoTime() - time;
      System.out.printf("%s field get and set time = %dms%n", description, (time / 1_000_000));
    }
  }
}
