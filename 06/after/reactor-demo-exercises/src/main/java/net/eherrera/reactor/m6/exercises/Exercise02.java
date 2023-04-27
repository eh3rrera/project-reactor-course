package net.eherrera.reactor.m6.exercises;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Exercise02 {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> fastPublisher = Flux.range(1, 5);

        // Change the threading context and apply the slowConsumer method
        Flux<Integer> processedFlux = fastPublisher
                .publishOn(Schedulers.single())
                .map(value -> slowConsumer(value));

        // Subscribe to the Flux and print the emitted element
        processedFlux.subscribe(System.out::println);

        Thread.sleep(15000);
    }

    public static Integer slowConsumer(Integer value) {
        try {
            // Simulate a slow consumer by adding a 1-second delay
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }
}
