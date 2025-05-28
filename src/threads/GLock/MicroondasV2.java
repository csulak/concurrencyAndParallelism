package threads.GLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ejemplo con TryLock (antes leer las notas de la clase Microondas)
 *
 * TryLock nos ofrece una forma de validar si el recurso esta ocupado o no
 * Lo que va a suceder es que el thread va a llegar hasta el "tryLock"
 * y este devolvera true o false si el lock (microondasLock) esta libre o no
 *
 */
public class MicroondasV2 {

    static final Lock microondasLock = new ReentrantLock();

    public void calentarComida(){

        String nombreThread = Thread.currentThread().getName();

        // Aca usamos un tryLock. El cual nos devuelve un booleano y vamos a validar cada 500ms si el candado esta locked o no
        // Es un pequeño ejemplo didactico, pero no es super wow, ya que el thread se va a quedar loopeando
        // ese while medio al pedo hasta que liberen el lock

        // Una mejor forma de hacerlo es usar un "if" y si no se puede calentarComida, simplemente no lo hace
        // Es decir usamos el tryLock y si el thread esta "lock" no hace nada.
        // Podria usarse en una operacion de bajo impacto que si no se hace, no importa y se prioriza que no quede bloqueado el thread
        try {
            while (!microondasLock.tryLock(500, TimeUnit.MILLISECONDS)) {
                System.out.println("Thread: " + nombreThread + ". El candado esta ocupado, no puedo calentar la comida");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Thread: " + nombreThread + ". Calentando comida...");
        try {
            Thread.sleep(2000); // Simula el tiempo de calentamiento
            System.out.println("Thread: " + nombreThread + ". Comida lista!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            microondasLock.unlock();
        }
    }

    public static void main(String[] args) {

        MicroondasV2 microondas = new MicroondasV2();
        Runnable runnable = () -> microondas.calentarComida();

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread1.start();
        thread2.start();
    }
}

