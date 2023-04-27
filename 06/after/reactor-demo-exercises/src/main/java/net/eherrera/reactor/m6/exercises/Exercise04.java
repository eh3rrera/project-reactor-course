package net.eherrera.reactor.m6.exercises;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Exercise04 {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> fluxRange = Flux.range(1, 10);

        // Use a parallel scheduler to change the threading context
        // Apply the processInteger method
        // Change the threading context for subscribing
        Flux<Integer> processedFlux = fluxRange
                .publishOn(Schedulers.parallel())
                .map(value -> processInteger(value))
                .subscribeOn(Schedulers.single());

        processedFlux.subscribe(value -> System.out.println("Received: " + value));

        Thread.sleep(11000);
    }

    public static Integer processInteger(Integer value) {
        try {
            // Simulate a task with a 500ms delay
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value * 2;
    }
}
