package generalSamples;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExample {

    private int iterations;

    public CompletableFutureExample(int iterations) {
        this.iterations = iterations;
    }

    public void runLogic() throws ExecutionException, InterruptedException
    {
        System.out.println("Running generalSamples.CompletableFutureExample");

        long startTime = System.currentTimeMillis();

        /**
         * CompletableFuture is a class that provides a way to write non-blocking, asynchronous code in Java.
         */
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            int sum = 0;
            for (int i = 1; i <= iterations; i++) {
                sum += i;
            }
            return sum;
        });

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken for preparing the data: " + duration + " milliseconds");


        startTime = System.currentTimeMillis();

        /**
         * The get() method is a blocking call which will wait until the result of the computation is available.
         */
        int result = future.get();

        /*-
        // thenAccept is a non-blocking call
        future.thenAccept(sum -> System.out.println("The sum is: " + sum));
         */

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;


        System.out.println("The sum is: " + result);
        System.out.println("Time taken: " + duration + " milliseconds");
    }
}