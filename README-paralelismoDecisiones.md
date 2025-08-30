# Guía de decisión de paralelismo en **Java 25**
*Virtual Threads (VT) · ForkJoinPool (FJP) · FixedThreadPool (FTP) · CompletableFuture (CF) · Structured Concurrency (preview)*

---

## 🧭 Diagrama de decisión (alto nivel)

```
[ Request entrante ]
        |
        |-- ¿El request corre en Virtual Thread (VT)?  -> (recomendado)
        |       |
        |       +-- ¿El trabajo es mayormente I/O-bound (APIs, BD, archivos)?
        |       |       |
        |       |       +-- SÍ --> Crear VTs adicionales para cada sub-I/O en paralelo
        |       |                 (opcional: Structured Concurrency para cancelar/propagar errores en grupo)
        |       |
        |       +-- NO (es CPU-bound, cálculo puro)
        |               |
        |               +-- ¿Se puede "dividir y conquistar" (subtareas recursivas/combinables)?
        |                       |
        |                       +-- SÍ --> Usar ForkJoinPool (work-stealing, tamaño ≈ núcleos)
        |                       |
        |                       +-- NO --> FixedThreadPool ≈ núcleos (o FJP igualmente, sin fork/join)
        |
        +-- Si el request NO corre en VT -> mismo árbol, pero:
                - Para I/O-bound: usar VT (o un pool I/O grande si no tenés VT)
                - Para CPU-bound: FJP o FTP ≈ núcleos
```

---

## ⚙️ Cuándo usar cada cosa (reglas rápidas)

- **Virtual Threads (VT):** I/O bloqueante, fan-out de llamadas remotas, pipelines que esperan red/BD/FS. Crea un VT por sub‑tarea sin miedo.
- **ForkJoinPool (FJP):** CPU-bound **divide & conquer**; algoritmos recursivos / arboles / map-reduce de colecciones grandes. `parallelism ≈ #cores`.
- **FixedThreadPool (FTP ≈ #cores):** CPU-bound de **pocas tareas grandes e independientes**; no necesitás la maquinaria FJP.
- **CompletableFuture (CF):** No es un pool; es la **capa de composición** (fan‑out/fan‑in, pipelines, timeouts, manejo de errores). Corre sobre el executor que vos elijas.
- **Structured Concurrency (preview en JDK 25):** Manejo conjunto de subtareas (cancelación, fallos, *join* coherente) cuando haces fan‑out con VT.

---

## 🧪 Ejemplos por rama

### 1) I/O-bound dentro de un request (VT → +VT “hijos”)
**Objetivo:** bajar latencia paralelizando llamadas remotas. Usá un executor de VTs o Structured Concurrency.

**Con `CompletableFuture` + executor de VTs**
```java
// vtExec: executor de virtual threads (p.ej., newVirtualThreadPerTaskExecutor() o VirtualThreadTaskExecutor en Spring)
var fUser   = CompletableFuture.supplyAsync(() -> userApi.get(id), vtExec);
var fOrders = CompletableFuture.supplyAsync(() -> ordersApi.list(id), vtExec);

var dto = fUser.thenCombine(fOrders, Dto::of)
               .orTimeout(1, java.util.concurrent.TimeUnit.SECONDS)
               .exceptionally(ex -> Dto.fallback())
               .join();
```

**Con Structured Concurrency (preview)**
```java
try (var scope = new jdk.incubator.concurrent.StructuredTaskScope.ShutdownOnFailure()) {
  var fu = scope.fork(() -> userApi.get(id));        // VT hijo
  var fo = scope.fork(() -> ordersApi.list(id));     // VT hijo
  scope.join();                                      // espera ambos o aborta si uno falla
  return Dto.of(fu.resultNow(), fo.resultNow());
}
```

> **Tips:** ajustar *pools* de conexiones HTTP/JDBC/Redis y *timeouts*. Si disparás muchas subtareas, aplicá límites (Semaphore/bulkhead).

---

### 2) CPU-bound “divide & conquer” (ForkJoinPool)
**Objetivo:** exprimir cores con *work‑stealing*.
```java
class SumTask extends java.util.concurrent.RecursiveTask<Long> {
  final int[] a; final int lo, hi; static final int TH=10_000;
  SumTask(int[] a, int lo, int hi){ this.a=a; this.lo=lo; this.hi=hi; }
  protected Long compute() {
    int n = hi - lo;
    if (n <= TH) { long s=0; for(int i=lo;i<hi;i++) s+=a[i]; return s; }
    int mid = lo + n/2;
    var left = new SumTask(a, lo, mid); left.fork();
    var right= new SumTask(a, mid, hi);
    return right.compute() + left.join();
  }
}
var fj = new java.util.concurrent.ForkJoinPool(
           Runtime.getRuntime().availableProcessors());
long total = fj.invoke(new SumTask(arr, 0, arr.length));
```

---

### 3) CPU-bound de pocas tareas grandes (FixedThreadPool ≈ núcleos)
```java
var n = Runtime.getRuntime().availableProcessors();
var cpuPool = java.util.concurrent.Executors.newFixedThreadPool(n);
var f1 = cpuPool.submit(() -> heavyCalcA());
var f2 = cpuPool.submit(() -> heavyCalcB());
var result = combine(f1.get(), f2.get());
```

---

## 🎛️ ¿Dónde entra `CompletableFuture`? (y con qué executor)

**CF** = *cómo orquesto* (encadenar, combinar, manejar errores/timeouts).
**Executor** = *dónde corre* (VT para I/O; FJP/FTP para CPU).

- **I/O-bound:** pasá **un executor de VTs** (evitá el *common* FJP para I/O bloqueante).
- **CPU-bound:** pasá **FJP** (o FTP ≈ #cores).

**Fan‑out / fan‑in (dos servicios remotos)**
```java
var a = CompletableFuture.supplyAsync(() -> svcA(), vtExec);
var b = CompletableFuture.supplyAsync(() -> svcB(), vtExec);
var out = a.thenCombine(b, this::merge)
           .orTimeout(800, java.util.concurrent.TimeUnit.MILLISECONDS)
           .exceptionally(ex -> fallback())
           .join();
```

**Pipeline con manejo de error**
```java
CompletableFuture.supplyAsync(() -> fetch(), vtExec)
    .thenApply(this::parse)
    .thenApply(this::enrich)
    .thenAccept(this::persist)
    .exceptionally(ex -> { log.error("falló", ex); return null; });
```

---

## 🧩 Spring Boot (con VT y sin VT)

- **Request en VT:** muchos setups modernos crean **un VT por request**. Dentro del handler podés crear **VTs hijos** para I/O en paralelo.
- **CPU-bound desde un handler VT:** derivá a **FJP** o **FTP ≈ #cores** para no sobre‑suscribir los carrier threads.
- **`@Async`:** útil como azúcar de Spring para enviar trabajo a un **TaskExecutor** (puede ser VT o un pool tradicional). No cambia la semántica de transacciones.

**Ejemplo REST (fan‑out I/O con VT + CF)**
```java
@GetMapping("/summary/{id}")
public Dto get(@PathVariable String id) {
  var fUser   = CompletableFuture.supplyAsync(() -> userApi.get(id), vtExec);
  var fOrders = CompletableFuture.supplyAsync(() -> ordersApi.list(id), vtExec);
  return fUser.thenCombine(fOrders, Dto::of).join();
}
```

---

## ❗ Pitfalls a evitar

- **Usar el common ForkJoinPool** para I/O bloqueante → puede afectar `parallelStream()` o CF de otros. Siempre pasá tu executor.
- **Paralelizar sin límites** → subís el consumo de conexiones y latencia p95/p99. Poné límites por dominio/endpoint.
- **Mezclar `@Transactional` con hilos distintos** esperando atomicidad global → consolidá escrituras en **una sola** TX; lo async antes o AFTER_COMMIT/Outbox después.
- **Meter I/O dentro de FJP** sistemáticamente → preferí VT para I/O; si bloqueás en FJP, evaluá `ManagedBlocker` (avanzado).
- **Olvidar manejo de errores en CF** → usá `exceptionally/handle/orTimeout`.

---

## ✅ Checklist final

- [ ] ¿Identifiqué si la carga es I/O‑bound o CPU‑bound?
- [ ] Para I/O: ¿ejecuto en VT (y si paralelizo, uso VT “hijos”)?
- [ ] Para CPU: ¿FJP si divide & conquer / FTP ≈ #cores si son tareas grandes e independientes?
- [ ] ¿CF sólo como orquestación y con el executor correcto (VT para I/O, FJP/FTP para CPU)?
- [ ] ¿Límites de concurrencia/pools/timeout configurados?
- [ ] ¿Transacciones consolidadas; efectos externos AFTER_COMMIT/Outbox?
