package threads.WaitAndNotify;

public class WaitAndNotifyMain {

    static class Entrega implements Runnable {
        private final Bandeja bandeja;

        public Entrega(Bandeja bandeja) {
            this.bandeja = bandeja;
        }

        @Override
        public void run() {
            System.out.println("Arranca Entrega");
            bandeja.setPassword("1234");
            synchronized (bandeja) {
                System.out.println("Dentro del synchronized de Entrega");
                bandeja.notifyAll();
            }
        }
    }

    static class Recibe implements Runnable {
        private final Bandeja bandeja;

        public Recibe(Bandeja bandeja) {
            this.bandeja = bandeja;
        }

        @Override
        public void run() {
            synchronized (bandeja) {
                System.out.println("Arranca Recibe");
                System.out.println("Dentro del synchronized de Recibe");
                try {
                    bandeja.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("hola Maru!: " + bandeja.getPassword());
            }
        }
    }

    static class Bandeja {

        private String password;

        public String getPassword() {
             if (password == null) {
                 throw new IllegalStateException("Password not set");
             }
             return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    /**
     * Esta logica anda y el wait es llamado antes del notifyAll simplemente porque el "Synchronized" del Recibe
     * comienza en la primer linea del run().
     * En cambio en el "synchronized" del Entega esta al final del run() solo para ejecutar el notifyAll().
     * De esta forma Entrega y Recibe arrancan en paralelo pero como Recibe tiene el Synchronized al principio
     * esta toma el control primero del monitor (Bandeja) y es por esto que logra llamar al wait antes que el notifyAll.
     *
     * En este ejemplo tambien vemos que hay 2 syncronized distintos en 2 clases distintas pero
     * ambos comparten el mismo objeto monitor (Bandeja).
     */
    public static void main(String[] args) {
        final Bandeja bandeja = new Bandeja();
        Thread entrega = new Thread(new Entrega(bandeja));
        Thread recibe = new Thread(new Recibe(bandeja));

        recibe.start();
        entrega.start();
    }
}
