# Transacciones y Asincronía en Spring: `@Transactional` + `@Async` + Virtual Threads

## 🧠 TL;DR
- Un método `@Async` **siempre corre en otro hilo** → **no comparte** la transacción del método que lo invoca.
- Un fallo dentro de `@Async` **no hace rollback** de la transacción del llamador.
- Para lograr **“todo-o-nada”**, consolidá **todas las escrituras** en **una sola** transacción (sin `@Async` en el tramo que escribe).
- Usá `@TransactionalEventListener(AFTER_COMMIT)` u **Outbox** para efectos externos que **solo** deben ocurrir si hubo **commit**.
- **Virtual Threads (VT)** cambian el tipo de hilo (más barato), **no** la semántica transaccional.

---

## Conceptos clave
- **Límite transaccional**: Spring propaga la transacción vía `ThreadLocal`. Cambiar de hilo ⇒ **nuevo contexto**.
- **`@Async`**: ejecuta el método en un **TaskExecutor** (pool clásico o Virtual Threads si lo habilitás). No hereda la TX del padre.
- **Excepciones**:
    - `@Async void`: la excepción **no vuelve** al caller (salvo `AsyncUncaughtExceptionHandler`).
    - `@Async CompletableFuture<T>`: la excepción viaja en el future (`join/get`), **no** afecta la TX del padre.
- **Virtual Threads**: podés usarlos con o **sin** `@Async`. VT abarata la concurrencia, **no** “une” transacciones entre hilos.

---

## ✅ Patrón A — Concurrencia afuera, **única transacción** adentro (recomendado)
Paralelizá I/O/cómputo **antes** de abrir la transacción de escritura. Si algo falla, abortás **antes** de persistir.

```java
@Service
class Orquestador {

  public void procesar() {
    // 1) Concurrencia sin TX (podés usar Virtual Threads)
    try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
      var fA = exec.submit(this::llamadaExternaA); // I/O o CPU que no escribe
      var fB = exec.submit(this::llamadaExternaB);

      var a = fA.get();        // si falla, lanza acá y no se persiste nada
      var b = fB.get();

      // 2) ÚNICA transacción para todas las escrituras
      guardarAtomico(a, b);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  void guardarAtomico(Dato a, Dato b) {
    repoA.save(a);
    repoB.save(b);
    // cualquier excepción aquí provoca rollback de TODO
  }
}
```

**Cuándo usar**: siempre que puedas separar precálculo/llamadas externas de las escrituras.

---

## ✅ Patrón B — `@Async` **solo** para preparar datos y **esperar antes** de la TX
Devolvé `CompletableFuture` y **hacé join** antes de abrir la transacción de escritura.

```java
@Service
class Orquestador {

  @Async("vtExecutor") // opcional: corre en Virtual Threads si lo configurás
  public CompletableFuture<Dato> pasoAsync() {
    return CompletableFuture.supplyAsync(this::llamadaExterna); // I/O o cómputo
  }

  public void procesar() {
    var dato = pasoAsync().join(); // si falla, no abrís la TX
    guardarAtomico(dato);          // única TX para persistir
  }

  @Transactional
  void guardarAtomico(Dato d) { repo.save(d); }
}
```

**Clave**: **no escribir** en BD dentro del `@Async` si querés rollback total.

---

## ✅ Patrón C — Efectos externos **después** del commit (AFTER_COMMIT / Outbox)
Para enviar emails, publicar eventos, hidratar cachés, escribir en otras BDs o ES **solo si** la TX comiteó.

**AFTER_COMMIT con listener asíncrono**
```java
@Service
class UserService {
  @Transactional
  public void createUser(User u) {
    repo.save(u);
    publisher.publishEvent(new UserCreated(u.getId())); // emitido dentro de la TX
  }
}

@Component
class UserEvents {
  @Async("vtExecutor") // opcional
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserCreated(UserCreated e) {
    emailService.sendWelcome(e.userId()); // ocurre sólo si hubo commit
  }
}
```

**Transactional Outbox (resiliente y con reintentos)**
1. En la **misma TX** que persiste el agregado, insertás en `outbox` (estado `PENDING`).
2. Un **worker** (polling o CDC/changefeed) procesa y marca `SENT`.
3. Consumidor **idempotente** en el destino.

---

## ❌ Antipatrones
- `@Transactional` (padre) → llama a `@Async` que **escribe** BD y esperás rollback global si falla. **No existe**: son TX separadas.
- `@Async void` sin manejo de excepciones → errores silenciados.
- Cachear/invalidar **antes** del commit (dentro de `@Async`) → inconsistencias.
- Mezclar `@Async` y escritura distribuida esperando atomicidad: para varios recursos, pensá en **Sagas/Outbox** o, si el contexto lo justifica, **2PC/XA**.

---

## 🧩 ¿Y si necesito varias BDs/servicios con “todo-o-nada”?
- **2PC/XA**: atomicidad fuerte, pero es **bloqueante** y complejo. Úsalo sólo si el entorno lo tolera.
- **Sagas (compensaciones) + Outbox**: consistencia eventual, **resiliente y escalable**. Pensar en **rollback de negocio**, no técnico.

---

## 🔧 Habilitar Virtual Threads en Spring (opcional)
**Opción rápida (Boot 3.2+):**
```properties
spring.threads.virtual.enabled=true
```
**Opción explícita:**
```java
@Configuration
@EnableAsync
class AsyncCfg {
  @Bean
  TaskExecutor vtExecutor() { return new VirtualThreadTaskExecutor("vt-"); }
}
```

> VT **no cambia** reglas de transacción; solo abarata la concurrencia.

---

## 🗺️ Diagramas ASCII

### 1) Concurrencia **antes** de la TX (patrón A)
```
[ Request ]
    |
    |-- precálculo en paralelo (VT/threads)
    |      |- tarea A  -----> (OK/falla) --    |      |- tarea B  -----> (OK/falla) --/   -> si falla alguna, aborta ANTES de escribir
    |
    v
[ @Transactional única ]
    |-- write A
    |-- write B
    '-- COMMIT (o ROLLBACK de todo)
```

### 2) `@Async` mal usado para escribir
```
[ @Transactional (padre) ]
    |-- write A
    |-- llama @Async ---> [ hilo aparte, otra TX ]
    |                       '-- write B (falla)  => ROLLBACK de B SOLA
    '-- COMMIT de A        (el fallo async NO revierte A)
```

### 3) AFTER_COMMIT / Outbox
```
[ @Transactional ]
   |-- write domain
   |-- insert outbox / publish event
   '-- COMMIT
        |
        +--> Listener AFTER_COMMIT / Worker Outbox
                '-- efectos externos (email, Kafka, ES, otra BD)
```

---

## ✔️ Checklist de implementación
- [ ] ¿Todas las **escrituras atómicas** están dentro de **una sola** `@Transactional`?
- [ ] ¿La parte `@Async` sólo **prepara datos** y **no** escribe en BD?
- [ ] ¿Efectos externos disparan **AFTER_COMMIT** o por **Outbox** con reintentos?
- [ ] ¿Manejo las excepciones de `@Async` (futures) y mido backpressure?
- [ ] ¿Uso VT donde ayuda (I/O), sin esperar cambios en semántica de TX?

---

## 📌 Regla de oro
> **Rollback total** = **una sola transacción** para todas las escrituras.  
> Lo asincrónico vive **antes** (preparación) o **después** (AFTER_COMMIT / Outbox), **nunca en el medio** si necesitás atomicidad fuerte.
