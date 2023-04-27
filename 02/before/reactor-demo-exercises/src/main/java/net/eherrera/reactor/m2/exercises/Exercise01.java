package net.eherrera.reactor.m2.exercises;

import reactor.core.publisher.Mono;

public class Exercise01 {
    public static void main(String[] args) {
        // TODO: Create two Optional variables, one with a value and one empty


        // TODO: Use the justOrEmpty method to create Mono instances for each Optional
        Mono<String> monoWithValue = null;
        Mono<String> monoEmpty = null;

        // Subscribe to both Monos and print the results
        monoWithValue.subscribe(
                element -> System.out.println("Mono with value - Value: " + element),
                error -> System.err.println("Mono with value - Error: " + error.getMessage()),
                () -> System.out.println("Mono with value complete")
        );

        monoEmpty.subscribe(
                element -> System.out.println("Mono empty - Value: " + element),
                error -> System.err.println("Mono empty - Error: " + error.getMessage()),
                () -> System.out.println("Mono empty complete")
        );
    }
}
