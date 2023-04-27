package net.eherrera.reactor.m3.exercises;

import reactor.core.publisher.Flux;

public class Exercise01 {
    public static void main(String[] args) {
        // Create a Flux of Integer values
        Flux<Integer> flux = Flux.just(10, 11, 12, 13, 14);

        // Transform Flux
        Flux<String> transformedFlux = flux
                .map(value -> String.format("0x%08X", value));

        // Subscribe to the transformed Flux
        transformedFlux.subscribe(System.out::println);
    }
}
