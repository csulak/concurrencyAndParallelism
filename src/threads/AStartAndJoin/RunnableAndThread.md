### Runnable and Thread

``
When we interact with Thread, instanciating threads we start using the logic of concurrency
``

``
Runnable is just an interface that allows us to implement the run method. thats all. The important thing is this encapsulate the logic of "OK, this logic will be used in a thread".
It doesn't make sense to use Runnable without a Thread. Because Runnable doesn't do anything is just for ordering the information and leave the logic to the Thread.
``

``
Es por esto que para dejar el codigo mas limpio y prolijo nos manejamos con Runnable dentro de Thread.
Al momento de crear un Thread, le pasamos un Runnable en el constructor.
Y de esta forma abstraemos la logica de la clase Thread.
``

```
Thread myThread = new Thread(new HiloUno());
// donde HiloUno es una clase que implementa Runnable

// y en el run() de HiloUno es donde se ejecuta la logica del hilo

// y el Thread se encarga de ejecutar el run() de HiloUno
// Simplemente haciendo:
myThread.start();
```
