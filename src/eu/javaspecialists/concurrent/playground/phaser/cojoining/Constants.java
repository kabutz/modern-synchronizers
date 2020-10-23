package eu.javaspecialists.concurrent.playground.phaser.cojoining;

public class Constants {
  public static final int PARTIES =
      Runtime.getRuntime().availableProcessors() / 2 - 2;
}
