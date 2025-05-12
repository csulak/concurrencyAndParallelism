package threads.ExecutorsSingleAndPool;

public class TareaFalsa implements Runnable {

    String nombre;
    int tiempo;
    int id;

    public TareaFalsa(String nombre, int tiempo, int id) {
        this.nombre = nombre;
        this.tiempo = tiempo;
        this.id = id;
    }


    @Override
    public void run() {
        System.out.println("Soy el hilo de Tarea Falsa: " + Thread.currentThread().threadId() + "\n Comenzamos la tarea con id: " + id);
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            System.out.println("La tarea con id: " + id + " ha sido interrumpida");
        }
        System.out.println("Terminamos la tarea con id: " + id);
        System.out.println();

    }
}
