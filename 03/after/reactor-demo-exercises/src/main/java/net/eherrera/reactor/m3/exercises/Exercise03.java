package net.eherrera.reactor.m3.exercises;

import reactor.core.publisher.Flux;

public class Exercise03 {
    public static void main(String[] args) {
        // Create a Flux of Integer values
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);

        // Transform each value emitted by the Flux with the method getNumberAndSquare
        Flux<Integer> transformedFlux = flux.flatMap(Exercise03::getNumberAndSquare);

        // Subscribe to the transformed Flux
        transformedFlux.subscribe(System.out::println);
    }

    private static Flux<Integer> getNumberAndSquare(Integer value) {
        return Flux.just(value, value * value);
    }
}
