package threads.AStartAndJoin;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //Como Thread implementa Runnable, y Runnable es una functional interface,
        // podemos usar una lambda directamente
        Thread miThread = new Thread(() -> {
            try {
                System.out.println("Hola desde Thread");
                Thread.sleep(1000);
                System.out.println("Chau desde Thread");
            } catch (InterruptedException e) {
                System.out.println("Fallo el Thread");
            }
        });

        System.out.println("Hola desde startAndJoin.Main");

        miThread.start();

        // El join hace que el hilo principal espere a que el hilo llamado miThread termine
        miThread.join();

        System.out.println("Chau desde startAndJoin.Main");

    }
}
