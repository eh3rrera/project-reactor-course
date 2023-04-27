package net.eherrera.reactor.m2.exercises;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

public class Exercise04 {
    public static void main(String[] args) {
        // Create a Supplier that provides a Publisher
        Supplier<Publisher<String>> supplier = createPublisherSupplier();

        // Use Flux.defer() with the supplier
        Flux<String> flux = Flux.defer(supplier);

        // Subscribe to the Flux
        flux.subscribe(
                value -> System.out.println("Subscriber 1 received: " + value)
        );

        // Subscribe again to the Flux
        flux.subscribe(
                value -> System.out.println("Subscriber 2 received: " + value)
        );
    }

    private static Supplier<Publisher<String>> createPublisherSupplier() {
        return () -> Flux.just("Current time: " + System.currentTimeMillis());
    }
}
