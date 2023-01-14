package org.martinez.mutuallyexclusive;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 1.可以一起讀
 * 2.不可一起寫
 * 3.讀要等寫
 */
public class FileEditor {

  ReadWriteLock lock = new ReentrantReadWriteLock();
  Lock rLock = lock.readLock();
  Lock wLock = lock.writeLock();

  private String content = "";

  public void addLine(String line) {
    wLock.lock();
    try {
      content += line;
    } finally {
      wLock.unlock();
    }
  }

  public String getContent() {
    rLock.lock();
    try {
      return content;
    } finally {
      rLock.unlock();
    }
  }
}
