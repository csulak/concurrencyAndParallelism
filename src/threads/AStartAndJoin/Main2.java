package threads.AStartAndJoin;

public class Main2 {

    public static void main(String[] args) {
        Thread thread1 = new Thread(new HiloUno());
        Thread thread2 = new Thread(new HiloUno());
        Thread thread3 = new Thread(new HiloUno());
        Thread thread4 = new Thread(new HiloUno());
        Thread thread5 = new Thread(new HiloUno());
        Thread thread6 = new Thread(new HiloUno());
        Thread thread7 = new Thread(new HiloUno());
        Thread thread8 = new Thread(new HiloUno());
        Thread thread9 = new Thread(new HiloUno());
        Thread thread10 = new Thread(new HiloUno());

        System.out.println("Hola desde startAndJoin.Main2");
        thread1.start();
        System.out.println(thread1.getName());
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();
        thread9.start();
        thread10.start();

        // Como no le hice Join a todos los threads.
        // El "Chau desde startAndJoin.Main2" se va a imprimir antes de que terminen todos los threads
        // Proba ejecutarlo varias veces que por casualidad podria terminar ultimo (o no)
        System.out.println("Chau desde startAndJoin.Main2");
    }
}
