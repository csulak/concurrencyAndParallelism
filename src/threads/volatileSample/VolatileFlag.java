 package threads.volatileSample;

public class VolatileFlag {
    // volatile is something that you can use in variables when you know that this will be shared for multiple threads
    // it is a way to tell the compiler that this variable can be changed by other threads
    // Cada Thread tiene una copia de la variable en su cache, y el volatile le dice al compilador que no use la copia
    // NEED TO CHECK: If you use this variable ONLY in a synchronized block, you don't need to use volatile
    //YOU SHOULD NOT USE volatile for  compound operations, atomic operations: x++ or x = x + y

    // Algunas alcaraciones: El volatile no va a garantizar 100% quiza se ejecute una vez mas el while
    // Esto se asegura al 100% con el synchronized. Pero bueno, quiza el cambio de logica es bastante grande
    // y con un simple volatile podemos asegurar bastante.

    // Example using volatile: En este ejemplo usamos una variable booleana
    private volatile boolean enEjecucion = true;

    public void ejecutar() {

        System.out.println("Hilo de ejecución iniciado.");
        while (enEjecucion) {
            System.out.println("Trabajando...");
            try {
                Thread.sleep(10); // Simula trabajo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Se detuvo el hilo.");
    }

    public void detener() {
        System.out.println("Señal para detener enviada.");
        enEjecucion = false;
    }

    public static void main(String[] args) throws InterruptedException {
        // En este ejemplo hay 2 threads uno es el "worker" y otro es el "main"

        VolatileFlag tarea = new VolatileFlag();

        //Thread worker = new Thread(tarea::ejecutar);
        Thread worker = new Thread(() -> tarea.ejecutar());
        worker.start();

        // Esperamos 3 segundos antes de detener
        Thread.sleep(3000);
        tarea.detener();

        // Esperamos a que termine
        worker.join();
        System.out.println("Programa finalizado.");
    }
}
