package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.*;

public class IntList {
  private final Object monitor = new Object();
  private int[] arr = new int[10];
  private int size = 0;

  public int size() {
    synchronized (monitor) {
      return size;
    }
  }

  public int get(int index) {
    synchronized (monitor) {
      rangeCheck(index, size);
      return arr[index];
    }
  }

  public boolean add(int e) {
    synchronized (monitor) {
      if (size + 1 > arr.length)
        arr = Arrays.copyOf(arr, size + 10);

      arr[size++] = e;
      return true;
    }
  }

  public void trimToSize() {
    synchronized (monitor) {
      if (size < arr.length)
        arr = Arrays.copyOf(arr, size);
    }
  }

  public int remove(int index) {
    synchronized (monitor) {
      rangeCheck(index, size);

      int oldValue = arr[index];

      int numMoved = size - index - 1;
      if (numMoved > 0)
        System.arraycopy(arr, index + 1,
            arr, index, numMoved);
      arr[--size] = 0;

      return oldValue;
    }
  }

  private static void rangeCheck(int index, int size) {
    if (index >= size)
      throw new IndexOutOfBoundsException(
          "Index: " + index + ", Size: " + size);
  }
}