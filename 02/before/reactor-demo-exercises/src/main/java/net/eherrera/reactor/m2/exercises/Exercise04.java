package net.eherrera.reactor.m2.exercises;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

public class Exercise04 {
    public static void main(String[] args) {
        // Create a Supplier that provides a Publisher
        Supplier<Publisher<String>> supplier = createPublisherSupplier();

        // TODO: Use Flux.defer() with the supplier

        // TODO: Subscribe to the Flux

        // TODO: Subscribe again to the Flux

    }

    private static Supplier<Publisher<String>> createPublisherSupplier() {
        return () -> Flux.just("Current time: " + System.currentTimeMillis());
    }
}
