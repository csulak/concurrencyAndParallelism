package threads.syncBlock;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Timbre timbre = new Timbre();
        Thread hilo1 = new Thread(new TimbreRunnable(timbre));
        Thread hilo2 = new Thread(new TimbreRunnable(timbre));

        hilo1.start();
        hilo2.start();
        hilo1.join();
        hilo2.join();
    }
}

/**
 * Podrias preguntarte. ¿Por que no tener la misma instancia de
 * TimbreRunnable para ambos threads?
 * EJ:
 *         Timbre timbre = new Timbre();
 *         TimbreRunnable timbreRunnable = new TimbreRunnable(timbre);
 *         Thread hilo1 = new Thread(timbreRunnable);
 *         Thread hilo2 = new Thread(timbreRunnable);
 *
 * La realidad es que si TimbreRunnable SOLO va a sobreescribir el metodo run()
 * No habria problema.
 *
 * Pero en el caso de que crezca y tome otras responsabilidades.
 * TimbreRunnable podria tener un estado interno que no es seguro compartir entre
 * distintos threads
 *

 */
