package threads.sync;

public class Contable implements Runnable{

    private Contador contador;

    public Contable(Contador contador) {
        this.contador = contador;
    }

    @Override
    public void run() {
        contador.contar();
    }
}

