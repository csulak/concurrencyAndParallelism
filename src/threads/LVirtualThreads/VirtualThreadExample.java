package threads.LVirtualThreads;

public class VirtualThreadExample {
    public static void main(String[] args) throws InterruptedException {
        Runnable tarea = () -> {
            System.out.println("Inicio: " + Thread.currentThread());
            try {
                Thread.sleep(1000); // Simula tarea con espera
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Fin: " + Thread.currentThread());
        };

        // Crear 10 virtual threads
        for (int i = 0; i < 10; i++) {
            Thread.startVirtualThread(tarea); // Java 22+
        }

        // Esperamos para ver la salida. Si dejamos comentada esta linea,
        // los virtual threads pueden no tener tiempo de ejecutarse antes de que el programa termine.
        Thread.sleep(2000);
        System.out.println("Programa terminado.");
    }
}
