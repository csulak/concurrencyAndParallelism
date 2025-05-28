package threads.HCondition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionMain {

    static volatile String paquete;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public void dejarPaquete(){
        lock.lock();
        try {
            System.out.println("Dejando paquete...");
            Thread.sleep(2000);
            paquete = "Iphone 999";
            System.out.println("Paquete dejado");
            condition.signalAll(); // Notifica al hilo que espera
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    public void recogerPaquete() {
        lock.lock();
        try {
            // Esperar mientras el paquete sea null
            while (paquete == null) {
                System.out.println("Esperando paquete...");
                condition.awaitUninterruptibly();
            }
            System.out.println("Paquete recogido!! : " + paquete);
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionMain conditionMain = new ConditionMain();
        Thread t1 = new Thread(() -> conditionMain.dejarPaquete()); // Esperador
        Thread t2 = new Thread(() -> conditionMain.recogerPaquete());   // Productor

        t2.start();
        t1.start();
    }
}
