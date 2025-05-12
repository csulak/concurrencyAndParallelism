# Concurrency and Parallelism Learning Project

Welcome to the **Concurrency and Parallelism Learning Project**! This repository is designed to help developers understand and experiment with concepts related to multithreading, synchronization, and atomic operations in Java. The project includes practical examples and exercises to deepen your knowledge of concurrent programming.

---

## 📖 **Overview**

This project focuses on the following key areas of concurrency and parallelism:

- **Thread Management**: Learn how to create and manage threads in Java.
- **Synchronization**: Explore techniques to avoid race conditions and ensure thread safety.
- **Locks and Conditions**: Understand how to use `ReentrantLock` and `Condition` for advanced thread coordination.
- **Atomic Operations**: Experiment with `AtomicInteger` and other atomic classes for lock-free thread-safe operations.
- **Wait/Notify Mechanisms**: Dive into inter-thread communication using `wait()`, `notify()`, and `notifyAll()`.

---

## 📂 **Project Structure**

The repository is organized into the following directories:

```
src/
├── threads/
│   ├── amotic/
│   │   └── AtomicExample.java       # Demonstrates atomic operations using AtomicInteger
│   ├── condition/
│   │   └── ConditionMain.java       # Example of using ReentrantLock and Condition
│   └── volatileSample/              # Examples of volatile keyword usage
└── ...
```

### Key Files:
- **`AtomicExample.java`**: Demonstrates the use of `AtomicInteger` for thread-safe operations.
- **`ConditionMain.java`**: Explains thread coordination using `ReentrantLock` and `Condition`.

---

## 🚀 **Getting Started**

### Prerequisites
- **Java 8 or higher**: Ensure you have the JDK installed.
- **IntelliJ IDEA**: Recommended IDE for running and debugging the examples.

### Running the Examples
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/concurrency-parallelism-learning.git
   ```
2. Open the project in IntelliJ IDEA.
3. Navigate to the desired example (e.g., `src/threads/amotic/AtomicExample.java`).
4. Run the file to see the output.

---

## 📚 **Topics Covered**

### 1. **Thread Management**
- Creating threads using `Thread` and `Runnable`.
- Using method references for thread execution.

### 2. **Synchronization**
- Using `synchronized` blocks to ensure thread safety.
- Avoiding race conditions in shared resources.

### 3. **Locks and Conditions**
- Using `ReentrantLock` for fine-grained control over thread synchronization.
- Coordinating threads with `Condition` objects.

### 4. **Atomic Operations**
- Leveraging `AtomicInteger` for lock-free thread-safe operations.
- Understanding atomicity and visibility in multithreaded environments.

### 5. **Wait/Notify Mechanisms**
- Using `wait()`, `notify()`, and `notifyAll()` for inter-thread communication.


---

Thanks to MAKIGAS for the initial structure and examples in this repository. This project is a collaborative effort to enhance understanding of concurrency and parallelism in Java.
Link to course: [Java Concurrency and Parallelism](https://www.youtube.com/watch?v=9ATZc9h4fHM&list=PLTd5ehIj0goMJFNUHaeHNAJEnmX8lZsjo&ab_channel=makigas)
