package generalSamples;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        int iterations = 10000000;

        BaseExampleWithoutParallelism baseExampleWithoutParallelism = new BaseExampleWithoutParallelism(iterations);
        baseExampleWithoutParallelism.runLogic();

        System.out.println("\n----------------------------------------------------\n");

        ParallelStreamExample parallelStreamExample = new ParallelStreamExample(iterations);
        parallelStreamExample.runLogic();

        System.out.println("\n----------------------------------------------------\n");

        ForkJoinPoolExample forkJoinPoolExample = new ForkJoinPoolExample(iterations);
        forkJoinPoolExample.runLogic();

        System.out.println("\n----------------------------------------------------\n");

        CompletableFutureExample completableFutureExample = new CompletableFutureExample(iterations);
        completableFutureExample.runLogic();

        System.out.println("\n----------------------------------------------------\n");

    }

}