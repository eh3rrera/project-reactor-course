package net.eherrera.reactor.m2.exercises;

import reactor.core.publisher.Flux;
import reactor.core.publisher.BaseSubscriber;

public class Exercise05 {
    public static void main(String[] args) {
        // Create a simple Flux that emits values
        Flux<Integer> flux = Flux.just(1, 2, 3);

        // Create a custom BaseSubscriber
        CustomBaseSubscriber<Integer> customSubscriber = new CustomBaseSubscriber<>();

        // Subscribe to the Flux using the custom BaseSubscriber
        flux.subscribe(customSubscriber);
    }
}

class CustomBaseSubscriber<T> extends BaseSubscriber<T> {
}
