package org.martinez.mutuallyexclusive;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {

  private static final Lock lock = new ReentrantLock();

  private final Condition notEmpty = lock.newCondition();

  private int cnt = 0;

  public void addOne() {
//    if(lock.tryLock(2000)) {//tryLock，會在嘗試拿取lock兩千毫秒都失敗的情況下回傳false
    if(lock.tryLock()) {//tryLock，只要在目前有人使用lock就會馬上回傳false
//    lock.lock(); //鎖，可以使用tryLock馬上拿取結果，不用等釋放^
      try {
        cnt += 1;
      } finally {
        lock.unlock();
      }
    }else {
      //針對沒有拿到鎖做額外的處裡(紀錄或先暫時處理其他資料再回來)
      System.out.println("Already been using");
    }
  }

  public int getCnt() {
    lock.lock();
    try {
      return cnt;
    } finally {
      lock.unlock();
    }
  }


  public static void main(String[] args) {
    final ReentrantLockExample reentrantLockExample = new ReentrantLockExample();
    for (int i = 0; i < 10; i++) {
      Thread t = new Thread(() -> {
        for (int j = 0; j < 10000; j++) {
          reentrantLockExample.addOne();
        }
      });
      t.start();
    }

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(reentrantLockExample.getCnt());
  }
}
