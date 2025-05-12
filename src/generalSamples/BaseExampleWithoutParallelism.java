package generalSamples;

import java.util.Arrays;

public class BaseExampleWithoutParallelism {

    private int iterations;
    public BaseExampleWithoutParallelism(int iterations)
    {
        this.iterations = iterations;
    }

    public void runLogic()
    {
        System.out.println("Running generalSamples.BaseExampleWithoutParallelism");

        int[] numbers = new int[iterations];

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken for preparing the data: " + duration + " milliseconds");


        startTime = System.currentTimeMillis();

        int sum = Arrays.stream(numbers)
                .sum(); // Sum all elements

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;


        System.out.println("The sum is: " + sum);
        System.out.println("Time taken: " + duration + " milliseconds");
    }
}
