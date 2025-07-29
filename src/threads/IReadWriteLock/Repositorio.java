package threads.IReadWriteLock;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * En un Repositorio, podriamos decir
 * que muchos pueden leer algo al mismo tiempo. Ahora si se hace una escritura,
 * solo uno escribe y el resto no puede leer.
 *
 * Por ende el ReadWriteLock es una implementacion de un candado que permite
 * que varios threads lean al mismo tiempo, pero solo uno escriba.
 *
 * Fijate que el metodo leer() llama a readWriteLock.readLock() y el metodo escribir() a readWriteLock.writeLock()
 * De esta forma todas las lecturas se hacen sin problema hasta que aparece una escritura y ahi queda completamente bloqueado hasta que finalice la escritura.
 */
public class Repositorio {

    Random random = new Random();

    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void leer() {
        try{
            logear("Pido permiso para leer (fuera del lock de lectura)");
            readWriteLock.readLock().lock();
            logear("*** Leyendo ***");
            esperar(random.nextInt(500, 1000));
            logear("Termino de leer.");
        }
        finally {
            readWriteLock.readLock().unlock();
        }

    }

    public void escribir() {
        try{
            logear("Pido permiso para escribir (fuera del lock de escritura)");
            readWriteLock.writeLock().lock();
            logear("*** ESCRIBIENDO ***");
            esperar(random.nextInt(5000, 6000));
            logear("Termino de escribir!!!");
        }
        finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private static void esperar(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void logear(String mensaje) {
        System.out.println(Thread.currentThread().getName() + ": " + mensaje);
    }


    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(4);

        Repositorio repo = new Repositorio();

        // Creamos threads para leer y escribir
        Runnable leer = new Thread(repo::leer);
        Runnable escribir = new Thread(repo::escribir);


        for (int i = 0; i < 10; i++) {
            service.submit(leer);
        }
        service.submit(escribir);
        for (int i = 0; i < 10; i++) {
            service.submit(leer);
        }

        service.shutdown();
    }
}
