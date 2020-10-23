package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.*;

/*
 */
public class IntList {
  private int[] arr = new int[10];
  private int size = 0;

  public synchronized int size() {
    return size;
  }

  public synchronized int get(int index) {
    rangeCheck(index, size);
    return arr[index];
  }

  public synchronized boolean add(int e) {
    if (size + 1 > arr.length)
      arr = Arrays.copyOf(arr, size + 10);
    arr[size++] = e;
    return true;
  }

  public synchronized void trimToSize() {
    if (size < arr.length)
      arr = Arrays.copyOf(arr, size);
  }

  public synchronized int remove(int index) {
    rangeCheck(index, size);

    int oldValue = arr[index];

    int numMoved = size - index - 1;
    if (numMoved > 0)
      System.arraycopy(arr, index + 1,
          arr, index, numMoved);
    arr[--size] = 0;

    return oldValue;
  }

  private static void rangeCheck(int index, int size) {
    if (index >= size)
      throw new IndexOutOfBoundsException(
          "Index: " + index + ", Size: " + size);
  }
}