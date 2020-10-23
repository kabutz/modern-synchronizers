package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.*;
import java.util.concurrent.locks.*;

public class IntList {
  private final StampedLock sl = new StampedLock();
  private int[] arr = new int[10];
  private int size = 0;

  private static void rangeCheck(int index, int size) {
    if (index >= size)
      throw new IndexOutOfBoundsException(
          "Index: " + index + ", Size: " + size);
  }

  public int size() {
    sl.tryOptimisticRead();
    return size;
  }

  public int get(int index) {
    long stamp = sl.tryOptimisticRead();
    var currentArr = arr;
    var currentSize = size;
    if (index < currentArr.length) {
      int result = currentArr[index];
      if (sl.validate(stamp)) {
        rangeCheck(index, currentSize);
        return result;
      }
    }
    stamp = sl.readLock();
    try {
      rangeCheck(index, size);
      return arr[index];
    } finally {
      sl.unlockRead(stamp);
    }
  }

  public boolean add(int e) {
    long stamp = sl.writeLock();
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

    long stamp = sl.tryOptimisticRead();
    var currentSize = size;
    var currentArr = arr;
    if (sl.validate(stamp) && currentSize == currentArr.length) return;

    stamp = sl.writeLock();
    try {
      if (size < arr.length)
        arr = Arrays.copyOf(arr, size);
    } finally {
      sl.unlockWrite(stamp);
    }
  }

  public int remove(int index) {
    long stamp = sl.readLock();
    try {
      while (true) {
        rangeCheck(index, size);
        long writeStamp = sl.tryConvertToWriteLock(stamp);
        if (writeStamp == 0) {
          sl.unlockRead(stamp);
          stamp = sl.writeLock();
        } else {
          stamp = writeStamp;
          return doActualRemove(index);
        }
      }
    } finally {
      sl.unlock(stamp);
    }
  }

  private int doActualRemove(int index) {
    int oldValue = arr[index];

    int numMoved = size - index - 1;
    if (numMoved > 0)
      System.arraycopy(arr, index + 1,
          arr, index, numMoved);
    arr[--size] = 0;

    return oldValue;
  }
}