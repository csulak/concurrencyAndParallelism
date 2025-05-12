package threads.sync;

public class Contador{
    private int contador;

    // Gracias al "synchronized" nadie mas puede acceder a este metodo al mismo tiempo
    // Es decir que queda bloqueado para el resto de los threads hasta que no se
    // Complete su ejecucion
    public synchronized void contar(){
        int old = this.contador;
        old = old + 1;
        this.contador = old;
    }

    public int getContador() {
        return contador;
    }
}

/**
 * Otra forma aplicar synchronized es a un bloque de codigo y no a todo un metodo
 */
/*-
        synchronized (this){
            int old = this.contador;
            old = old + 1;
            this.contador = old;
        }
 */
