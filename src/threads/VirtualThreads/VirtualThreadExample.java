package threads.VirtualThreads;

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

        // Esperamos para ver la salida
        Thread.sleep(2000);
        System.out.println("Programa terminado.");
    }
}
