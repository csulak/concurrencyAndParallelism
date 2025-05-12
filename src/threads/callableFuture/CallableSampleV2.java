package threads.callableFuture;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Un ejemplo de Callable. Es similar a un Runnable, pero este puede devolver un resultado.
 */
public class CallableSampleV2 implements Callable<Integer> {

    private int id;
    private String nombre;
    private int tiempo;

    public CallableSampleV2(int id, String nombre, int tiempo) {
        this.id = id;
        this.nombre = nombre;
        this.tiempo = tiempo;
    }

    @Override
    public Integer call() throws Exception {
        Random random = new Random();
        System.out.println("Soy el hilo de Callable: " + Thread.currentThread().threadId() + "\nComenzamos la tarea con id: " + id);
        Thread.sleep(tiempo);
        System.out.println("Terminamos la tarea con id: " + id);
        return id + random.nextInt(100, 200);
    }

    public static void main(String[] args) {
        System.out.println("Soy el hilo de generalSamples.Main: " + Thread.currentThread().threadId() + "\n");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CallableSampleV2 callableSample1 = new CallableSampleV2(1, "Tarea 111", 2000);
        CallableSampleV2 callableSample2 = new CallableSampleV2(2, "Tarea 222", 2000);
        CallableSampleV2 callableSample3 = new CallableSampleV2(3, "Tarea 333", 2000);
        try {
            //Integer result = callableSample.call();
            Future<Integer> future1 = executor.submit(callableSample1);
            Future<Integer> future2 = executor.submit(callableSample2);
            Future<Integer> future3 = executor.submit(callableSample3);

            Integer result1 = future1.get();
            Integer result2 = future2.get();
            Integer result3 = future3.get();

            System.out.println("El resultado de la tarea1 es: " + result1);
            System.out.println("El resultado de la tarea2 es: " + result2);
            System.out.println("El resultado de la tarea3 es: " + result3);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            executor.shutdown();
        }
    }
}
