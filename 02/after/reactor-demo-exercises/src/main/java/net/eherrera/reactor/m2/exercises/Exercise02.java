package net.eherrera.reactor.m2.exercises;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Exercise02 {
    public static void main(String[] args) {
        // Create a simple Publisher
        Publisher<Integer> publisher = createPublisher();

        // Convert the Publisher to a Mono using fromDirect()
        Mono<Integer> mono = Mono.fromDirect(publisher);

        // Subscribe to the Mono
        mono.subscribe(
                value -> System.out.println("Received: " + value),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completed")
        );
    }

    private static Publisher<Integer> createPublisher() {
        // Create a Flux publisher that emits two values
        return Flux.just(1, 2);
    }
}
