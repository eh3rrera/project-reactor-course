package net.eherrera.reactor.m8.exercises;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Exercise03 {
    public static void main(String[] args) {
        // Create a Mono from the blockingOperation method
        Mono<String> blockingMono = Mono.fromCallable(Exercise03::blockingOperation);

        // Run the blocking code on a bounded elastic schedulers
        blockingMono = blockingMono.subscribeOn(Schedulers.boundedElastic());

        // Subscribe to the Mono and print the emitted value
        blockingMono.subscribe(System.out::println);
    }

    public static String blockingOperation() {
        try {
            // Simulate a blocking operation using Thread.sleep()
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Blocking operation completed";
    }
}
