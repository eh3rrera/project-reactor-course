package net.eherrera.reactor.m6.exercises;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

public class Exercise05 {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> fluxRange = Flux.range(1, 10);

        // Create a ParallelFlux with 4 rails
        // Use Schedulers.parallel() to run the work in parallel
        // Apply the processingFunction method
        ParallelFlux<Integer> parallelFlux = fluxRange
                .parallel(4)
                .runOn(Schedulers.parallel())
                .map(value -> processingFunction(value));

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
