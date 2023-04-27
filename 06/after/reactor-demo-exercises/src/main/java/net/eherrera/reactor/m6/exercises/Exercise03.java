package net.eherrera.reactor.m6.exercises;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Exercise03 {
    public static void main(String[] args) throws InterruptedException {
        // Call the method slowPublisher and
        // Use subscribeOn with a new bounding elastic scheduler
        Flux<Integer> processedFlux = slowPublisher()
                .subscribeOn(
                        Schedulers.newBoundedElastic(
                                2,
                                2,
                                "bounded-elastic",
                                30,
                                true)
                );

        // Subscribe to processedFlux passing the fastConsumer method
        processedFlux.subscribe(Exercise03::fastConsumer);

        Thread.sleep(11000);
    }

    private static Flux<Integer> slowPublisher() {
        return Flux.create(sink -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    // Simulate blocking IO with a 1-second delay
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sink.next(i);
            }
            sink.complete();
        });
    }

    public static void fastConsumer(Integer value) {
        System.out.println("Received: " + value);
    }
}
