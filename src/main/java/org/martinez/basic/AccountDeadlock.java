package org.martinez.basic;

/**
 * 死鎖的四個條件：
 * 1.禁止搶占（no preemption）：系統資源不能被強制從Thread退出。
 * 2.持有和等待（hold and wait）：Thread可以在等待時持有系統資源。
 * 3.互斥（mutual exclusion）：資源只能同時分配給一個Thread，無法多個行程共享。
 * 4.循環等待（circular waiting）：Thread互相持有其他Thread所需要的資源，並且等待已被占用的資源。
 */

public class AccountDeadlock {

  private int balance = 10000;
  private String password;

  private final Object balanceLock = new Object();
  private final Object passwordLock = new Object();

  // Withdraw money from account
  int withdraw(int amt) {
    synchronized (balanceLock) {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (this.balance > amt) {
        this.balance -= amt;
        return this.balance;
      }
      return 0;
    }
  }

  // Check the balance
  int getBalance() {
    synchronized (passwordLock) {
      return balance;
    }
  }

  void transfer(AccountDeadlock source, int amt) {
    synchronized (balanceLock) {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      int money = source.withdraw(amt);
      if (money > 0) {
        this.balance += amt;
      }
    }
  }

  // Change the password
  void updatePassword(String pw) {
    synchronized (passwordLock) {
      this.password = pw;
    }
  }

  // Check the password
  String getPassword() {
    synchronized (passwordLock) {
      return password;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    final AccountDeadlock a1 = new AccountDeadlock();
    final AccountDeadlock a2 = new AccountDeadlock();

    Thread t1 = new Thread(() -> {
      a1.transfer(a2, 1000);
    });

    Thread t2 = new Thread(() -> {
      a2.transfer(a1, 5000);
    });
//    A和B Account都同時呼叫Transfer
//    都各自拿到本身物件的Balance Lock
//    向對方(A->B B->A)呼叫withdraw method
//    各自的withdraw method都因為lock被transfer拿走了所以無限等待
    t1.start();
    t2.start();

    Thread.sleep(1000);

    System.out.println(a1.getBalance());
    System.out.println(a2.getBalance());
  }
}
