package org.martinez.mutuallyexclusive;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache<T> {
  private T data;
  private volatile boolean isChanged = false;
  ReadWriteLock lock = new ReentrantReadWriteLock();
  Lock wLock = lock.writeLock();
  Lock rLock = lock.readLock();

  public void offer(T input) {
    wLock.lock();

    try {
      data = input;
      // Save data to db
      isChanged = true;
    } finally {
      wLock.unlock();
    }
  }

  public T get() {
    rLock.lock();
    if (isChanged) {
      rLock.unlock();
      wLock.lock();
      try {
        // Check again maybe other Thread is already get data from db
        if (!isChanged) {
          // Get data from db and update data (Fake method)
//          data = getDataFromDB();
          isChanged = false;
        }

        // rLock before release wLock to make sure data won't be changed when we release wLock
        rLock.lock();
      } finally {
        wLock.unlock();
      }
    }
    try {
      return data;
    } finally {
      rLock.unlock();
    }
  }
}
