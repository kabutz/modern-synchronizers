package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.*;
import java.util.concurrent.locks.*;

/*
Best values:
	size()        17,875,374
	get()         19,995,470
	add/remove()  22,565,827
Worst values:
	size()        14,349,009
	get()         15,625,824
	add/remove()  18,758,457

 */
public class IntList {
  private final StampedLock sl = new StampedLock();
  private int[] arr = new int[10];
  private int size = 0;

  public int size() {
    sl.tryOptimisticRead();
    return size;
  }

  public int get(int index) {
    long stamp = sl.tryOptimisticRead();
    int[] localArr = arr;
    int localSize = size;
    if (index < localArr.length) {
      int localValue = localArr[index];
      if (sl.validate(stamp)) {
        rangeCheck(index, localSize);
        return localValue;
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
    long stamp = sl.writeLock();
    try {
      if (size < arr.length)
        arr = Arrays.copyOf(arr, size);
    } finally {
      sl.unlockWrite(stamp);
    }
  }

  public int remove(int index) {
    long stamp = sl.writeLock();
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