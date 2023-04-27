package net.eherrera.reactor.m3.exercises;

import reactor.core.publisher.Mono;

public class Exercise02 {
    public static void main(String[] args) {
        // Create a Mono of Integer
        Mono<Integer> mono = Mono.just(2);

        // Transform the Mono using the method getSquareAsync
        Mono<Integer> transformedMono = mono.flatMap(Exercise02::getSquareAsync);

        // Subscribe to the transformed Mono
        transformedMono.subscribe(System.out::println);
    }

    private static Mono<Integer> getSquareAsync(Integer value) {
        // Create a Mono publisher that emits the square of the input number
        return Mono.fromCallable(() -> value * value);
    }
}
