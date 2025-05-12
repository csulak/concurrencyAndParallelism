package threads.ExecutorsSingleAndPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Single thread puede ser util cuando queremos que una tarea (o lista de tareas) se ejecuten en un solo hilo
 * En este caso tarea1, tarea2 y tarea3 se ejecutan en el mismo hilo
 */
public class MainSingleThreadExecutor {

    public static void main(String[] args) throws InterruptedException {

        TareaFalsa tarea1 = new TareaFalsa("Tarea 1", 2000, 1);
        TareaFalsa tarea2 = new TareaFalsa("Tarea 2", 2000, 2);
        TareaFalsa tarea3 = new TareaFalsa("Tarea 3", 2000, 3);

        // create single thread executor service
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.submit(tarea1);
        service.submit(tarea2);
        service.submit(tarea3);

        // Esta linea es importante porque es la que avisa que se apague el "apague" el hilo creado al finalizar
        service.shutdown();
        //service.shutdownNow() // Esto apaga el hilo inmediatamente, sin esperar a que termine la tarea

        System.out.println("Soy el hilo MainSingleThreadExecutor: " + Thread.currentThread().threadId() + "\n");

        // Podriamos decir que esto funciona como un Join.
        // Entonces la aplicacion va a esperar en este punto a que se completen las tareasFalsas para continuar
        // la ejecucion del hilo principal.
        // Le ponemos la cantidad de tiempo que queremos que espere antes de continuar ejecutando las proximas lineas
        service.awaitTermination(60, TimeUnit.SECONDS);

        System.out.println("La aplicacion finalizo");

    }
}
