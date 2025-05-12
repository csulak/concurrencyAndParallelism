package generalSamples;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

class SumTask extends RecursiveTask<Integer> {
    private final int[] numbers;
    private final int start;
    private final int end;

    public SumTask(int[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (end - start <= 10) { // Case base
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += numbers[i];
            }
            return sum;
        } else {
            int mid = (start + end) / 2;
            SumTask leftTask = new SumTask(numbers, start, mid);
            SumTask rightTask = new SumTask(numbers, mid, end);
            leftTask.fork(); // Inits the left task
            int rightResult = rightTask.compute(); // Process the right task
            int leftResult = leftTask.join(); // Wait and get the result of the left task
            return leftResult + rightResult;
        }
    }
}

public class ForkJoinPoolExample {

    private int iterations;

    public ForkJoinPoolExample(int iterations)
    {
        this.iterations = iterations;
    }

    public void runLogic()
    {
        System.out.println("Running generalSamples.ForkJoinPoolExample");

        int[] numbers = new int[iterations];

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken for preparing the data: " + duration + " milliseconds");

        ForkJoinPool pool = new ForkJoinPool();
        SumTask task = new SumTask(numbers, 0, numbers.length);

        startTime = System.currentTimeMillis();

        /**
         * The invoke() method is a blocking call which will wait until the result of the computation is available.
         */
        int result = pool.invoke(task);

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;

        System.out.println("The sum is: " + result);
        System.out.println("Time taken: " + duration + " milliseconds");
    }
}


