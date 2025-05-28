package threads.GLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ejemplo con Lock
 *
 * En este ejercicio se propone implementar un microondas, donde solo una persona (thread) puede calentarComida a la vez
 * El primer thread que llegue al microondas va a calentarComida, y el segundo thread que llegue va a tener que esperar
 * Esto sucede gracias a microondasLock.lock(); cuando el primer thread toma el lock, el segundo thread no puede
 * Este primer thread va a liberar el lock cuando se ejecute microondasLock.unlock();
 *
 * Digamos que es una forma distinta de usar el Synchronized
 */
public class Microondas {

    static final Lock microondasLock = new ReentrantLock();

    public void calentarComida(){

        microondasLock.lock();

        System.out.println("Calentando comida...");
        try {
            Thread.sleep(2000); // Simula el tiempo de calentamiento
            System.out.println("Comida lista!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            microondasLock.unlock();
        }
    }

    public static void main(String[] args) {

        Microondas microondas = new Microondas();
        Runnable runnable = () -> microondas.calentarComida();

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread1.start();
        thread2.start();
    }
}

