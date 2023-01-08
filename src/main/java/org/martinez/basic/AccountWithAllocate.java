package org.martinez;

import java.util.ArrayList;
import java.util.List;

/**
 * 持有和等待(hold and wait) : 將需要的lock都拿走，一口氣拿走在執行完後再一口氣釋放，就不會有拿不到的問題
 * 透過自行設計調度者(Allocator)，管理lock資源，將lock管理抽出程式邏輯，不過現在的設計有個缺點，Lock的申請是透過poll(輪詢)的方式會比較耗費CPU資源
 */
class Allocator {

  private static List<Object> lockList = new ArrayList<>();

  synchronized public boolean apply(Object source, Object target) {
    if (lockList.contains(source) || lockList.contains(target)) {
//      return false;  //直接回傳false讓while再次輪詢 lock
      try {
        wait(); //優化，最大可能的消除輪詢(poll)，等到有所被釋放才會繼續執行
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    lockList.add(source);
    lockList.add(target);
    return true;
  }

  synchronized public void free(Object source, Object target) {
    lockList.remove(source);
    lockList.remove(target);
    notifyAll();  //釋放鎖時通知其他thread可以繼續輪詢
  }

}

public class AccountWithAllocate {

  private int balance = 10000;
  private static Allocator allocator = new Allocator();

  private final Object balanceLock = new Object();

  // Withdraw money from account
  int withdraw(int amt) {
    synchronized (balanceLock) {
      if (this.balance > amt) {
        this.balance -= amt;
        return this.balance;
      }
      return 0;
    }
  }

  void transfer(AccountWithAllocate source, int amt) {
    while (!allocator.apply(source, this))
      ;

    int money = source.withdraw(amt);
    if (money > 0) {
      this.balance += amt;
    }
    allocator.free(source, this);
  }
}
