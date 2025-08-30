# @Async vs Virtual Threads directo en Java

## 🔹 ¿Qué hace `@Async`?
- Es una **anotación de Spring** para ejecutar un método en **otro hilo** usando un **TaskExecutor**.
- Simplifica el wiring: no tenés que manejar manualmente `ExecutorService`.

Ejemplo clásico:
```java
@Service
public class MiServicio {

    @Async
    public void trabajoPesado() {
        // se corre en un thread del pool de Spring
    }
}
```

- Executor usado por Spring:
    - Por defecto: **ThreadPoolTaskExecutor** (pool fijo).
    - Con Java 21 + Spring Boot 3.2+: podés habilitar **Virtual Threads** (`spring.threads.virtual.enabled=true`).

---

## 🔹 ¿Qué son los Virtual Threads?
- Son una feature nativa del **JDK 21 (Project Loom)**.
- Podés crearlos **sin depender de Spring**.

Ejemplo directo:
```java
Thread.startVirtualThread(() -> {
    callApi(); // corre en un virtual thread
});
```

Con executor:
```java
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> f = exec.submit(() -> callApi());
    System.out.println(f.get());
}
```

---

## 🔹 Comparación rápida

| Caso | Usar `@Async` (Spring) | Usar Virtual Threads directo (JDK) |
|------|-------------------------|-----------------------------------|
| Querés integración con Spring | ✅ Natural | Opcional |
| Querés control total del executor | Más limitado | ✅ Completo |
| Código fuera de Spring | No aplica | ✅ Sí |
| Querés simplicidad declarativa | ✅ `@Async` | Más código |

---

## 🔹 Ejemplos comparativos

### Con `@Async` + VT en Spring
```java
@Service
public class MiServicio {
    @Async
    public void tarea() {
        apiCall(); // corre en un virtual thread (si activaste VT en Spring)
    }
}
```

### Sin `@Async`, usando VT puro
```java
public class MiApp {
    public static void main(String[] args) {
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            exec.submit(() -> apiCall()); // cada submit = un VT
        }
    }
}
```

---

## ✅ Conclusión
- **Virtual Threads** los podés usar siempre, con o sin Spring.
- `@Async` = azúcar sintáctico de Spring para delegar a un executor.
- Si configurás Spring con VT → tus `@Async` corren en Virtual Threads.
- Si no querés depender de `@Async`, podés trabajar con VT directo desde el JDK.
