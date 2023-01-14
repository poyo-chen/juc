package org.martinez.basic;

/**
 * 每個Account ID值，讓在匯款時比對ID值，ID高的先鎖住在鎖住ID低的 Account，這個方法可以初步的打破循環等待的問題，但是若是Account一多還是有可能會Deadlock
 */
public class AccountWithID {

  public int id = (int) (Math.random() * 100);
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

  void transfer(AccountWithID source, int amt) {
    AccountWithID higher;
    AccountWithID lower;
    if (this.id > source.id) {
      higher = this;
      lower = source;
    } else {
      higher = source;
      lower = this;
    }
    synchronized (higher) {
      synchronized (lower) {
        int money = source.withdraw(amt);
        if (money > 0) {
          this.balance += amt;
        }
        allocator.free(source, this);
      }
    }
  }
}
