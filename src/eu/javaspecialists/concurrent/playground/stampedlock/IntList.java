package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.*;
import java.util.concurrent.locks.*;

public class IntList {
  private final StampedLock sl = new StampedLock();
  private int[] arr = new int[10];
  private int size = 0;

  public int size() {
    sl.tryOptimisticRead();
    return size;
  }

  public int get(int index) {
    for (int i = 0; i < 5; i++) {
      var stamp = sl.tryOptimisticRead();
      int[] currentArr = arr;
      int currentSize = size;
      if (index < currentArr.length) {
        int value = currentArr[index];
        if (sl.validate(stamp)) {
          rangeCheck(index, currentSize);
          return value;
        }
      }
    }
    var stamp = sl.readLock();
    try {
      rangeCheck(index, size);
      return arr[index];
    } finally {
      sl.unlockRead(stamp);
    }
  }

  public boolean add(int e) {
    var stamp = sl.writeLock();
    try {
      if (size + 1 > arr.length)
        arr = Arrays.copyOf(arr, size + 10);

      arr[size++] = e;
      return true;
    } finally {
      sl.unlockWrite(stamp);
    }
  }

  public void trimToSize() {
    var stamp = sl.writeLock();
    try {
      if (size < arr.length)
        arr = Arrays.copyOf(arr, size);
    } finally {
      sl.unlockWrite(stamp);
    }
  }

  public int remove(int index) {
    var stamp = sl.writeLock();
    try {
      rangeCheck(index, size);
      int oldValue = arr[index];

      int numMoved = size - index - 1;
      if (numMoved > 0)
        System.arraycopy(arr, index + 1,
            arr, index, numMoved);
      arr[--size] = 0;

      return oldValue;
    } finally {
      sl.unlockWrite(stamp);
    }
  }

  private static void rangeCheck(int index, int size) {
    if (index >= size)
      throw new IndexOutOfBoundsException(
          "Index: " + index + ", Size: " + size);
  }
}