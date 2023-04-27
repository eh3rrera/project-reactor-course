package net.eherrera.reactor.m6.exercises;

import reactor.core.publisher.Flux;

public class Exercise02 {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> fastPublisher = Flux.range(1, 5);

        // TODO: Change the threading context and apply the slowConsumer method
        Flux<Integer> processedFlux = null;

        // TODO: Subscribe to the Flux and print the emitted elements

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
