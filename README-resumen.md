# 🧭 Guía rápida: ExecutorService, ForkJoinPool, CompletableFuture y Virtual Threads

## 1. Elección rápida (5 preguntas)
1. **¿Tu carga es I/O-bound o CPU-bound?**
   - I/O (red, BD, archivos, APIs externas): → **Virtual Threads** o **FixedThreadPool grande**.
   - CPU (cálculo puro): → **ForkJoinPool** o **parallelStream**.

2. **¿Necesitás componer/encadenar tareas asíncronas?**
   - Sí → **CompletableFuture** (con un executor adecuado: VT o fixed pool).
   - No → con VT podés escribir código bloqueante simple por cada tarea.

3. **¿Tus tareas se pueden dividir y combinar (divide & conquer)?**
   - Sí (árboles, quicksort, reduce de colecciones grandes) → **ForkJoinPool**.
   - No → VT o fixed pool.

4. **¿Buscás simplicidad estilo “un hilo por request” sin tunear pools?**
   - Sí → **Virtual Threads** (executor `newVirtualThreadPerTaskExecutor()`).

5. **¿Hay bloqueos largos (ej. timeouts de 1–5 s) y mucha concurrencia?**
   - Sí → **Virtual Threads** brillan (no gastan 1MB de stack, se aparcan barato).

---

## 2. Qué usar y cuándo (con VT en juego)

| Necesidad | Recomendado hoy | Por qué |
|---|---|---|
| Muchas llamadas a APIs/BD (I/O) | **Virtual Threads** | Modelo simple, enorme escalabilidad sin pelearte con tamaños de pool. |
| Pipelines asíncronos (componer, combinar, fallback) | **CompletableFuture + VT** (o fixed pool si preferís) | CF sigue siendo la mejor API de composición; el executor puede ser VT. |
| Cálculo CPU-bound y “divide & conquer” | **ForkJoinPool** | Work-stealing maximiza uso de CPU; VT no aporta extra aquí. |
| Trabajos I/O + algo de CPU | **VT** para orquestación; offload de CPU a **ForkJoinPool** si hace falta | Mantiene simple el flujo; derivás lo CPU-intensivo. |
| Servidores web (sin reactive) | **VT por request** | Código bloqueante, claro y escalable. |

> ¿Algo quedó “en desuso” por VT?  
> **No**. VT **no reemplaza** ForkJoin para CPU-bound ni **CompletableFuture** para composición.  
> Lo que **sí** reemplaza en la práctica es gran parte del “malabar de pools” para **I/O-bound**.

---

## 3. Starters de configuración

### Virtual Threads (I/O-bound típico)
```java
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
  Future<String> f = exec.submit(() -> callApiBlocking());
  System.out.println(f.get());
}
```

### CompletableFuture sobre VT (composición asíncrona)
```java
var vexec = Executors.newVirtualThreadPerTaskExecutor();

CompletableFuture<String> userF = CompletableFuture.supplyAsync(() -> getUser(), vexec);
CompletableFuture<String> ordersF = CompletableFuture.supplyAsync(() -> getOrders(), vexec);

var result = userF.thenCombine(ordersF, (u, o) -> merge(u, o)).join();
```

### CPU-bound puro (ForkJoin)
```java
ForkJoinPool fj = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
long total = fj.invoke(new MyRecursiveTask(data));
```

### FixedThreadPool (cuando NO usás VT)
```java
ExecutorService ioPool = Executors.newFixedThreadPool(64); // I/O-bound
ExecutorService cpuPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
```

---

## 4. Pitfalls (con y sin VT)
- **Common ForkJoinPool**: evitá bloquearlo con I/O (perjudica parallel streams/CF de otros).
- **Parallel Streams**: bien para CPU puro; **no** para I/O.
- **Tamaños de pool** (sin VT):
   - I/O-bound: empezar en 4–8× cores y ajustar con métricas.
   - CPU-bound: ≈ cantidad de cores.
- **Blocking en reactive**: si estás en reactive (Project Reactor), bloquear threads rompe el modelo; VT ayuda **si** el framework lo soporta o si no estás en reactive.

---

## 5. Regla de oro actual
- **Si es I/O** → **Virtual Threads** primero; te simplifican la vida y escalan muy bien.
- **Si es CPU-bound y paralelizable** → **ForkJoinPool**/`parallelStream`.
- **Si necesitás *composición* asíncrona** → **CompletableFuture** (ejecutando en VT/fixed pool según el caso).
