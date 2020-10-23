package eu.javaspecialists.concurrent.playground.phaser.cojoining;

public interface Cojoiner {
  void runWaiter();

  void runSignaller();
}
