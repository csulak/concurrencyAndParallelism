package generalSamples;

import java.math.BigInteger;
import java.util.Arrays;

public class ParallelStreamExample {

    private int iterations;
    public ParallelStreamExample(int iterations)
    {
        this.iterations = iterations;
    }

    public void runLogic()
    {
        System.out.println("Running generalSamples.ParallelStreamExample");

        BigInteger[] numbers = new BigInteger[iterations];

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = BigInteger.valueOf(i + 1);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken for preparing the data: " + duration + " milliseconds");


        startTime = System.currentTimeMillis();

        /**
         * Parallel streams are capable of operating on multiple threads and can speed up the processing of large collections.
         */
        BigInteger sum = Arrays.stream(numbers)
                .parallel() // Enables parallel processing
                .reduce(BigInteger.ZERO, BigInteger::add); // Sum all elements

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;


        System.out.println("The sum is: " + sum);
        System.out.println("Time taken: " + duration + " milliseconds");
    }


    public String hola(String name)
    {
        return "hola";
    }

    /*
    public Integer hola(String name)
    {
        return 0;
    }

     */

}

