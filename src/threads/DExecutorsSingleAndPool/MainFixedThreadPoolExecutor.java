package threads.DExecutorsSingleAndPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainFixedThreadPoolExecutor {

    public static void main(String[] args) throws InterruptedException {


        int CANTIDAD_DE_TAREAS = 8;

        System.out.println("Numero de procesadores disponibles: " + Runtime.getRuntime().availableProcessors());

        // Creamos un ThreadPoolExecutor con x hilos
        ExecutorService service = Executors.newFixedThreadPool(4);

        for(int i = 0; i< CANTIDAD_DE_TAREAS; i++)
        {
            service.submit(new TareaFalsa("Tarea " + i, 2000, i));
        }

        // Esta linea es importante porque es la que avisa que se apague el "apague" el hilo creado al finalizar
        service.shutdown();
        //service.shutdownNow() // Esto apaga el hilo inmediatamente, sin esperar a que termine la tarea

        System.out.println("Soy el hilo MainFixedThreadPoolExecutor: " + Thread.currentThread().threadId() + "\n");

        // Podriamos decir que esto funciona como un Join.
        // Entonces la aplicacion va a esperar en este punto a que se completen las tareasFalsas para continuar
        // la ejecucion del hilo principal.
        // Le ponemos la cantidad de tiempo que queremos que espere antes de continuar ejecutando las proximas lineas
        service.awaitTermination(60, TimeUnit.SECONDS);

        System.out.println("La aplicacion finalizo");

    }
}
