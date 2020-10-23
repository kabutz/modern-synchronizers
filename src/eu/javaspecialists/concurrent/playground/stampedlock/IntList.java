package eu.javaspecialists.concurrent.playground.stampedlock;

import java.util.*;
import java.util.concurrent.locks.*;

/*
Best values:
	size()        24,581,981
	get()         23,267,573
	add/remove()  25,833,279
Worst values:
	size()        11,578,778
	get()         11,132,780
	add/remove()  19,594,781

Best values:
	size()        8,679,142
	get()         12,198,555
	add/remove()  39,125,641
Worst values:
	size()        4,998,569
	get()         7,448,027
	add/remove()  16,349,285

 */
public class IntList {
  private static final int OPTIMISTIC_RETRIES = 5;
  private final StampedLock sl = new StampedLock();
  private int[] arr = new int[10];
  private int size = 0;

  public int size() {
    sl.tryOptimisticRead();
    return size;
  }

  public int get(int index) {
    for (int i = 0; i < OPTIMISTIC_RETRIES; i++) {
      long stamp = sl.tryOptimisticRead();
      var currentSize = size;
      var currentArr = arr;
      if (index < currentArr.length) {
        var r = currentArr[index];
        if (sl.validate(stamp)) {
          rangeCheck(index, currentSize);
          return r;
        }
      }
    }
    long stamp = sl.readLock();
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