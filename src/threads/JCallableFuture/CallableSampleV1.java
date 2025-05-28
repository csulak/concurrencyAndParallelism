package threads.JCallableFuture;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Un ejemplo de Callable. Es similar a un Runnable, pero este puede devolver un resultado.
 */
public class CallableSampleV1 implements Callable<Integer> {

    private int id;
    private String nombre;
    private int tiempo;

    public CallableSampleV1(int id, String nombre, int tiempo) {
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

    public static void main(String[] args) throws Exception {
        System.out.println("Soy el hilo de CallableSampleV1.Main: " + Thread.currentThread().threadId() + "\n");

        CallableSampleV1 callableSample1 = new CallableSampleV1(1, "Tarea 111", 2000);

        Integer result = callableSample1.call();

        System.out.println("El resultado de la tarea1 es: " + result);

    }
}
