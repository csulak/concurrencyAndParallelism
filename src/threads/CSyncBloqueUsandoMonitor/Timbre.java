package threads.CSyncBloqueUsandoMonitor;

public class Timbre {

    public void tocar(){

        System.out.println("** Me acerco al timbre **");
        synchronized (this){
            System.out.print("Ding...");
            try {
                Thread.sleep(2000);
                System.out.println("  Dong");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
