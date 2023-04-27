package net.eherrera.reactor.m6.exercises;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

public class Exercise05 {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> fluxRange = Flux.range(1, 10);

        // TODO: Create a ParallelFlux with 4 rails
        // TODO: Use Schedulers.parallel() to run the work in parallel
        // TODO: Apply the processingFunction method
        ParallelFlux<Integer> parallelFlux = null;

        parallelFlux.subscribe(value -> System.out.println("Received: " + value));

        Thread.sleep(10000);
    }

    public static Integer processingFunction(Integer value) {
        try {
            // Simulate a task with a 500ms delay
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value * 2;
    }
}
