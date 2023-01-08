package org.martinez;

/**
 * 可見性問題 CPU在計算的時候將資料都佔存在各自的Cache Memory中，所以造成不一致的問題
 */
public class ConcurrencyProblem {

  public static int cnt = 0;

  public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
      for (int i = 0; i < 10000; i++) {
        cnt += 1;
      }
    });

    Thread t2 = new Thread(() -> {
      for (int i = 0; i < 10000; i++) {
        cnt += 1;
      }
    });

    t1.start();
    t2.start();
    Thread.sleep(2000);
    System.out.println(cnt);
  }
}
