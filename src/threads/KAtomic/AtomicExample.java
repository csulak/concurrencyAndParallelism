package threads.KAtomic;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicExample implements Runnable {
    AtomicInteger count = new AtomicInteger();


    public void run() {
        for (int i = 0; i < 10000; i++) {
            count.incrementAndGet();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicExample example = new AtomicExample();
        Thread thread1 = new Thread(example);
        Thread thread2 = new Thread(example);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Final count: " + example.count);
    }
}
