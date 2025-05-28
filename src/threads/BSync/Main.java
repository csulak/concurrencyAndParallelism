package threads.BSync;


public class Main {
    public static void main(String[] args) throws InterruptedException {

        // Loopeo de forma infinta el metodo de abajo
        for(;;)
        {
            iterarEsto();
        }
    }

    private static void iterarEsto() throws InterruptedException {
        // Esta instancia se va a compartir por todos los threads instanciados mas abajo
        Contador contador = new Contador();
        int THREADS = 1000;

        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            // Instancio todos los threads y van a compartir la MISMA instancia de "contador"
            threads[i] = new Thread(new Contable(contador));
        }

        for (int i = 0; i < THREADS; i++) {
            threads[i].start();
        }

        // Espero a que se joineen todos los threads para seguir ejecutando mi codigo
        for (int i = 0; i < THREADS; i++) {
            threads[i].join();
        }

        // imprimo la variable compartida
        System.out.println("La variable a contar es: " + contador.getContador());
    }
}